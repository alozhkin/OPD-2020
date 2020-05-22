package utils;

import database.models.Website;
import logger.LoggerUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that read CSV file "id";"company_id";"website", and stores content in {@link CSVParser#links}
 * and {@link CSVParser#domainsIds}
 */
public class CSVParser {
    private static final String SEPARATOR = ";";
    private static final String QUOTATION = "\"";
    private final Map<String, Integer> domainsIds = new HashMap<>();
    private final List<Link> links = new ArrayList<>();

    public void parse(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            // skip first line (heading)
            var line = br.readLine();
            while ((line = br.readLine()) != null) {
                convertLineToValues(line);
            }
        }
    }

    public List<Link> getLinks() {
        return links;
    }

    public Map<String, Integer> getDomainsIds() {
        return domainsIds;
    }

    public Collection<Website> getWebsites() {
        Set<Website> set = new HashSet<>();
        for (Map.Entry<String, Integer> entry : domainsIds.entrySet()) {
            try {
                Website website = new Website(entry.getValue(), new Link(entry.getKey()));
                set.add(website);
            } catch (WrongFormedLinkException ignored) {}
        }
        return set;
    }

    private void convertLineToValues(String line) {
        var splitLine = line.split(SEPARATOR);
        var site = splitLine[2].replaceAll(QUOTATION, "");
        try {
            var link = new Link(site);
            links.add(link);
            var id = Integer.valueOf(splitLine[0]);
            domainsIds.put(link.getAbsoluteURL(), id);
        } catch (WrongFormedLinkException e) {
            LoggerUtils.consoleLog.error("CSVParser - Not a site {}", site);
            LoggerUtils.debugLog.error("CSVParser - Not a site {}", site, e);
        }
    }
}
