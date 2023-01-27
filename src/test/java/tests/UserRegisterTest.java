package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully create user with already existing email")
    @DisplayName("Test negative create user")
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Owner("Test owner")
    @Description("This test successfully create user")
    @DisplayName("Test positive create user")
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully create user with wrong email")
    @DisplayName("Test negative create user with wrong email")
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserWithWrongEmail() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestWithWrongEmail("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @Owner("Test owner")
    @Description("This test unsuccessfully create user with blank required fields")
    @DisplayName("Test negative create user because of bad request")
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserWithoutParam(String condition) {

        Map<String, String> userData = DataGenerator.getRegistrationData();
        if (condition.equals("email")) {
            Response responseCreateAuth = apiCoreRequests
                    .makePostRequestWithoutEmail("https://playground.learnqa.ru/api/user", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: email");
        } else if (condition.equals("password")) {
            Response responseCreateAuth = apiCoreRequests
                    .makePostRequestWithoutPassword("https://playground.learnqa.ru/api/user", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: password");
        } else if (condition.equals("username")) {
            Response responseCreateAuth = apiCoreRequests
                    .makePostRequestWithoutUsername("https://playground.learnqa.ru/api/user", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: username");
        } else if (condition.equals("firstName")) {
            Response responseCreateAuth = apiCoreRequests
                    .makePostRequestWithoutFirstName("https://playground.learnqa.ru/api/user", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: firstName");
        } else if (condition.equals("lastName")) {
            Response responseCreateAuth = apiCoreRequests
                    .makePostRequestWithoutLastName("https://playground.learnqa.ru/api/user", userData);

            Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
            Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: lastName");
        }
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully create user with short name")
    @DisplayName("Test negative create user with short name")
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserWithShortName() {

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestWithShortFirstName("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too short");
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully create user with long name")
    @DisplayName("Test negative create user with long name")
    @Severity(value = SeverityLevel.NORMAL)
    public void testCreateUserWithLongName() {

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequestWithLongFirstName("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'firstName' field is too long");
    }
}