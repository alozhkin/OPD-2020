package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;

public class CrawlersFilter {
    public static boolean filter(@NotNull String URL, String rootURL) {

        if (URL.contains(rootURL))//Сюда встроить проверку наличия URL в list
            System.out.println(URL);
        return false;
    }
}

