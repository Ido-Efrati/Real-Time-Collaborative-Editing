package RTCE.Client;

import static org.junit.Assert.*;

import org.junit.Test;



/*
 * Testing for the View of the MVC.
 * 
 * Strategy:
 * 
 *      Again some parts of the view cannot be tested with JUnit. For those
 *      tests please see MVC_BlackBoxTesting.java where the Black Box Testing
 *      is explained for the GUI.
 *      
 *      Here we test:
 *      
 *          1) Make sure that when constructor all buttons besides the New 
 *             and the Switch buttons.
 *             
 *          2) Make sure the method that sets the text of the view actually
 *             and correctly does this.
 *             
 *          3) Make sure that the method that sets the title of the view
 *             actually and correctly does this.
 *           
 *          4) Make sure that the method that releases the buttons 
 *             that were disabled upon construction releases these
 *             buttons.
 *             
 *          5) Make sure that method that gets the text of the view
 *             actually and correctly does so.
 * 
 */
public class RTCE_ViewTest {
    
    private RTCE_View view = new RTCE_View();
    
    @Test
    public void allInitialButtonsFalseTest() {
        assertFalse(view.getplainButton().isEnabled());
        assertFalse(view.getBoldButton().isEnabled());
        assertFalse(view.getItalicButton().isEnabled());
        assertFalse(view.getCopyButton().isEnabled());
        assertFalse(view.getCutButton().isEnabled());
        assertFalse(view.getPasteButton().isEnabled());
        assertFalse(view.getReplaceButton().isEnabled());
        assertFalse(view.getSaveFileButton().isEnabled());
        assertFalse(view.getOpenFileButton().isEnabled());
        assertFalse(view.getFontSize().isEnabled());
        assertFalse(view.getFontName().isEnabled());
        assertFalse(view.getColorOptions().isEnabled());
    }
    
    @Test
    public void setViewTextTest() {
        view.setViewText("I am testing the text");
        assertEquals(view.getText(), "I am testing the text");
    }
    
    @Test
    public void setViewTitleTest() {
        view.setViewTitle("testing the title");
        assertEquals(view.getTitle(), "testing the title");
    }
    
    @Test
    public void releaseScreenTest() {
        view.releaseScreen(true);
        assertTrue(view.getplainButton().isEnabled());
        assertTrue(view.getBoldButton().isEnabled());
        assertTrue(view.getItalicButton().isEnabled());
        assertTrue(view.getCopyButton().isEnabled());
        assertTrue(view.getCutButton().isEnabled());
        assertTrue(view.getPasteButton().isEnabled());
        assertTrue(view.getReplaceButton().isEnabled());
        assertTrue(view.getSaveFileButton().isEnabled());
        assertTrue(view.getOpenFileButton().isEnabled());
        assertTrue(view.getFontSize().isEnabled());
        assertTrue(view.getFontName().isEnabled());
        assertTrue(view.getColorOptions().isEnabled());
        
    }
    
    public void getTextTest() {
        view.getTextPane().setText("i am testing now");
        assertEquals(view.getText(), "i am testing now");
    }



}
