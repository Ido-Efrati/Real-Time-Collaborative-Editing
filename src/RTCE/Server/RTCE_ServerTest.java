package RTCE.Server;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLEncoder;

import org.junit.Test;

/*
 * Most of the testing for the server must be done as a Black Box 
 * Testing. (As we were taught in PS3). One of the main reasons we 
 * built terminal capability for interacting with the server is 
 * for debugging and testing. 
 * 
 * Here we use it to test our server. We do as follows. 
 * 
 *      1) Run the server
 *      
 *      2) Open the terminal and connect to port 4444
 *         with the IP Address the server is running from.
 *         
 *      3) Try out all the protocol messages from Final Design
 *         Document.pdf and make sure that all specification are
 *         followed correctly. 
 *      
 *      4) Caution: The GUI makes sure that you don't call 'view',
 *         'insert' or 'delete' when you are not switched to any
 *         document. The server does not check for that. So do
 *         not send these messages from the terminal.
 *         
 *     Testing of some methods that can be tested with JUnit:
 *          
 *          Strategy:
 *          
 *                  1) Try all the messages with the handleRequest method.
 *                     Make sure that what it returns is according to protocol.
 *                     This will also automatically test the helper functions
 *                     for each request.
 *                     
 *                  2) Try an invalid message. Make sure that it returns the
 *                     "Invalid Request." response.
 *                     
 *                  3) Test the user number getter and setter.
 *                  
 *                  4) Test the document number counter getter
 *                     and setter.
 *          
 */
public class RTCE_ServerTest {
    Socket socket1 = new Socket();
    Socket socket2 = new Socket();
    
    Thread thread1 = new Thread();
    Thread thread2 = new Thread();
    
    RTCE_User client1 = new RTCE_User(thread1, "client1", socket1);
    RTCE_User client2 = new RTCE_User(thread2, "client2", socket2);

    @Test
    // Testing the 'new' request without a file name specified
    public void newUntitledRequestTest() throws UnsupportedEncodingException {
        String newRequest = RTCE_Server.handleRequest("new", client1);
        assertEquals(newRequest, URLEncoder.encode("untitled1.txt", "UTF-8") 
                + String.format("%n") + "EOF");
    }
    
    @Test
    // Testing the 'new' request with a filename specified
    public void newWithNameRequestTest() throws UnsupportedEncodingException {
        String newRequest = RTCE_Server.handleRequest("new test.txt", client2);
        assertEquals(newRequest, URLEncoder.encode("test.txt", "UTF-8") 
                + String.format("%n") + "EOF");
        
    }
    
    @Test
    // Testing the 'switch' and 'insert' requests
    public void insertRequestTest() throws UnsupportedEncodingException {
        RTCE_Server.handleRequest("switch untitled1.txt", client1);
        String insertRequest = RTCE_Server.handleRequest("insert 0 hello", client1);
        assertEquals(insertRequest, URLEncoder.encode("hello", "UTF-8") 
                + String.format("%n") + "EOF");
    }
    
    @Test
    // Testing the 'delete' request
    public void deleteRequest() throws UnsupportedEncodingException {
        RTCE_Server.handleRequest("new", client1);
        RTCE_Server.handleRequest("insert 0 hello world", client1);
        String deleteRequest = RTCE_Server.handleRequest("delete 0 3", client1);
        assertEquals(deleteRequest, URLEncoder.encode("lo world", "UTF-8") 
                + String.format("%n") + "EOF"); 
    }
    
    @Test
    // Tests the 'view' request
    public void viewRequestTest()  throws UnsupportedEncodingException {
        RTCE_Server.handleRequest("switch test.txt", client2);
        RTCE_Server.handleRequest("insert 0 testing view", client2);
        String viewRequest = RTCE_Server.handleRequest("view", client2);
        assertEquals(viewRequest, URLEncoder.encode("testing view", "UTF-8") 
                + String.format("%n") + "EOF");
    }
    // Tests the 'hello' request
    public void helloRequestTest() throws UnsupportedEncodingException {
        String helloRequest = RTCE_Server.handleRequest("hello", client1);
        assertEquals(helloRequest, "Welcome to RTCE " 
                + Integer.toString(RTCE_Server.getNumberOfUsers()) +
                " people are currently connected. Type help for help."
                + String.format("%n") + "EOF");
    }
    
