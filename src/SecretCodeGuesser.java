public class SecretCodeGuesser {
    private static final char[] ALPH = {'B','A','C','X','I','U'};
    private final SecretCode code = new SecretCode();
    private static final int MAX_LENGTH = 18;


    static int order(char c) {
        if (c == 'B') {
            return 0;
        } else if (c == 'A') {
            return 1;
        } else if (c == 'C') {
            return 2;
        } else if (c == 'X') {
            return 3;
        } else if (c == 'I') {
            return 4;
        }
        return 5;
    }

    public void start() {
        // 1) Find the correct length by brute-force
        int correctLength = -1;
        int matchedA = -1; // score for the last length probe
        for (int length = 1; length <= MAX_LENGTH; length++) {
            String codeGuess = "A".repeat(length);
            int result = code.guess(codeGuess);
            if (result != -2) {
                if (length == result) { // Length = result mean that we have find the secret code
                    System.out.println(codeGuess);
                    return;
                }
                correctLength = length;
                matchedA = result; // equals number of 'A' in the secret
                break;
            }
        }
        if (correctLength == -1) {
            System.out.println("Failed to determine secret code length.");
            return;
        }

        // 2) Learn global and remaining counts of each letter (remaining is used to help prune letter tests)
        int[] globalCount = new int[6], remaining = new int[6];
        remaining[order('A')] = globalCount[order('A')] = matchedA; // We already did "AAAA...A" when detecting correctLength; reuse that result
        for (char c : ALPH) {
            if (c == 'A') continue;
            String codeGuess = String.valueOf(c).repeat(correctLength);
            int matched = code.guess(codeGuess);
            if (matched == correctLength) { // If matched = correctLength then we have found the secret code
                System.out.println(codeGuess);
                return;
            }
            remaining[order(c)] = globalCount[order(c)] = matched;
        }

        // 3) Choose a baseline letter and build a new string based on that letter
        char baseline = ALPH[0];
        for (char c : ALPH) {
            if (globalCount[order(c)] > globalCount[order(baseline)]) baseline = c;
        }

        char[] cur = new char[correctLength];
        for (int i = 0; i < correctLength; i++) cur[i] = baseline;
        int currentMatchedResult = globalCount[order(baseline)]; // current number (baseline) of exact matches

        // 4) Resolve each position by mutating exactly one index at a time
        for (int i = 0; i < correctLength; i++) {
            char original = cur[i];

            // Try letters in a good order:
            // - If we have remaining counts, try letters with highest remaining first
            // - Skip letters with remaining count = 0
            boolean[] tried = new boolean[ALPH.length];
            boolean positionLocked = false;

            for (int t = 0; t < ALPH.length - 1; t++) {
                int bestIdx = -1;
                int bestScore = Integer.MIN_VALUE;
                // Select the letter with the highest frequency in the code
                for (int j = 0; j < ALPH.length; j++) {
                    if (tried[j]) continue;
                    char candidate = ALPH[j];
                    if (candidate == original) continue;
                    if (remaining[order(candidate)] == 0) { tried[j] = true; continue; }

                    int score = remaining[order(candidate)]; // order hint
                    if (score > bestScore) { bestScore = score; bestIdx = j; }
                }
                if (bestIdx == -1) break; // nothing left to try

                tried[bestIdx] = true;
                cur[i] = ALPH[bestIdx];

                int newMatchedResult = code.guess(new String(cur));

                if (newMatchedResult > currentMatchedResult) {  // Found the correct letter for this position
                    currentMatchedResult = newMatchedResult;
                    positionLocked = true;
                    remaining[order(cur[i])]--;
                    if (currentMatchedResult == correctLength) { // solved early
                        System.out.println(new String(cur));
                        return;
                    }
                    break;
                } else if (newMatchedResult < currentMatchedResult) { // Original letter at i was correct; revert and lock
                    cur[i] = original;
                    positionLocked = true;
                    remaining[order(original)]--;
                    break;
                } else {  // newMatchedResult == currentMatchedResult: candidate is not correct here; revert and keep trying
                    cur[i] = original;
                }
            }

            // If none of the trials changed the score, original must be correct
            if (!positionLocked) remaining[order(original)]--;
        }

        // 5) Output the discovered secret
        System.out.println(new String(cur));
    }
}
