package TestExecution;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TestReport {
    private static TestReport instance;
    private String configFilePath = "", outputReportPath = "";
    private HashMap<String, String> envConfigMap;
    private ExtentReports extentReports;

    private ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private ArrayList<ThreadLocal<ExtentTest>> testSuites = new ArrayList<>();

    public static TestReport getInstance() {
        if (instance == null) {
            instance = new TestReport();
        }
        return instance;
    }

    public void initExtentReport(String configFilePath, String outputReportPath, HashMap<String, String> envConfigMap) {
        if (extentReports == null) {
            extentReports = setUpExtentReport(configFilePath, outputReportPath, envConfigMap);
        } else if (!this.configFilePath.equals(configFilePath) || this.envConfigMap.equals(envConfigMap)) {
            extentReports = setUpExtentReport(this.configFilePath, this.outputReportPath, this.envConfigMap);
        }
    }

    private ExtentReports setUpExtentReport(String configFilePath, String outputReportPath, HashMap<String, String> envConfigMap) {
        ExtentReports report = new ExtentReports();
        this.configFilePath = configFilePath;
        this.outputReportPath = outputReportPath;
        this.envConfigMap = envConfigMap;

        // Create report directory
        File reportFolder = new File(FilenameUtils.getFullPathNoEndSeparator(outputReportPath));
        if (!reportFolder.exists()) {
            reportFolder.mkdir();
        }

        // Initialise HTML report
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(outputReportPath);
        if (!configFilePath.equals("")) {
            report.setReportUsesManualConfiguration(true);
            htmlReporter.loadXMLConfig(configFilePath);
        }
        report.attachReporter(htmlReporter);
        for (String key : envConfigMap.keySet()) {
            report.setSystemInfo(key, envConfigMap.get(key));
        }
        return report;
    }

    public ExtentReports getExtentReports() {
        return extentReports;
    }

    public void flush() {
        extentReports.flush();
    }

    public void setAnalysis(AnalysisStrategy level) {
         /*
        SUITE: Generate stats for 3 levels: Suite, Class, Test
        CLASS: Generate stats for 2 levels: Class, Test
        TEST: Generate stats for 1 level: Test, will be 2 levels if containing Step
        BDD: BDD-style Gherkin
         */
        extentReports.setAnalysisStrategy(level);
    }

    public void addTestCase(String name, String description, String... categories) {
        // Create node
        ExtentTest curTest = testSuites.get(testSuites.size() - 1).get().createNode(name, description);
        test.set(curTest);
        test.get().assignCategory(categories);
    }

    public void addTestSuite(String name, String description, String... categories) {
        // Create test
        ThreadLocal<ExtentTest> curSuite = new ThreadLocal<>();
        curSuite.set(extentReports.createTest(name, description));
        curSuite.get().assignCategory(categories);
        testSuites.add(curSuite);
    }

    public String getLastTestSuiteName() {
        return testSuites.size() > 0 ? testSuites.get(testSuites.size() - 1).get().getModel().getName() : null;
    }

     /*
    Used when test case is executed
    - PASS
    - FAIL
    - SKIP
    - INFO
    */

    public void testLog(Status status, String message) {
        test.get().log(status, message);
    }

    public void testPass(String message) {
        test.get().pass(message);
    }

    public void testFail(String message) {
        test.get().fail(message);
    }

    public void testSkip(String message) {
        test.get().skip(message);
    }

    public void testInfo(String message) {
        test.get().info(message);
    }
}
