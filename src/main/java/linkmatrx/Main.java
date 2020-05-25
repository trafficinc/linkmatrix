package linkmatrx;

import java.io.File;

import org.apache.commons.cli.*;

import linkmatrx.ConfigManager;

public class Main {

    public static void main(String[] args) {
        Options options = new Options();

        Option mode = new Option("m", "mode", true, "crawl mode");
        mode.setRequired(true);
        options.addOption(mode);

        Option url = new Option("u", "url", true, "url");
        url.setRequired(false);
        options.addOption(url);

        Option depth = new Option("d", "depth", true, "depth");
        url.setRequired(false);
        options.addOption(depth);

        // output types: console, text, csv, json
        Option output = new Option("o", "output", true, "output type");
        output.setRequired(false);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }


        ConfigManager.load();

        if (Main.checkReportsDir()) {

            String urlFlagPath = cmd.getOptionValue("url");
            String outputFlagPath = cmd.getOptionValue("output");
            String crawlDepth = cmd.getOptionValue("depth");

            int cdepth = 1;
            if (crawlDepth != null) {
                cdepth = Integer.parseInt(crawlDepth);
            }


            int docsv = 0;
            if (outputFlagPath.contentEquals("csv")) {
                docsv = 1;
            }


            new InputControl(urlFlagPath, docsv, cdepth);
        } else {
            System.out.println("Cannot create 'reports' DIR");
            Logging.log("Cannot create 'reports' DIR");
            System.exit(0);
        }

    }


    private static boolean checkReportsDir() {
        String dirPath = "./reports/";
        File file = new File(dirPath);

        if (file.isDirectory()) {
            return true;
        } else {
            File reportsDir = new File("./reports");
            if (!reportsDir.exists()) {
                if (reportsDir.mkdir()) {
                    return true;
                } else {
                    Logging.log("Failed to create directory!");
                    return false;
                }
            }
        }
        return false;
    }

}
