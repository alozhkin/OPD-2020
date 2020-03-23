package selenium;

import org.openqa.selenium.By;
import java.util.HashSet;

public class BySet extends HashSet<By> {

    public class Builder {

        public Builder addTagNames(String... selectors){
            for (String o : selectors) {
                add(By.tagName(o));
            }
            return this;
        }

        public Builder addClassNames(String... selectors) {
            for (String o : selectors) {
                add(By.className(o));
            }
            return this;
        }

        public Builder addNames(String... selectors) {
            for (String o : selectors) {
                add(By.name(o));
            }
            return this;
        }

        public Builder addIds(String... selectors) {
            for (String o : selectors) {
                add(By.id(o));
            }
            return this;
        }

        public Builder addXpaths(String... selectors) {
            for (String o : selectors) {
                add(By.xpath(o));
            }
            return this;
        }

        public Builder addLinkTexts(String... selectors) {
            for (String o : selectors) {
                add(By.linkText(o));
            }
            return this;
        }

        public Builder addPartialLinkTexts(String... selectors) {
            for (String o : selectors) {
                add(By.partialLinkText(o));
            }
            return this;
        }

        public Builder addCssSelectors(String... selectors) {
            for (String o : selectors) {
                add(By.cssSelector(o));
            }
            return this;
        }
    }

    public Builder getBuilder(){
        return new Builder();
    }

}
