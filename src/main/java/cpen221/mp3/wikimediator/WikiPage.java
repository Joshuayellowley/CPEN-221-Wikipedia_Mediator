package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;

public class WikiPage implements Cacheable{

        private String pageTitle;
        private String pageText;
        private Wiki wiki = new Wiki("en.wikipedia.org");

        WikiPage(String pageTitle){
            this.pageTitle = pageTitle;
            this.pageText = wiki.getPageText(pageTitle);
        }

        public String id(){
            return this.pageTitle;
        }

        String pageText(){
            return this.pageText;
        }

}






