package selenium;

import org.openqa.selenium.By;

import java.util.HashSet;

public class BySet extends HashSet<By> {

    public class Builder {

        public Builder addTagNames(Object... selectors){
            for (Object o : selectors) {
                add(By.tagName((String) o));
            }
            return this;
        }

        public Builder addClassNames(Object... selectors) {
            for (Object o : selectors) {
                add(By.className((String) o));
            }
            return this;
        }

        public Builder addNames(Object... selectors) {
            for (Object o : selectors) {
                add(By.name((String) o));
            }
            return this;
        }

        public Builder addIds(Object... selectors) {
            for (Object o : selectors) {
                add(By.id((String) o));
            }
            return this;
        }

        public Builder addXpaths(Object... selectors) {
            for (Object o : selectors) {
                add(By.xpath((String) o));
            }
            return this;
        }

        public Builder addLinkTexts(Object... selectors) {
            for (Object o : selectors) {
                add(By.linkText((String) o));
            }
            return this;
        }

        public Builder addPartialLinkTexts(Object... selectors) {
            for (Object o : selectors) {
                add(By.partialLinkText((String) o));
            }
            return this;
        }

        public Builder addCssSelectors(Object... selectors) {
            for (Object o : selectors) {
                add(By.cssSelector((String) o));
            }
            return this;
        }
    }

    public Builder getBuilder(){
        return new Builder();
    }

}
