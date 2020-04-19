package TestEntity;

import java.util.LinkedHashMap;

public class TestAPIAction {
    private String name;
    private String description;
    private String method;
    private String urlTemplate;
    private String url;

    private LinkedHashMap<String, Object> testParameters;
    private LinkedHashMap<String, Object> responseItems;

    public enum TestParameterType {
        Path,
        Query,
        Body
    }

    public TestAPIAction() {
        setName("");
        setDescription("");
        setMethod("");
        setUrlTemplate("");
        setTestParameters(new LinkedHashMap<>());
        setResponseItems(new LinkedHashMap<>());
    }

    public TestAPIAction(String name, String description, String method, String urlTemplate) {
        setName(name);
        setDescription(description);
        setMethod(method);
        setUrlTemplate(urlTemplate);
        setTestParameters(new LinkedHashMap<>());
        setResponseItems(new LinkedHashMap<>());
    }

    public TestAPIAction(TestAPIAction apiAction) {
        this(apiAction.getName(), apiAction.getDescription(), apiAction.getMethod(), apiAction.getUrlTemplate());
        for (String key : apiAction.getTestParameters().keySet()) {
            testParameters.put(key, apiAction.getTestParameters().get(key));
        }
        for (String key : apiAction.getResponseItems().keySet()) {
            responseItems.put(key, apiAction.getResponseItems().get(key));
        }
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
        this.url = urlTemplate;
    }

    public String getUrl() {
        LinkedHashMap<String, Object> pathParams = (LinkedHashMap<String, Object>) getTestParameters().get(TestParameterType.Path.toString());
        for (String key : pathParams.keySet()) {
            url = urlTemplate.replace("{" + key + "}", (String) pathParams.get(key));
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinkedHashMap<String, Object> getTestParameters() {
        return testParameters;
    }

    public void setTestParameters(LinkedHashMap<String, Object> testParameters) {
        this.testParameters = testParameters;
    }

    public LinkedHashMap<String, Object> getResponseItems() {
        return responseItems;
    }

    public void setResponseItems(LinkedHashMap<String, Object> responseItems) {
        this.responseItems = (LinkedHashMap<String, Object>) responseItems.clone();
    }

    public void putAPITestParam(TestParameterType type, LinkedHashMap<String, Object> params) {
        getTestParameters().put(type.toString(), params);
    }

    public void clear() {
        setName("");
        setDescription("");
        setMethod("");
        setUrlTemplate("");
        testParameters.clear();
        responseItems.clear();
    }
}
