# ReqRes API Automation

## Which App and Why
I chose the **ReqRes API** (reqres.in) because it provides a reliable and realistic set of RESTful endpoints. It is excellent for practicing and demonstrating CRUD operations, handling JSON payloads, and validating standard HTTP status codes.

## Framework and Language
* **Language:** Java
* **Framework:** REST Assured (for API testing), TestNG (for test execution and assertions), and Maven (for dependency management).

## How to Run the Tests
1. Ensure you have Java (JDK 11 or higher) and Maven installed.
2. Clone this repository and navigate to the project directory.
3. Run the following command in your terminal:
   ```bash
   mvn clean test
   ```
4. The test results will be displayed in the console and an HTML report will be generated in the `target/surefire-reports` folder.

## Assumptions and Limitations
* **Mock Data:** ReqRes provides mock responses. Operations like creating or updating a user will return a successful response, but the data isn't permanently saved on the server.
* **Network Dependency:** The tests require an active internet connection to reach the external API endpoints.
