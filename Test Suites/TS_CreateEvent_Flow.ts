<?xml version="1.0" encoding="UTF-8"?>
<TestSuite id="1" name="TS_CreateEvent_Flow" projectId="default">

   <description>MSxpo create-event flow, app.msxpo.sa (Event Host super admin). TC_01..TC_06 must run in order in the same browser session — each test case continues the wizard left open by the previous one. Error handling: stop on first failure (Katalon default). Each run creates one draft event "test 1.0"; delete it from the Events list afterwards.</description>

   <variable/>

   <testCase id="2" enabled="true">TC_01_Login</testCase>
   <testCase id="3" enabled="true">TC_02_EventBasics</testCase>
   <testCase id="4" enabled="true">TC_03_EventAdmin</testCase>
   <testCase id="5" enabled="true">TC_04_Questionnaire</testCase>
   <testCase id="6" enabled="true">TC_05_FloorPlan</testCase>
   <testCase id="7" enabled="true">TC_06_Review_SaveDraft</testCase>

</TestSuite>
