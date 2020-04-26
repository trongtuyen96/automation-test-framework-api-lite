package TestExecution;

import TestEntity.TestStep;
import TestEntity.TestCase;
import TestEntity.TestSuite;
import Utils.DateTimeHandler;
import Utils.HashMapHandler;
import Utils.JSONHandler;
import Utils.UtilityFunctions;
import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.Status;
import config.Config;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BaseTestExecution {
    protected static String TEST_DATA_FOLDER = Config.getProperty("testDataFolder");
    protected LinkedHashMap<String, TestSuite> testSuiteMap = new LinkedHashMap<>();
    private APIExecution apiExecution;

    @BeforeSuite
    public void beforeSuite() {
        TestReport.getInstance().initExtentReport(System.getProperty("user.dir") + "\\src\\test\\java\\config\\extentReportConfig.xml", System.getProperty("user.dir") + "\\" + Config.getProperty("extReportPath")
                        .replaceFirst("date", DateTimeHandler.currentDateAsString("yyyy.MM.dd.HH.mm.ss")),
                new HashMap<String, String>() {
                    {
                        put("User Name", System.getProperty("user.name"));
                        put("OS Version", System.getProperty("os.name"));
                        put("Java Version", System.getProperty("java.version"));
                        put("Computer Name", System.getenv("COMPUTERNAME"));
                        put("Project", "ATWT REST API Test Framework - Lite Version");
                    }
                });
        TestReport.getInstance().setAnalysis(AnalysisStrategy.SUITE);
    }

    @AfterSuite
    public void afterSuite() {
        TestReport.getInstance().flush();
    }

    @BeforeClass
    @Parameters({"testDataPath"})
    public void beforeClass(String testDataPath) {
        clearTestSuiteMap();

        // Load multiple test data path here
        // Only on PRO version
        testSuiteMap.put(testDataPath.trim(), loadTestSuiteFromJSON(testDataPath));
    }

    @AfterClass
    public void afterClass() {
    }

    @DataProvider
    public Object[] getTestData() {
        ArrayList<Object[]> arrayTestData = new ArrayList<>();
        int lastIndex = 0;
        for (String path : testSuiteMap.keySet()) {
            ArrayList<TestCase> allTestCases = testSuiteMap.get(path).getTestCases();
            for (int i = lastIndex; i < lastIndex + allTestCases.size(); i++) {
                arrayTestData.add(new Object[]{allTestCases.get(i - lastIndex), path});
            }
            lastIndex = lastIndex + testSuiteMap.get(path).getTestCases().size();
        }
        return arrayTestData.toArray();
    }

    @BeforeMethod
    public void beforeMethod(Object[] params, ITestContext testContext) {
        try {
            Object[] testCaseInfo = (Object[]) params[0];
            TestCase dataRow = (TestCase) testCaseInfo[0];
            String dataPath = (String) testCaseInfo[1];

            TestSuite testSuite = testSuiteMap.get(dataPath);
            String testSuiteName = testSuite.getName();
            String testSuiteDescription = testSuite.getDescription() + "<br>- Total Test cases: " + testSuite.getTestCases().size();

            if (TestReport.getInstance().getLastTestSuiteName() == null || !testSuiteName.equals(TestReport.getInstance().getLastTestSuiteName())) {
                TestReport.getInstance().addTestSuite(testSuiteName, testSuiteDescription);
            }

            String testCaseDescription = dataRow.getDescription();
            if (!dataRow.getObjective().equals("")) {
                dataRow.setObjective((String) UtilityFunctions.replaceVariableWithVariableMap(dataRow.getObjective(), "@var->", "@", TestVariableMap.getInstance().getProcessVariables()));
                testCaseDescription += "<br><b><font color=\"blue\">Objective</font></b>: " + dataRow.getObjective();
            }

            String testCaseName = "";
            if (!dataRow.getId().equals("")) {
                testCaseName = "Test ID - " + dataRow.getId() + ": ";
            }

            testCaseName += dataRow.getName();
            TestReport.getInstance().addTestCase(testCaseName, testCaseDescription, "<h6><b>" + testContext.getSuite().getName().toUpperCase() + "</b></h6>"
                    , "<b>&emsp;" + testContext.getName().toUpperCase() + "</b>"
                    , "<b>&emsp;&emsp;" + testSuiteName + "</b>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Load the common variables
        TestVariableMap.getInstance().loadCommonVariables();
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        logTestResultNotPass(result);
    }

    private void logTestResultNotPass(ITestResult result) {
        String log = apiExecution.getOutputWriter().toString();
        Reporter.log(log, true);

        switch (result.getStatus()) {
            case ITestResult.SKIP:
                Reporter.log(result.getThrowable().toString());
                TestReport.getInstance().testSkip(result.getThrowable().getMessage());
                break;
            case ITestResult.FAILURE:
                Reporter.log(result.getThrowable().toString());
                TestReport.getInstance().testFail(result.getThrowable().getMessage());
                break;
        }
    }

    @Test(dataProvider = "getTestData")
    public void runTestCase(Object params) {
        Object[] testCaseInfo = (Object[]) params;
        boolean stepResult = false;
        TestCase currentTC = (TestCase) testCaseInfo[0];
        if (currentTC.getActive()) {
            int count = 0;
            for (TestStep testStep : currentTC.getTestSteps()) {
                String log = "<b><font color=\"blue\"><h6><u>Step " + ++count + "</u></h6></font> " + (testStep.getName() != null ? testStep.getName() : "") + "</b>"
                        + "<br><b><font color=\"blue\">Method</font>: " + testStep.getMethod() + "</b>";
                TestReport.getInstance().testLog(Status.INFO, log);

                apiExecution = new APIExecution(testStep);
                stepResult = apiExecution.doExecution();

                log = "<br>" + apiExecution.getOutputWriter();
                if (stepResult) {
                    TestReport.getInstance().testPass(log);
                } else {
                    Assert.fail(log);
                    break;
                }
            }
        } else {
            TestReport.getInstance().testSkip(currentTC.getObjective());
        }
    }

    private void clearTestSuiteMap() {
        TestVariableMap.getInstance().getProcessVariables().clear();
        for (String keyTS : testSuiteMap.keySet()) {
            testSuiteMap.get(keyTS).clear();
        }
        testSuiteMap.clear();
    }

    protected TestSuite loadTestSuiteFromJSON(String testDataPath) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("jsonPath", testDataPath);
        JSONObject jsonObject = JSONHandler.loadJSONFile(TEST_DATA_FOLDER + map.get("jsonPath"));
        TestSuite testSuite = new TestSuite((String) jsonObject.get("suiteName"), (String) jsonObject.get("suiteDescription"));

        JSONArray testCases = (JSONArray) JSONHandler.getValueOfJSONObject(jsonObject, "testCases");
        if (testCases != null) {
            for (int i = 1; i < testCases.size(); i++) {
                JSONObject jsonTC = (JSONObject) testCases.get(i - 1);
                testSuite.getTestCases().add(loadTestCaseFromJSON(jsonTC));
            }
        }
        return testSuite;
    }

    private TestCase loadTestCaseFromJSON(JSONObject jsonTestCase) {
        TestCase testCase = new TestCase((String) jsonTestCase.get("testId"),
                (String) jsonTestCase.get("testName"),
                (String) jsonTestCase.get("testDescription"),
                (String) jsonTestCase.get("testObjective"),
                (Boolean) jsonTestCase.get("isActive"));

        JSONArray testSteps = (JSONArray) jsonTestCase.get("testSteps");
        for (Object testStep : testSteps) {
            JSONObject jsonStep = (JSONObject) testStep;
            JSONObject params = (JSONObject) jsonStep.get("parameters");

            TestStep apiStep = new TestStep((String) jsonStep.get("name"),
                    (String) jsonStep.get("description"),
                    (String) jsonStep.get("method"),
                    (String) params.get("url"));

            JSONObject request = JSONHandler.getJSONObject(params, "request");
            apiStep.putAPITestParam(TestStep.TestParameterType.Path, HashMapHandler.convertJSONObjectToHashMap(JSONHandler.getJSONObject(request, "path")));
            apiStep.putAPITestParam(TestStep.TestParameterType.Query, HashMapHandler.convertJSONObjectToHashMap(JSONHandler.getJSONObject(request, "query")));
            apiStep.putAPITestParam(TestStep.TestParameterType.Body, HashMapHandler.convertJSONObjectToHashMap(JSONHandler.getJSONObject(request, "body")));
            apiStep.putAPITestParam(TestStep.TestParameterType.Header, HashMapHandler.convertJSONObjectToHashMap(JSONHandler.getJSONObject(request, "header")));
            apiStep.getResponse().putAll(HashMapHandler.convertJSONObjectToHashMap(JSONHandler.getJSONObject(params, "response")));
            testCase.getTestSteps().add(apiStep);
        }
        return testCase;
    }
}

