package ui;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import main.Main;
import me.tongfei.progressbar.ProgressBar;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class ConsoleUI implements UI {
    public static ProgressBar pb = new ProgressBar("Test", 100);
    private static final String PATH = "output.csv";

    @Argument( required = true)
    private String input;

    @Option( name = "-o")
    private String output;


    public static void main(String[] args) {
        new ConsoleUI().start(args);
    }

    private void start(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return;
        }
        Main.start(input, Objects.requireNonNullElse(output, PATH));
    }
}