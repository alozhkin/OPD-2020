package selenium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class BySetTest {

    @Test
    public void testBuilder() {
        BySet bySet = new BySet();
        bySet.builder()
                .addTagNames("button", "div", "a")
                .addClassNames("button", "someClass", "click")
                .addIds("19234", "13424", "12435", "13342", "18378", "12223");
        Assertions.assertEquals(12, bySet.size());
    }

    @Test
    public void testElementHasRightInstance() {
        BySet bySet = new BySet();
        bySet.builder().addClassNames("111", "222", "333");
        Assertions.assertEquals(By.ByClassName.class, bySet.iterator().next().getClass());
    }

}
