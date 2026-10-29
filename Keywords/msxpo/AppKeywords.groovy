package msxpo

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

import org.openqa.selenium.Keys
import org.openqa.selenium.interactions.Actions

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * Reusable keywords for the MSxpo create-event wizard (app.msxpo.sa).
 * All UI facts here are from live DOM capture (2026-10-26) plus the QA reports
 * (login-test-report.md, create-event-flow-report.md, create-event-test-case-report.md).
 * Workarounds for known app findings are marked [F#] with their report of origin.
 */
class AppKeywords {

	// ------------------------------------------------------------------ login

	/**
	 * [login-test-report F4] Sign In can double-fire and never disables — click once,
	 * then wait for redirect; [F5] watch for the intermittent bounce back to /login.
	 */
	@Keyword
	def login(String email, String password) {
		WebUI.openBrowser('')
		WebUI.navigateToUrl(Config.baseUrl() + '/login')
		WebUI.waitForElementVisible(Selectors.emailInput(), 15)
		WebUI.setText(Selectors.emailInput(), email)
		WebUI.setText(Selectors.passwordInput(), password)
		WebUI.click(Selectors.signInButton())
		WebUI.waitForCondition({ return !WebUI.getUrl().contains('/login') }, 20)
		WebUI.comment("Logged in as ${email} -> ${WebUI.getUrl()}")
	}

	// ------------------------------------------------------------- navigation

	@Keyword
	def openCreateEventWizard() {
		WebUI.navigateToUrl(Config.baseUrl() + '/events/create')
		WebUI.waitForElementVisible(Selectors.nameInput(), 15)
		WebUI.verifyMatch(WebUI.getPageText(), '.*Event Basics.*', true)
	}

	/** Forward control on steps 1-4 is always "Continue". */
	@Keyword
	def continueWizard() {
		WebUI.waitForElementVisible(Selectors.continueButton(), 10)
		WebUI.click(Selectors.continueButton())
		WebUI.delay(1)
	}

	// ------------------------------------------------------ dropdowns (shim selects)

	/**
	 * The native <select> is a hidden shim — setting its value does NOT update React state
	 * (verified live: trigger kept showing "Select Event Type"). Must click the visible
	 * trigger, then the [role=option] rendered in the portal.
	 */
	@Keyword
	def selectDropdownByLabel(TestObject trigger, String optionText) {
		WebUI.click(trigger)
		WebUI.waitForElementVisible(Selectors.dropdownOption(optionText), 5)
		WebUI.click(Selectors.dropdownOption(optionText))
		WebUI.delay(0.3)
	}

	// ------------------------------------------------------------- calendar

	/**
	 * shadcn calendar: click the field trigger -> step months via the aria-labelled nav
	 * buttons -> click the exact day cell (past days are @disabled, dates must be future).
	 * The end-date picker may open on an unexpected month — the loop normalises it.
	 */
	@Keyword
	def pickDate(TestObject trigger, LocalDate date) {
		String monthName = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
		WebUI.click(trigger)
		WebUI.waitForElementVisible(Selectors.calendarCaption(), 5)

		String target = monthName + ' ' + date.year
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern('MMMM yyyy', Locale.ENGLISH)
		int guard = 0
		while (!WebUI.getText(Selectors.calendarCaption()).trim().contains(target) && guard < 24) {
			YearMonth shown = YearMonth.parse(WebUI.getText(Selectors.calendarCaption()).trim(), fmt)
			TestObject nav = shown.isBefore(YearMonth.of(date.year, date.month)) ?
				Selectors.calendarNextMonth() : Selectors.calendarPrevMonth()
			WebUI.click(nav)
			WebUI.delay(0.2)
			guard++
		}
		WebUI.verifyMatch(WebUI.getText(Selectors.calendarCaption()).trim(), '.*' + target + '.*', true)
		WebUI.click(Selectors.calendarDay(date.dayOfMonth, monthName))
		WebUI.delay(0.3)
	}

	// -------------------------------------------------- visibility switches [F1]

	/**
	 * [create-event-test-case-report F1 — High] Featured/stats LOOK enabled but the draft
	 * persisted all-false with no error. Toggle, verify control state now, then re-verify
	 * on Review (verifyReviewFlags) and recover if the loss is visible there.
	 */
	@Keyword
	def ensureSwitchOn(TestObject switchObj, String label) {
		String state = WebUI.getAttribute(switchObj, 'aria-checked')
		if (!'true'.equalsIgnoreCase(state)) {
			WebUI.click(switchObj)
			WebUI.delay(0.3)
			state = WebUI.getAttribute(switchObj, 'aria-checked')
		}
		WebUI.verifyMatch(state, 'true', false, FailureHandling.CONTINUE_ON_FAILURE)
		WebUI.comment("${label} switch = ${state}")
	}

	@Keyword
	def ensureCheckboxOn(TestObject box, String label) {
		if (!WebUI.verifyElementChecked(box, 3, FailureHandling.CONTINUE_ON_FAILURE)) {
			WebUI.click(box)
			WebUI.delay(0.2)
		}
		WebUI.verifyElementChecked(box, 3)
		WebUI.comment("${label} checkbox = checked")
	}

	/**
	 * Safety net for step 3: every switch on the questionnaire page (master enable,
	 * questionnaire-level required, per-question required) must end up ON for this spec —
	 * regardless of DOM order changes.
	 */
	@Keyword
	def ensureAllSwitchesOn() {
		int total = WebUI.getNumberOfElements(Selectors.anySwitch())
		for (int i = 1; i <= total; i++) {
			ensureSwitchOn(Selectors.switchAt(i), "switch[${i}]")
		}
		WebUI.comment("Verified all ${total} switches on the step are ON")
	}

	// ---------------------------------------------------------- media upload

	/**
	 * Dropzone uses a hidden input[type=file][accept=image/*] — uploadFile targets it
	 * directly (works reliably — create-event-test-case-report F8).
	 */
	@Keyword
	def uploadEventMedia(String filePath) {
		WebUI.uploadFile(Selectors.mediaFileInput(), filePath)
		WebUI.delay(2) // S3 handshake
	}

	// ------------------------------------------------------------- event admin

	/**
	 * [create-event-flow-report F1] Directory search reacts only to REAL keystrokes —
	 * programmatic value-setting (paste) yields no suggestions. Type char-by-char.
	 * [flow-report F2] "Add Admin" can double-fire — duplicate empty draft rows do not
	 * block Continue in practice; we log when it happens so the flake is visible.
	 */
	@Keyword
	def addAdminFromDirectory(String fullName) {
		int before = WebUI.getNumberOfElements(Selectors.adminNameInputs())
		WebUI.click(Selectors.addAdminButton())
		WebUI.waitForElementVisible(Selectors.adminNameInput(before + 1), 5)
		TestObject nameField = Selectors.adminNameInput(before + 1)

		WebUI.click(nameField)
		for (char c : fullName.toCharArray()) {
			WebUI.sendKeys(nameField, String.valueOf(c))
			WebUI.delay(0.12) // debounced directory query
		}
		WebUI.delay(1.0)

		// two offers appear: directory match (name + email) vs 'Add "..." as new admin'
		WebUI.waitForElementVisible(Selectors.directorySuggestion(fullName), 8)
		WebUI.click(Selectors.directorySuggestion(fullName))
		// trusted click commits the row: editable inputs swap to a read-only row (F7)
		WebUI.verifyElementNotPresent(nameField, 5)

		int after = WebUI.getNumberOfElements(Selectors.adminNameInputs())
		if (after > 1) {
			WebUI.comment("[F2] Add Admin double-fired: ${after} rows present (directory row committed)")
		}
		WebUI.comment("Directory admin '${fullName}' selected and committed")
	}

	// ------------------------------------------------ visitor questionnaire

	@Keyword
	def enableQuestionnaire() {
		WebUI.click(Selectors.questionnaireSwitch())
		WebUI.delay(0.5)
		WebUI.verifyElementVisible(Selectors.addQuestionButton(), 5)
	}

	@Keyword
	def addQuestion(int index, String text, String type, String placeholder, boolean required) {
		WebUI.click(Selectors.addQuestionButton())
		WebUI.waitForElementVisible(Selectors.questionTextInput(index), 5)
		WebUI.setText(Selectors.questionTextInput(index), text)
		if (placeholder) {
			WebUI.setText(Selectors.questionPlaceholderInput(index), placeholder)
		}
		// question type defaults to "Text" — only open the dropdown for Media
		if (!type.equalsIgnoreCase('Text')) {
			WebUI.click(Selectors.questionTypeTrigger(index))
			WebUI.waitForElementVisible(Selectors.dropdownOption(type), 5)
			WebUI.click(Selectors.dropdownOption(type))
			WebUI.delay(0.3)
		}
		if (required) {
			ensureSwitchOn(Selectors.questionRequiredSwitch(index), "Q${index} required")
		}
	}

	// -------------------------------------------------- floor plan: customise

	/**
	 * Customise panel (live capture): halls button group 1-5, arrangement cards
	 * (Grid/U-Shape/Theater/Perimeter), then exactly 12 tier number inputs in order:
	 * basic count/price/w/h, standard ..., premium .... Text commits on blur -> Tab.
	 */
	@Keyword
	def configureCustomise(int halls, String arrangement,
			int basicCount, int basicPrice, int basicW, int basicH,
			int stdCount, int stdPrice, int stdW, int stdH,
			int premCount, int premPrice, int premW, int premH) {
		WebUI.click(Selectors.blankTemplateCustomise())
		WebUI.waitForElementVisible(Selectors.hallsButton(1), 5)

		WebUI.click(Selectors.hallsButton(halls))
		WebUI.delay(0.3)
		WebUI.click(Selectors.arrangementOption(arrangement))
		WebUI.delay(0.3)

		setNumber(1, basicCount); setNumber(2, basicPrice); setNumber(3, basicW); setNumber(4, basicH)
		setNumber(5, stdCount);   setNumber(6, stdPrice);   setNumber(7, stdW);   setNumber(8, stdH)
		setNumber(9, premCount);  setNumber(10, premPrice); setNumber(11, premW); setNumber(12, premH)

		WebUI.click(Selectors.configurePlannerButton())
		WebUI.waitForElementVisible(Selectors.editorAddBooth(), 20)
		WebUI.delay(1.5) // Konva canvas + auto-generated hall/booths render
	}

	@Keyword
	def setNumber(int index, int value) {
		TestObject field = Selectors.tierNumberInput(index)
		WebUI.setText(field, String.valueOf(value))
		WebUI.sendKeys(field, Keys.chord(Keys.TAB)) // blur = commit
		WebUI.delay(0.15)
	}

	// ----------------------------------------------- floor plan: editor assets

	/**
	 * Banner/Decoration spawn immediately at x≈2,y≈2 with EMPTY price and block
	 * Save Configuration [create-event-test-case-report F2 — High]. Workaround: after each
	 * spawn, immediately write position + price through the properties panel.
	 * Coordinates are pre-aligned to the 3 m grid (F6 snaps silently on commit anyway).
	 *
	 * Selection guard: the properties panel swaps only when the NEW element is selected —
	 * banners prove it via the Price field appearing (halls have no price);
	 * decorations run AFTER all banners, so price field DISAPPEARING proves deco selected
	 * (decorations store no price — F2).
	 */
	@Keyword
	def addBannerAt(int x, int y, int price) {
		WebUI.click(Selectors.assetBanner())
		WebUI.waitForElementVisible(Selectors.bannerPriceInput(), 5)
		setProp(Selectors.propNumber('X (m)'), x)
		setProp(Selectors.propNumber('Y (m)'), y)
		setProp(Selectors.bannerPriceInput(), price)
		WebUI.comment("Banner placed at (${x},${y}) price ${price}")
	}

	@Keyword
	def addDecorationAt(int x, int y) {
		WebUI.click(Selectors.assetDecoration())
		WebUI.delay(0.8)
		if (WebUI.verifyElementVisible(Selectors.bannerPriceInput(), 2, FailureHandling.CONTINUE_ON_FAILURE)) {
			throw new IllegalStateException(
				"Decoration spawn: price field still visible -> selection did not move to the new decoration. " +
				"Refusing to write coordinates onto the previously selected element.")
		}
		WebUI.waitForElementVisible(Selectors.propNumber('X (m)'), 5)
		setProp(Selectors.propNumber('X (m)'), x)
		setProp(Selectors.propNumber('Y (m)'), y)
		WebUI.comment("Decoration placed at (${x},${y})")
	}

	@Keyword
	def setProp(TestObject field, int value) {
		WebUI.setText(field, String.valueOf(value))
		WebUI.sendKeys(field, Keys.chord(Keys.TAB))
		WebUI.delay(0.15)
	}

	/**
	 * Konva canvas is pixel-rendered (no DOM children) — placement clicks need real mouse
	 * events at element-relative coordinates.
	 */
	@Keyword
	def clickCanvasAt(int offsetX, int offsetY) {
		new Actions(DriverFactory.getWebDriver())
			.moveToElement(WebUI.findWebElement(Selectors.canvas(), 5), offsetX, offsetY)
			.click().perform()
		WebUI.delay(0.4)
	}

	/**
	 * Configuration Check then Save. Trust ONLY the post-save state — the on-screen
	 * overlap counter is stale between saves [test-case-report F3]. Never press Undo
	 * (F4: it can wipe the entire hall). Throws with the validator's own text on failure.
	 */
	@Keyword
	def validateAndSaveConfiguration() {
		WebUI.click(Selectors.configCheckSaveButton())
		WebUI.delay(1.5)

		String page = WebUI.getPageText()
		if (!(page =~ /(?s)Configuration check is valid/)) {
			// surface whatever the validation panel says
			String issues = page.replaceAll(/(?s).*?((\d+\s+(critical issues|overlaps?|missing prices?)[^]*?)).*/, '$1')
			throw new AssertionError("Floor plan validation failed. Page reports: ${issues.take(300)}")
		}
		WebUI.comment('Configuration check is valid')

		// if the check already returned to step 4, the save button is gone — skip it
		if (WebUI.verifyElementVisible(Selectors.saveConfigurationButton(), 3, FailureHandling.CONTINUE_ON_FAILURE)) {
			WebUI.click(Selectors.saveConfigurationButton())
			WebUI.delay(1.5)
		}
	}

	// ------------------------------------------------------ review & save

	/**
	 * Review must show the flags the user set — F1's silent loss becomes visible here.
	 * Positive form: "FEATURED: Yes" / "PUBLIC STATS: <list>" (lost form: No / None).
	 */
	@Keyword
	def verifyReviewFlags(boolean expectFeatured, boolean expectAnyStats) {
		String page = WebUI.getPageText()
		if (expectFeatured) {
			WebUI.verifyMatch(page, '(?s).*FEATURED:\\s*Yes.*', true)
		}
		if (expectAnyStats) {
			WebUI.verifyMatch(page, '(?s).*PUBLIC STATS:\\s*(?!None).*', true)
		}
	}

	/**
	 * F1 recovery: if Review shows the flags were dropped, use the first "Edit" shortcut
	 * (Event Details), re-toggle on step 1, then Continue x4 back to Review
	 * (all data is preserved across steps — flow report).
	 */
	@Keyword
	def recoverFeaturedIfLost(boolean expectFeatured, boolean expectAnyStats) {
		String page = WebUI.getPageText()
		boolean lost = (expectFeatured && (page =~ /FEATURED:\s*No/)) ||
			(expectAnyStats && (page =~ /PUBLIC STATS:\s*None/))
		if (!lost) {
			WebUI.comment('Review flags intact — no recovery needed')
			return
		}
		WebUI.comment('[F1 workaround] flags lost on persist — re-toggling via Edit shortcut')
		WebUI.click(Selectors.editEventDetailsShortcut())
		WebUI.waitForElementVisible(Selectors.nameInput(), 10)
		if (expectFeatured) ensureSwitchOn(Selectors.featuredSwitch(), 'Featured')
		if (expectAnyStats) {
			ensureCheckboxOn(Selectors.statCheckbox('Total visitors'), 'Total visitors')
			ensureCheckboxOn(Selectors.statCheckbox('Halls count'), 'Halls count')
			ensureCheckboxOn(Selectors.statCheckbox('Booths count'), 'Booths count')
			ensureCheckboxOn(Selectors.statCheckbox('Participants count'), 'Participants count')
		}
		for (int i = 0; i < 4; i++) { // steps 1 -> 2 -> 3 -> 4 -> 5
			continueWizard()
		}
		WebUI.delay(1)
	}

	/**
	 * POST /tenant-admin/events -> 201. Careful: '/events/create' also contains
	 * '/events/' — exclude it or the wait passes instantly on the wizard itself.
	 */
	@Keyword
	def saveAsDraft() {
		WebUI.click(Selectors.saveAsDraftButton())
		WebUI.waitForCondition({
			String u = WebUI.getUrl()
			return (u.endsWith('/events') || (u.contains('/events/') && !u.contains('/events/create')))
		}, 30)
		WebUI.comment("Event saved as draft; landed on ${WebUI.getUrl()}")
	}

	// ------------------------------- manage participant > approve participant

	/**
	 * Walk into Manage Participant -> Approve Participant.
	 * Both hops are optional on purpose: if the nav entry / tab is not on screen we assume the
	 * page was already opened directly (deep link) or the labelling differs — the caller asserts
	 * the target participant's name is visible, which is the real proof we reached the list.
	 */
	@Keyword
	def openApproveParticipants() {
		if (WebUI.verifyElementVisible(Selectors.manageParticipantsNav(), 5, FailureHandling.OPTIONAL)) {
			WebUI.click(Selectors.manageParticipantsNav())
			WebUI.delay(1)
		} else {
			WebUI.comment('No "Manage Participant" nav entry visible — assuming the screen is already open')
		}
		if (WebUI.verifyElementVisible(Selectors.approveParticipantsTab(), 5, FailureHandling.OPTIONAL)) {
			WebUI.click(Selectors.approveParticipantsTab())
			WebUI.delay(1)
		} else {
			WebUI.comment('No "Approve Participant" tab visible — assuming the approve list is already shown')
		}
	}

	/**
	 * Click the Approve button of the participant whose name matches `name` — AND ONLY that
	 * participant's button: the locator is anchored on the exact name text and scoped to the
	 * enclosing row (Selectors.approveButtonInRow), so other pending rows are never touched.
	 *
	 * Safety behaviour:
	 *  - fails fast when the named participant has no Approve button (already approved / not pending);
	 *  - clicks a confirmation dialog only when one actually opens, and only inside role=dialog;
	 *  - verifies afterwards that this row stopped offering Approve and that the total number of
	 *    Approve buttons dropped by exactly one (i.e. no other participant was approved).
	 */
	@Keyword
	def approveParticipantByName(String name) {
		name = (name ?: '').trim()
		if (!name) {
			throw new IllegalArgumentException('Participant name from test data is empty — nothing to approve')
		}
		if (name.contains("'")) {
			throw new IllegalArgumentException(
				"Participant names containing an apostrophe are not supported by the XPath locator: '${name}'")
		}

		TestObject nameCell   = Selectors.participantNameCell(name)
		TestObject approveBtn = Selectors.approveButtonInRow(name)

		WebUI.waitForElementVisible(nameCell, 10)
		if (!WebUI.verifyElementVisible(approveBtn, 5, FailureHandling.OPTIONAL)) {
			throw new IllegalStateException(
				"No Approve button in the row of participant '${name}' — either it is already approved, " +
				"not on this list (check the event filter), or the row structure differs from the " +
				"captured one (fix Selectors.approveButtonInRow).")
		}

		int before = WebUI.getNumberOfElements(Selectors.anyApproveButton())
		WebUI.comment("Clicking Approve for '${name}' (row has ${before} Approve buttons on screen)")
		WebUI.click(approveBtn)

		// confirm only a REAL dialog (never the list's own Approve buttons)
		if (WebUI.verifyElementVisible(Selectors.confirmDialogButton(), 3, FailureHandling.OPTIONAL)) {
			WebUI.click(Selectors.confirmDialogButton())
			WebUI.delay(0.5)
		}

		// the click must be consumed by this row: it no longer offers Approve
		if (WebUI.verifyElementVisible(approveBtn, 10, FailureHandling.OPTIONAL)) {
			throw new AssertionError(
				"The Approve button of '${name}' is still on screen after clicking — the click did not " +
				"change this row (list not refreshed, approval rejected, or the button stays visible " +
				"disabled once approved).")
		}
		// ... and nothing else changed: exactly one Approve button left the screen
		int after = WebUI.getNumberOfElements(Selectors.anyApproveButton())
		WebUI.verifyMatch(String.valueOf(after), String.valueOf(before - 1), true, FailureHandling.CONTINUE_ON_FAILURE)
		WebUI.comment("Approved participant '${name}' (Approve buttons on the list: ${before} -> ${after})")
	}

	// ----------------------------------------------------------------- data

	static Map readRow(String testDataName, int row) {
		def data = findTestData(testDataName)
		Map out = [:]
		data.getColumnNames().each { col -> out[col] = data.getValue(col, row) }
		return out
	}
}
