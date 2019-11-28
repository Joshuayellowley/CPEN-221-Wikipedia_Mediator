package cpen221.mp3.wikimediator;

import cpen221.mp3.cache.Cacheable;

public class WikiPage implements Cacheable{

        private String id;

        WikiPage(String id){
            this.id = id;
        }

        public String id(){
            return this.id;
        }
}






