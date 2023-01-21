import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetCookieTest {

    @Test
    public void getCookieTest() {
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = response.getCookies();

        assertTrue(cookies.containsKey("HomeWork"), "Response doesn't have 'HomeWork' cookie");
        assertEquals("hw_value", cookies.get("HomeWork"), "Value of cookie 'HomeWork' is not equal to \"hw_value\"");
    }
}