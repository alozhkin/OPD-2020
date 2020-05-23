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
    private static final String luaScript = "splash.webgl_enabled = false\n" +
            "splash.media_source_enabled = false\n" +
            "local ok, reason = splash:go(args.url)\n" +
            "if not ok then\n" +
            "  if reason:sub(0,4) == 'http' then\n" +
            "      splash:set_result_status_code(reason:sub(4))\n" +
            "  else\n" +
            "     error(reason)\n" +
            "  end\n" +
            "end\n" +
            "local frames = splash:evaljs(\"let htmls=new Array(frames.length);for(let i=0;i<frames.length;i++)" +
            "{const doc=frames[i].document;if(doc){htmls[i]=doc.documentElement.outerHTML}}htmls;\")\n" +
            "local html = splash:html()\n" +
            "local url = splash:url()\n" +
            "splash:runjs(\"window.close()\")\n" +
            "return {html=html, url=url, frames=frames}";

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
                .url(context.getSplashUrl().toString() + "/run")
                .post(RequestBody.create(jsonObject.toString(), JSON))
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();
    }
}
