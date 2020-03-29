package selenium;

import org.openqa.selenium.By;

import java.util.HashSet;

public class BySet extends HashSet<By> {

    public BySet addTagNames(String... selectors) {
        for (String o : selectors) {
            add(By.tagName(o));
        }
        return this;
    }

    public BySet addClassNames(String... selectors) {
        for (String o : selectors) {
            add(By.className(o));
        }
        return this;
    }

    public BySet addNames(String... selectors) {
        for (String o : selectors) {
            add(By.name(o));
        }
        return this;
    }

    public BySet addIds(String... selectors) {
        for (String o : selectors) {
            add(By.id(o));
        }
        return this;
    }

    public BySet addXpaths(String... selectors) {
        for (String o : selectors) {
            add(By.xpath(o));
        }
        return this;
    }

    public BySet addLinkTexts(String... selectors) {
        for (String o : selectors) {
            add(By.linkText(o));
        }
        return this;
    }

    public BySet addPartialLinkTexts(String... selectors) {
        for (String o : selectors) {
            add(By.partialLinkText(o));
        }
        return this;
    }

    public BySet addCssSelectors(String... selectors) {
        for (String o : selectors) {
            add(By.cssSelector(o));
        }
        return this;
    }

}
