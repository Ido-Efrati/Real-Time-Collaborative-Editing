package RTCE.Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import RTCE.Document.RTCE_FileReader;
import RTCE.Document.RTCE_FileWriter;
/*
 * Thread safety argument 
 * 
 * The method setViewText is thread safe because it is using a setText method from the JTextArea build in methods
 * This method is thread safe.
 * 
 *  Even though swing components are not thread safe, each client has its own GUI that is created on a
 *  separate thread that is dedicated to this specific client.
 *  Since all of the JComponents are created on the swing event dispatch thread that is unique to the user
 *  we can claim thread safety by confinement. Which means that all of the components are not accessible 
 *  by the other users and only the local user can access them.
 *  Furthermore, race condition is eliminated by the controller and its worker and by the server that 
 *  prevents illogical order of operation. So even if race condition will occur the controller and 
 *  server will resolve it.
 */
/**
 * The RTCE_View class creates the client's graphical user interface.
 * The class creates the GUI by:
 *      (a) Add all necessary components.
 *      (b) Build the starting state of the GUI.
 *      (c) Register action listeners and notify controller
 *          that an event happened.
 * @author Ido Efrati
 */
@SuppressWarnings("serial")
public class RTCE_View extends JFrame implements ActionListener {
    
    /*
     * JComponents class variables
     */
    private JTextArea textPane;
    private JTree documentTree;
    private JTextField wordToReplaceField, wordToReplaceWithField;
    private JComboBox fontSize, fontName, colorOptions;
    private JButton boldButton, italicButton, replaceButton,newDocButton,switchButton, plainButton, 
    saveFileButton, openFileButton, copyButton, cutButton, pasteButton, replaceOneButton, replaceAllButton;
    boolean setText = false;
    private  DefaultMutableTreeNode top = new DefaultMutableTreeNode("Active Documents"); 
    private String switchTo = "";

    /*
     * values for JCompontents 
     */

    //initial welcome and info message
    private final String initialHello = "Welcome to Real Time Collaborative Editor!" + String.format("%n")
            +"To start editing please create a new document by clicking on the New button," +
            " or pick an active document from the list and click on the Switch button." + String.format("%n")+
            String.format("%n") + "Please use one of the extra fetures to improve your experience." + String.format("%n")+
            String.format("%n") + "Styling:" + String.format("%n") +
            "1) Plain - remove styling from your text." +String.format("%n") +
            "2) Bold - bold your text." + String.format("%n") +
            "3) Italic - italicize your text." + String.format("%n") +
            "4) Size - change the size of your font." + String.format("%n") +
            "5) Font - change the font of your text." + String.format("%n") +
            "6) Color - change the color of your text."  + String.format("%n") +
            String.format("%n") + "Editing:" + String.format("%n") +
            "1) Copy - allows you to copy a text." + String.format("%n") +
            "2) Cut - allows you to cut a text." + String.format("%n") +
            "3) Paste - allows you to paste a text." + String.format("%n") +
            "4) Replace - allows you to replace either the first occurence of a word" +
            " ('replace') or all its occurences ('replace all')." +String.format("%n") +
            String.format("%n") + "File options:" + String.format("%n") +
            "1) New - creates a new document." + String.format("%n") +
            "2) Switch - switch to a different document." + String.format("%n") +
            "3) Save - save the current document to a file on your system." + String.format("%n") +
            "4) Open - Open a file from your system into the text editor."  + String.format("%n") +
            String.format("%n")+"We are hoping you will have a good time.";

    //Color names for the color menu.
    private final String[] colorsName = {"<html> <font color=#000000>black</font>",
            "<html><font color=#0000FF>blue</font>","<html> <font color=#808080>gray</font>",
            "<html><font color=#008000>green</font>","<html><font color=#FFA500>orange</font>",
            "<html> <font color=#FFC0CB>pink</font>", "<html> <font color=#FF0000>red</font>",
            "<html> <font color=#FFD700>yellow</font>"
    };

    //Sizes for the size menu
    private static final Integer[] sizes = {
        8, 9, 10, 12, 14, 16, 18, 20, 24, 26, 28, 36, 48, 72
    };
    //Fonts for the font menu
    private static final String[] fonts = {
        "Arial","Courier New","Georgia","Times New Roman","Verdana"
    };

    /*
     *  MVC class variables
     */
    RTCE_Model model = new RTCE_Model(this);
    RTCE_Controller controller = new RTCE_Controller(this, model);

