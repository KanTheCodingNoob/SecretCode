public final class SecretCodeGuesser {
    private static final char[] ALPH = {'B','A','C','X','I','U'};
    private final SecretCode code = new SecretCode();

    public void start() {
        // 1) Find the correct length L by brute-force
        int L = -1;
        int lastLenScore = -1; // score for the last length probe
        for (int length = 1; length <= 10000; length++) {
            String codeGuess = "A".repeat(length);
            int result = code.guess(codeGuess);
            if (result != -2) {
                if (length == result) { // Length = result mean that we have find the secret code
                    System.out.println(codeGuess);
                    return;
                }
                L = length;
                lastLenScore = result; // equals number of 'A' in the secret
                break;
            }
        }
        if (L == -1) {
            System.out.println("Failed to determine secret code length.");
            return;
        }

        // 2) Learn global and remaining counts of each letter (remaining is used to help prune letter tests)
        int[] globalCount = new int[256], remaining = new int[256];
        remaining['A'] = globalCount['A'] = lastLenScore; // We already did "AAAA...A" when detecting L; reuse that result
        for (char c : ALPH) {
            if (c == 'A') continue;
            String codeGuess = String.valueOf(c).repeat(L);
            int matched = code.guess(codeGuess);
            if (matched == L) { // If matched = L then we have found the secret code
                System.out.println(codeGuess);
                return;
            }
            remaining[c] = globalCount[c] = matched;
        }

        // 3) Choose a baseline letter
        char baseline = ALPH[0];
        for (char c : ALPH) {
            if (globalCount[c] > globalCount[baseline]) baseline = c;
        }

        char[] cur = new char[L];
        for (int i = 0; i < L; i++) cur[i] = baseline;
        int k = globalCount[baseline]; // current number (baseline) of exact matches

        // 4) Resolve each position by mutating exactly one index at a time
        for (int i = 0; i < L; i++) {
            char original = cur[i];

            // Try letters in a good order:
            // - If we have remaining counts, try letters with highest remaining first
            // - Skip letters with remaining count = 0
            boolean[] tried = new boolean[ALPH.length];
            boolean positionLocked = false;

            for (int t = 0; t < ALPH.length - 1; t++) {
                int bestIdx = -1;
                int bestScore = Integer.MIN_VALUE;
                for (int j = 0; j < ALPH.length; j++) {
                    if (tried[j]) continue;
                    char cand = ALPH[j];
                    if (cand == original) continue;
                    if (remaining[cand] == 0) { tried[j] = true; continue; }

                    int score = remaining[cand]; // order hint
                    if (score > bestScore) { bestScore = score; bestIdx = j; }
                }
                if (bestIdx == -1) break; // nothing left to try

                tried[bestIdx] = true;
                cur[i] = ALPH[bestIdx];

                int r = code.guess(new String(cur));

                if (r > k) {  // Found the correct letter for this position
                    k = r;
                    positionLocked = true;
                    remaining[cur[i]]--;
                    if (k == L) { // solved early
                        System.out.println(new String(cur));
                        return;
                    }
                    break;
                } else if (r < k) { // Original letter at i was correct; revert and lock
                    cur[i] = original;
                    positionLocked = true;
                    remaining[original]--;
                    break;
                } else {  // r == k: candidate is not correct here; revert and keep trying
                    cur[i] = original;
                }
            }

            // If none of the trials changed the score, original must be correct
            if (!positionLocked) remaining[original]--;
        }

        // 5) Output the discovered secret
        System.out.println(new String(cur));
    }
}
