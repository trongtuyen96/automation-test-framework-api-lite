package Utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class JSONHandler {
    public static JSONObject loadJSONFile(String filePath) {
        JSONObject object = null;
        try {
            BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            JSONParser jsonParser = new JSONParser();
            object = (JSONObject) jsonParser.parse(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONObject getJSONObject(JSONObject parent, String objName) {
        JSONObject jsonObject = null;
        if (parent != null) {
            Object object = parent.get(objName);
            jsonObject = object == null ? null : (JSONObject) object;
        }
        return jsonObject;
    }

    public static Object getValueOfJSONObject(JSONObject parent, String objName) {
        Object value = null;
        if (parent != null) {
            value = parent.get(objName);
        }
        return value;
    }

    public static JSONObject replaceVariableWithDataMap(JSONObject source, String beginPattern, String endPattern, LinkedHashMap<String, Object> row) {
        JSONObject resultValue = new JSONObject(source);
        for (Object key : source.keySet()) {
            if (source.get(key) instanceof String) {
                resultValue.put(key, UtilityFunctions.replaceVariableWithVariableMap(((String) source.get(key)), beginPattern, endPattern, row));
            }
            if (source.get(key) instanceof JSONObject) {
                resultValue.put(key, replaceVariableWithDataMap((JSONObject) source.get(key), beginPattern, endPattern, row));
            }
        }
        return resultValue;
    }
}
