package utils;

import database.models.Website;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CSVParser {
    private static final String SEPARATOR = ";";
    private static final String QUOTATION = "\"";
    private final Map<String, Integer> domainsIds = new HashMap<>();
    private final List<Link> links = new ArrayList<>();

    public void parse(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            var line = br.readLine();
            while ((line = br.readLine()) != null) {
                var splitLine = line.split(SEPARATOR);
                var link = new Link(splitLine[2].replaceAll(QUOTATION, ""));
                links.add(link);
                var id = Integer.valueOf(splitLine[0]);
                domainsIds.put(link.getAbsoluteURL(), id);
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
        return domainsIds
                .entrySet()
                .stream()
                .map(entry -> new Website(entry.getValue(), new Link(entry.getKey())))
                .collect(Collectors.toSet());
    }
}
