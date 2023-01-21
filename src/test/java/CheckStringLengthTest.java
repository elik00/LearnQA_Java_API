import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckStringLengthTest {

    @ParameterizedTest
    @ValueSource(strings = {"Fifteen symbols", "Fifteen+ symbols"})
    public void checkStringLengthTest(String text) {
        assertTrue(text.length() > 15, "The length of text less than 15");
    }
}