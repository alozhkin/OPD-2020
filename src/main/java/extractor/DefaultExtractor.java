package extractor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.HTML;
import util.Link;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;


public class DefaultExtractor implements Extractor {

   /* public static void main(String[] args) {
        Link l = new Link("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)");
        HTML h = new HTML("<!DOCTYPE html>\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "\t<meta charset=\"utf-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "\t<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                "\t<link rel=\"stylesheet\" href=\"css/style.css\">\n" +
                "\t<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\">\n" +
                "\t<title>Sas&Co Tea</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<div class=\"d-flex flex-column flex-md-row align-items-center p-3 px-md-4 mb-3 bg-white border-bottom shadow-sm\">\n" +
                "  <h5 class=\"my-0 mr-md-auto font-weight-normal\">Sas&Co</h5>\n" +
                "  <nav class=\"my-2 my-md-0 mr-md-3\">\n" +
                "    <a class=\"p-2 text-dark\" href=\"/\">Главная</a>\n" +
                "    <a class=\"p-2 text-dark\" href=\"/about.php\">Контакты</a>\n" +
                "  </nav>\n" +
                "    <a class=\"btn btn-outline-primary\" href=\"/auth.php\">Войти</a>\n" +
                "\t</div>\t<div class=\"container mt-5\">\n" +
                "\t\t<h3 class=\"mb-5\">Выберите свой чай</h3>\n" +
                "\t\t <div class=\"d-flex flex-wrap\">\n" +
                "\n" +
                "\t\t\t<div class=\"card mb-4 shadow-sm\">\n" +
                "\t      <div class=\"card-header\">\n" +
                "\t        <h4 class=\"my-0 font-weight-normal\">Чай с лимоном</h4>\n" +
                "\t      </div>\n" +
                "\t      <div class=\"card-body\">\n" +
                "\t      \t<img src=\"img/1.jpg\" class=\"img-thumbnail\">\n" +
                "\t        <ul class=\"list-unstyled mt-3 mb-4\">\n" +
                "\t          <li>Будоражащий вкус</li>\n" +
                "\t          <li>Уникальная кислинка</li>\n" +
                "\t          <li>+15 к иммунитету</li>\n" +
                "\t          <li>Плед в комплекте</li>\n" +
                "\t        </ul>\n" +
                "\t        <button type=\"button\" class=\"btn btn-lg btn-block btn-outline-primary\">Заказать чай</button>\n" +
                "\t      </div>\n" +
                "\t    </div> <!-- -->\n" +
                "\t    <div class=\"card mb-4 shadow-sm\">\n" +
                "\t      <div class=\"card-header\">\n" +
                "\t        <h4 class=\"my-0 font-weight-normal\">Зеленый чай</h4>\n" +
                "\t      </div>\n" +
                "\t      <div class=\"card-body\">\n" +
                "\t      \t<img src=\"img/2.jpg\" class=\"img-thumbnail\">\n" +
                "\t        <ul class=\"list-unstyled mt-3 mb-4\">\n" +
                "\t          <li>Аромат газона</li>\n" +
                "\t          <li>Бодрит лучше кофе</li>\n" +
                "\t          <li>Пятая чашка бесплатно</li>\n" +
                "\t          <li>Мята по вкусу</li>\n" +
                "\t        </ul>\n" +
                "\t        <button type=\"button\" class=\"btn btn-lg btn-block btn-outline-primary\">Заказать чай</button>\n" +
                "\t      </div>\n" +
                "\t    </div>\n" +
                "\t    <div class=\"card mb-4 shadow-sm\">\n" +
                "\t      <div class=\"card-header\">\n" +
                "\t        <h4 class=\"my-0 font-weight-normal\">Chamaenerion</h4>\n" +
                "\t      </div>\n" +
                "\t      <div class=\"card-body\">\n" +
                "\t      \t<img src=\"img/3.jpg\" class=\"img-thumbnail\">\n" +
                "\t        <ul class=\"list-unstyled mt-3 mb-4\">\n" +
                "\t          <li>Чай из иван-чая</li>\n" +
                "\t          <li>Собран бабушкой</li>\n" +
                "\t          <li>Выдержка 19 лет</li>\n" +
                "\t          <li>Оттенки заката</li>\n" +
                "\t        </ul>\n" +
                "\t        <button type=\"button\" class=\"btn btn-lg btn-block btn-outline-primary\">Заказать чай</button>\n" +
                "\t      </div>\n" +
                "\t    </div> <!-- -->\n" +
                "\t    <div class=\"card mb-4 shadow-sm\">\n" +
                "\t      <div class=\"card-header\">\n" +
                "\t        <h4 class=\"my-0 font-weight-normal\">Китайский 尿液</h4>\n" +
                "\t      </div>\n" +
                "\t      <div class=\"card-body\">\n" +
                "\t      \t<img src=\"img/4.jpg\" class=\"img-thumbnail\">\n" +
                "\t        <ul class=\"list-unstyled mt-3 mb-4\">\n" +
                "\t          <li>Невообразимый 混蛋</li>\n" +
                "\t          <li>Лучший 吹 из всех</li>\n" +
                "\t          <li>Подарит дыхание 耶穌</li>\n" +
                "\t          <li>При заказе 秘密成分 бесплатно!</li>\n" +
                "\t        </ul>\n" +
                "\t        <button type=\"button\" class=\"btn btn-lg btn-block btn-outline-primary\">Заказать чай</button>\n" +
                "\t      </div>\n" +
                "\t    </div> <!-- -->\n" +
                "\t    <div class=\"card mb-4 shadow-sm\">\n" +
                "\t      <div class=\"card-header\">\n" +
                "\t        <h4 class=\"my-0 font-weight-normal\">Гранатовый сок</h4>\n" +
                "\t      </div>\n" +
                "\t      <div class=\"card-body\">\n" +
                "\t      \t<img src=\"img/5.jpg\" class=\"img-thumbnail\">\n" +
                "\t        <ul class=\"list-unstyled mt-3 mb-4\">\n" +
                "\t          <li>Освежает до упаду</li>\n" +
                "\t          <li>Ягодный взрыв</li>\n" +
                "\t          <li>Кладезь железа</li>\n" +
                "\t          <li>Что он тут делает?</li>\n" +
                "\t        </ul>\n" +
                "\t        <button type=\"button\" class=\"btn btn-lg btn-block btn-outline-primary\">Заказать чай</button>\n" +
                "\t      </div>\n" +
                "\t    </div> <!-- -->\n" +
                "\n" +
                "\n" +
                "\t</div>\n" +
                "\t</div>\n" +
                "\t<footer class=\"container pt-4 my-md-5 pt-md-5 border-top\">\n" +
                "    <div class=\"row\">\n" +
                "      <div class=\"col-12 col-md\">\n" +
                "        <img class=\"mb-2\" src=\"/docs/4.4/assets/brand/bootstrap-solid.svg\" alt=\"\" width=\"24\" height=\"24\">\n" +
                "        <small class=\"d-block mb-3 text-muted\">© 2020</small>\n" +
                "      </div>\n" +
                "      <div class=\"col-6 col-md\">\n" +
                "        <h5>Features</h5>\n" +
                "        <ul class=\"list-unstyled text-small\">\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Cool stuff</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Random feature</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Team feature</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Stuff for developers</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Another one</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Last time</a></li>\n" +
                "        </ul>\n" +
                "      </div>\n" +
                "      <div class=\"col-6 col-md\">\n" +
                "        <h5>Resources</h5>\n" +
                "        <ul class=\"list-unstyled text-small\">\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Resource</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Resource name</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Another resource</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Final resource</a></li>\n" +
                "        </ul>\n" +
                "      </div>\n" +
                "      <div class=\"col-6 col-md\">\n" +
                "        <h5>About</h5>\n" +
                "        <ul class=\"list-unstyled text-small\">\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Team</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Locations</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Privacy</a></li>\n" +
                "          <li><a class=\"text-muted\" href=\"#\">Terms</a></li>\n" +
                "        </ul>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </footer><div style=\"text-align: right;position: fixed;z-index:9999999;bottom: 0;width: auto;right: 1%;cursor: pointer;line-height: 0;display:block !important;\"><a title=\"Hosted on free web hosting 000webhost.com. Host your own website for FREE.\" target=\"_blank\" href=\"https://www.000webhost.com/?utm_source=000webhostapp&utm_campaign=000_logo&utm_medium=website&utm_content=footer_img\"><img src=\"https://cdn.000webhost.com/000webhost/logo/footer-powered-by-000webhost-white2.png\" alt=\"www.000webhost.com\"></a></div><script>function getCookie(t){for(var e=t+\"=\",n=decodeURIComponent(document.cookie).split(\";\"),o=0;o<n.length;o++){for(var a=n[o];\" \"==a.charAt(0);)a=a.substring(1);if(0==a.indexOf(e))return a.substring(e.length,a.length)}return\"\"}getCookie(\"hostinger\")&&(document.cookie=\"hostinger=;expires=Thu, 01 Jan 1970 00:00:01 GMT;\",location.reload());var wordpressAdminBody=document.getElementsByClassName(\"wp-admin\")[0],notification=document.getElementsByClassName(\"notice notice-success is-dismissible\"),hostingerLogo=document.getElementsByClassName(\"hlogo\"),mainContent=document.getElementsByClassName(\"notice_content\")[0],wpSidebar=document.getElementById(\"adminmenuwrap\"),wpTopBarRight=document.getElementById(\"wp-admin-bar-top-secondary\");if(null!=wordpressAdminBody&&notification.length>0&&null!=mainContent){var googleFont=document.createElement(\"link\");googleFontHref=document.createAttribute(\"href\"),googleFontRel=document.createAttribute(\"rel\"),googleFontHref.value=\"https://fonts.googleapis.com/css?family=Roboto:300,400,600\",googleFontRel.value=\"stylesheet\",googleFont.setAttributeNode(googleFontHref),googleFont.setAttributeNode(googleFontRel);var css=\"@media only screen and (max-width: 576px) {#main_content {max-width: 320px !important;} #main_content h1 {font-size: 30px !important;} #main_content h2 {font-size: 40px !important; margin: 20px 0 !important;} #main_content p {font-size: 14px !important;} #main_content .content-wrapper {text-align: center !important;}} @media only screen and (max-width: 781px) {#main_content {margin: auto; justify-content: center; max-width: 445px;} .upgrade-btn-sidebar {display: none;} #wp-toolbar .top-bar-upgrade-btn {width: 52px; height: 46px !important; padding: 0 !important;} .top-bar-upgrade-btn__text {display: none;} .dashicons-star-filled.top-bar-upgrade-btn__icon::before {font-size: 28px; margin-top: 10px; width: 28px; height: 28px;}} @media only screen and (max-width: 1325px) {.web-hosting-90-off-image-wrapper {position: absolute; max-width: 95% !important;} .notice_content {justify-content: center;} .web-hosting-90-off-image {opacity: 0.3;}} @media only screen and (min-width: 769px) {.notice_content {justify-content: space-between;} #main_content {margin-left: 5%; max-width: 445px;} .web-hosting-90-off-image-wrapper {position: absolute; right: 0; display: flex; padding: 0 5%}} @media only screen and (max-width: 960px) {.upgrade-btn-sidebar {border-radius: 0 !important; padding: 10px 0 !important; margin: 0 !important;} .upgrade-btn-sidebar__icon {display: block !important; margin: auto;} .upgrade-btn-sidebar__text {display: none;}}  .web-hosting-90-off-image {max-width: 90%; margin-top: 20px;} .content-wrapper {z-index: 5} .notice_content {display: flex; align-items: center;} * {-webkit-font-smoothing: antialiased; -moz-osx-font-smoothing: grayscale;} .upgrade_button_red_sale{box-shadow: 0 2px 12px -6px #cc292f; max-width: 350px; border: 0; border-radius: 3px; background-color: #6747c7 !important; padding: 15px 55px !important;  margin-bottom: 48px; font-size: 14px; font-weight: 800; color: #ffffff;} .upgrade_button_red_sale:hover{color: #ffffff !important; background: rgba(103,71,199, 0.9) !important;} .upgrade-btn-sidebar {text-align:center;background-color:#ff4546;max-width: 350px;border-radius: 3px;border: 0;padding: 12px; margin: 20px 10px;display: block; font-size: 12px;color: #ffffff;font-weight: 700;text-decoration: none;} .upgrade-btn-sidebar:hover, .upgrade-btn-sidebar:focus, .upgrade-btn-sidebar:active {background-color: rgba(255,69,70, 0.9); color: #ffffff;} .upgrade-btn-sidebar__icon {display: none;} .top-bar-upgrade-btn {height: 100% !important; display: inline-block !important; padding: 0 10px !important; color: #ffffff; cursor: pointer;} .top-bar-upgrade-btn:hover, .top-bar-upgrade-btn:active, .top-bar-upgrade-btn:focus {background-color: #ff4546 !important; color: #ffffff !important;} .top-bar-upgrade-btn__icon {margin-right: 6px;}\",style=document.createElement(\"style\"),sheet=window.document.styleSheets[0];style.styleSheet?style.styleSheet.cssText=css:style.appendChild(document.createTextNode(css)),document.getElementsByTagName(\"head\")[0].appendChild(style),document.getElementsByTagName(\"head\")[0].appendChild(googleFont);var button=document.getElementsByClassName(\"upgrade_button_red\")[0],link=button.parentElement;link.setAttribute(\"href\",\"https://www.hostinger.com/hosting-starter-offer?utm_source=000webhost&utm_medium=panel&utm_campaign=000-wp\"),link.innerHTML='<button class=\"upgrade_button_red_sale\">Upgrade Now</button>',(notification=notification[0]).setAttribute(\"style\",\"background-color: #f8f8f8; border-left-color: #6747c7 !important;\"),notification.className=\"notice notice-error is-dismissible\";var mainContentHolder=document.getElementById(\"main_content\");mainContentHolder.setAttribute(\"style\",\"padding: 0;\"),hostingerLogo[0].remove();var h1Tag=notification.getElementsByTagName(\"H1\")[0];h1Tag.className=\"000-h1\",h1Tag.innerHTML=\"Limited Time Offer\",h1Tag.setAttribute(\"style\",\"color: #32454c;  margin-top: 48px; font-size: 48px; font-weight: 700;\");var h2Tag=document.createElement(\"H2\");h2Tag.innerHTML=\"From $0.79/month\",h2Tag.setAttribute(\"style\",\"color: #32454c; margin: 20px 0 45px 0; font-size: 48px; font-weight: 700;\"),h1Tag.parentNode.insertBefore(h2Tag,h1Tag.nextSibling);var paragraph=notification.getElementsByTagName(\"p\")[0];paragraph.innerHTML=\"Don’t miss the opportunity to enjoy up to <strong>4x WordPress Speed, Free SSL and all premium features</strong> available for a fraction of the price!\",paragraph.setAttribute(\"style\",'font-family: \"Roboto\", sans-serif; font-size: 18px; font-weight: 300; color: #6f7c81; margin-bottom: 20px;');var list=notification.getElementsByTagName(\"UL\")[0];list.remove();var org_html=mainContent.innerHTML,new_html='<div class=\"content-wrapper\">'+mainContent.innerHTML+'</div><div class=\"web-hosting-90-off-image-wrapper\"><img class=\"web-hosting-90-off-image\" src=\"https://cdn.000webhost.com/000webhost/promotions/wp-inject-default-img.png\"></div>';mainContent.innerHTML=new_html;var saleImage=mainContent.getElementsByClassName(\"web-hosting-90-off-image\")[0];wpSidebar.insertAdjacentHTML(\"beforeend\",'<a href=\"https://www.hostinger.com/hosting-starter-offer?utm_source=000webhost&amp;utm_medium=panel&amp;utm_campaign=000-wp-sidebar\" target=\"_blank\" class=\"upgrade-btn-sidebar\"><span class=\"dashicons dashicons-star-filled upgrade-btn-sidebar__icon\"></span><span class=\"upgrade-btn-sidebar__text\">Upgrade</span></a>'),wpTopBarRight.insertAdjacentHTML(\"beforebegin\",'<a class=\"top-bar-upgrade-btn\" href=\"https://www.hostinger.com/hosting-starter-offer?utm_source=000webhost&amp;utm_medium=panel&amp;utm_campaign=000-wp-top-bar\" target=\"_blank\"><span class=\"ab-icon dashicons-before dashicons-star-filled top-bar-upgrade-btn__icon\"></span><span class=\"top-bar-upgrade-btn__text\">Go Premium</span></a>')}</script><script type=\"text/javascript\" src=\"https://a.opmnstr.com/app/js/api.min.js\" data-campaign=\"f6brbmuxflyqoriatchv\" data-user=\"71036\" async></script></body>\n" +
                "</html>",l);
       HashSet setOfWords = (HashSet) new DefaultExtractor().extract(h);
       System.out.println(setOfWords);
    }*/


    public HashSet<String> extract(HTML html) {

        Document doc = Jsoup.parse(html.toString());

        //doc = Jsoup.parse( new File("D:\\Users\\Kazuru\\Downloads\\Sas&Co Tea.html"), "windows-1251", html.getUrl().toString());
        //doc = Jsoup.connect("https://ru.wikipedia.org/wiki/%D0%AF%D0%B4%D0%BE%D0%B2%D0%B8%D1%82%D1%8B%D0%B9_%D0%BF%D0%BB%D1%8E%D1%89_(%D1%84%D0%B8%D0%BB%D1%8C%D0%BC,_1992)").get();

        String allInfo = doc.text();

        System.out.println(allInfo);

        String[] stringsArray;

        stringsArray = allInfo.split("\\s");
        // System.out.println(setOfWords);

        return new HashSet<>(Arrays.asList(stringsArray));
    }


}
