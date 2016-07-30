import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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

        int temp = input.length() / 3;
        int tmpCount = 0;

        List allVariants = new ArrayList();
        for (int position = temp; position < (input.length() - temp); position++) {
            System.out.println("Count: "+ ++tmpCount);
            WordVariant[] variants = cutString(input, position, words);
            for (WordVariant variant : variants) {
                System.out.println(variant);
            }
            allVariants.add(variants);
            System.out.println("\n\n");
        }

        allVariants.sort(new WordVariantsComparator());

        System.out.println("For rebus:");
        WordVariant[] forRebus = (WordVariant[]) allVariants.get(allVariants.size()/2);
        System.out.println(forRebus[0]);
        System.out.println(forRebus[1]);
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
                    "Changes count: " + changesCount + "\n";
        }
    }

    static class WordVariantsComparator implements Comparator<WordVariant[]> {

        @Override
        public int compare(WordVariant[] o1, WordVariant[] o2) {
            return o1[0].changesCount+o1[1].changesCount -
                    o2[0].changesCount+o2[1].changesCount;
        }

        @Override
        public Comparator<WordVariant[]> reversed() {
            return null;
        }

        @Override
        public Comparator<WordVariant[]> thenComparing(Comparator<? super WordVariant[]> other) {
            return null;
        }

        @Override
        public <U> Comparator<WordVariant[]> thenComparing(Function<? super WordVariant[], ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
            return null;
        }

        @Override
        public <U extends Comparable<? super U>> Comparator<WordVariant[]> thenComparing(Function<? super WordVariant[], ? extends U> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<WordVariant[]> thenComparingInt(ToIntFunction<? super WordVariant[]> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<WordVariant[]> thenComparingLong(ToLongFunction<? super WordVariant[]> keyExtractor) {
            return null;
        }

        @Override
        public Comparator<WordVariant[]> thenComparingDouble(ToDoubleFunction<? super WordVariant[]> keyExtractor) {
            return null;
        }
    }

    private static class Change {
        int changeType = 0;
        String first;
        String second;
    }
}
