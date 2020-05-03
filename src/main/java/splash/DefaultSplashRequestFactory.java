package splash;

import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.Base64;

public class DefaultSplashRequestFactory implements SplashRequestFactory {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String luaScript =
                    "    splash.webgl_enabled = false\n" +
                    "    splash.media_source_enabled = false\n" +
                    "    splash:go(args.url)\n" +
                    "    local html = splash:html()\n" +
                    "    splash:runjs(\"window.close()\")\n" +
                    "    return html\n";

    @Override
    public Request getRequest(SplashRequestContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", context.getSiteUrl().toString());
        jsonObject.addProperty("images", 0);
        jsonObject.addProperty("filters", "filter,easyprivacy,fanboy-annoyance");
        jsonObject.addProperty("lua_source", luaScript);
        String credentials = context.getUsername() + ":" + context.getPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return new Request.Builder()
                .url(context.getSplashUrl().toString())
                .post(RequestBody.create(JSON, jsonObject.toString()))
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();
    }
}
