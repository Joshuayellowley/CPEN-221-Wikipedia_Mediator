package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;


/**
 * Representation Invariant:
 * pageTitle, pageText not null
 * wiki has the domain en.wikipedia.org
 * <p>
 * Abstraction Function:
 * pageTitle refers to the title of a page that
 * may or may not exist on en.wikipedia.org
 * pageText contains the text of the wikipedia page
 * with title pageTitle. If pageTitle does not exist
 * on wikipedia, then pageText = ""
 */
public class WikiPage implements Cacheable {

    private String pageTitle;
    private String pageText;
    private Wiki wiki = new Wiki("en.wikipedia.org");

    /*
     * Constructor for WikiPage which denotes the WikiPage id, and collects the pageText
     * for access by the getPage method in WikiMediator
     */
    WikiPage(String pageTitle) {
        this.pageTitle = pageTitle;
        this.pageText = wiki.getPageText(pageTitle);
    }

    /**
     * Gets the id in order to be stored into a cache
     *
     * @return the id of this object to store in a cache
     */
    public String id() {
        return this.pageTitle;
    }

    /**
     * Gets the text of the page titled pageTitle from wikipedia.
     *
     * @return the text of the page on wikipedia. If such a page does not exist,
     * returns ""
     */
    String pageText() {
        return this.pageText;
    }

}






