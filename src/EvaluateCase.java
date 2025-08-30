import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvaluateCase {
    static class GuessResult {
        int guesses;
        String code;

        GuessResult(int guesses, String code) {
            this.guesses = guesses;
            this.code = code;
        }
    }

    public static void main(String[] args) {
        String filePath = "src/result_18.txt";
        List<GuessResult> results = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Number of guesses:")) {
                    // Extract number
                    int guesses = Integer.parseInt(line.replace("Number of guesses:", "").trim());

                    // Next line is the code
                    String code = br.readLine();
                    if (code != null) {
                        results.add(new GuessResult(guesses, code.trim()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sort descending by guesses
        results.sort((a, b) -> Integer.compare(b.guesses, a.guesses));

        // Print top 5
        System.out.println("Top 5 worst cases:");
        for (int i = 0; i < Math.min(5, results.size()); i++) {
            GuessResult r = results.get(i);
            System.out.println(r.guesses + " -> " + r.code);
        }

        // Compute average
        if (!results.isEmpty()) {
            double sum = 0;
            for (GuessResult r : results) {
                sum += r.guesses;
            }
            double average = sum / results.size();
            System.out.printf("Average number of guesses: %.2f%n", average);
        }
    }
}
