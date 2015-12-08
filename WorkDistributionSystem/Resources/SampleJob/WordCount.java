import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WordCount {

    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];

        // Will keep the word counts here
        Map<String, Integer> wordCount = new HashMap<>();

        // Open the input file
        try (BufferedReader input = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int lineCount = 0;
            
            // Read input file line by line
            while ((line = input.readLine()) != null) {
                // Normalise. Clean punctuation and multiple spaces. Put in lowercase.
                String normalisedLine = normaliseLine(line);
                
                // Skip empty lines
                if(normalisedLine.isEmpty()) {
                    continue;
                }
                
                // Update word counts ...
                updateWordCount(wordCount, normalisedLine);
                
                // Debug output message & Sleep to make the job run longer ...
                if(++lineCount % 500 == 0) {
                    System.out.printf("Processed %d lines%n", lineCount);
                    Thread.sleep(1000);
                }
            }
            
            // Write final result
            writeResult(wordCount, outputFile);
        } catch (IOException | InterruptedException e) {
            System.err.println("Word Count Job failed!");
            e.printStackTrace();
            // Indicate failure of the job process with a non-zero exit code!
            System.exit(1);
        }
    }

    private static void updateWordCount(Map<String, Integer> wordCount, String normalisedLine) {
        for (String word : normalisedLine.split("\\s")) {
            // Skip short-ish words
            if(word.length() > 3) {
                int wordCountSoFar = wordCount.containsKey(word)? wordCount.get(word) : 0;
                wordCount.put(word, wordCountSoFar + 1);
            }
        }
    }

    private static String normaliseLine(String line) {
        return line.replaceAll("[^a-zA-Z\\s]", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase(Locale.ENGLISH);
    }

    private static void writeResult(Map<String, Integer> wordCount, String outputFile) throws IOException {
        try(BufferedWriter output = new BufferedWriter(new FileWriter(outputFile))) {
            // Sort by word count
            List<Map.Entry<String, Integer>> wordCountList = new ArrayList<>(wordCount.entrySet());
            Collections.<Map.Entry<String, Integer>>sort(wordCountList, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            for (Map.Entry<String, Integer> wordCountEntry : wordCountList) {
                output.write(String.format("%-20s - %d%n", wordCountEntry.getKey(), wordCountEntry.getValue()));
            }
        }
    }
}
