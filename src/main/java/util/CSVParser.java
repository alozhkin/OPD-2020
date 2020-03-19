package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVParser {
    private static final String SEPARATOR = ";";
    private static final String QUOTATION = "\"";
    private Map<String, Integer> domainsIds = new HashMap<>();
    private List<Link> links = new ArrayList<>();

    public void parse(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            var line = br.readLine();
            while ((line = br.readLine()) != null) {
                var splitLine = line.split(SEPARATOR);
                var link = new Link(splitLine[2].replaceAll(QUOTATION, ""));
                links.add(link);
                var id = Integer.valueOf(splitLine[0]);
                domainsIds.put(link.getDomain(), id);
            }
        } catch (IOException e) {
            // TODO нужен logger
            e.printStackTrace();
        }
    }

    public List<Link> getLinks() {
        return links;
    }

    public Map<String, Integer> getDomainsIds() {
        return domainsIds;
    }
}
