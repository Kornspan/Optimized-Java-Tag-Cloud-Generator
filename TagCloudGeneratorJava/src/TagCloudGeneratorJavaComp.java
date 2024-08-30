import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * class to handle exception when user enter negative number for the input of
 * number of words.
 *
 * @author Krish Patel, Zach Kornspan, Nathaniel Crutchfield
 *
 */
class InvalidNumException extends Exception {
    /**
     * prints the error message str when InvalidNumException Occurs.
     *
     * @param str
     */
    InvalidNumException(String str) {
        super(str);
    }
}

/**
 * Tag cloud generator from a given file with a given amount of words.
 *
 * @author Krish Patel, Zach Kornspan, Nathaniel Crutchfield
 */

public final class TagCloudGeneratorJavaComp {

    /**
     * Compare {@code Map.Pair<String,Integer>}s in lexicographic order of their
     * key but ignoring the Case.
     *
     */

    private static class SortByAlphabeticalOrder
            implements Serializable, Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {

            //compares the keys while ignoring case
            int result = o1.getKey().compareToIgnoreCase(o2.getKey());

            //if result then again compare them using normal compare to ensure
            //that they are equal
            if (result == 0) {
                result = o1.getKey().compareTo(o2.getKey());
            }
            return result;
        }

    }

    /**
     * Compare {@code Map.Pair<String,Integer>}s in decreasing order of their
     * value.
     *
     */
    private static class SortByWordCounts
            implements Serializable, Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            //compares values
            int result = o2.getValue().compareTo(o1.getValue());

            //if the values are equal then compares then compares the9ir keys
            if (result == 0) {
                result = o1.getKey().compareTo(o2.getKey());
            }
            return result;

        }
    }

    /**
     * No argument constructor--private to prevent instantiation.
     *
     * @return
     */
    private TagCloudGeneratorJavaComp() {
        // no code needed here
    }

    /**
     * Outputs the opening tags for tag cloud in the given output file.
     *
     * @param out
     *            output stream
     * @param name
     *            name of the input file
     * @param num
     *            the number of words to be included in tag cloud
     * @ensures out.content = #out.content * [the initial tag for HTML web page]
     */

    private static void outputHeader(PrintWriter out, String name, int num) {
        //prints starting tags for html and head
        out.println("<html>");

        //prints title
        out.println("<head>");
        out.println("<title>Top " + num + " words in " + name + "</title>");

        //prints link to the given css file
        out.println("<link href=\"https://web.cse.ohio-state.edu"
                + "/software/2231/web-sw2/assignments/projects/tag-cloud-generator"
                + "/data/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");
        out.println(
                "<link href=\"tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");

        //closes head and start body
        out.println("</head>");
        out.println("<body>");

        //prints html for p,div, h2 and hr
        out.println("<h2>Top " + num + " words in " + name + "</h2>");
        out.println("<hr>");
        out.println("<div class = \"cdiv\">");
        out.println("<p class = \"cbox\">");

    }

    /**
     * Generates Map<String, Integer> {@code wordCount} of the words in a given
     * file by reading the input by using BufferedReader {@code fileReader}. The
     * definition of word here is alphabets and digits.
     *
     * @param fileReader
     *            the input stream
     * @return the Map{@code wordCount} having word as key and count as value
     * @throws IOException
     * @ensures Map=[Contains all the words as its key and count as its
     *          corresponding value]
     */

    private static Map<String, Integer> fileToMap(BufferedReader fileReader)
            throws IOException {
        Map<String, Integer> wordCount = new HashMap<>();

        //Reads the file till end
        String line = fileReader.readLine();
        while (line != null) {

            for (int a = 0; a < line.length(); a++) {
                char lineCharacter = line.charAt(a);

                //check if the character is alphabet or digit
                //and if not it is substituted by space character

                if (!(Character.isLetter(lineCharacter)
                        || Character.isDigit(lineCharacter))) {
                    line = line.substring(0, a) + " " + line.substring(a + 1);
                }
            }
            //split the line with only words into individual words
            //and store it into string

            String[] lineSplit = line.split(" ");
            for (int i = 0; i < lineSplit.length; i++) {
                String word = lineSplit[i];
                word = word.toLowerCase(); //changes word to lower case

                //adds the word into the Map if it does not exist
                // if it exists then increase the value of the word by 1

                if (!wordCount.containsKey(word)) {
                    wordCount.put(word, 1);
                } else {
                    Integer count = wordCount.get(word);
                    wordCount.remove(word);
                    wordCount.put(word, count + 1);
                }
            }
            line = fileReader.readLine();
        }

        //removes the empty character as it is not word

        if (wordCount.containsKey("")) {
            wordCount.remove("");
        }
        return wordCount;
    }

    /**
     * Calculates a good font size depending upon the count of the highest and
     * lowest word, and from the count of the given word.
     *
     * @param maxCount
     *            maximum count of a word in a tag cloud
     * @param minCount
     *            minimum count of a word in a tag cloud
     * @param count
     *            count of the current word
     * @return a good font size for a word with a given count
     */
    private static String fontSize(int maxCount, int minCount, int count) {
        //maximum font possible according to css
        final int maxFont = 48;
        final int minFont = 11;

        //actual font initialization to max and min difference
        int actualFont = maxFont - minFont;

        //executes when the difference is not 0 otherwise error of divide by zero
        if (minCount - maxCount != 0) {
            //counts the actual font
            actualFont *= (count - minCount);
            actualFont /= (maxCount - minCount);
            actualFont += minFont;
        } else {
            //defaults the value to 25 if every word has same count
            final int defaultFont = 25;
            actualFont = defaultFont;
        }
        //returns actualFont as string
        return "" + actualFont;

    }

    /**
     * prints the HTML for a total num amount of words with the highest count in
     * alphabetical order.
     *
     * @param out
     *            output stream
     * @param wordCounts
     *            map of words and its count
     * @param num
     *            number of words to be printed
     * @clears wordCounts
     * @ensures out.content = #out.content * [HTML for tag cloud]
     */
    private static void printTopWords(PrintWriter out,
            Map<String, Integer> wordCounts, int num) {

        //creates a new Object of SortByWordCounts

        Comparator<Map.Entry<String, Integer>> wordCountOrder = new SortByWordCounts();

        //creates a ArrayList for sorting by count

        List<Map.Entry<String, Integer>> countSorter;
        countSorter = new ArrayList<Map.Entry<String, Integer>>();

        //creates set so we can use iterator

        Set<Map.Entry<String, Integer>> transferSet1 = wordCounts.entrySet();
        Iterator<Map.Entry<String, Integer>> countIterator = transferSet1
                .iterator();

        //adds whole map into countSorter using iterator
        while (countIterator.hasNext()) {
            Map.Entry<String, Integer> entry = countIterator.next();
            countIterator.remove();
            countSorter.add(entry);
        }

        //sort the ArrayList based on the count of words
        countSorter.sort(wordCountOrder);

        //create a new Object of SortByAlphabeticalOrder
        //and creates a new ArrayList for adding top num words

        Comparator<Map.Entry<String, Integer>> alphabeticalOrder;
        alphabeticalOrder = new SortByAlphabeticalOrder();
        List<Map.Entry<String, Integer>> alphabeticalSorter;
        alphabeticalSorter = new ArrayList<Map.Entry<String, Integer>>();
        int wordsAdded = 0, minCount = 0, maxCount = 0;

        //removes the num words from the front of countSorter
        //and adds it to alphabeticalSorter
        // and also gets the minCount,maxCount

        while (wordsAdded < num && countSorter.size() > 0) {
            Map.Entry<String, Integer> countPair = countSorter.remove(0);
            alphabeticalSorter.add(countPair);
            if (wordsAdded == 0) {
                maxCount = countPair.getValue();
            }
            wordsAdded++;
            minCount = countPair.getValue();
        }

        alphabeticalSorter.sort(alphabeticalOrder);

        //removes all the pairs from alphabeticalSorter and prints them in HTML
        // for the tag cloud and calculates fontSize by using fontSize method

        while (alphabeticalSorter.size() > 0) {
            Map.Entry<String, Integer> wordCount = alphabeticalSorter.remove(0);
            out.println("<span style=\"cursor:default\" class=\"f"
                    + fontSize(maxCount, minCount, wordCount.getValue())
                    + "\" title=\"count: " + wordCount.getValue() + "\">"
                    + wordCount.getKey() + "</span>");
        }

    }

    /**
     * creates a map of words and its count form the given BufferedReader and
     * then prints top words using printTopWords method.
     *
     * @param in
     *            input stream
     * @param out
     *            output stream
     * @param num
     *            the num of words to be included in the tag cloud
     * @throws IOException
     */

    private static void printTagCloud(BufferedReader in, PrintWriter out,
            int num) throws IOException {
        Map<String, Integer> wordCount = fileToMap(in); //creates the word and count map
        printTopWords(out, wordCount, num); //prints the top words

    }

    /**
     * prints final closing tags for the tag cloud by using the given
     * PrintWriter.
     *
     * @param out
     *            output stram
     *
     * @ensures out.content = #out.content * [HTML for last closing tags]
     */

    private static void outputFooter(PrintWriter out) {

        //prints the closing tags
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments; unused here
     */
    public static void main(String[] args) throws InvalidNumException {

        //creates a Bufferedreader to read from console
        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));

        //asks the user for the input file
        //and if an IO error occurs shows the error message and ask for the file again

        boolean inFlag = false;
        BufferedReader read = null;
        String inputFile = "";
        while (!inFlag) {
            try {
                System.out.print("Enter the input file:");
                inputFile = in.readLine();
                read = new BufferedReader(new FileReader(inputFile));
                inFlag = true;
            } catch (IOException e) {
                System.out.println();
                System.out.println("Invalid input file because of error" + e);
            }
        }

        //asks the user for the output file
        //and if an IO error occurs shows the error message and ask for the file again

        boolean outFlag = false;
        PrintWriter write = null;
        while (!outFlag) {
            try {
                System.out.print("Enter the output file:");
                String outputFile = in.readLine();
                write = new PrintWriter(
                        new BufferedWriter(new FileWriter(outputFile)));
                outFlag = true;

            } catch (IOException e) {
                System.out.println();
                System.out.println("Invalid output file because of error" + e);
            }
        }

        //asks the use for the number of words to be printed
        // and if a particular error occurred then shows respective message
        // and ask for the number again
        int numWords = -1;
        boolean numFlag = false;
        while (!numFlag) {
            try {
                System.out.print(
                        "Enter the number of words required in tag cloud:");
                String numString = in.readLine();
                numWords = Integer.parseInt(numString);
                if (numWords < 0) {
                    //throws error when entered number is negative
                    throw new InvalidNumException(
                            "The entered number is negative");

                }
                numFlag = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format");

            } catch (IOException e) {
                System.out.println("Error occured while reading input");
            } catch (InvalidNumException e) {
                System.out.println(e);
            }
        }

        //prints the whole HTML page
        //and if an IO error occurs shows the error message
        try {
            outputHeader(write, inputFile, numWords);
            printTagCloud(read, write, numWords);
            outputFooter(write);
        } catch (IOException e) {
            System.out.print("Error occured while trying to generate output");
        }

        //closes ever IO streams and if error occurred shows the message
        write.close();
        try {
            read.close();
        } catch (IOException e) {

            System.out.print("Erro while closing file");
        }
        try {
            in.close();
        } catch (IOException e) {
            System.out.println("Error while closing console reader");
        }

    }

}
