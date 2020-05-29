package splash;

import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Base64;

/**
 * Splash is very flexible, because it allows to set settings with every request.
 * DefaultSplashRequestFactory tune splash to be good at extracting words.
 * It sends requests to /run, with custom script. With help of filters it ignores images, css, analytics.
 * Response status code depends on site status code
 */
public class DefaultSplashRequestFactory implements SplashRequestFactory {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String luaScript = "function main(splash, args)\n" +
            "    splash.webgl_enabled = false\n" +
            "    splash.media_source_enabled = false\n" +
            "    goToPage(splash, args.url)\n" +
            "    return scrape_page(splash)\n" +
            "end\n" +
            "\n" +
            "function goToPage(splash, url_to_go)\n" +
            "    local ok, reason = splash:go(url_to_go)\n" +
            "    if not ok then\n" +
            "        local url_without_protocol = get_url_without_protocol(url_to_go)\n" +
            "        if reason:sub(0,4) == 'http' then\n" +
            "        local code = tonumber(reason:sub(5))\n" +
            "            if code == 503 or code == 502 then\n" +
            "                splash:set_result_header(\"Retry-After\", \"-1\")\n" +
            "            end\n" +
            "        splash:set_result_status_code(code)\n" +
            // sometimes No address associated with host name without www
            "        elseif reason == \"network3\" and url_without_protocol:sub(0, 4) ~= \"www.\" then\n" +
            "            goToPage(splash, \"http://www.\" .. url_without_protocol)\n" +
            // network301 https://github.com/scrapinghub/splash/issues/896
            "        elseif reason ~= \"network301\" then\n" +
            "            error(reason)\n" +
            "        end\n" +
            "    end\n" +
            "end\n" +
            "\n" +
            "function scrape_page(splash)\n" +
            "    local frames = splash:evaljs(\"let htmls=new Array(frames.length);for(let i=0;i<frames.length;i++)" +
            "{const doc=frames[i].document;if(doc){htmls[i]=doc.documentElement.outerHTML}}htmls;\")\n" +
            "    local html = splash:html()\n" +
            "    local lowHtml = string.lower(html)\n" +
            "    local tag = \"<meta http%-equiv=\\\"refresh\\\"\"\n" +
            "    if lowHtml:find(tag) then\n" +
            "        splash:wait(2)\n" +
            "        html = splash:html()\n" +
            "    end\n" +
            "    local url = splash:url()\n" +
            "    splash:runjs(\"window.close()\")\n" +
            "    return {html=html, url=url, frames=frames}\n" +
            "end\n" +
            "\n" +
            "function get_url_without_protocol(url_to_go)\n" +
            "    if (url_to_go:sub(0,5) == \"https\") then \n" +
            "        return url_to_go:sub(9)\n" +
            "    else \n" +
            "        return url_to_go:sub(8)\n" +
            "    end\n" +
            "end";

    /**
     * @param context credentials and variables
     * @return request to Splash, with all settings
     */
    @Override
    public Request getRequest(DefaultSplashRequestContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", context.getSiteUrl().toString());
        jsonObject.addProperty("images", 0);
        jsonObject.addProperty("iframe", 1);
        jsonObject.addProperty("filters", "filter,easyprivacy,fanboy-annoyance");
        // in seconds, time for page to render
        jsonObject.addProperty("timeout", 20.0);
        // in seconds, time for page to load resources
        jsonObject.addProperty("resource_timeout", 16.0);
        jsonObject.addProperty("lua_source", luaScript);
        String credentials = context.getUsername() + ":" + context.getPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return new Request.Builder()
                .url(context.getSplashUrl().toString() + "/execute")
                .post(RequestBody.create(jsonObject.toString(), JSON))
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();
    }
}
