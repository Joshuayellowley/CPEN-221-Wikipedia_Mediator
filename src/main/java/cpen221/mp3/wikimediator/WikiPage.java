package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;
import fastily.jwiki.core.Wiki;

public class WikiPage implements Cacheable{

        private String id;
        private String pageText;
        private Wiki wiki = new Wiki("en.wikipedia.org");

        WikiPage(String id){
            this.id = id;
            this.pageText = wiki.getPageText(id);
        }

        public String id(){
            return this.id;
        }

        String pageText(){
            return this.pageText;
        }

}






