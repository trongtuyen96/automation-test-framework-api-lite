package Utils;

import java.util.LinkedHashMap;

public class UtilityFunctions {
    public static Object replaceVariableWithVariableMap(String source, String beginPattern, String endPattern, LinkedHashMap<String, Object> row) {
        if (source != null) {
            String[] parts = source.split(beginPattern);
            Object resultValue = parts[0];
            String key = "";
            if (parts.length == 2 && source.indexOf(beginPattern) == 0 && parts[1].indexOf(endPattern) == parts[1].length() - 1) {
                key = parts[1].substring(0, parts[1].indexOf(endPattern));
                resultValue = row.getOrDefault(key, source);
            } else {
                for (int i = 1; i < parts.length; i++) {
                    int endIndex = parts[i].indexOf(endPattern);
                    if (endIndex > -1) {
                        key = parts[i].substring(0, endIndex);
                    }
                    resultValue += beginPattern + parts[i];
                    if (row.containsKey(key)) {
                        resultValue = ((String) resultValue).replace(beginPattern + key + endPattern, String.valueOf(row.get(key)));
                    }
                }
            }
            return resultValue;
        }
        return null;
    }
}
