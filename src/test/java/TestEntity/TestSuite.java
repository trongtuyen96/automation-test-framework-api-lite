package TestEntity;

import java.util.ArrayList;

public class TestSuite {
    private String name;
    private String description;
    private ArrayList<TestCase> testCases;

    public TestSuite() {
        setName("");
        setDescription("");
        setTestCases(new ArrayList<>());
    }

    public TestSuite(String name, String description) {
        setName(name);
        setDescription(description);
        setTestCases(new ArrayList<>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(ArrayList<TestCase> testCases) {
        this.testCases = testCases;
    }

    public void clear() {
        setName("");
        setDescription("");
        for (TestCase testCase : testCases) {
            testCase.clear();
        }
        testCases.clear();
    }
}
