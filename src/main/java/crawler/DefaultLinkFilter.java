package crawler;

import org.jetbrains.annotations.NotNull;
import utils.Link;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultLinkFilter implements LinkFilter {
   // List<String> fileDate;
    //List<String> langDate;

   // {     try {
   //       //langDate = new ArrayList<>(Files.readAllLines(Paths.get("src/main/resources/language.txt")));
   //         fileDate = new ArrayList<>(Files.readAllLines(Paths.get("src/main/resources/FilesTeg.txt")));
   //     } catch (IOException e) {
   //         e.printStackTrace();}}

    public Set<Link> filter(@NotNull List<Link> links, String domain) {

        Set<Link> exit = new HashSet<>();
        for (Link unUrl : links) {
            String url = fixer(unUrl);
            if (url.contains(domain) && !url.contains("#")) {
                if (true) {
                    exit.add(new Link(url));
                }
            }
        }
        return exit;
    }

    public String fixer(@NotNull Link url) {
        if (url.toString().charAt(url.toString().length() - 1) == '/') return url.toString();
        return url.toString() + '/';
    }

    // public boolean formatChecker(@NotNull String url, String domain) {
    //     String test = url.replaceAll(domain, "");
    //     if (test.equals(""))
    //         return false;
    //     if (test.contains(".")) {
    //         if (fileDate.contains(test)) {
    //             return true;
    //         } else return false;
    //     }
    //     return true;
    // }
}
