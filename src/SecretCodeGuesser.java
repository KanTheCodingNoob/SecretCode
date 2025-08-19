public class SecretCodeGuesser {
  SecretCode code = new SecretCode();
  public void start() {
    // brute force secret code guessing
    int correctLength = -1; // track correct key length
    
    // First, find correct length by brute-force (keep it - no need for change as worst case scenario is 18 guesses)
    for (int length = 1; length <= 20; length++) {
      String candidate = "B".repeat(length);
      int result = code.guess(candidate);
      if (result != -2) { // not a "wrong length" response
        correctLength = length;
        break;
      }
    }

    if (correctLength == -1) {
      System.out.println("Failed to determine secret code length.");
      return;
    }

    // brute force key guessing
    String str = "B".repeat(correctLength); // use discovered length
    String secretCode = findSecretCode(str);
    System.out.println("I found the secret code. It is " + secretCode);
  }

  static char charOf(int order) {
    if (order == 0) {
      return 'B';
    } else if (order == 1) {
      return 'A';
    } else if (order == 2) {
      return 'C';
    } else if (order == 3) {
      return 'X';
    } else if (order == 4) {
      return 'I';
    } 
    return 'U';
  }

  /* - Explanation:
    This function strive to solve the secret code through flicking each
    character of the string one by one, seeing if the match counter increase
    when flicked. If it did, then we confirmed that is the correct character
    and move onto the next character until the end of the string
    - Detailed time complexity:
    Let N = length of the secret code
    Let K = number of possible characters in the code

    Steps:
      1. Initial guess() call: O(N)
      2. Outer loop runs N times (once per character position)
      3. Inner loop runs at most K-1 times (tries alternative characters)
         Each iteration calls guess(), which is O(N)
         Plus constant-time assignments and comparisons.

    Per position cost: (K-1) × O(N) ≈ O(KN)
    Across all positions: N × O(KN) = O(KN²)

    Since K is a small constant (here 6 possible letters total, 5 tested),
    the time complexity simplifies to O(N²) for asymptotic analysis.

  - Summary:
    Exact: at most (K-1) × N + 1 calls to guess(), each O(N)
    Big-O: O(N²)
  */
  public String findSecretCode(String current) {
//    SecretCode code = new SecretCode();
    int matched = code.guess(current); // Check the current amount of match characters with the current string
    char[] curr = current.toCharArray();
    for (int i = 0; i < current.length(); i++) { // Looping through every character in the string
      for (int j = 1; j < 6; j++) { // Looping through every character except for 'B' since the input is just a string of 'B'
        curr[i] = charOf(j);
        int charMatchAfterGuess = code.guess(String.valueOf(curr)); // Check the current amount of match characters with the newly modified string
        if (charMatchAfterGuess < matched) { // If the amount of match character from the newly created string is lower than the current match amount, that would mean it was already correct
          curr[i] = charOf(0);
          break;
        } else if (charMatchAfterGuess > matched) {
          matched++;
          break;
        }
      }
    }
    return String.valueOf(curr); // {1}
  }  
}
