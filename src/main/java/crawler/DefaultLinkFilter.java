package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {
   // static List<String> langList;    static {        try {            langList = new ArrayList<>(Files.readAllLines(Paths.get("src/main/resources/language.txt")));        } catch (IOException e) { e.printStackTrace(); } }

    public Set<Link> filter(@NotNull List<Link> links, String domain) {

        Set<Link> exit = new HashSet<>();
        for (Link unUrl : links) {
            String url = unUrl.fixer();
            if (url.contains(domain) && !url.contains("#")) {
                exit.add(new Link(url.toLowerCase()));
            }
        }
        return exit;
    }


   // public static boolean formatChecker(@NotNull String lang, @NotNull Link url) { ///не работает тк человек не робот но должно убирать страницы на других языках
   //     String actUrl = url.toString() + "/";if (lang.equals("") || !langList.contains(lang)) return true;
   //     for (String actLang : langList) {if (actUrl.contains("/" + actLang + "/") && !actLang.equals(lang)) {
   //     System.out.println(actUrl);return true; }}return true;}
}
