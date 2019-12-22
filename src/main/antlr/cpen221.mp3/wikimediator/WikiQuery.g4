grammar WikiQuery;

@header {
package cpen221.mp3.wikimediator;
}

// This adds code to the generated lexer and parser.
// DO NOT CHANGE THESE LINES
@members {
    // This method makes the lexer or parser stop running if it encounters
    // invalid input and throw a RuntimeException.
    public void reportErrorsAsExceptions() {
        //removeErrorListeners();

        addErrorListener(new ExceptionThrowingErrorListener());
    }

    private static class ExceptionThrowingErrorListener
                                              extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer,
                Object offendingSymbol, int line, int charPositionInLine,
                String msg, RecognitionException e) {
            throw new RuntimeException(msg);
        }
    }
}

/*
 * These are the lexical rules. They define the tokens used by the lexer.
 *  antlr requires tokens to be CAPITALIZED, like START_ITALIC, and TEXT.
 */

CONDITION : SIMPLE_CONDITION | LPAREN CONDITION ' and ' CONDITION RPAREN | LPAREN CONDITION ' or ' CONDITION RPAREN;
LPAREN : '(';
RPAREN : ')';
SIMPLE_CONDITION : 'title is '  STRING | 'author is ' STRING | 'category is ' STRING;
ITEM : 'page' | 'author' | 'category';
SORTED : 'asc' | 'desc';
STRING : '\'' ( ~'\'' | '\'\'' )* '\'';
WHITESPACE : [ \t\r\n]+ -> skip ;

/*
 * These are the parser rules. They define the structures used by the parser.
 * Antlr requires grammar nonterminals to be lowercase,
 */

wikiquery : query EOF ;
query: 'get' ITEM 'where' CONDITION SORTED?;

