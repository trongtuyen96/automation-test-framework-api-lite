package TestExecution;

import TestEntity.TestStep;
import Utils.HashMapHandler;
import Utils.UtilityFunctions;
import config.Config;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.output.WriterOutputStream;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class APIExecution {
    private StringWriter actionInfoWriter;

    private StringWriter requestWriter;
    private static PrintStream requestCapture;

    private StringWriter responseWriter;
    private static PrintStream responseCapture;

    private ValidatableResponse validatableRes;
    private TestStep testAPIAction;
    private List<AssertionError> errors = new ArrayList<AssertionError>();

    public APIExecution(TestStep tAction) {
        this.testAPIAction = tAction;
        setUpAPITest();
    }

    public boolean doExecution() {
        loadVariables();
        return runAPIAction(testAPIAction);
    }

    public void loadVariables() {
        testAPIAction.setName((String) UtilityFunctions.replaceVariableWithVariableMap(testAPIAction.getName(), "@var->", "@", TestVariableMap.getInstance().getCommonVariables()));
        testAPIAction.setDescription((String) UtilityFunctions.replaceVariableWithVariableMap(testAPIAction.getDescription(), "@var->", "@", TestVariableMap.getInstance().getCommonVariables()));
        testAPIAction.setUrl((String) UtilityFunctions.replaceVariableWithVariableMap(testAPIAction.getUrl(), "@var->", "@", TestVariableMap.getInstance().getCommonVariables()));

        testAPIAction.setResponse(HashMapHandler.replaceVariableWithDataMap(testAPIAction.getResponse(), "@var->", "@", TestVariableMap.getInstance().getCommonVariables()));

        testAPIAction.setRequest(HashMapHandler.replaceVariableWithDataMap(testAPIAction.getRequest(), "@var->", "@", TestVariableMap.getInstance().getCommonVariables()));
    }

    public void setUpAPITest() {
        initLogWriter();

        // Load from config file
        RestAssured.baseURI = Config.getProperty("baseURI");
        RestAssured.port = Integer.parseInt(Config.getProperty("port"));
    }

    private void initLogWriter() {
        actionInfoWriter = new StringWriter();

        requestWriter = new StringWriter();
        requestCapture = new PrintStream(new WriterOutputStream(requestWriter, "UTF-8"), true);

        responseWriter = new StringWriter();
        responseCapture = new PrintStream(new WriterOutputStream(responseWriter, "UTF-8"), true);
    }

    public StringWriter getOutputWriter() {
        StringWriter outputWriter = new StringWriter();
        outputWriter.write("<pre>" + actionInfoWriter.toString() + "</pre>" + "<br>" +
                "Request: <br><pre>" + requestWriter.toString() + "</pre>" + "<br>" +
                "Response: <be><pre>" + responseWriter.toString() + "</pre>" + "<br>" +
                formatErrors());
        return outputWriter;
    }

    private String formatErrors() {
        StringBuilder formatErrorsString = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            formatErrorsString.append(String.format("<font color=\"red\"><b>Error %s:</b></font> <br><pre>%s</pre><br>", errors.size() != 1 ? i + 1 : "", errors.get(i)));
        }
        return formatErrorsString.toString();
    }

    private boolean runAPIAction(TestStep ta) {
        RestAssured.basePath = ta.getUrl();
        LinkedHashMap<String, Object> queryParams = null, bodyParams = null, headers = null;
        Map<String, Object> testParams = ta.getRequest();

        // Get parameters of test action
        headers = (LinkedHashMap<String, Object>) testParams.get(TestStep.TestParameterType.Header.toString());
        queryParams = (LinkedHashMap<String, Object>) testParams.get(TestStep.TestParameterType.Query.toString());
        bodyParams = (LinkedHashMap<String, Object>) testParams.get(TestStep.TestParameterType.Body.toString());
        Map<String, Object> expResponseItems = ta.getResponse();

        // Write text for action info writer
        String headerText = "<br>" + "<br>Headers: </br>";
        for (Map.Entry<String, Object> entry : headers.entrySet()) {
            headerText += "<br>" + "- " + entry.getKey() + ":" + entry.getValue();
        }

        String stepText = "<br>" + "<br>Query: </br>";
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            stepText += "<br>" + "- " + entry.getKey() + ":" + entry.getValue();
        }
        actionInfoWriter.write(stepText);

        // Run test method
        switch (ta.getMethod().toLowerCase()) {
            case "get":
                runGetRequest(ContentType.JSON, headers, queryParams, expResponseItems);
                break;
            case "post":
                runPostRequest(ContentType.JSON, headers, queryParams, bodyParams, expResponseItems);
                break;
            case "put":
                runPutRequest(ContentType.JSON, headers, queryParams, bodyParams, expResponseItems);
                break;
            case "delete":
                runDeleteRequest(ContentType.JSON, headers, queryParams, expResponseItems);
                break;
            default:
                return false;
        }
        return errors.size() <= 0;
    }

    // ==================== Run Method Request ==================== //
    private void runGetRequest(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> expectedItems) {
        ResponseOptions responseOptions = executeGetMethod(contentType, headers, queryParams);
        validateResponse((Response) responseOptions, expectedItems);
    }

    private void runPostRequest(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> bodyParams, Map<String, Object> expectedItems) {
        ResponseOptions responseOptions = executePostMethod(contentType, headers, queryParams, bodyParams);
        validateResponse((Response) responseOptions, expectedItems);
    }

    private void runPutRequest(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> bodyParams, Map<String, Object> expectedItems) {
        ResponseOptions responseOptions = executePutMethod(contentType, headers, queryParams, bodyParams);
        validateResponse((Response) responseOptions, expectedItems);
    }

    private void runDeleteRequest(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> expectedItems) {
        ResponseOptions responseOptions = executeDeleteMethod(contentType, headers, queryParams);
        validateResponse((Response) responseOptions, expectedItems);
    }

    protected ValidatableResponse validateResponse(Response response, Map<String, Object> expItems) {
        validatableRes = response.then();

        // storedParamValue here
        // Only on PRO version

        expItems = testAPIAction.getResponse();
        for (Map.Entry<String, Object> entry : expItems.entrySet()) {
            switch (entry.getKey()) {
                case "statusCode":
                    int status;
                    if (entry.getValue() instanceof Integer) {
                        status = (Integer) entry.getValue();
                    } else {
                        status = ((Long) entry.getValue()).intValue();
                    }
                    try {
                        validatableRes.statusCode(status);
                    } catch (AssertionError e) {
                        errors.add(e);
                    }
                    break;
                case "schemaPath":
                    try {
                        // This auto find file from class path to below
                        // We can specify the directory by data\\api\\ + entry.getValue()
                        validatableRes.body(JsonSchemaValidator.matchesJsonSchemaInClasspath((String) entry.getValue()));
                    } catch (AssertionError e) {
                        errors.add(e);
                    }
                    break;
                case "body":
                    JSONObject fieldValues = (JSONObject) entry.getValue();
                    for (Object fieldValue : fieldValues.keySet()) {
                        try {
                            JSONObject jsonMatchers = (JSONObject) fieldValues.get(fieldValue);
                            for (Object matcher : jsonMatchers.keySet()) {
                                Matcher<?> refinedMatcher = buildMatcherPattern((String) matcher, jsonMatchers.get(matcher));
                                validatableRes.body((String) fieldValue, refinedMatcher);
                            }
                        } catch (AssertionError e) {
                            errors.add(e);
                        }
                    }
                    break;
            }
        }
        return validatableRes;
    }

    // ==================== Hamcrest Matcher ==================== //
    private Matcher buildMatcherPattern(String matcherPattern, Object valueMatcher) {
        Matcher refinedMatcher = null;

        // handle for serial number of matcher pattern
        matcherPattern = matcherPattern.indexOf('[') == 0 && matcherPattern.indexOf(']') > 0
                ? matcherPattern.substring(matcherPattern.indexOf(']') + 1).trim()
                : matcherPattern;

        String[] funcParts = matcherPattern.split("\\.");
        if (valueMatcher instanceof Long) {
            int value = ((Long) valueMatcher).intValue();
            if (((Long) valueMatcher) == value) {
                valueMatcher = value;
            }
        } else if (valueMatcher instanceof String) {
            valueMatcher = valueMatcher.toString();
        }

        for (int i = funcParts.length - 1; i >= 0; i--) {
            if (i == funcParts.length - 1) {
                refinedMatcher = createMatcher(funcParts[i], valueMatcher);
            } else {
                refinedMatcher = createMatcher(funcParts[i], refinedMatcher);
            }
            if (refinedMatcher == null) {
                break;
            }
        }
        return refinedMatcher;
    }

    private Matcher createMatcher(String funcName, Object value) {
        switch (funcName) {
            case "containsIgnoringCase":
                // Write extension for string
                return null;
            case "equalTo":
                if (value instanceof Double) {
                    return Matchers.equalTo(Float.valueOf(value.toString()));
                }
                return Matchers.equalTo(value);
            case "equalToIgnoringCase":
                return Matchers.equalToIgnoringCase((String) value);
            case "everyItem":
                return Matchers.everyItem((Matcher<?>) value);
            case "greaterThan":
                if (value instanceof String) {
                    return Matchers.greaterThan((String) value);
                } else if (value instanceof Integer) {
                    return Matchers.greaterThan((Integer) value);
                }
                return Matchers.greaterThan((Long) value);
            case "greaterOrEqualTo":
                if (value instanceof String) {
                    return Matchers.greaterThanOrEqualTo((String) value);
                } else {
                    if (value instanceof Integer) {
                        return Matchers.greaterThanOrEqualTo((Integer) value);
                    } else if (value instanceof Float) {
                        return Matchers.greaterThanOrEqualTo((float) value);
                    }
                }
                return Matchers.greaterThanOrEqualTo((Long) value);
            case "greaterThanOrEqualToIgnoringCase":
                // Write extension for string
                return null;
            case "hasKey":
                return Matchers.hasKey(value);
            case "hasItem":
                return Matchers.hasItem(value);
            case "hasItems":
                return Matchers.hasItems(((JSONArray) value).toArray());
            case "is":
                return Matchers.is(value);
            case "lessThan":
                if (value instanceof String) {
                    return Matchers.lessThan((String) value);
                } else if (value instanceof Integer) {
                    return Matchers.lessThan((Integer) value);
                }
                return Matchers.lessThan((Long) value);
            case "lessOrEqualTo":
                if (value instanceof String) {
                    return Matchers.lessThanOrEqualTo((String) value);
                } else {
                    if (value instanceof Integer) {
                        return Matchers.lessThanOrEqualTo((Integer) value);
                    } else if (value instanceof Float) {
                        return Matchers.lessThanOrEqualTo((float) value);
                    }
                }
                return Matchers.lessThanOrEqualTo((Long) value);
            case "lessThanOrEqualToIgnoringCase":
                // Write extension for string
                return null;
            case "not":
                return Matchers.not(value);
        }
        return null;
    }

    // ==================== Method Execution ==================== //
    private ResponseOptions executeGetMethod(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams) {
        RequestSpecification requestSpecification = given()
                .contentType(contentType)
                .headers(headers)
                .filter(new RequestLoggingFilter(requestCapture))
                .filter(new ResponseLoggingFilter(responseCapture))
                .log().ifValidationFails();

        // Add query parameters
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            requestSpecification.queryParam(entry.getKey(), entry.getValue());
        }
        return requestSpecification.when().get();
    }

    private ResponseOptions executePostMethod(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> bodyParams) {
        RequestSpecification requestSpecification = given()
                .contentType(contentType)
                .headers(headers)
                .filter(new RequestLoggingFilter(requestCapture))
                .filter(new ResponseLoggingFilter(responseCapture))
                .log().ifValidationFails();

        // Add query parameters
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            requestSpecification.queryParam(entry.getKey(), entry.getValue());
        }

        // Assign body parameters
        requestSpecification.body(bodyParams);

        return requestSpecification.when().post();
    }

    private ResponseOptions executePutMethod(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams, Map<String, Object> bodyParams) {
        RequestSpecification requestSpecification = given()
                .contentType(contentType)
                .headers(headers)
                .filter(new RequestLoggingFilter(requestCapture))
                .filter(new ResponseLoggingFilter(responseCapture))
                .log().ifValidationFails();

        // Add query parameters
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            requestSpecification.queryParam(entry.getKey(), entry.getValue());
        }

        // Add body parameters
        requestSpecification.body(bodyParams);

        return requestSpecification.when().put();
    }

    private ResponseOptions executeDeleteMethod(ContentType contentType, Map<String, Object> headers, Map<String, Object> queryParams) {
        RequestSpecification requestSpecification = given()
                .contentType(contentType)
                .headers(headers)
                .filter(new RequestLoggingFilter(requestCapture))
                .filter(new ResponseLoggingFilter(responseCapture))
                .log().ifValidationFails();

        // Add query parameters
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            requestSpecification.queryParam(entry.getKey(), entry.getValue());
        }

        return requestSpecification.when().delete();
    }
}