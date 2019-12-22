package cpen221.mp3.wikimediator;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation Invariant:
 * text, type not null
 * If left is null, then first is not null
 * If first is null, then left is not null
 * If right is null, then second is not null
 * If second is null, then right is not null
 * left != this
 * right != this
 * goRight is true if this.compound is true and right == null
 * <p>
 * <p>
 * Abstraction Function:
 * QueryCondition is the condition that a page, author or category must
 * satisfy to be returned by a specific query.
 * Text represents the string of the text of the whole condition to be evaluated
 * If compound is true the QueryCondition is a more advanced condition and
 * type == "and" or type == "or" conditional.
 * In the case where this is a compound condition,
 * left represents the left side conditional and right represents the right side conditional if the
 * resulting left and right conditionals of this are compound.
 * However if left.compound == false, the condition is represented by first
 * and if right.compound == false, the condition is represented by second
 */

public class QueryCondition {

    String text;
    Map<String, String> first;
    Map<String, String> second;
    String type;
    QueryCondition left;
    QueryCondition right;
    boolean compound;
    boolean goRight;

    /**
     * Creates a new instance of Query Condition
     *
     * @param text is the full condition to be evaluated
     */
    public QueryCondition(String text) {
        this.text = text;
        this.type = "";
        first = new HashMap<>();
        second = new HashMap<>();
        setUp();
    }

    /**
     * Helper Method that sets up the recursive data tree
     */
    private void setUp() {

        this.compound = text.charAt(0) == '(';
        int index = 0;

        if (text.charAt(1) == '(') {
            String strForLeft = text.substring(1);

            for (int h = 0; h < text.length(); h++) {
                if (strForLeft.charAt(h) == ')') {
                    strForLeft = strForLeft.substring(0, h + 1);
                    break;
                }
            }

            left = new QueryCondition(strForLeft);


            for (int k = strForLeft.length() + 2; k < text.length(); k++) {
                if (text.substring(k, k + 2).equals("or")) {
                    this.type = "or";
                    k = k + 3;
                    index = k;
                    break;
                }

                if (text.substring(k, k + 3).equals("and")) {
                    this.type = "and";
                    k = k + 4;
                    index = k;
                    break;
                }
            }

            goRight = text.charAt(index) != '(';

        } else {
            index = setUpLeftMap();
        }
        if (goRight) {

            right = null;
            setUpRightMap(index);

        } else {


            right = new QueryCondition(text.substring(index));

        }
    }

    /**
     * This method is only for if the left side is not a compound condition sets up the first map to
     * hold the simple condition.
     *
     * @return the index of the character within the full condition
     */
    private int setUpLeftMap() {


        int i = 0;
        if (this.compound) {
            i = 1;
        }
        String temp = "";

        if (text.charAt(i) == 'a') {
            temp = "author";
            i = i + 10;
        }

        if (text.charAt(i) == 't') {
            temp = "title";
            i = i + 9;
        }

        if (text.charAt(i) == 'c') {
            temp = "category";
            i = i + 12;
        }

        while (true) {

            if (text.charAt(i) == '\'') {
                for (int p = i + 1; p < text.length(); p++) {
                    if (text.charAt(p) == '\'') {
                        first.put(temp, text.substring(i + 1, p));
                        i = p;
                        break;
                    }
                }
            }

            if (this.compound) {

                if (text.substring(i, i + 2).equals("or")) {
                    this.type = "or";
                    i = i + 3;
                    break;
                }

                if (text.substring(i, i + 3).equals("and")) {
                    this.type = "and";
                    i = i + 4;
                    break;
                }
            } else {
                break;
            }

            i++;
        }

        goRight = text.charAt(i) != '(';

        return i;
    }

    /**
     * This method is only for if the right side is not a compound condition,
     * sets up the second map to
     * hold the simple condition.
     *
     * @param i the index of the character to start at within the full condition
     */
    private void setUpRightMap(int i) {
        String temp = "";

        if (text.charAt(i) == 'a') {
            temp = "author";
            i = i + 10;
        }

        if (text.charAt(i) == 't') {
            temp = "title";
            i = i + 9;
        }

        if (text.charAt(i) == 'c') {
            temp = "category";
            i = i + 12;
        }

        for (int g = i; g < text.length(); g++) {
            if (text.charAt(g) == '\'') {
                for (int p = g + 1; p < text.length(); p++) {
                    if (text.charAt(p) == '\'') {
                        second.put(temp, text.substring(g + 1, p));
                        g = p;
                        break;
                    }
                }
            }
        }
    }

}
