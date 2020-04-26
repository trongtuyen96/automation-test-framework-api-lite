package TestEntity;

import java.util.LinkedHashMap;

public class TestStep {
    private String name;
    private String description;
    private String method;
    private String url;

    private LinkedHashMap<String, Object> request;
    private LinkedHashMap<String, Object> response;

    public enum TestParameterType {
        Path,
        Header,
        Query,
        Body,
    }

    public TestStep() {
        setName("");
        setDescription("");
        setMethod("");
        setUrl("");
        setRequest(new LinkedHashMap<>());
        setResponse(new LinkedHashMap<>());
    }

    public TestStep(String name, String description, String method, String url) {
        setName(name);
        setDescription(description);
        setMethod(method);
        setUrl(url);
        setRequest(new LinkedHashMap<>());
        setResponse(new LinkedHashMap<>());
    }

    public TestStep(TestStep apiAction) {
        this(apiAction.getName(), apiAction.getDescription(), apiAction.getMethod(), apiAction.getUrl());
        for (String key : apiAction.getRequest().keySet()) {
            request.put(key, apiAction.getRequest().get(key));
        }
        for (String key : apiAction.getResponse().keySet()) {
            response.put(key, apiAction.getResponse().get(key));
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

    public String getUrl() {
        LinkedHashMap<String, Object> pathParams = (LinkedHashMap<String, Object>) getRequest().get(TestParameterType.Path.toString());
        for (String key : pathParams.keySet()) {
            url = url.replace("{" + key + "}", (String) pathParams.get(key));
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinkedHashMap<String, Object> getRequest() {
        return request;
    }

    public void setRequest(LinkedHashMap<String, Object> request) {
        this.request = request;
    }

    public LinkedHashMap<String, Object> getResponse() {
        return response;
    }

    public void setResponse(LinkedHashMap<String, Object> response) {
        this.response = (LinkedHashMap<String, Object>) response.clone();
    }

    public void putAPITestParam(TestParameterType type, LinkedHashMap<String, Object> params) {
        getRequest().put(type.toString(), params);
    }

    public void clear() {
        setName("");
        setDescription("");
        setMethod("");
        setUrl("");
        request.clear();
        response.clear();
    }
}
