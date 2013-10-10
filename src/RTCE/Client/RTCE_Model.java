package RTCE.Client;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/*
 * Thread safety argument 
 * The RTCE_Model class update the view for the user based on inputs from the controller
 * 
 * The constructor does not need to be synchronize because it is making a new object that is confined. 
 * Therefore, Java won't let you synchronize it. 
 * 
 * All of the data types are thread safe because they are either thread safe data types (vectors), 
 * or are immutable (unmodifiableMap)
 * 
 *The updateText method is thread safe because it calls a thread safe method in the view.
 * 
 * by confinement - all of the method in this class are performed on one thread that is localized to the user
 * and are not shared with any other clients. Furthermore, there is no actual race condition because each
 * method is responsible to update a different JComponent , so there is no concern that their order of
 * execution will interfere with the view.  
 */

 
/**
 * Model part of the MVC design implementation that we are using for
 * the RTCE. This is the part that will:
 * 
 *      (1) Store the information of the
 *          View. Stores the styling information as well.
 *      (2) Make any of the updates on the GUI:
 *          (a) Changing the text
 *          (b) Changing the style
 *          (c) Updating the document tree
 *          (d) Unblocking the buttons
 *      
 */
public class RTCE_Model {
    
    private Vector<String> inTree = new Vector<String>(); // Store all document 
                                                          // that are currently
                                                          // in the tree.
    private RTCE_View view;
    private int fontSize;
    private int styleType;
    private int colorType;
    int fontName;
    
    // Map for styling mapping integers to the font style that they represent.
    @SuppressWarnings("serial")
    private  Map<Integer, Integer> styleMap = 
            Collections.unmodifiableMap(new HashMap<Integer, Integer>() {
                        {
                            put(0, Font.PLAIN);
                            put(1, Font.BOLD);
                            put(2, Font.ITALIC);
                        }
                    });
    
    // Map for styling mapping integers to the color that they represent.
    @SuppressWarnings("serial")
    private Map<Integer, Color> colorMap = 
            Collections.unmodifiableMap(new HashMap<Integer, Color>() {
        {
            put(0, Color.BLACK);
            put(1, Color.BLUE);
            put(2, Color.GRAY);
            put(3, Color.GREEN);
            put(4, Color.ORANGE);
            put(5, Color.PINK);
            put(6, Color.RED);
            put(7, Color.YELLOW);
        }
    });
    
    private String[] fonts = {"Arial","Courier New","Georgia","Times New Roman","Verdana"};

    public RTCE_Model(RTCE_View v) {
        view = v;
        fontSize = 12;
        styleType = Font.PLAIN;
        colorType = 0;
        fontName= 3;   
    }

    @SuppressWarnings("unchecked")
    /**
     * Method to get the vector of the names of all the 
     * files that are in the document tree.
     * @return
     */
    public Vector<String> getInTree() {
        return (Vector<String>) inTree.clone();
    }
    
    /**
     * A Method to set the text of the GUI. 
     * @param String text, the text you want to display on the GUI.
     * @throws UnsupportedEncodingException
     */
    // SetViewText is thread safe!
    public void updateText(String text) throws UnsupportedEncodingException{
        String newText = URLDecoder.decode(text,"UTF-8");
        view.setViewText(newText);
    }
    
    /**
     * A method to update the title of the GUI.
     * @param String title, the title you want to give the GUI.
     */
    public void updateViewTitle(String title) {
        view.setViewTitle(title);   
    }
    
    /**
     * A Method to update the document tree.
     * @param String response, the names you want in the tree.
     */
    public void updateDocTree(String response) {
        view.updateDocTree(response);
        inTree.add(response);
    }
    
    /**
     * Method to set the font size that you are storing 
     * for the view.
     * @param int size, the font size
     */
    public void setFontSize(int size ) {
        fontSize = size;  
    }
    
    /**
     * A Method to set the style that you are using 
     * for the view.
     * @param int style, the integer mapping to the style you
     * are storing for the view.
     */
    public void setStyle(int style) {
        styleType = style;
    }
    
    /**
     * A method to set the color that you are using
     * for the view.
     * @param int color, the integer mapping to the color
     * you are storing for the view.
     */
    public void setColor(int color) {
        colorType = color;
    }
    
    /**
     * A method to set the font type you are storing 
     * for the document.
     * @param int name, the integer mapping to the font name
     * you are using in the view.
     */
    public void setFontName(int name) {
        fontName = name;
    }
    
    /**
     * A method to get the style used in the view.
     * @return int, the integer mapping to the style used in the view.
     */
    public int getStyle() {
        return this.styleType;
    }
    
    /**
     * A method to get the color used in the view.
     * @return int, the integer mapping to the color used in the view.
     */
    public int getColor() {
        return this.colorType;
    }

    /**
     * A method to get the font name used in the view.
     * @return int, the integer mapping to the font name used in the view.
     */
    public int getFontName() {
        return this.fontName;
    }
    
    /**
     * A method to get the font size used in the view.
     * @return int, the integer mapping to the font size used in the view.
     */
    public int getFontSize() {
        return this.fontSize;     
    }
    
    /**
     * A method to set the style of the GUI. Takes all the integers mapping to the
     * various styles as parameters.
     * @param int fontName, the integer mapping to the font name you want 
     * to give the GUI.
     * @param int fontStyle, the integer mapping to the font style you want
     * to give the GUI.
     * @param int fontSize, the integer mapping to the font size you want 
     * to give the GUI
     * @param int color, the integer mapping to the color you want to set
     * the GUI's font.
     */
    public void setViewStyle(int fontName, int fontStyle, int fontSize, int color) {
        view.getTextPane().setFont(new Font(fonts[fontName], styleMap.get(fontStyle),fontSize));
        view.getTextPane().setForeground(colorMap.get(color));          
    }
    
    /**
     * Method that updates the view and releases all the buttons and combo boxes so that 
     * after the user clicks New or Switch he can use all the options.
     * @param bool
     */
    public void releaseScreen(boolean bool) {
        view.releaseScreen(bool);
    }
}