package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;

public class CrawlersFilter {
    public static boolean filter(@NotNull String URL, String rootURL, Links list) {

        if (URL.contains(rootURL))//нужно ли +
            if (!list.contains(URL)) {
               // System.out.println(URL);
                return true;
            }
        return false;
    }
}