    /*
     * Import Export variables
     */
    RTCE_FileReader fr = new RTCE_FileReader();
    RTCE_FileWriter fw = new RTCE_FileWriter();
    static private String fileRead = null;
    static private String fileName = null;
    private JFileChooser fileChooser = new JFileChooser();

    /*
     * IP class variables
     */
    private static String ipAddress = null;
    private final String ValidIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    private final String ValidHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    /**
     * Constructor for the GUI Client. Performs three main tasks:
     *      1) Builds the GUI by adding JComponents to panels.
     *      2) Add ActionListeners for eventual user's actions.
     *      3) Sets the initial state of the GUI.
     */
    public RTCE_View() {
        super("Welcome to Real Time Collaborative Editor"); // set an initial title for the GUI

        /*
         *Request a valid IP address , will prompt the user for another ip address if the ip is not valid
         *or if it failed to made a connection.
         */
        while ((ipAddress == null)) {
            ipAddress = JOptionPane.showInputDialog("Insert IP address or hostname:","localhost");
            if (ipAddress != null) {
                while ((!ipAddress.matches(ValidIpAddressRegex))
                        && (!ipAddress.matches(ValidHostnameRegex))
                        ) {
                    ipAddress = JOptionPane.showInputDialog("The IP address or hostname you chose is " +
                            "not a valid ip address or hostname. " +
                            "Please insert a valid IP address or hostname:","localhost");
                } 
            }
        }
        controller.setIPAddress(ipAddress); // set the IP Address or hostname

        /*
         * Initialize JTextArea and tree
         */
        textPane= new JTextArea();
        textPane.setLineWrap(true);
        textPane.setEditable(false);
        textPane.setWrapStyleWord(true);
        textPane.setText(initialHello);
        documentTree=new JTree();

        /*
         * Replace screen
         */
        wordToReplaceField = new JTextField(20);
        wordToReplaceWithField = new JTextField(20);
        replaceOneButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");

        /*
         * initialize JButtons
         */
        //BOLD
        ImageIcon boldPic = new ImageIcon("icons/bold.png");
        boldButton = new JButton(boldPic);
        boldButton.setMaximumSize(boldButton.getPreferredSize());
        //ITALIC
        ImageIcon italicPic = new ImageIcon("icons/italic.png");
        italicButton = new JButton(italicPic);
        italicButton.setMaximumSize(italicButton.getPreferredSize());
        //PLAIN STYLE
        ImageIcon plainPic = new ImageIcon("icons/plain.png");
        plainButton = new JButton(plainPic);
        plainButton.setMaximumSize(plainButton.getPreferredSize());
        //REPLACE
        ImageIcon replace = new ImageIcon("icons/replace.png");
        replaceButton = new JButton(replace);
        replaceButton.setMaximumSize(replaceButton.getPreferredSize());
        //SAVE FILE
        ImageIcon savePic = new ImageIcon("icons/save.png");
        saveFileButton = new JButton(savePic);
        saveFileButton.setMaximumSize(saveFileButton.getPreferredSize());
        //OPEN FILE
        ImageIcon openPic = new ImageIcon("icons/open.png");
        openFileButton = new JButton(openPic);
        openFileButton.setMaximumSize(openFileButton.getPreferredSize());
        //COPY
        ImageIcon copyPic = new ImageIcon("icons/copy.png");
        copyButton = new JButton(copyPic);
        copyButton.setMaximumSize(copyButton.getPreferredSize());
        //CUT
        ImageIcon cutPic = new ImageIcon("icons/cut.png");
        cutButton = new JButton(cutPic);
        cutButton.setMaximumSize(cutButton.getPreferredSize());
        //PASTE
        ImageIcon pastePic = new ImageIcon("icons/paste.png");
        pasteButton = new JButton(pastePic);
        pasteButton.setMaximumSize(pasteButton.getPreferredSize());
        //NEW
        ImageIcon newFilePic = new ImageIcon("icons/new.png");
        newDocButton = new JButton(newFilePic);
        //SWITCH
        ImageIcon switchPic = new ImageIcon("icons/switch.png");
        switchButton = new JButton(switchPic);

        /*
         * Initialize JComboBox
         */
        fontSize = new JComboBox(sizes);
        fontSize.setSelectedIndex(3);
        fontSize.setMaximumSize(fontSize.getPreferredSize());
        fontName = new JComboBox(fonts);
        fontName.setSelectedIndex(3);
        fontName.setMaximumSize(fontName.getPreferredSize());
        colorOptions = new JComboBox(colorsName);
        colorOptions.setMaximumSize(colorOptions.getPreferredSize());

        /*
        Create the tree's root and append documents to it.
        Create a tree that allows one selection at a time.
         */
        documentTree = new JTree(top);
        documentTree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);

