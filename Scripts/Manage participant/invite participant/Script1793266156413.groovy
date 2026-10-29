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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable

import static org.assertj.core.api.InstanceOfAssertFactories.BYTE

import org.openqa.selenium.By
import org.openqa.selenium.By.ByXPath
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser(null)

WebUI.navigateToUrl('https://app.msxpo.sa/login')

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Enter your email'), email_address)

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Enter your password'), password)

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Sign In'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Participants', [(industry) : '']))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_Invite ParticipantsInvite Participant'))

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Participant Name'), participant_name)

//WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Select industry', [(industry) : '']))

/*
 // Open the Industry dropdown
WebUI.click(findTestObject('Page_YourPage/IndustryDropdown'))

// Wait for the option to appear
WebUI.waitForElementPresent(
    findTestObject('Page_YourPage/IndustryDropdown'),
    10
)

// Select Finance & Banking
def driver = DriverFactory.getWebDriver()

driver.findElement(
    By.xpath("//*[normalize-space(text())='Finance & Banking']")
).click()
 * */

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Select industry'))
WebUI.waitForElementPresent(
	findTestObject('Page_MSxpo - Admin Panel/button_Select industry'),
	10
)

def driver = DriverFactory.getWebDriver()
driver.findElement(By.xpath("//*[normalize-space(text())='Finance & Banking']")
).click()



// WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_radix-_r_23'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_BackContinue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_BackContinue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Person Name'), contact_name)

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Contact Email'), contact_email)

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Participant Admin'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/span_radix-_r_3d'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_Contact Person _Contact Email _Role _Contac'))

WebUI.setText(findTestObject('Page_MSxpo - Admin Panel/input_Contact Phone'), contact_phone)

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_BackContinue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_BackContinue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/div_BackContinue'))

WebUI.click(findTestObject('Page_MSxpo - Admin Panel/button_Continue'))

