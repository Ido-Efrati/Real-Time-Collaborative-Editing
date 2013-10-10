package RTCE.Document;

import java.util.ArrayList;

import RTCE.Server.RTCE_Queue;
import RTCE.Server.RTCE_User;
/*
 * Thread safety argument
 * The document class holds the actual document the the users are editing
 * The constructor does not need to be synchronize because it is making a new object that is confined. 
 * Therefore, Java won't let you synchronize it. 
 * 
 *all of the methods that might mutate the data, and can be subjects for race condition are locked by 
 *synchronization and as a result they are thread safe.
 */
/**
 * The RTCE_Document class for the RTCE. 
 * Supports all specification from Design (see Design.pdf).
 * Has two fields: the String Document name, docName and 
 * the text in the document, called data, represented as a
 * StringBuffer.
 * @author Philippe
 *
 */
public class RTCE_Document {

    private String docName;
    private String style="1 0 12 0";
    private StringBuffer data;
    private RTCE_Queue requestQueue;
    private ArrayList<RTCE_User> listOfClients = new ArrayList<RTCE_User>();
    /**
     * Constructor for RTCE_Document. The only parameter that is 
     * needed is the name of the document as a String. The data is 
     * instantiated as an empty (length 16) StringBuffer.
     * @param String docName, the document name
     */
    public RTCE_Document(String docName) {
        this.docName = docName;
        this.data = new StringBuffer();
        requestQueue = new RTCE_Queue(this);
    }

    /**
     * Inserts text at a given position in the Document.
     * This is the first of the two "Edit's" supported
     * for the RTCE.
     * modifies: docName.
     * @param int pos, requested position for insertion.
     * @param String text, the text to insert.
     */
    public synchronized void insert(int pos, String text) {

        if (pos >= 0 && pos <= this.data.length()) this.data.insert(pos, text);
        else if (pos >= 0) {
            int len = this.data.length();
            for (int i = 0; i <= pos - len - 1; i++) {
                this.data.append(" ");
            }
            this.data.append(text);
        }
    }

    /** A method to get the list of all users working on
     * a document so that the server can know what users
     * are working on a given document at a given time.
     * @return ArrayList<RTCE_User>, an ArrayList of RTCE_Users.
     */
    public  ArrayList<RTCE_User> getList(){
        return listOfClients;
    }

    /**
     * A method to add a user to the ArrayList that stores
     * all the users working on a document at a given time.
     * @param RTCE_User u, the user you want to add.
     */
    public void setUser(RTCE_User u){
        listOfClients.add(u);
    }

    /**
     * Deletes text at a given position in the Document.
     * This is the second of the two "Edit's" supported
     * for the RTCE.
     * @param int beginPos, from where to delete, inclusive.
     * @param int endPos, until where to delete, exclusive.
     */
    public synchronized void delete(int beginPos, int endPos) {
        try {
            this.data.delete(beginPos, endPos);
        } catch (StringIndexOutOfBoundsException e) {
            throw new RuntimeException("Index bounds error in delete of RTCE_Document.");
        }
    }

    /**
     * A method to replace ALL the instances of a given string
     * "replaceFrom" to another string "replaceTo".
     * @param String replaceFrom, the string you want to replace.
     * @param String replaceTo, the string you want to replace with.
     */
    public synchronized void replaceAll(String replaceFrom, String replaceTo) {
        String dataTemp = this.data.toString();
        String replacedDataTemp = dataTemp.replaceAll(replaceFrom, replaceTo);
        this.data = new StringBuffer(replacedDataTemp);
    }

    /**
     * A method to replace the FIRST instance of a given String
     * "replaceFrom" to another String "replaceTo".
     * @param String replaceFrom, the string you want to replace.
     * @param String replaceTo, the string you want to replace with.
     */
    public synchronized void replaceOne(String replaceFrom, String replaceTo) {
        String dataTemp = this.data.toString();
        String replacedDataTemp = dataTemp.replaceFirst(replaceFrom, replaceTo);
        this.data = new StringBuffer(replacedDataTemp);
    }


    /**
     * Method to set the document name to the given String.
     * @param String docName
     */
    public void setName(String docName) {
        this.docName = docName;
    }

