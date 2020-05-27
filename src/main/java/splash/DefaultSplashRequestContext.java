package splash;

import utils.Link;

/**
 * Class that define parts of request which can vary from request to request:
 * credentials, ip for access splash and url to be scraped
 */
public class DefaultSplashRequestContext {
    private final Link siteUrl;
    private final Link splashUrl;
    private final String username;
    private final String password;

    public static class Builder {
        private static final Link localhost = new Link("localhost:8050");
        private static final Link haproxy = new Link("haproxy:8050");
        private static Boolean insideContainer;
        private Link siteUrl;
        private Link splashUrl;
        private String username = "user";
        private String password = "userpass";

        public Builder() {
            if (insideContainer == null) {
                insideContainer = System.getProperty("inside.container").equals("true");
            }
            splashUrl = insideContainer ? haproxy : localhost;
        }

        public Builder setSiteUrl(Link siteUrl) {
            this.siteUrl = siteUrl;
            return this;
        }

        public Builder setSplashUrl(Link splashUrl) {
            this.splashUrl = splashUrl;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public DefaultSplashRequestContext build() {
            return new DefaultSplashRequestContext(siteUrl, splashUrl, username, password);
        }
    }

    private DefaultSplashRequestContext(Link siteUrl, Link splashUrl, String username, String password) {
        this.siteUrl = siteUrl;
        this.splashUrl = splashUrl;
        this.username = username;
        this.password = password;
    }

    public Link getSiteUrl() {
        return siteUrl;
    }

    public Link getSplashUrl() {
        return splashUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
