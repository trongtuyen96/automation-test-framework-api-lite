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
 <a><img src="https://github.com/trongtuyen96/automation-test-framework-api-lite/blob/master/covers/architecture.PNG" alt="architecture"></a>
Execution xml file is used to trigger the test and specify the json test case data to run.

The framework read and get the test case data from json file, map it into test suite (a set of test cases).

Each test case will be executed with corresponding method (GOT, POST, PUT, DELTE), request headers, body.

The response will be stored and validated with the expected status code, values or schemas.

The test result of test cases is stored and written into test report.

The whole process of execute test case is iterated until no test cases left.

The whole process of execute test suite is iterated until no test suites left (since an execution xml file may specify more than 1 test data json file).

The total result is written into report.

## Key Features

## How to use

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