    @Test
    // Tests the 'list' request
    public void listRequestTEst() throws UnsupportedEncodingException {
        String listRequest = RTCE_Server.handleRequest("list", client1);
        assertEquals(listRequest, "untitled1.txt "
                    + String.format("%n") + "test.txt "
                    + String.format("%n") + "untitled2.txt "
                    + String.format("%n")
                    + String.format("%n")
                    + "EOF");
    }
    
    @Test
    // Tests the 'help' request
    public void helpRequestTest() throws UnsupportedEncodingException {
        String helpRequest = RTCE_Server.handleRequest("help", client1);
        assertEquals(helpRequest, "Welcome to RTCE. Please use one of the follwoing commands:"
                + String.format("%n") +
                "view- shows the user the current document" +
                String.format("%n") +
                "insert x text- insert text to a numeric postion" 
                + String.format("%n") +
                "delete x text- delete text from a numeric postion"
                + String.format("%n") +
                "help- shows the user the help menu"
                + String.format("%n") +
                "list- shows the user a list of current open documents"
                + String.format("%n") +
                "switch file - allows the user to switch from one open document to another"
                + String.format("%n") +
                "new file - creates a new file, if file is not specified it will be named by the server"
                + String.format("%n") + "exit- close the connection to the server"
                + String.format("%n") + "hello- return an hello message"
                + String.format("%n")
                + "EOF");
    }
    
    @Test
    // Tests the 'exit' request
    public void exitRequestTest() throws UnsupportedEncodingException {
        String exitRequest = RTCE_Server.handleRequest("exit", client1);
        assertEquals(exitRequest, "exit" + String.format("%n") + "EOF");
    }
    
    
    @Test
    // Tests the 'replaceOne' and the 'ReplaceAll' requests
    public void replaceRequestsTest() throws UnsupportedEncodingException {
        RTCE_Server.handleRequest("new", client1);
        RTCE_Server.handleRequest("insert 0 hello hello hello hello", client1);
        String replaceOne = RTCE_Server.handleRequest("replaceOne hello bye", client1);
        String replaceAll = RTCE_Server.handleRequest("replaceAll hello bye", client1);
        
        assertEquals(replaceOne, URLEncoder.encode("bye hello hello hello", "UTF-8")
                                 + String.format("%n") + "EOF");
        assertEquals(replaceAll, URLEncoder.encode("bye bye bye bye", "UTF-8")
                + String.format("%n") + "EOF");
    }
    
    @Test
    // Tests the 'style' and 'giveStyle' requests
    public void styleRequestsTest() throws UnsupportedEncodingException {
        RTCE_Server.handleRequest("new", client2);
        RTCE_Server.handleRequest("style 1 2 3 4", client2);
        String giveStyle = RTCE_Server.handleRequest("giveStyle", client2);
        assertEquals(giveStyle, URLEncoder.encode("1 2 3 4", "UTF-8")
                                + String.format("%n") + "EOF");
    }
    
    @Test
    // Tests an invalid request to the server
    public void invalidRequestTest() throws UnsupportedEncodingException {
        String invalidRequest = RTCE_Server.handleRequest("badsjd", client1);
        assertEquals(invalidRequest,"Invalid Request."
            + String.format("%n") + "EOF");
    }
    
    @Test
    // Tests the updateUsers  and getNumberOfUsers methods
    public void updateUsersTest() {
        RTCE_Server.updateUsers(1);
        assertEquals(RTCE_Server.getNumberOfUsers(), 1);
    }
    
    @Test
    // Tests the updateDocumentCounter method
    public void updateDocCounterTest() {
        RTCE_Server.updateDocumentCounter(1);
        assertEquals(RTCE_Server.getDocumentCounter(), 6); // we already have 6 docs
    }
    
}
