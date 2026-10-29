package msxpo

import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.testobject.ConditionType

/**
 * Central registry of UI selectors for app.msxpo.sa (Event Host super admin).
 * ALL selectors below were captured from the live app on 2026-10-26 via DOM inspection.
 * Keep every selector here so a UI change is fixed in exactly one place.
 */
class Selectors {

	static TestObject xp(String xpath) {
		return new TestObject().addProperty('xpath', ConditionType.EQUALS, xpath)
	}
	static TestObject css(String css) {
		return new TestObject().addProperty('css', ConditionType.EQUALS, css)
	}

	// ---------------------------------------------------------------- login
	static TestObject emailInput()    { return css('#email') }        // input#email placeholder "Enter your email"
	static TestObject passwordInput() { return css('#password') }     // input#password placeholder "Enter your password"
	static TestObject signInButton()  { return xp("//button[normalize-space()='Sign In']") }

	// ----------------------------------------------------------- shell/nav
	static TestObject createEventButton() { return xp("//button[normalize-space()='Create Event']") }
	static TestObject continueButton()    { return xp("(//button[normalize-space()='Continue'])[1]") }
	static TestObject backButton()        { return xp("(//button[normalize-space()='Back'])[1]") }

	// ------------------------------------------------- step 1: event basics
	static TestObject nameInput()  { return css('#name') }            // placeholder "e.g. Tech Expo 2026"
	static TestObject descTextarea(){ return css('#description') }    // placeholder "Brief event overview..."
	// labelled trigger button (text changes once a value is picked, so anchor on the label)
	static TestObject eventTypeTrigger()  { return xp("(//label[contains(.,'Event Types')]/ancestor::div[1]//button[1])[1]") }
	static TestObject startDateTrigger()  { return xp("(//label[contains(.,'Start Date')]/ancestor::div[1]//button[1])[1]") }
	static TestObject endDateTrigger()    { return xp("(//label[contains(.,'End Date')]/ancestor::div[1]//button[1])[1]") }
	static TestObject industryTrigger()   { return xp("(//label[contains(.,'Industry')]/ancestor::div[1]//button[1])[1]") }
	static TestObject timezoneTrigger()   { return xp("//button[@aria-label='Selected: Asia/Kolkata']") }
	static TestObject mediaFileInput()    { return xp("//input[@type='file']") }  // hidden, accept="image/*"
	// Radix dropdown option rendered in a portal
	static TestObject dropdownOption(String label) { return xp("//div[@role='option'][normalize-space()='${label}']") }
	// Featured switch = the only role=switch on step 1
	static TestObject featuredSwitch()   { return xp("(//button[@role='switch'])[1]") }
	static TestObject statCheckbox(String label) {
		return xp("//label[normalize-space()='${label}']//input[@type='checkbox']")
	}

	// --------------------------------------------------- calendar picker
	// caption div holds the visible month, e.g. "October 2026"
	static TestObject calendarCaption()  { return xp("(//*[contains(@class,'caption')])[1]") }
	static TestObject calendarPrevMonth(){ return xp("//button[@aria-label='Go to the Previous Month']") }
	static TestObject calendarNextMonth(){ return xp("//button[@aria-label='Go to the Next Month']") }
	// day cell: exact day text + month in the aria-label ("Sunday, November 1st, 2026"), future days enabled
	static TestObject calendarDay(int day, String monthName) {
		return xp("//td//button[normalize-space()='${day}' and contains(@aria-label,'${monthName}') and not(@disabled)]")
	}

	// -------------------------------------------------- step 2: event admin
	static TestObject addAdminButton()      { return xp("//button[normalize-space()='Add Admin']") }
	static TestObject adminNameInput(int row) { return xp("(//input[@placeholder='Full name'])[${row}]") }
	static TestObject adminNameInputs()     { return xp("//input[@placeholder='Full name']") }
	static TestObject adminEmailInput(int row){ return xp("(//input[@placeholder='Email address'])[${row}]") }
	static TestObject adminPhoneInput(int row){ return xp("(//input[@type='tel'])[${row}]") }
	// directory suggestion = button showing the name AND an email (vs the 'Add "..." as new admin' offer)
	static TestObject directorySuggestion(String name) {
		return xp("(//button[contains(.,'${name}') and contains(.,'@')])[1]")
	}

	// ------------------------------------- step 3: visitor questionnaire
	// master toggle = first switch on the step
	static TestObject questionnaireSwitch() { return xp("(//button[@role='switch'])[1]") }
	// generic switch accessors for the all-switches safety net
	static TestObject anySwitch() { return xp("//button[@role='switch']") }
	static TestObject switchAt(int i) { return xp("(//button[@role='switch'])[${i}]") }
	static TestObject questionnaireTitleInput()  { return xp("//input[@placeholder='e.g., Visitor Information Form']") }
	static TestObject questionnaireDescTextarea(){ return xp("//textarea[@placeholder='Provide instructions or context for visitors...']") }
	static TestObject addQuestionButton() { return xp("//button[normalize-space()='Add Question']") }
	static TestObject questionTextInput(int q)  { return xp("(//input[@placeholder='Question *'])[${q}]") }
	static TestObject questionPlaceholderInput(int q) { return xp("(//input[@placeholder='Placeholder (optional)'])[${q}]") }
	// question-type trigger: only these buttons carry exactly Text or Media; n-th question = n-th such button
	static TestObject questionTypeTrigger(int q) {
		return xp("(//button[normalize-space()='Text' or normalize-space()='Media'])[${q}]")
	}
	// page switches: [1]=master, [2]=Q1 required, [3]=Q2 required ...
	static TestObject questionRequiredSwitch(int q) { return xp("(//button[@role='switch'])[${q + 1}]") }