    /**
     * Method that allows you to set the data of the Document
     * directly. You must give it a StringBuffer
     * @param String data, the data you want to give the Document.
     */
    public synchronized void setDate(StringBuffer data) {
        this.data = data;
    }

    /**
     * Method to get the document name
     * @return String, the document name.
     */
    public String getName() {
        return this.docName;
    }

    /**
     * Method to get all the data in the Document.
     * Returns a copy of the data so mutability of
     * StringBuffer is not an issue.
     * @return StringBuffer, the StringBuffer representing the data in
     * the Document.
     */
    public synchronized StringBuffer getData()  {
        String dataString = this.data.toString();
        StringBuffer dataCopy = new StringBuffer(dataString);
        return dataCopy;
    }

    /**
     * Method to get all the text in the document as a String.
     * @return String of entire text in document.
     */
    public synchronized String getAllText() {
        return this.data.toString();
    }

    /**
     * A method to set the style of the document. Input given
     * as a string of 4 numbers seperated by spaces. 
     * eg: "1 1 12 1"
     * Format: <br>
     * "x_0, x_1, x_2, x_3": <br>
     * x_0: Font Name.<br>
     * x_1: Style (0 = plain, 1 = bold, 2 = italic)<br>
     * x_2: Font Size.<br>
     * x_3: Colour.<br>
     * @param String styleToSet, comprised of 4 numbers.
     */
    public synchronized void setStyle(String styleToSet) {
        this.style = styleToSet;
    }

    /**
     * A method to get the style of the document. Return
     * value returned as a String of 4 numbers seperated by spaces.
     * eg: "1 1 12 1"
     * Format: <br>
     * "x_0, x_1, x_2, x_3": <br>
     * x_0: Font Name.<br>
     * x_1: Style (0 = plain, 1 = bold, 2 = italic)<br>
     * x_2: Font Size.<br>
     * x_3: Color.<br>
     * @return String, the style of the document as a string of 4 numbers.
     */
    public synchronized String getStyle() {
        return this.style;
    }
    /**
     * Method to get the text within a document in a given range.
     * @param int beginPos, from where we want the text, inclusive.
     * @param int endPos, until where we want the text, exclusive.
     * @return String, the text at position between beginPos and endPos.
     */
    public synchronized String getTextAtPos(int beginPos, int endPos) {
        return this.data.subSequence(beginPos, endPos).toString();
    }

    /**
     * Method to get a char at a given position. 
     * @param int pos, the position at which you want to get the char.
     * @return char, character at pos.
     */
    public synchronized char getCharAt(int pos) {
        try {
            return this.data.charAt(pos);
        } catch(IndexOutOfBoundsException e) {
            throw new RuntimeException("No char at this point.");        
        }
    }

    /**
     * Get the length of the text in the document.
     * @return int, the length of the document as an int.
     */
    public int getLength() {
        return this.data.length();
    }

    /**
     * Public getter for the queue of edit requests for
     * a given document.
     * @return RTCE_Queue, The RTCE_Queue for this document.
     */
    public RTCE_Queue getQueue() {
        return requestQueue;
    }

    /**
     * Method that allows you to make a shallow copy of an
     * RTCE_Document.
     * @return RTCE_Document, a copy of the Document.
     */
    public RTCE_Document copy() {
        RTCE_Document doc = new RTCE_Document(this.getName());
        doc.setDate(this.getData());
        return doc;
    }

    /**
     * Hash function for the RTCE_Document datatype. Used to
     * compare multiple versions of the same document and manage
     * the application of operational transformations. Using
     * getAllText() instead of accessing the data field directly
     * because a StringBuffer cannot be hashed.
     * 
     * @return Integer representing the hash of the document.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.getAllText() == null) ? 0 : this.getAllText().hashCode());
        result = prime * result + ((docName == null) ? 0 : docName.hashCode());
        return result;
    }

    /**
     * Tests equality of two RTCE_Documents. Used to compare
     * multiple versions of the same document and manage the
     * application of operational transformations.
     * 
     * @return True if the two documents are equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RTCE_Document other = (RTCE_Document) obj;
        if (this.getAllText() == null) {
            if (other.getAllText() != null)
                return false;
        } else if (!this.getAllText().equals(other.getAllText()))
            return false;
        if (docName == null) {
            if (other.docName != null)
                return false;
        } else if (!docName.equals(other.docName))
            return false;
        return true;
    }
}