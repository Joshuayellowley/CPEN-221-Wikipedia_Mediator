package cpen221.mp3.wikimediator;

public class QueryCondition{

    String text;
    String type;
    QueryCondition c;
    boolean compound;

    public QueryCondition(String text){
        this.text = text;
        setUp();
    }

    void setUp(){

        String textCopy = "";

        if(text.charAt(0) == '('){
            this.compound = true;
            textCopy = text.substring(5);
        }else{
            this.compound = false;
        }






    }



}