	// ----------------------------------------------- step 4: floor plan
	static TestObject blankTemplateCustomise() {
		// card: div.group > button (card body) + div > button "Customise"
		return xp("(//button[contains(.,'Blank Template')]/parent::div//button[normalize-space()='Customise'])[1]")
	}
	static TestObject hallsButton(int n) {
		return xp("(//*[normalize-space()='Number of Halls']/following::button[normalize-space()='${n}'])[1]")
	}
	static TestObject arrangementOption(String name) { return xp("//button[contains(.,'${name}')]") }
	// customise panel has exactly 12 number inputs, in tier order:
	// 1 basicCount 2 basicPrice 3 basicW 4 basicH 5 stdCount 6 stdPrice 7 stdW 8 stdH 9 premCount 10 premPrice 11 premW 12 premH
	static TestObject tierNumberInput(int index) { return xp("(//input[@type='number'])[${index}]") }
	static TestObject configurePlannerButton() { return xp("//button[normalize-space()='Configure Planner']") }

	// ------------------------------------------------- floor plan editor
	static TestObject editorAddBooth()  { return xp("//button[normalize-space()='Add Booth']") }
	static TestObject assetBanner()     { return xp("//button[normalize-space()='Banner']") }
	static TestObject assetDecoration() { return xp("//button[normalize-space()='Decoration']") }
	static TestObject canvas()          { return xp("//div[contains(@class,'konvajs-content')]//canvas[1]") }
	// properties panel fields (shared by hall/booth/banner/decoration), anchored on their labels
	static TestObject propNumber(String label) { return xp("(//*[normalize-space()='${label}']/following::input[@type='number'])[1]") }
	// banner price label is "Price ($)"; fall back handled in AppKeywords
	static TestObject bannerPriceInput() { return xp("(//*[starts-with(normalize-space(),'Price')]/following::input[@type='number'])[1]") }
	static TestObject configCheckSaveButton() {
		// real button text is "Configuration Check" + "Save" -> normalize to ConfigurationCheckSave
		return xp("//button[normalize-space(translate(normalize-space(.),' ',''))='ConfigurationCheckSave']")
	}
	static TestObject saveConfigurationButton() { return xp("//button[normalize-space()='Save Configuration']") }

	// --------------------------------------------- step 5: review & save
	static TestObject saveAsDraftButton() { return xp("//button[normalize-space()='Save as Draft']") }
	static TestObject publishEventButton(){ return xp("//button[normalize-space()='Publish Event']") }
	// review "Edit" shortcuts: first Edit = Event Details section
	static TestObject editEventDetailsShortcut() { return xp("(//button[normalize-space()='Edit'])[1]") }
	static TestObject draftBadge() { return xp("//*[normalize-space()='Draft']") }

	// ------------------------------- manage participant > approve participant
	// Text-anchored selectors: this screen has NO live DOM capture yet (all other selectors in
	// this file do) — they match the visible labels "Manage Participant" / "Approve Participant"
	// and the exact participant name. If the app renames them, fix them HERE only.

	// sidebar / page entry, e.g. "Manage Participant(s)"
	static TestObject manageParticipantsNav() {
		return xp("(//a[contains(.,'Manage Participant')] | //button[contains(.,'Manage Participant')])[1]")
	}
	// tab / section / button "Approve Participant(s)" inside the manage-participant screen
	static TestObject approveParticipantsTab() {
		return xp("(//a[contains(.,'Approve Participant')] | //button[contains(.,'Approve Participant')]" +
			" | //*[@role='tab' and contains(.,'Approve Participant')])[1]")
	}
	// a cell whose ENTIRE visible text is exactly the participant name (never a partial match)
	static TestObject participantNameCell(String name) {
		return xp("//*[normalize-space(.)='${name}']")
	}
	// every "Approve" button on the list — only used for the "exactly one row changed" bookkeeping
	static TestObject anyApproveButton() { return xp("//button[normalize-space()='Approve']") }
	// confirmation dialog buttons ONLY (scoped to role=dialog) so another row's Approve button
	// can never be mistaken for a confirm control
	static TestObject confirmDialogButton() {
		return xp("(//*[@role='dialog']//button[normalize-space()='Approve' or normalize-space()='Confirm'" +
			" or normalize-space()='Yes' or normalize-space()='OK' or normalize-space()='Approve Participant'])[1]")
	}

	// row-shaped containers the name cell may live in (table row, list item, card, ARIA row)
	private static final String APPROVE_ROW_ANCESTOR =
		"self::tr or self::li or self::article or self::div[@role='row']" +
		" or contains(@class,'row') or contains(@class,'card') or contains(@class,'item')"

	/**
	 * The Approve button OF THE NAMED PARTICIPANT'S ROW — nothing else.
	 * Strategy: start at the name text and walk UP to the nearest row/card/list-item ancestor
	 * that also carries an "Approve" button, then return THAT button. With several pending rows
	 * on screen the first branch (exact whole-text match) is used; the second branch is the
	 * fallback for names wrapped in a link/badge (innermost element containing the name text).
	 */
	static TestObject approveButtonInRow(String name) {
		String strict = "(//*[normalize-space(.)='${name}']/ancestor-or-self::*[${APPROVE_ROW_ANCESTOR}]" +
			"[.//button[normalize-space()='Approve']][1]//button[normalize-space()='Approve'])"
		String fallback = "(//*[contains(.,'${name}') and not(.//*[contains(.,'${name}')])]" +
			"/ancestor-or-self::*[${APPROVE_ROW_ANCESTOR}]" +
			"[.//button[normalize-space()='Approve']][1]//button[normalize-space()='Approve'])"
		return xp("(${strict} | ${fallback})[1]")
	}
}
