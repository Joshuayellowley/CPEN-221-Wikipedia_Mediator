package cpen221.mp3.wikimediator;

public class InvalidQueryException extends Throwable {

    public InvalidQueryException() {
        System.out.println("The query entered is invalid.  Please try again.");
    }

}
