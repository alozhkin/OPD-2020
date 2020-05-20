package spider;

import utils.Link;

import java.util.Collection;

public interface OnSpiderChangesListener {

    void onDomainsParsed(Collection<Link> domains);

    void onDomainScraped();

    void onDataExported();

    void onFinished();
}
