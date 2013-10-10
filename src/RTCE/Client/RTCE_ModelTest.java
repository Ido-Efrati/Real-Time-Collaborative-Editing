package RTCE.Client;

import static org.junit.Assert.*;

import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.junit.Test;

public class RTCE_ModelTest {

    /*
     * A part of the model is updating the view. We test that
     * using Black Box Testing. See MVC_BlackBoxTesting.java
     * 
     * Here we test with the following strategy:
     * 
     * Strategy:
     * 
     *      1) Test the constructor that the initial styling is
     *         what we want.
     *      
     *      2) Test the document tree. Add some names and see
     *         if they appear in the Vector that stores them.
     *         
     *      3) Test the setter and getter for the font size
     *      
     *      4) Test the setter and the getter for the font style
     *      
     *      5) Test the setter and the getter for the font color
     *      
     *      6) Test the setter and the getter for the font name
     *      
     *      7) Test that the updateViewTitle() method actually and 
     *         correctly sets the title of the view.
     *         
     *      8) Test that the updateText() method actually and 
     *         correctly sets the text in the view
     */


    RTCE_View view = new RTCE_View();
    RTCE_Model model = new RTCE_Model(view);

    @Test
    public void contructorTest() {
        assertEquals(model.getFontSize(), 12);
        assertEquals(model.getStyle(), Font.PLAIN);
        assertEquals(model.getColor(), 0);
        assertEquals(model.getFontName(), 3);
    }

    @Test
    public void testUpdateDocTree() {

        model.updateDocTree("test.txt");
        Vector<String> filenames = model.getInTree();
        String docName = filenames.get(0);

        assertEquals(docName, "test.txt");

        model.updateDocTree("hahah.txt");
        filenames = model.getInTree();
        String docName2 = filenames.get(1);
        assertEquals(docName2, "hahah.txt");
    }

    @Test
    public void testSetGetFontSize() {
        model.setFontSize(72);
        assertEquals(72, model.getFontSize());
    }

    @Test
    public void testSetGetStyle() {
        model.setStyle(3);
        assertEquals(model.getStyle(), 3);
    }

    @Test
    public void testSetGetColor() {
        model.setColor(1);
        assertEquals(model.getColor() ,1);
    }
    
    @Test
    public void testSetGetFontName() {
        model.setFontName(4);
        assertEquals(model.getFontName(), 4);
    }
    
    @Test
    public void testTitleSet() {
        model.updateViewTitle("Testing...");
        assertEquals(view.getTitle(), "Testing...");
    }
    
    @Test
    public void testTextSet() throws UnsupportedEncodingException {
        model.updateText("we are testing");
        assertEquals(view.getText(), "we are testing");
    }


}
