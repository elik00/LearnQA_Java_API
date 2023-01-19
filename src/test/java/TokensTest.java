import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class TokensTest {
    @Test
    public void tokenTest() throws InterruptedException {
        JsonPath createJobResponse = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        int seconds = createJobResponse.get("seconds");
        String token = createJobResponse.get("token");

        JsonPath getJobStatusResponse = RestAssured
                .given()
                .queryParam("token", token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String status = getJobStatusResponse.get("status");

        if (status.contentEquals("Job is NOT ready")) {
            System.out.println("Job is not ready");
            Thread.sleep(seconds * 1000);
            getJobStatusResponse = RestAssured
                    .given()
                    .queryParam("token", token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            status = getJobStatusResponse.get("status");
            if (status.contentEquals("Job is ready")) {
                System.out.println("Status after " + seconds + " seconds:\"Job is ready\"");
                String result = getJobStatusResponse.get("result");
                if (result == null) {
                    System.out.println("The key 'result' is absent");
                } else {
                    System.out.println("result:" + result);
                }
            } else {
                System.out.println("Job is still not ready");
            }
        } else {
            System.out.println("Status is incorrect");
        }
    }
}