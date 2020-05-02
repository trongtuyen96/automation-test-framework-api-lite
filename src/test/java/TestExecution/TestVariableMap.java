package TestExecution;

import Utils.DateTimeHandler;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.LinkedHashMap;

public class TestVariableMap {
    private static TestVariableMap instance;

    // This stores default variables such as system date, time, random value
    private LinkedHashMap<String, Object> commonVariables;

    // This store variables generated in run time such as uniqueId of previous action, then use it for next action
    // Only available in PRO version
    private LinkedHashMap<String, Object> processVariables;

    public TestVariableMap() {
        setCommonVariables(new LinkedHashMap<>());
        setProcessVariables(new LinkedHashMap<>());
    }

    public static TestVariableMap getInstance() {
        if (instance == null) {
            instance = new TestVariableMap();
        }
        return instance;
    }

    public LinkedHashMap<String, Object> getCommonVariables() {
        return commonVariables;
    }

    public void setCommonVariables(LinkedHashMap<String, Object> commonVariables) {
        this.commonVariables = commonVariables;
    }

    public LinkedHashMap<String, Object> getProcessVariables() {
        return processVariables;
    }

    public void setProcessVariables(LinkedHashMap<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public void loadCommonVariables() {
        // Today with date time
        commonVariables.put("TODAY", DateTimeHandler.currentDayPlus("yyyy-MM-dd_HH-mm-ss", 0));

        // Now with hour minute format
        commonVariables.put("NOW_HH:mm", DateTimeHandler.currentDayPlus("HH:mm", 0));
        commonVariables.put("NOW_HHmm", DateTimeHandler.currentDayPlus("HHmm", 0));
        commonVariables.put("NOW+1_HH:mm", DateTimeHandler.currentDayMinutePlus("HH:mm", 1));
        commonVariables.put("NOW+1_HHmm", DateTimeHandler.currentDayMinutePlus("HHmm", 1));

        // Date with day month year format
        commonVariables.put("TODAY_yyyy-MM-dd", DateTimeHandler.currentDayPlus("yyyy-MM-dd", 0));
        commonVariables.put("YESTERDAY_yyyy-MM-dd", DateTimeHandler.currentDayPlus("yyyy-MM-dd", -1));
        commonVariables.put("TOMORROW_yyyy-MM-dd", DateTimeHandler.currentDayPlus("yyyy-MM-dd", +1));
        commonVariables.put("NEXTMONTH_yyyy-MM-dd", DateTimeHandler.currentDayPlus("yyyy-MM-dd", +30));

        // Random with alphabet and number
        commonVariables.put("RANDOM_alphanumeric_5", RandomStringUtils.randomAlphanumeric(5));

        // Random with number
        commonVariables.put("RANDOM_numeric_5", Integer.parseInt(RandomStringUtils.randomNumeric(5)));

        // Random with alphabet
        commonVariables.put("Return ", RandomStringUtils.randomAlphabetic(5));
    }
}
