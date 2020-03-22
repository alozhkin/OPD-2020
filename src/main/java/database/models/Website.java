package database.models;

import utils.Link;

import java.util.Objects;

public class Website {
    private final int companyId;
    private final Link link;

    public Website(int companyId, Link link) {
        this.companyId = companyId;
        this.link = link;
    }

    public int getCompanyId() {
        return companyId;
    }

    public Link getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website = (Website) o;
        return companyId == website.companyId && Objects.equals(link.getAbsoluteURL(), website.link.getAbsoluteURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, link);
    }

    @Override
    public String toString() {
        return "Website{" +
                "companyId=" + companyId +
                ", link=" + link.getAbsoluteURL() +
                '}';
    }
}
