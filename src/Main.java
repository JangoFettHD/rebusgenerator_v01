import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);

        String input = scanner.nextLine();
        input = input.toLowerCase();

        if (input.length() < 4) {
            System.out.println("String too small");
            return;
        }

        List<String> words = new ArrayList<>();


        Scanner fileScanner = new Scanner(new File("C:\\Users\\JangoFettHD\\IdeaProjects\\rebusgenerator_v0\\word_rus.txt"));
        while (fileScanner.hasNext()) {
            words.add(fileScanner.nextLine());
        }
        WordVariant[] variants = cutString(input, input.length() / 2, words);
        for (WordVariant variant : variants) {
            System.out.println(variant);
        }
    }


    static WordVariant[] cutString(String input, int cutPosition, List<String> wordlist) {
        String first = input.substring(0, cutPosition);
        String second = input.substring(cutPosition);

        WordVariant[] variants = new WordVariant[]{new WordVariant(), new WordVariant()};

        for (String word : wordlist) {
            int dst1 = levenshteinDistance(first, word);
            int dst2 = levenshteinDistance(second, word);

            if (dst1 < variants[0].changesCount) {
                variants[0] = new WordVariant(first, word, new Change[]{}, dst1);
            }
            if (dst2 < variants[1].changesCount) {
                variants[1] = new WordVariant(second, word, new Change[]{}, dst2);
            }
        }

        return variants;
    }


    public static int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++) cost[i] = i;

        // dynamically computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {
            // initial cost of skipping prefix in String s1
            newcost[0] = j;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {
                // matching current letters in both strings
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

    private static class WordVariant implements Comparable {
        String originalWord = "";
        String rebusWord = "";
        Change[] changes;
        int changesCount = 99999;

        public WordVariant(String originalWord, String rebusWord, Change[] changes, int changesCount) {
            this.originalWord = originalWord;
            this.rebusWord = rebusWord;
            this.changes = changes;
            this.changesCount = changesCount;
        }

        public WordVariant() {

        }


        @Override
        public int compareTo(Object o) {
            if (o instanceof WordVariant) {
                WordVariant other = (WordVariant) o;
                return changesCount - other.changesCount;
            } else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return "Original word: " + originalWord + "\n" +
                    "Rebus word: " + rebusWord + "\n" +
                    "Changes count: " + changesCount + "\n\n\n";
        }
    }

    private static class Change {
        int changeType = 0;
        String first;
        String second;
    }
}
