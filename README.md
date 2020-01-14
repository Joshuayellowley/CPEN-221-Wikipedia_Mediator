**MP3 / CPEN 221 / Fall 2019**

# Wikipedia Mediator and Graph Databases

This project was completed as apart of CPEN 221: Principles of Software Construction in November 2019.

Following the guidelines of the cache abstract data type, an implementation of a cache was created with support for basic operations such as removing and adding objects to a cache and keeping only up to date items in the cache while removing the last recently used item when space in the cache runs out.

Using this cache, a WikiMediator class utilizes the cache to hold wikipedia pages to limit the processing time to fetch information from wikipedia.  Additionally, there is support for searching wikipedia, getting page text, looking at recent requests made on the mediator and more described within the method Javadocs.

Support for this was extended to have simultaneous clients access the mediator using gson and multi-threading to allow multiple users to make requests of the wikipedia server.  

Using ANTLR, more advanced requests can be made using structured queries.  Through the use of ANTLR grammars, indepth searches can be made on wikipedia for pages relating to different categories, authors and page title. This is performed in the executeQuery method.

