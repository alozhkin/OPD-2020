package ui;

import java.util.Objects;

import main.Main;
import me.tongfei.progressbar.ProgressBar;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import static logger.LoggerUtils.consoleLog;
import static logger.LoggerUtils.debugLog;

public class ConsoleUI {
    public static ProgressBar pb = new ProgressBar("Test", 100);
    private static final String OUTPUT_PATH = "output.csv";

    @Argument(required = true)
    private String input;
    @Option(name = "-o")
    private String output;

    public static void main(String[] args) {
        new ConsoleUI().start(args);
        pb.close();
    }

    private void start(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            debugLog.error("ConsoleUI - Failed in Cmd line parser: ", e);
            consoleLog.error("ConsoleUI - Failed in Cmd line parser: ", e);
            return;
        }
        Main.start(input, Objects.requireNonNullElse(output, OUTPUT_PATH));
    }
}