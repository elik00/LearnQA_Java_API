package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User deleting cases")
@Feature("User deleting")
public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully delete user which cannot be removed")
    @DisplayName("Test negative delete user")
    @Severity(value = SeverityLevel.NORMAL)
    public void negativeUserDeleteTest() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Owner("Test owner")
    @Description("This test successfully delete user")
    @DisplayName("Test positive delete user")
    @Severity(value = SeverityLevel.NORMAL)
    public void positiveUserDeleteTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        //CREATE
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        int userId = getIntFromJson(responseCreateAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        //LOGIN
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully delete user because there was authorize by another user")
    @DisplayName("Test negative delete another user")
    @Severity(value = SeverityLevel.NORMAL)
    public void deleteAnotherUserTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        //CREATE
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        int userId = getIntFromJson(responseCreateAuth, "id");

        //CREATE ANOTHER USER
        Map<String, String> anotherUserData = DataGenerator.getRegistrationData();
        Response responseCreateAnotherAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", anotherUserData);

        //LOGIN WITH ANOTHER USER
        Map<String, String> authData = new HashMap<>();
        authData.put("email", anotherUserData.get("email"));
        authData.put("password", anotherUserData.get("password"));

        Response responseGetAnotherAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String anotherHeader = getHeader(responseGetAnotherAuth, "x-csrf-token");
        String anotherCookie = getCookie(responseGetAnotherAuth, "auth_sid");

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequest("https://playground.learnqa.ru/api/user/" + userId, anotherHeader, anotherCookie);

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //LOGIN WITH THE SAME USER
        authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertJsonByName(responseUserData, "id", userId);
    }
}