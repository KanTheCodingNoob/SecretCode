import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SecretCodeTest {
    public static void main(String[] args) {
        String filePath = "src/secret_codes_18.txt";
        String code;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                code = line; // set code for each line
                SecretCodeGuesser guesser = new SecretCodeGuesser();
                guesser.setCode(new SecretCode(code));
                guesser.start();

                // Example: use the guesser with this code
                // guesser.guess(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
