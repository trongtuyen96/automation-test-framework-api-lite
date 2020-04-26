package TestEntity;

import java.util.ArrayList;

public class TestCase implements Cloneable {
    // Implementing Cloneable allows to legibly clone an object
    private String id;
    private String name;
    private String description;
    private String objective;
    private Boolean isActive;
    private ArrayList<TestStep> testSteps;

    public TestCase() {
        setId("");
        setName("");
        setDescription("");
        setObjective("");
        setActive(true);
        setTestSteps(new ArrayList<>());
    }

    public TestCase(String id, String name, String description, String objective, Boolean isActive) {
        setId(id);
        setName(name);
        setDescription(description);
        setObjective(objective);
        setActive(isActive);
        setTestSteps(new ArrayList<>());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        if (isActive == null) {
            isActive = true;
        } else {
            isActive = active;
        }
    }

    public ArrayList<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(ArrayList<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public void clear() {
        setId("");
        setName("");
        setDescription("");
        setObjective("");
        setActive(null);
        for (TestStep action : testSteps) {
            action.clear();
        }
        testSteps.clear();
    }
}
