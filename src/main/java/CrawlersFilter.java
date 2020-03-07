import org.jetbrains.annotations.NotNull;

public class CrawlersFilter {
    public void filter(@NotNull String URL, String rootURL) {

        if (URL.contains(rootURL))
            //  links.add(actualURL);
            if (temp.link.add(URL))
                System.out.println(URL);
    }
    //  System.out.print(getURLs());
}

