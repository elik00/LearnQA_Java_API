import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PasswordTest {

    @Test
    public void passwordTest() {
        ArrayList<String> passwords = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords").get();
            Elements trs = doc.select("table.wikitable");
            Elements trr = trs.get(1).getElementsByAttributeValue("align", "left");

            for (Element tr : trr) {
                if (tr.text().contains("[a]")) {
                    tr.childNode(1).remove();
                }
                passwords.add(tr.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> set = new HashSet<>(passwords);
        passwords.clear();
        passwords.addAll(set);

        String[] array = set.toArray(new String[0]);

        Map<String, String> credentials = new HashMap<>();

        for (String pass : array) {
            credentials.put("login", "super_admin");
            credentials.put("password", pass);

            Response getAuthCookieResponse = RestAssured
                    .given()
                    .body(credentials)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String responseCookie = getAuthCookieResponse.getCookie("auth_cookie");

            Response checkAuthCookieResponse = RestAssured
                    .given()
                    .cookie("auth_cookie", responseCookie)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String answer = checkAuthCookieResponse.asString();
            if (answer.equals("You are authorized")) {
                System.out.println("password: " + pass);
                System.out.println("answer: " + answer);
            }
        }
    }
}