        /*
         * disable all JTextArea ,JButtons and JComboBoxes:
         * This will prevent an attempt to edit when the user is not on an actual document.
         * New and Switch buttons are unlocked and a click on them will release all components.
         */
        plainButton.setEnabled(false);
        boldButton.setEnabled(false);
        italicButton.setEnabled(false);
        copyButton.setEnabled(false);
        cutButton.setEnabled(false);
        pasteButton.setEnabled(false);
        replaceButton.setEnabled(false);
        saveFileButton.setEnabled(false);
        openFileButton.setEnabled(false);
        fontSize.setEnabled(false);
        fontName.setEnabled(false);
        colorOptions.setEnabled(false);

        /*
         *Create the scroll pane and add the tree to it. 
         */
        JScrollPane qPane = new JScrollPane(documentTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);        
        JScrollPane scrollBarText = new JScrollPane(textPane); // JTextArea is placed in a JScrollPane.
        JSplitPane jsp = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, true, qPane, scrollBarText);
        jsp.setDividerLocation(0.5);

        /*
         * Creates the layout of the GUI
         */
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(newDocButton)
                        .addComponent(switchButton)
                        .addComponent(plainButton)
                        .addComponent(boldButton)
                        .addComponent(italicButton)
                        .addComponent(fontSize)
                        .addComponent(fontName)
                        .addComponent(colorOptions)
                        .addComponent(copyButton)
                        .addComponent(cutButton)
                        .addComponent(pasteButton)
                        .addComponent(replaceButton)
                        .addComponent(saveFileButton)
                        .addComponent(openFileButton)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jsp))
                ));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(layout.createParallelGroup(
                        GroupLayout.Alignment.BASELINE)
                        .addComponent(newDocButton)
                        .addComponent(switchButton)
                        .addComponent(plainButton)
                        .addComponent(boldButton)
                        .addComponent(italicButton)
                        .addComponent(fontSize)
                        .addComponent(fontName)
                        .addComponent(colorOptions)
                        .addComponent(copyButton)
                        .addComponent(cutButton)
                        .addComponent(pasteButton)
                        .addComponent(replaceButton)
                        .addComponent(saveFileButton)
                        .addComponent(openFileButton)
                        )
                        .addGroup(layout.createParallelGroup(
                                GroupLayout.Alignment.BASELINE)
                                .addComponent(jsp)));

        /*
         * Attach ActionListners to JComponents for the controler's use.
         */
        newDocButton.addActionListener(this);
        switchButton.addActionListener(this);
        plainButton.addActionListener(this);
        boldButton.addActionListener(this);
        italicButton.addActionListener(this);
        fontSize.addActionListener(this);
        fontName.addActionListener(this);
        colorOptions.addActionListener(this);
        replaceButton.addActionListener(this);
        saveFileButton.addActionListener(this);
        fileChooser.addActionListener(this);
        openFileButton.addActionListener(this);
        copyButton.addActionListener(this);
        cutButton.addActionListener(this);
        pasteButton.addActionListener(this);
        replaceAllButton.addActionListener(this);
        replaceOneButton.addActionListener(this);


        /*
         * Creates a documentListener to allow insert and delete.
         */
        DocumentListener documentListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent changeEvent) {
            }
            public void insertUpdate(DocumentEvent insertEvent) {
                if(!setText) {
                    int pos = insertEvent.getOffset();
                    String text;
                    try {
                        text = textPane.getText(pos,1);

                        controller.handleInsertUpdate(pos, text);
                    } catch (BadLocationException e) {
                        throw new RuntimeException("Bad location in insertUpdate.");
                    }
                }
            }

            /*
             * RemoveUpdate detects a remove event from the document.
             * It updates the document's text by removing the char that was deleted by the user.
             */
            public void removeUpdate(DocumentEvent removeEvent) {
                if(!setText) {
                    int endLocation = removeEvent.getOffset() + removeEvent.getLength();
                    int startLocation = removeEvent.getOffset();
                    if(startLocation == endLocation) {
                        startLocation--;
                    }
                    if(startLocation > endLocation) {
                        int switchInteger = startLocation;
                        startLocation = endLocation;
                        endLocation = switchInteger;
                    }
                    controller.handleDeleteUpdate(startLocation,endLocation);}
            }
        };
        textPane.getDocument().addDocumentListener(documentListener);
        /*
         * attach a listener to the list, so we will be able to update it with new documents 
         */
        documentTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent eventForTree) {
                if(eventForTree.getPath().getLastPathComponent().toString().replace(String.format("%n"), "").contains(".txt")){
                    switchTo = eventForTree.getPath().getLastPathComponent().toString().replace(System.getProperty("line.separator"), "");
                    controller.setSwitchTo(switchTo);
                }   
            } 
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.handleAction(e);
        /*
         * Open the replace window upon a click on the replace button
         */
        if(e.getSource() == replaceButton) {
            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("Find word:"));
            myPanel.add(wordToReplaceField);
            myPanel.add(new JLabel("Replace with:"));
            myPanel.add(wordToReplaceWithField);
            myPanel.add(replaceOneButton);
            myPanel.add(replaceAllButton);
            JDialog dialog = new JDialog();
            dialog.add(myPanel);
            dialog.setSize(260, 170);
            dialog.setLocation(550, 280);
            dialog.setVisible(true); 
        }

        /*
         * Open a save file window , that will enable the user to save a file on his system.
         */
        if(e.getSource() == saveFileButton) {
            
            if (fileChooser.showSaveDialog(RTCE_View.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileWriter f;
                try {
                    f = new FileWriter(file.getPath());
                    BufferedWriter b = new BufferedWriter(f); 
                    PrintWriter out = new PrintWriter(b); 
                    fw.writeData(textPane.getText(), out);
                    out.close(); 
                } catch (IOException fileWriterException) {
                    throw new RuntimeException("Problem saving the file.");
                } 
            }
        }
        /*
         * Open an open file window, that will enable to user to find a file in it's system
         * and upload it.
         */
        if(e.getSource() == openFileButton) {
            if (fileChooser.showOpenDialog(RTCE_View.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                fileName = (file.getName());
                String fileToRead =file.toString();
                fileRead = fr.FileToString(fileToRead);
                if(fileRead != null && fileName != null){
                    controller.setOpenFile(fileName, fileRead);
                }
            }
        }
    }
    /**
     * A method to get the new document JButton.
     * @return a newDocButton JButton.
     */
    public JButton getNewDocButton() {
        return newDocButton;
    }

    /**
     * A method to get the switch document JButton.
     * @return a switchButton JButton.
     */
    public JButton getSwitchButton() {
        return switchButton;
    }

    
    /**
     * A method to get the replace JButton.
     * @return a replace JButton
     */
    public JButton getReplaceButton() {
        return replaceButton;
    }
    /**
     * A method to get the replace all JButton.
     * @return a replaceAllButton JButton.
     */
    public JButton getReplaceAllButton() {
        return replaceAllButton;
    }

    /**
     * A method to get the replace one occurrence JButton.
     * @return a replaceOneButton JButton.
     */
    public JButton getReplaceOneButton() {
        return replaceOneButton;
    }

    /**
     * The method returns an String from the replace screen. 
     * This will be the word we would like to replace.
     * @return a String of the word that we want to replace.
     */
    public String getWordToReplaceText() {
        return wordToReplaceField.getText();
    }

    /**
     * The method returns an String from the replace screen. 
     * This will be the word we would like to replace with.
     * @return a String of the word that we would like to replace with.
     */
    public String getWordToReplaceWithText() {
        return wordToReplaceWithField.getText();
    }

    /**
     * A method to get the copy JButton.
     * @return the copyButton JButton.
     */
    public JButton getCopyButton() {
        return copyButton;
    }

    /**
     * A method to get the cut JButton.
     * @return the cutButton JButton.
     */
    public JButton getCutButton() {
        return cutButton;
    }

    /**
     * A method to get the paste JButton.
     * @return the pasteButton JButton.
     */
    public JButton getPasteButton() {
        return pasteButton;
    }
    /**
     * A method to get the JTextArea.
     * @return the textPane JTextArea.
     */
    public JTextArea getTextPane() {
        return textPane;
    }

    /**
     * A method to get the plain style JButton.
     * @return the plainButton JButton.
     */
    public JButton getplainButton() {
        return plainButton;
    }

    /**
     * A method to get the bold JButton.
     * @return the boldButton JButton.
     */
    public JButton getBoldButton() {
        return boldButton;
    }

    /**
     * A method to get the italic JButton.
     * @return the italicButton JButton.
     */
    public JButton getItalicButton() {
        return italicButton;
    }
    
    
    /**
     * A method to get the save file JButton
     * @return the save file JButton
     */
    public JButton getSaveFileButton() {
        return saveFileButton;
    }
    
    /**
     * A method to get the open file JButton
     * @return the open file JButton
     */
    public JButton getOpenFileButton() {
        return openFileButton;
    }
    


    /**
     * A method to get the the font size JComboBox.
     * @return the fontSize JComboBox.
     */
    public JComboBox getFontSize() {
        return fontSize;
    }

    /**
     * A method to get the the font name JComboBox.
     * @return the fontName JComboBox.
     */
    public JComboBox getFontName() {
        return fontName;
    }

    /**
     * A method to get the the color JComboBox.
     * @return the colorOptions JComboBox.
     */
    public JComboBox getColorOptions() {
        return colorOptions;
    }

    /**
     * A method to get the the open file JFileChooser.
     * @return the fileChooser JFileChooser.
     */
    public JFileChooser getOpenFileChooser() {
        return fileChooser;
    }
    
    

    /**
     * A method to get the file name of the file we want to open from our system.
     * @return a String of the file name.
     */
    public String getOpenFileName() {
        return fileName;
    }

    /**
     * A method to get the file text from the file we want to open from our system.
     * @return a String of the file content.
     */
    public String getOpenFileRead() {
        return fileRead;
    }

    /**
     * A method to lock the documentListner from detecting the set of a new document in the file
     * after a view update. 
     * 
     * SetText is a thread safe method, which will prevent concurency issues
     */
    public void setViewText(String text) {
        setText = true;
        textPane.setText(text);
        setText = false;
    }

    /**
     * A method to set the caret position to it's original position after an insert.
     * @param position - the old position of the caret that we would like to set to.
     */
    public void setLocationAfterInsert(int position) {
        textPane.setCaretPosition(position);
    }

    /**
     * A method to set the title of the JFrame , to the name of the current active document.
     * This will indicate to the user what file is is working on.
     * @param fileName - a name of the current active document that the user is working on.
     */
    public void setViewTitle(String fileName) {
        setTitle(fileName);
    }

    /**
     * A Method to release the screen JComponents, after a first creation of a new document or a switch to
     * an active document in the document tree.
     * @param bool - a boolean to release the JComponents (true), or to lock them (false).
     */
    public void releaseScreen(boolean bool) {
        textPane.setEditable(bool);
        plainButton.setEnabled(bool);
        boldButton.setEnabled(bool);
        italicButton.setEnabled(bool);
        copyButton.setEnabled(bool);
        cutButton.setEnabled(bool);
        pasteButton.setEnabled(bool);
        replaceButton.setEnabled(bool);
        saveFileButton.setEnabled(bool);
        openFileButton.setEnabled(bool);
        fontSize.setEnabled(bool);
        fontName.setEnabled(bool);
        colorOptions.setEnabled(bool);
    }

    /**
     * A method to update the document tree with a new document based on its name.
     * @param docName - the document name that will be updated in the documents tree.
     */
    public void updateDocTree(String docName) {
        buildTheDocumentTree(docName, top);
        ((DefaultTreeModel)documentTree.getModel()). //line continues
        nodeStructureChanged((DefaultMutableTreeNode)top);
    }

    /**
     * The method takes a list of documents name and will add all of those documents to the tree's root.
     * The list of active documents comes from the RTCE_Server
     * @param list- a list of all of the active documents on the server.
     * @param treeRoot- the tree's root
     */
    private void buildTheDocumentTree(String docName, DefaultMutableTreeNode treeRoot ){
        DefaultMutableTreeNode category = null;            
        category = new DefaultMutableTreeNode(docName);
        treeRoot.add(category);
    }

    /**
     * A method that tries to connect to a valid IP address or to a valid host name. The method will try to
     * connect and prompt the user for another IP address if the connection failed.
     * @param chosenIp - an IP address or a valid host name to connect to.
     */
    public void setIpAddressAgain(String chosenIp) {
        ipAddress = chosenIp ;
        while ((!ipAddress.matches(ValidIpAddressRegex))
                && (!ipAddress.matches(ValidHostnameRegex))) {
            ipAddress = JOptionPane.showInputDialog("The IP address or hostname you chose is " +
                    "not a valid ip address or hostname. Please insert a valid IP address or hostname:","localhost");
        }
    }
    
    /**
     * A method to get the text on the pane. Mainly used for
     * testing.
     * @return String the text that the JTextPane contains.
     */
    public String  getText() {
        return textPane.getText();
    }
}