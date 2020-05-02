<h1 align="center">
  <br>
  <a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/ATWT_background.PNG" alt="background"></a>
    <a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/rest_api_lite_badge.png" alt="rest api badge"></a>
  <br>
  Automation API Test Framework - Lite Version
  <br>
</h1>

<h3 align="center" style="bold">An automation testing framework for REST API using <a href="https://testng.org/">TestNG</a>, <a href="http://rest-assured.io/">RestAssured</a>, <a href="https://www.automatedtestingwithtuyen.com/post/data-driven-testing-framework">Data-Driven</a>, <a href="https://extentreports.com/">ExtentReport</a>.</h3>

## Table of Contents
- [Project Structure](#project-structure)
- [How It Works](#how-it-works)
- [Key Features](#key-features)
- [How To Use](#how-to-use)
- [Author](#author)
- [License](#license)

## Project Structure
  <a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/project_structure.PNG" alt="project structure" width="250"></a>

- reports: where the generated reports located.
- execution: contains execution xml file to run the test (follow TestNG framework).
- resources: contains schema file for schema validation.
- config: contains config file for url, port, extent report config, ...
- suites: contains test cases groups by suites.
- TestEntity: contains test object including test suite, test case, test step.
- TestExecution: functions and methods to execute the test.
- Utils: some additional functions written here.

## How It Works
<h1 align="center"><a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/architecture.PNG" alt="architecture" width="800"></a></h1>
 
1. Execution xml file is used to trigger the test and specify the json test case data to run.

2. The framework read and get the test case data from json file, map it into test suite (a set of test cases).

3. Each test case will be executed with corresponding method (GOT, POST, PUT, DELTE), request headers, body.

4. The response will be stored and validated with the expected status code, values or schemas.

5. The test result of test cases is stored and written into test report.

6. The whole process of execute test case is iterated until no test cases left.

7. The whole process of execute test suite is iterated until no test suites left (since an execution xml file may specify more than 1 test data json file).

8. The total result is written into report.

## Key Features
1. Fully operational testing process that implemeneted Data-Driven Framework using json-format test cases as data source.

2. Easy to learn, easy to understand, easy to create json test cases yourself with well-structured format.

3. Built-in ExtentReport with best customized output reports for REST API testing.

4. Implemented <a href="http://hamcrest.org/JavaHamcrest/javadoc/2.1/org/hamcrest/Matchers.html">Hamcrest Matchers</a> for validation of API response's value. For example: greater than, less than, has key, has item, not, is, equal to, every item, contains, ...

5. Implemented <a href="http://rest-assured.io/">RestAssured</a> for comparing the actual response schema property with expected schema and making fully supported API request with content type, headers, body, ...

6. Test cases executed with run-time variables or pre-defined variables. For example: "datetime" : @var->TODAY@, when the test case is executed, the value will be repaced as "datetime": "2020-02-20".

7. Extended from TestNG for multi-thread test execution, parallel testing with running multiple set of test suite at the same time.

8. Test execution from command line, which is super beneficial for CI/CD pipeline process.

## How to use
#### Write basic test case
Create a new json file with structure as below description, place it under suites folder:
<a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/example_1.PNG" alt="example 1"></a>

- suiteName: Name of the suite.
- suiteDescription: Description of the suite.
- testId: ID of the test case.
- testName: Name of the test case.
- testDescription: Description of the test case.
- testObjective: Objective of the test case.
- method: Method of API request (GET/POST/PUT/DELETE).
- name: Name of the test step.
- url: URL to the endpoints (only specify the route since the base URL had been specified).
- request: The request section, may contain path, query parameters, body request, headers.
- response: The expected result or validation.
- statusCode: The expected status code of return response.
- schemaPath: The path to the expected schema that will be used to compare with actual response schema.
- body: Hamcrest Matchers method for validation.

#### Validate response value
There 3 type of validation of the response: status code, schema and body value.
- statusCode: The expected status code of the response. Ex: 200, 201, 404, 500, ...
- schemaPath: The path to the schema file. The schema file is located in resources/schemas folder.
- body: Based on Hamcrest Matcher functions. 

Ex1: To compare value of field name is equal to Tester, the below script is used:

"name": {"is": "Tester"} 

where "is" is one of the functions of Hamcrest Matchers.

Ex2: To compare value of field count is greater than 5, the below script is used:

"count": {"greaterThan": 5} 

where "greaterThan" is one of the functions of Hamcrest Matchers.

More Hamcrest Matchers functions can be built by coding yourself.
Some available built-in matchers in project:
	
	1. equalTo: Compare if logically equal to expected value.
	
	2. equalToIgnoringCase: Compare if equal to expected value ignoring the case of characters.
	
	3. everyItem: Compare every item of response.
	
	4. greaterThan: Compare if greater than the expected value.
	
	5. greaterThanOrEqual: Compare if greater than or equal the expected value.
	
	6. lessThan: Compare if less than the expected value.
	
	7. lessThanOrEqual: Compare if less than or equal the expected value.
	
	8. hasItem: Compare if the response has the expected item.
	
	9. not: Invert the logic.

#### Use variables
Variable is generated at runtime. The structure is @var-> <name of the variable> @.

Here is the example of using TODAY value, which can prevent causing the error of creating duplicate emails:

<a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/example_2.PNG" alt="example 2"></a>

Some available built-in variables in project:
	
	1. TODAY: Today string.
	
	2. NOW_HH:mm: Now string format.
	
	4. NOW+1_HH:mm: Current hour +1 format.
	
	4. TODAY_yyyy-MM-dd: Today string with yy-MM-dd format.
	
	5. YESTERDAY_yyyy-MM-dd: Yesterday string with yy-MM-dd format.
	
	6. TOMORROW_yyyy-MM-dd: Tomorrow string with yy-MM-dd format.
	
	7. NEXTMONTH_yyyy-MM-dd: Next month date string with yy-MM-dd format.
	
	8. RANDOM_alphanumeric_5: Random string of 5 alphanumeric.
	
	9. RANDOM_numeric_5: Random string of 5 numeric.
	
	10. RANDOM_alphabetic_5: Random string of 5 alphabet.
	
#### Execute test
Test execution methodology is powered by TestNG, it requires the xml execution file, which is under execution folder.

<a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/example_3.PNG" alt="example 3"></a>

Specify the test case json file that has been created under suite folder.
Run the xml file for running the test. Use Ctrl + Shift + F10 in the case of IntelliJ IDEA.

#### Command line execution
Powered by Maven plugins.

<a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/example_4.PNG" alt="example 4"></a>

Format: mvn clean test -DtestSuiteName="path to execution file"

## Author
<h4 align="center">
	Tuyen Nguyen - QA Automation Engineer
	</h4>
	<h5 align="center">
	<a href="trongtuyen96@gmail.com">trongtuyen96@gmail.com</a>
	</h5>
<p align="center">
	 <a alt="Github" href="https://github.com/trongtuyen96">
    <img src="https://user-images.githubusercontent.com/25218255/47360756-794c1f00-d6fa-11e8-86fa-7b1c2e4dda92.png" width="50">
  </a>
		 <a alt="LinkedIn" href="https://www.linkedin.com/in/tuyennguyen96/">
    <img src="https://user-images.githubusercontent.com/25218255/47360366-8583ac80-d6f9-11e8-8871-219802a9a162.png" width="50">
  </a>
		 <a alt="Facebook" href="https://www.facebook.com/tuyen.trong.3">
    <img src="https://user-images.githubusercontent.com/25218255/47360363-84eb1600-d6f9-11e8-8029-818481536200.png" width="50">
  </a>
</p>

## License
~~~~
Copyright 2020 Tuyen Nguyen

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
~~~~
