import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import static com.kms.katalon.core.testdata.TestDataFactory.findTestData

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint

import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW

import com.kms.katalon.core.llm.keyword.LlmKeywords as LLM

import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import com.kms.katalon.core.model.FailureHandling as FailureHandling

import com.kms.katalon.core.testcase.TestCase as TestCase

import com.kms.katalon.core.testdata.TestData as TestData

import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW

import com.kms.katalon.core.testobject.TestObject as TestObject

import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import internal.GlobalVariable as GlobalVariable

import org.openqa.selenium.Keys as Keys



WebUI.openBrowser(null)



WebUI.navigateToUrl('https://app.msxpo.sa/login')



WebUI.maximizeWindow()



WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Enter your email'), email_address)



WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Enter your password'), password)



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Sign In'))



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Events'))



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Create Event'))



WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_e.g. Tech Expo 2026'), event_name)



// Event type: the native <select> ('Manage event/event_type') is a HIDDEN shim - selectOptionByLabel
// on it does nothing (the app's React state never updates). The visible control is a Radix combobox
// whose DOM id (radix-_r_...) changes on every page load, so the recorded 'span_radix-_r_4q' object
// can never match. Fix: click the labelled trigger, then the [role=option] whose text equals the
// test-data value of 'event_type'.
TestObject eventTypeTrigger = findTestObject('Manage event/event_type_trigger')
if (!WebUI.verifyElementPresent(eventTypeTrigger, 5, FailureHandling.OPTIONAL)) {
	// pristine page state fallback: recorded trigger still showing the "Select Event Type" placeholder
	eventTypeTrigger = findTestObject('Page_MSxpo - Admin Panel/button_Select Event Type')
}
WebUI.click(eventTypeTrigger)

TestObject eventTypeOption = findTestObject('Manage event/event_type_option', [('event_type') : event_type])
WebUI.waitForElementVisible(eventTypeOption, 5)
WebUI.click(eventTypeOption)
WebUI.delay(0.5) // dropdown closes, trigger now shows the chosen type


// The date picker popover opens asynchronously - wait for the day cell to be present before
// clicking it, otherwise the click races the popover opening (WebElementNotFoundException flake
// in run 20261029_151941, attempts 1 and 2).
WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Select start date', [(end_date) : '']))

TestObject startDayCell = findTestObject('Page_MSxpo - Admin Panel/button_Saturday, October 31st, 2026')
WebUI.waitForElementPresent(startDayCell, 10)
WebUI.click(startDayCell)



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Select end date'))

TestObject endDayCell = findTestObject('Page_MSxpo - Admin Panel/button_Saturday, October 31st, 2026')
WebUI.waitForElementPresent(endDayCell, 10)
WebUI.click(endDayCell)



WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/textarea_Brief event overview'), description)



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))



// Step 2 (Event Admin):
// The admin row renders asynchronously after 'Add Admin'. Email starts disabled=true and the
// phone cell is wrapped in 'pointer-events-none opacity-40' - BOTH unlock only when the name
// suggestion popover ("Add 'X' as new admin", blue h-4 plus) is CLICKED. That popover appears
// ~0.5-1.5s after typing the name, so wait for it with waitForElementPresent (verifyElementPresent
// does NOT poll in this Katalon version - it returned in 0.16s and skipped the click in run
// 20261029_143813, leaving phone non-interactable in run 20261029_144744).
// Phone must be set BEFORE email: filling email auto-commits the row to a read-only 'Pending'
// state that REMOVES the phone input (run 20261029_143813 failure: PHONE shown as '-' no input).
WebUI.click(findTestObject('Page_MSxpo - Admin Panel/svg_tabler-icon tabler-icon-plus h-3 w-3'))

TestObject adminFullName = findTestObject('Page_MSxpo - Admin Panel/input_Full name')
WebUI.waitForElementPresent(adminFullName, 15)
WebUI.setText(adminFullName, event_admin_name)

TestObject addNewAdmin = findTestObject('Page_MSxpo - Admin Panel/svg_tabler-icon tabler-icon-plus h-4 w-4 text-')
WebUI.waitForElementPresent(addNewAdmin, 10)
WebUI.click(addNewAdmin)
WebUI.delay(0.5) // let the unlock re-render settle

TestObject adminPhone = findTestObject('Page_MSxpo - Admin Panel/input_500 000 000')
WebUI.waitForElementPresent(adminPhone, 10)
WebUI.setText(adminPhone, phone_number)

TestObject adminEmail = findTestObject('Page_MSxpo - Admin Panel/input_Email address')
WebUI.waitForElementPresent(adminEmail, 10)
WebUI.setText(adminEmail, admin_email_address) // last editable field -> row commits to Pending



WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue_1'))



// Step 3 (Visitor Questionnaire) and Step 4 (Floor Plan) are NOT mandatory - skip straight
// through. Step 3 has nothing required: advance with its 'Continue' button. Step 4 has no
// Continue at all - its advance button is 'Skip Floor Planner'.
TestObject questionnaireContinue = findTestObject('Page_MSxpo - Admin Panel/button_Continue_2')
WebUI.waitForElementPresent(questionnaireContinue, 15)
WebUI.click(questionnaireContinue)

TestObject skipFloorPlanner = findTestObject('Page_MSxpo - Admin Panel/button_Skip Floor Planner')
WebUI.waitForElementPresent(skipFloorPlanner, 15)
WebUI.click(skipFloorPlanner)



// Step 5 (Review & Create): 'Save as Draft' completes the create-event flow.
TestObject saveAsDraft = findTestObject('Page_MSxpo - Admin Panel/button_Save as Draft')
WebUI.waitForElementPresent(saveAsDraft, 15)
WebUI.click(saveAsDraft)

// After the save the app lands on the event page whose <h1> shows the event name. The draft is
// already created once 'Save as Draft' returns, so this last confirmation click is optional.
TestObject savedEventTitle = findTestObject('Page_MSxpo - Admin Panel/h1_Test 2.0')
WebUI.waitForElementPresent(savedEventTitle, 10, FailureHandling.OPTIONAL)
WebUI.click(savedEventTitle, FailureHandling.OPTIONAL)
