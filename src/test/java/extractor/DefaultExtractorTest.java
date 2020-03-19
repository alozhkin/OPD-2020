package extractor;

import org.junit.jupiter.api.Test;
import util.HTML;
import util.Link;


import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DefaultExtractorTest {

    Link l = new Link("");
    HTML h = new HTML("<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\t<title>be1.ru</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<blockquote>\n" +
            "<p style=\"text-align:center\"><strong>Простенький html, для теста</strong></p>\n" +
            "\n" +
            "<p>Проверим как он вытащит и отфильтрует все это говно</p>\n" +
            "</blockquote>\n" +
            "\n" +
            "<ol>\n" +
            "\t<li>Список</li>\n" +
            "\t<li>повторяющихся</li>\n" +
            "\t<li>слов</li>\n" +
            "\t<li>список</li>\n" +
            "\t<li>для проверки</li>\n" +
            "\t<li>совпадений</li>\n" +
            "\t<li>слов</li>\n" +
            "\t<li>ыыыыы&nbsp;</li>\n" +
            "</ol>\n" +
            "\n" +
            "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px\">\n" +
            "\t<tbody>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td>Тут еще говно</td>\n" +
            "\t\t\t<td>Прикольно, да?</td>\n" +
            "\t\t</tr>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td>Надо бы еще вставить англ слова</td>\n" +
            "\t\t\t<td>К примеру как</td>\n" +
            "\t\t</tr>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td>gamburger</td>\n" +
            "\t\t\t<td>или что то на немецком</td>\n" +
            "\t\t</tr>\n" +
            "\t</tbody>\n" +
            "</table>\n" +
            "\n" +
            "<p>&nbsp;\n" +
            "<h3>Versuchs- und Lehranstalt<br />\n" +
            "f&uuml;r Brauerei in Berlin (VLB) e.V.</h3>\n" +
            "</p>\n" +
            "</body>\n" +
            "</html>\n", l);


    @Test
    void extractTest() {
        Set<String> expected = Set.of("Простенький", "be1.ru", "html,", "теста",
                "Проверим", "он", "вытащит", "и",
                "отфильтрует", "все", "это", "Список",
                "повторяющихся", "список", "для",
                "проверки", "совпадений", "слов", "ыыыыы", "Тут",
                "говно", "Надо", "бы", "еще",
                "вставить", "англ", "слова", "Прикольно,", "да?",
                "К", "примеру", "как", "gamburger", "или", "что", "то", "на", "немецком",
                "Versuchs-", "und", "Lehranstalt", "für", "Brauerei",
                "in", "Berlin", "(VLB)", "e.V.");


        Collection<String> set = new DefaultExtractor().extract(h);

        assertEquals(expected, set);

    }

}