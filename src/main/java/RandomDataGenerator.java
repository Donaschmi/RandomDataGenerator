import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.RandomStringUtils;

import static java.lang.Integer.parseInt;

public class RandomDataGenerator {

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
