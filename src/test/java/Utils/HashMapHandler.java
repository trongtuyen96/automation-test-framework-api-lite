package Utils;

import org.json.simple.JSONObject;

import java.util.LinkedHashMap;

public class HashMapHandler {
    public static LinkedHashMap<Integer, Object> createHashMapValueWithKeyIndex(Object[] obj, int startIndex) {
        LinkedHashMap<Integer, Object> indexHashMap = new LinkedHashMap<>();
        for (int i = startIndex; i <= obj.length; i++) {
            indexHashMap.put(i, obj[i - startIndex]);
        }
        return indexHashMap;
    }

    public static LinkedHashMap<String, Object> convertJSONObjectToHashMap(JSONObject obj) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (obj != null) {
            for (Object key : obj.keySet()) {
                map.put((String) key, obj.get(key));
            }
        }
        return map;
    }

    public static LinkedHashMap<String, Object> replaceVariableWithDataMap(LinkedHashMap<String, Object> source, String beginPattern, String endPattern, LinkedHashMap<String, Object> map) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>(source);
        for (String key : source.keySet()) {
            if (source.get(key) instanceof String) {
                result.put(key, UtilityFunctions.replaceVariableWithVariableMap((String) source.get(key), beginPattern, endPattern, map));
            }
            if (source.get(key) instanceof LinkedHashMap) {
                result.put(key, replaceVariableWithDataMap((LinkedHashMap<String, Object>) source.get(key), beginPattern, endPattern, map));
            }
            if (source.get(key) instanceof JSONObject) {
                result.put(key, JSONHandler.replaceVariableWithDataMap((JSONObject) source.get(key), beginPattern, endPattern, map));
            }
        }
        return result;
    }
}
