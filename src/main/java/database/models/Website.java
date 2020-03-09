package database.models;

import java.util.Objects;

public class Website {
    private final int companyId;
    private final String link;

    public Website(int companyId, String link) {
        this.companyId = companyId;
        this.link = link;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Website website1 = (Website) o;
        return companyId == website1.companyId &&
                Objects.equals(link, website1.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId, link);
    }
}
