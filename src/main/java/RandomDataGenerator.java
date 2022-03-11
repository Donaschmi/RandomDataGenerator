import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Random Data Generator intended to create large files of dummy data
 * @author Donatien Schmitz
 */
public class RandomDataGenerator {

    private static final int ROW_SIZE = 1024;
    private static final int BYTES_IN_GB = 1073741824;
    private static final String ROW_TEMPLATE = "%d|%s\n";

    final static public Random RANDOM = new Random(System.currentTimeMillis());
    final static public double BIAS = 1.0;

    // From https://stackoverflow.com/a/13548135
    static public int nextSkewedBoundedDouble(int min, int max, double skew) {
        double range = max - min;
        double mid = min + range / 2.0;
        double unitGaussian = RANDOM.nextGaussian();
        double biasFactor = Math.exp(BIAS);
        double retval = mid+(range*(biasFactor/(biasFactor+Math.exp(-unitGaussian/skew))-0.5));
        return (int) retval;
    }

    public static void generateData(int size, int keys, String path, double skewness) {
        Path file = Paths.get(path);
        try {
            Files.createDirectories(file.getParent());
            BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
            int rows = (BYTES_IN_GB / ROW_SIZE) * size;
            for(int i = 0; i < rows; i++) {
                int key = (keys == -1) ? i : (skewness == 0.0 ? RANDOM.nextInt(keys) : nextSkewedBoundedDouble(0, keys, skewness));
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
        double skewness = 0;

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

            if (line.hasOption("skewness"))
                skewness = parseDouble(line.getOptionValue("skewness"));
        }
        catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        System.out.printf("%d, %d, %s%n", size, keys, dataDir);
        generateData(size, keys, dataDir, skewness);
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

        Option skewness = Option
                .builder("skewness")
                .argName("skewness")
                .hasArg()
                .desc("Skewness, 0 if unskewered data")
                .build();

        Options options = new Options();
        options.addOption(size);
        options.addOption(keys);
        options.addOption(dataDir);
        options.addOption(skewness);

        return options;
    }
}
