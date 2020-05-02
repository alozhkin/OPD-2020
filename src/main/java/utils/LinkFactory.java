package utils;

import java.util.HashMap;

public class LinkFactory {
    private static int id = -1;
    private static final HashMap<Link, Integer> domains = new HashMap<>();

    public static int getDomainId(Link domain) {
        if (domains.containsKey(domain)) {
            return domains.get(domain);
        } else {
            domains.put(domain, ++id);
            return id;
        }
    }
}
