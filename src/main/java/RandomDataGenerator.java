import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static java.lang.Integer.parseInt;

/**
 * Random Data Generator intended to create large files of dummy data
 * @author Donatien Schmitz
 */
public class RandomDataGenerator {

    private static final int ROW_SIZE = 1024;
    private static final int BYTES_IN_GB = 1073741824;
    private static final String ROW_TEMPLATE = "%d|%s\n";

    public static void generateData(String path, int size, int keys) {
        Path file = Paths.get(path);
        try {
            Files.createDirectories(file.getParent());
            BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
            int rows = (BYTES_IN_GB / ROW_SIZE) * size;
            Random rn = new Random();
            for(int i = 0; i < rows; i++) {
                int key = (keys == -1) ? i : rn.nextInt(keys);
                writer.write(String.format(ROW_TEMPLATE, key, RandomStringUtils.randomAlphabetic(ROW_SIZE)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int size = 1;
        int keys = 10;
        String dataDir = "/tmp/table.dat";

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options(), args);

            if (line.hasOption("size"))
                size = parseInt(line.getOptionValue("size"));

            if (line.hasOption("keys"))
                keys = parseInt(line.getOptionValue("keys"));

            if (line.hasOption("dataDir"))
                dataDir = line.getOptionValue("dataDir");
        }
        catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        System.out.printf("%d, %d, %s%n", size, keys, dataDir);
        generateData(dataDir, size, keys);
    }

    public static Options options() {
        Option size = Option
                .builder("size")
                .argName("size")
                .hasArg()
                .desc("dataset size")
                .build();

        Option keys = Option
                .builder("keys")
                .argName("keys")
                .hasArg()
                .desc("# of different keys (not guaranteed)")
                .build();

        Option dataDir = Option
                .builder("dataDir")
                .argName("dataDir")
                .hasArg()
                .desc("Path to data dir")
                .build();

        Options options = new Options();
        options.addOption(size);
        options.addOption(keys);
        options.addOption(dataDir);

        return options;
    }
}
