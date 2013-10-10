package RTCE.Server;

import RTCE.Document.RTCE_Document;
/*
 * Thread safety argument: since the main mutator method for this class,
 * applyTransform, is exclusively called from the resolveRequest method
 * in RTCE_Queue, the queue has to be locked as a precondition and thus
 * cannot be mutated for the duration of the request resolution by
 * another thread. We also synchronize the document in the applyEdit method
 * to avoid the possibility of a race condition.

 */
/**
 * Represents a edit request made for a particular document. Has five
 * fields: requestType (insert or delete), parentDoc (the document for
 * which the request was made), startPos (starting position of edit),
 * endPos (ending position of edit), insertText (text to add - for
 * insert only). Rep invariant is parentDoc, the document to which the
 * edit is being applied, which is immutable.
 * 
 */
public class RTCE_Request {
    public final RTCE_Document parentDoc;

    private String requestType;
    private int startPos;
    private int endPos;
    private String insertText;

    /**
     * Constructor methods for a RTCE_Request, which are dispatched based
     * on insert and delete messages sent from the client to the server.
     * The parameters for the request object are given by the messages.
     * 
     */

    /**
     * Constructor for an insert request:
     * 
     * @param String type, a String representing the request type (insert or delete).
     * @param int start, An integer representing the starting index of the request.
     * @param int end, An integer representing the ending index of the request.
     * @param String text, String of text to insert (leave empty for delete requests).
     * @param RTCE_Document doc, the document you want to edit.
     */
    public RTCE_Request(String type, int start, String text, RTCE_Document doc) {
        requestType = type;
        startPos = start;
        endPos = start + text.length();
        insertText = text;
        parentDoc = doc;
    }

    /**
     * Constructor for the delete request. This takes a start position and an
     * end position insted of a string.
     *
     * @param String type, the type of request you want. In this case will be "delete.
     * @param int start, an integer representing the start index of the request.
     * @param int end, an integer representing the end index of the request. 
     * @param doc, the document you want to edit.
     */
    public RTCE_Request(String type, int start, int end, RTCE_Document doc) {
        requestType = type;
        startPos = start;
        endPos = end;
        insertText = "";
        parentDoc = doc;
    }

    /**
     * Applies an operational transform for a request, mutating the startPos
     * and endPos fields based on displacements caused when edits are made.
     * See RTCE_RequestTest.java or the design doc for a full description of
     * the operational transformation needed for each possible configuration.
     * 
     * @param other The RTCE_Request that is being resolved by the server;
     * that is, a request that precedes this one in the queue.
     */
    public void applyTransform(RTCE_Request other) {
        if (other.getStartPos() >= endPos) // an edit only shifts text that follows it
            return;
        else if (other.getEndPos() <= startPos) { // shift without any overlapping chars
            if (other.requestType == "insert") {
                startPos += (other.getEndPos() - other.getStartPos());
                endPos += (other.getEndPos() - other.getStartPos());
            } else { // other.requestType == "delete"
                startPos -= (other.getEndPos() - other.getStartPos());
                endPos -= (other.getEndPos() - other.getStartPos());
            }
        } else { // overlapping chars
            if (other.requestType == "delete" && requestType == "delete") { // delete-delete
                if (other.getStartPos() >= startPos && other.getEndPos() >= endPos) // A starts/ends after B
                    endPos = other.getStartPos();
                else if (other.getStartPos() <= startPos && other.getEndPos() <= endPos) { // A starts/ends before B
                    startPos = other.getEndPos();
                    startPos -= other.getEndPos() - other.getStartPos();
                    endPos -= other.getEndPos() - other.getStartPos();
                } else if (other.getStartPos() <= startPos && other.getEndPos() >= endPos) { // B is contained in A
                    parentDoc.getQueue().removeRequest(this); // no additional deletion
                } else if (other.getStartPos() >= startPos && other.getEndPos() <= endPos) { // A is contained in B
                    endPos = other.getStartPos() + endPos - other.getEndPos();
                }
            } else if (other.requestType == "insert" && requestType == "insert") { // insert-insert
                startPos = other.getStartPos(); // move insert to the start of the inserted portion
                endPos = startPos + insertText.length();
            } else if (other.requestType == "delete" && requestType == "insert") { // delete-insert
                startPos = other.getStartPos(); // move insert to the start of the deleted portion
                endPos = startPos + insertText.length();
            } else { // insert-delete
                int shift = other.getInsertText().length();
                RTCE_Queue queue = parentDoc.getQueue();
                endPos = other.getStartPos(); // startPos, other.getStartPos()
                RTCE_Request sub = new RTCE_Request("delete", other.getStartPos() + shift,
                                                    other.getEndPos() + shift, parentDoc);
                queue.addRequestAtIndex(queue.findRequest(this) + 1, sub);
                // other.getEndPos() + shift, endPos + shift
            }
        }
    }

    /**
     * Resolves the request, applying the edit to the current sequence.
     */
    public void applyEdit() {
        if (requestType == "insert") {
            synchronized(parentDoc) {
                parentDoc.insert(startPos, insertText);
            }
        } else { // requestType == "delete"
            synchronized(parentDoc) {
                parentDoc.delete(startPos, endPos);
            }
        }
    }

    /**
     * Resolves the request, applying the edit to the current sequence.
     * Method used specifically for JUnit testing.
     * @param currText String representing the current sequence of text.
     * @return String representing the sequence after the edit has been applied.
     */
    public String applyEditTesting(String currText) {
        StringBuilder sb = new StringBuilder(currText);
        if (requestType == "insert") {
            sb.insert(startPos, insertText);
            return sb.toString();
        } else { // requestType == "delete"
            sb.delete(startPos, endPos);
            return sb.toString();
        }
    }

    /**
     * Method to get the type of edit represented by a request.
     * @return String requestType String representing the edit type.
     */
    public String getRequestType() {
        return requestType;
    }

    /**
     * Method to get the start position of the edit represented by
     * a request.
     * @return int startPos The index of the start of this edit (inclusive).
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * Method to get the end position of the edit represented by
     * a request.
     * @return int endPos The index of the end of this edit (exclusive).
     */
    public int getEndPos() {
        return endPos;
    }

    /**
     * Method to get the text of the insert represented by this
     * request.
     * @return String insertText The text to be inserted.
     */
    public String getInsertText() {
        return insertText;
    }
}