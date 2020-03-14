package extractor;

import util.Link;
import util.HTML;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DefaultWordFilter implements WordFilter {

     public static void main(String[] args) {
        Link l = new Link("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)");
        HTML h = new HTML("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
                "<html><head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\">\n" +
                "\n" +
                "<title>Сервисный центр по ремонту мобильной электроники \"РсТ\"</title><meta name=\"description\" content=\"Ремонт сотовых телефонов\">\n" +
                "<meta name=\"keywords\" content=\"ремонт, сотовые\">\n" +
                "<link href=\"css/style.css\" rel=\"stylesheet\" type=\"text/css\"></head><body>\n" +
                "<table bgcolor=\"#999999\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td class=\"base-l-bg\" align=\"center\" background=\"images/base-l-bg.jpg\" valign=\"top\">&nbsp;</td>\n" +
                "<td align=\"center\" valign=\"top\" width=\"750\"><table bgcolor=\"#405688\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"750\">\n" +
                "<tbody><tr>\n" +
                "<td height=\"1\" valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td width=\"1\"><img src=\"images/ml.jpg\" alt=\"\" height=\"30\" width=\"141\"></td>\n" +
                "<td class=\"menu\"><a href=\"index.html\" class=\"menu_lnk\">Главная</a></td>\n" +
                "<td class=\"menu\"><img src=\"images/m-sep.gif\" alt=\"\" height=\"30\" width=\"8\"></td>\n" +
                "<td class=\"menu\"><a href=\"Service.html\" class=\"menu_lnk\">Сервисный центр</a></td>\n" +
                "<td class=\"menu\"><img src=\"images/m-sep.gif\" alt=\"\" height=\"30\" width=\"8\"></td>\n" +
                "<td class=\"menu\"><a href=\"Klient.html\" class=\"menu_lnk\">В помощь клиенту</a></td>\n" +
                "<td class=\"menu\"><img src=\"images/m-sep.gif\" alt=\"\" height=\"30\" width=\"8\"></td>\n" +
                "<td class=\"menu\"><a href=\"price.html\" class=\"menu_lnk\">Прайс лист на услуги</a></td>\n" +
                "<td class=\"menu\"><img src=\"images/m-sep.gif\" alt=\"\" height=\"30\" width=\"8\"></td>\n" +
                "<td class=\"menu\"><a href=\"contact.html\" class=\"menu_lnk\">Наши координаты</a></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td height=\"1\" valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td width=\"1\"><img src=\"images/t1.jpg\" alt=\"\" height=\"166\" width=\"132\"></td>\n" +
                "<td class=\"norepeat\" background=\"images/t3.jpg\" valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td><img src=\"images/spacer.gif\" alt=\"\" height=\"32\" width=\"1\"></td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td width=\"1\"><img src=\"images/spacer.gif\" alt=\"\" height=\"1\" width=\"150\"></td>\n" +
                "<td width=\"1\"><img src=\"images/spacer.gif\" alt=\"\" height=\"1\" width=\"56\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"c_name\">Сервисный центр  \"РсТ\" &nbsp; (41147) 4-04-19</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td><img src=\"images/spacer.gif\" alt=\"\" height=\"62\" width=\"1\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td class=\"search\">ВВЕДИТЕ НОМЕР КВИТАНЦИИ</td>\n" +
                "<td>&nbsp;</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td>&nbsp;</td>\n" +
                "<td>&nbsp;</td>\n" +
                "<td class=\"find\"><input name=\"textfield\" class=\"find\" style=\"width: 148px; background-color: rgb(194, 200, 217); height: 20px;\" value=\"\" type=\"text\"></td>\n" +
                "<td align=\"center\"><a href=\"#\"><img src=\"images/btn-go.gif\" alt=\"Go!\" border=\"0\" height=\"22\" width=\"32\"></a></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"bgx\" background=\"images/c-bg.jpg\" height=\"100%\" valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td valign=\"top\" width=\"1\"><img src=\"images/c1.jpg\" alt=\"\" height=\"121\" width=\"164\"></td>\n" +
                "<td valign=\"top\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td class=\"welcome\" valign=\"top\">Прайс лист на услуги:</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"body_txt\" height=\"100%\" valign=\"top\"><p>\n" +
                "<script type=\"text/javascript\"><!--\n" +
                "google_ad_client = \"pub-0500162490295296\";\n" +
                "google_ad_width = 468;\n" +
                "google_ad_height = 60;\n" +
                "google_ad_format = \"468x60_as\";\n" +
                "google_ad_type = \"text\";\n" +
                "//2006-11-21: Web templates - LAYOUTS\n" +
                "google_ad_channel = \"0286771451\";\n" +
                "google_color_border = \"405688\";\n" +
                "google_color_bg = \"1e2541\";\n" +
                "google_color_link = \"FFFFFF\";\n" +
                "google_color_text = \"FFFFFF\";\n" +
                "google_color_url = \"B3B3B3\";\n" +
                "//--></script>\n" +
                "\n" +
                "<table class=\"body_txt\" border=\"1\">\n" +
                "\t<caption>Диагностика при согласии на ремонт бесплатно.<p>&nbsp;</p></caption>\n" +
                "\t\t\t<col width=\"80%\">\n" +
                "\t<col width=\"20%\">\n" +
                "\t<tbody><tr>\n" +
                "\t\t<td class=\"head\"><b>Описание</b></td>\n" +
                "\t\t<td class=\"head\"><b>Цена</b></td>\n" +
                "\t</tr>\n" +
                "\t\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Техническая экспертиза состояния устройства с выдачей акта (для юридических лиц, для физических лиц-по запросу организаций)</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">300</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Диагностика устройства без выдачи технического заключения на бумажном носителе</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">150</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t<td colspan=\"2\" class=\"title\"><b>Вид ремонта</b></td>\n" +
                "\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Восстановление устройства после попадания токопроводящих жидкостей</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">от 800</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Восстановление\n" +
                "пайки БИС, включая регулировку и восстановление радиоблока, работа по замене\n" +
                "дискретных электронных компонентов, аналоговой и цифровой части платы.\n" +
                "</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">от 800</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Замена\n" +
                "корпусных деталей, промывка и очистка внешних контактов,замена уплотнительных элементов.</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">от 500</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Замена микросхем радиоблока, аналоговой и цифровой части платы трансивера, восстановление проводников печатной платы.</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">от 1000</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Замена \n" +
                "электромеханических элементов и механических (микрофоны, динамики,\n" +
                "предохранители и пр.), регулировка трансивера, восстановление пайки\n" +
                " дискретных компонентов,</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">от 900</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t<tr>\n" +
                "\t\t\t\t<td class=\"name\">Устранение сбоя, обновление программного обеспечения устройства, русификация, снятие установленных блокировок .</td>\n" +
                "\t\t\t\t<td class=\"sale\" align=\"center\">1000-3000</td>\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\t\t\t</tbody></table>\n" +
                "</p><p>&nbsp;</p>\n" +
                "<p>Стоимость ремонта на модели не указанные в прайс-листе можно узнать\n" +
                "по телефону (41147)&nbsp;4-04-19 или после\n" +
                "диагностики сотового телефона.</p>\n" +
                "\n" +
                "\t\t\t<br><br><br><br><br>\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\n" +
                "\n" +
                "<p>&nbsp;</p></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "<td height=\"100%\" valign=\"top\" width=\"1\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td class=\"norepeat\" background=\"images/r-top-bg.gif\" height=\"1\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td width=\"1\"><img src=\"images/spacer.gif\" alt=\"\" height=\"32\" width=\"1\"></td>\n" +
                "<td class=\"right-head\">Горячие Новости!</td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"r-text\" background=\"images/r-bg.gif\" height=\"100%\"><div align=\"center\"><p>Ожидается большое поступление аксессуаров к сотовым телефонам. Аккумуляторы, зарядные устройства и т.д.</p></div>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td><img src=\"images/r-bot-bg.gif\" alt=\"\" height=\"21\" width=\"169\"></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td class=\"bottom_menu\" bgcolor=\"#2f3e65\" valign=\"top\">© 2009-2018 Copyright. All Rights Reserved</td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "<td class=\"base-r-bg\" align=\"center\" valign=\"top\">&nbsp;</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "</body></html>",l);
       HashSet setOfWords = (HashSet) new DefaultExtractor().extract(h);
       System.out.println(setOfWords);
         try {
             new DefaultWordFilter().filter(setOfWords);
         } catch (IOException e) {
             e.printStackTrace();
         }
         Collection newSet = null;
         try {
             newSet = new DefaultWordFilter().filter(setOfWords);
         } catch (IOException e) {
             e.printStackTrace();
         }
         System.out.println(newSet);

    }

   /*public static void main(String[] args) throws IOException {
        HashSet set = new HashSet<String>();
        set.add("галлаграфический:");
        set.add("on");
        set.add("жопе,");
        set.add("in");
        set.add("Кемрово.");
        set.add("");
        String s = "Кола,";
        new DefaultWordFilter().filter(set);
        Collection newSet = new DefaultWordFilter().filter(set);
        System.out.println(set);
        System.out.println(newSet);
    }*/

    @Override
    public Collection<String> filter(HashSet<String> words) throws IOException {
        HashSet newSet = PunctuationMarkFilter(words);
        UnnecessaryWordsFilter(newSet);
        DeleteBlankLines(newSet);
        return newSet;
    }


    // Фильтр ненужных слов
    // Кпд данного метода не определен из-за кодировок

    public static void UnnecessaryWordsFilter(HashSet<String> set) throws IOException {
        String fileName = "src\\main\\resources\\ListOfWordsForFiltration.txt";

        String content = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).reduce("", String::concat);

        String[] stringsArray = content.split("\\s");

        HashSet<String> filterWords = new HashSet<> (Arrays.asList(stringsArray));

        set.removeAll(filterWords);

    }

    //Удаление пустого элемента
    public static void DeleteBlankLines(HashSet<String> set) {
        set.removeIf(String::isEmpty);
    }

    /*Iterator<Integer> iterator = set.iterator();

        while(iterator.hasNext())
    {

        if (iterator.next()>10) iterator.remove();
    }*/

    //Фильтр знаков препинания

    public static HashSet<String> PunctuationMarkFilter(HashSet<String> set) throws IOException {
        HashSet<String> newSet = new HashSet<>();
        for (String setObj : set)
        newSet.add(delNoDigOrLet(setObj));
        return newSet;
    }

    // Метод удаления знаков препинания из строки
    // Кпд данного метода не определен из-за кодировок

    private static String delNoDigOrLet (String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (Character .isLetterOrDigit(s.charAt(i)))
                sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    //Удаляет умляубля (äöü)
    // String result = s.replaceAll("\\W", "");

}
