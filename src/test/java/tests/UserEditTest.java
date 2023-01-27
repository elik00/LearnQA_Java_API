package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User editing cases")
@Feature("User editing")
public class UserEditTest extends BaseTestCase {

    int userId;
    Map<String, String> userData;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void generateUser() {
        userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        this.userId = this.getIntFromJson(responseCreateAuth, "id");
    }

    @Test
    @Owner("Test owner")
    @Description("This test successfully edit user")
    @DisplayName("Test positive edit user")
    @Severity(value = SeverityLevel.NORMAL)
    public void editJustCreatedTest() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.userData.get("email"));
        authData.put("password", this.userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Owner("Test owner")
    @Description("This test try edit user without authorization")
    @DisplayName("Test negative edit user")
    @Severity(value = SeverityLevel.NORMAL)
    public void editJustCreatedWithoutAuthTest() {
        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestWithoutTokenAndCookie("https://playground.learnqa.ru/api/user/" + this.userId, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @Owner("Test owner")
    @Description("This test try edit user which was created by another user")
    @DisplayName("Test negative edit user")
    @Severity(value = SeverityLevel.NORMAL)
    public void editAnotherUserTest() {
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

        //EDIT
        String newName = "Changed name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, anotherHeader, anotherCookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 200);

        //LOGIN WITH THE SAME USER
        authData = new HashMap<>();
        authData.put("email", this.userData.get("email"));
        authData.put("password", this.userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully edit user with wrong email")
    @DisplayName("Test edit user with wrong email")
    @Severity(value = SeverityLevel.NORMAL)
    public void editJustCreatedWithWrongEmailTest() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.userData.get("email"));
        authData.put("password", this.userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String newEmail = "testexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Test
    @Owner("Test owner")
    @Description("This test unsuccessfully edit user with too short name")
    @DisplayName("Test edit user with too short name")
    @Severity(value = SeverityLevel.NORMAL)
    @Flaky
    public void editJustCreatedWithShortFirstNameTest() {
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.userData.get("email"));
        authData.put("password", this.userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = getHeader(responseGetAuth, "x-csrf-token");
        String cookie = getCookie(responseGetAuth, "auth_sid");

        //EDIT
        String newName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId, header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseErrorTextEquals(responseEditUser, "Too short value for field firstName");
    }
}