package ui;

import java.io.IOException;
import java.util.Objects;

import main.Main;
import me.tongfei.progressbar.ProgressBar;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleUI implements UI {
    public static ProgressBar pb = new ProgressBar("Test", 100);
    private static final String PATH = "output.csv";
    public static Logger consoleLog = LoggerFactory.getLogger("STDOUT");

    @Argument(required = true)
    private String input;
    @Option(name = "-o")
    private String output;

    public static void main(String[] args) {
        new ConsoleUI().start(args);
    }

    private void start(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            consoleLog.error("CmdLineParser - Failed", e);
            return;
        }
        try {
            Main.start(input, Objects.requireNonNullElse(output, PATH));
        } catch (IOException e) {
            consoleLog.error("Main.start - Failed", e);
            return;
        }
    }
}