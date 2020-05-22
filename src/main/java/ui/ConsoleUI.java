package ui;

import java.util.Collection;
import java.util.Objects;

import main.Main;
import me.tongfei.progressbar.ProgressBar;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import spider.OnSpiderChangesListener;
import utils.Link;

import static logger.LoggerUtils.*;

public class ConsoleUI implements OnSpiderChangesListener {
    private static ProgressBar pb;
    private static final String OUTPUT_PATH = "output.csv";

    @Argument(required = true)
    private String input;
    @Option(name = "-o")
    private String output;

    public static void main(String[] args) {
        new ConsoleUI().start(args);
    }

    private void start(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        consoleLog = getUILogger();
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            debugLog.error("ConsoleUI - Failed in Cmd line parser: ", e);
            consoleLog.error("ConsoleUI - Failed in Cmd line parser: ", e);
            return;
        }
        Main.start(input, Objects.requireNonNullElse(output, OUTPUT_PATH), this);
    }

    @Override
    public void onDomainsParsed(Collection<Link> domains) {
        pb = new ProgressBar("Words_Extractor", domains.size() + 1);
    }

    @Override
    public void onDomainScraped() {
        pb.step();
    }

    @Override
    public void onDataExported() {
        pb.step();
    }

    @Override
    public void onFinished() {
        pb.close();
    }
}