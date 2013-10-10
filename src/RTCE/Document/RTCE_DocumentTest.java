package RTCE.Document;

import static org.junit.Assert.*;

import org.junit.Test;

public class RTCE_DocumentTest {
    /**
     * These tests are to make sure that all methods of 
     * the RTCE_Document class work properly.
     * 
     * Strategy: -Try edge cases (like empty document, begin of document,
     *           delete all text, delete with out bounds index)
     *           
     *           -Try all different types of operations (insert, delete)
     *           
     *           -Try different types of operations in all the possible
     *           varieties (the middle of a word, after a word)
     *           
     *           -replaceOne tests:
     *              try replacing one simple word at beginning of document
     *              try replacing one simple word at end of document
     *              try replacing one simple word in middle of document
     *              try replacing one word when it occurs multiple times and
     *                  make sure that it only replaces once
     *              try replacing with an empty string
     *              try replacing a word that does not occur
     *                  nothing should happen. it should just return
     *                  the same text
     *              
     *           - replaceAll tests:
     *              try replacing a word that occurs multiple times
     *              try replacing a word that occurs only once
     *              try replacing a word that does not occur
     *              try replacing with an empty string 
     * @author Philippe
     */

    // Insert Tests
    @Test
    public void insertStringInEmptyDoc() {
        RTCE_Document d = new RTCE_Document("tests.txt");
        d.insert(0, "hello world");
        assertEquals(d.getAllText(), "hello world");
    }

    @Test
    public void insertStringBeforeExistingText() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "hello world.");
        d.insert(0, "I say: ");
        assertEquals(d.getAllText(), "I say: hello world.");
    }

    @Test
    public void insertStringAfterExistingText() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Philippe is the MAN");
        d.insert(19, ", THE MAN!!!");
        assertEquals(d.getAllText(), "Philippe is the MAN, THE MAN!!!");
    }

    @Test
    public void insertBetweenText() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "ad");
        d.insert(1, "bc");
        assertEquals(d.getAllText(), "abcd"); 
    }

    @Test
    public void insertOutOfBounds() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Madam I'm Adam.");
        d.insert(50, "A racecar.");
        assertEquals(d.getAllText().indexOf("A racecar."), 50);
    }

    @Test
    public void insertNonLetter() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(4, "~!@#$%^&*()_+{}|:<>?");
        assertEquals(d.getAllText(), "    ~!@#$%^&*()_+{}|:<>?");
    }
    // Delete Testing

    @Test
    public void deleteEmpty() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.delete(0, 1);
        assertEquals(d.getAllText(), "");
    }

    @Test
    public void deleteBeginOfString() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Hahahahaha");
        d.delete(0, 4);
        assertEquals(d.getAllText(),"hahaha" );
    }

    @Test
    public void deleteEndOfSring() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Begin End");
        d.delete(6, 9);
    }

    @Test
    public void deleteInString() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "aaaadeletebbbb");
        d.delete(4, 10);
        assertEquals(d.getAllText(), "aaaabbbb");

    }

    @Test
    public void deleteAll() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(3, "qwertyuiop");
        d.delete(0, 100);
        assertEquals(d.getAllText(), "");  
    }

    // Set/Get Name Tests

    @Test
    public void setNameOfDocWithOnlyLetters() {
        RTCE_Document d = new RTCE_Document("letters.txt");
        assertEquals(d.getName(), "letters.txt");        
    }

    @Test
    public void setNameOfDocWithOnlyNumbers() {
        RTCE_Document d = new RTCE_Document("91210.txt");
        assertEquals(d.getName(), "91210.txt");        
    }


    @Test
    public void setNameOfDocWithNumbersAndLetters() {
        RTCE_Document d = new RTCE_Document("BevHills91210.txt");
        assertEquals(d.getName(), "BevHills91210.txt");        
    }


    @Test
    public void resetNameOfDoc() {
        RTCE_Document d = new RTCE_Document("try1.txt");
        d.setName("try2.txt");
        assertEquals(d.getName(), "try2.txt");        
    }

    // replaceOne tests:
    @Test
    public void replaceOneAtBeginTest() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "hello how are you?");
        d.replaceOne("hello", "bye");
        assertEquals(d.getAllText(), "bye how are you?");
    }

    @Test
    public void replaceOneAtEnd() {
        RTCE_Document d = new RTCE_Document("Test.txt");
        d.insert(0, "this is one small step for [a] man, one giant leap for mankind." );
        d.replaceOne("mankind.", "beating the Russians.");
        assertEquals(d.getAllText(), "this is one small step for [a] man, one giant leap for beating the Russians.");
    }

    @Test
    public void replaceOneInMiddleTest() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "it's a small world after all");
        d.replaceOne("small", "big");
        assertEquals(d.getAllText(), "it's a big world after all");
    }

    @Test
    public void replaceOneThatAppeareMultipleTimes() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(5, "money money money, money money, in a rich man's world.");
        d.replaceOne("money", "cash");
        assertEquals(d.getAllText(), "     cash money money, money money, in a rich man's world.");

    }

    @Test
    public void replaceOneWithEmptyString() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Vanity of Vanities");
        d.replaceOne("of", "");
        assertEquals(d.getAllText(), "Vanity  Vanities");
    }

    @Test
    public void replaceNonExistingWord() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "In the clearing stands a boxer, And a fighter by his trade...");
        d.replaceOne("leyleyley", "lielielie");
        assertEquals(d.getAllText(), "In the clearing stands a boxer, And a fighter by his trade..." );
    }

    //replaceAll tests:
    @Test
    public void replaceAllMutiple() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "...I still have adream. It is adream deeply rooted in the American dream...");
        d.replaceAll("adream", "a dream");
        assertEquals(d.getAllText(), "...I still have a dream. It is a dream deeply rooted in the American dream...");
    }
    
    @Test
    public void replaceAllWordOnlyOnce() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Froth at the top, dregs at bottom, but the middle excellent.");
        d.replaceAll("excellent", "...I don't know");
        assertEquals(d.getAllText(), "Froth at the top, dregs at bottom, but the middle ...I don't know."); 
    }
    
    @Test
    public void replaceAllWhenWordDoesNotOccurTest() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0,  "You must be the change you wish to see in the world.");
        d.replaceAll("gandhi", "Gandhi");
        assertEquals(d.getAllText(), "You must be the change you wish to see in the world.");
    }
    
    @Test
    public void replaceAllWithAnEmptyString() {
        RTCE_Document d = new RTCE_Document("test.txt");
        d.insert(0, "Work like you don’t need money, love like you’ve never been hurt, and dance like no one’s watching you.");
        d.replaceAll(" you", "");
        assertEquals(d.getAllText(), "Work like don’t need money, love like’ve never been hurt, and dance like no one’s watching.");
    }
}