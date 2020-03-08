package OPD.WebCrawler;

import org.jetbrains.annotations.NotNull;

public class CrawlersFilter {
    public static boolean filter(@NotNull String URL, String rootURL, Links list) {

        if (URL.contains(rootURL))//Сам фильтр URL ссылок с сайта, проверяет прнинадлежит ли сайт к нашему Главному домену
            if (!list.contains(URL)) {//Проверяет на повтор(можно упразнить тк HashSet)
               // System.out.println(URL);
                return true;
            }
        return false;
    }
}

