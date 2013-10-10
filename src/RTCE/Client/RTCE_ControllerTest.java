package RTCE.Client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Test;


/* 
 * 
 * We have tried to make our code as unit testable as possible. However,
 * many GUI parts are difficult to test with JUnit. For those tests please
 * view MVC_BlackBoxTesting.java for a full description.
 * 
 * Here we will test as much as we could with JUnit.
 * 
 * Some of these tests were run with the server open. YOU MUST MANUALLY
 * RUN THE SERVER BEFORE RUNNING THESE TESTS.
 * Therefore, any test assumes that the server has been running
 * through all of them. They are consecutive tests on one server.
 * 
 * Also, this test creates 6 clients. So if it passes it shows that our
 * server can handle at least 6 clients. There is no theoretical limit to 
 * the amount of clients our server can handle.
 * 
 * Testing strategy:
 * 
 *      The only methods that could be tested with JUnit are the openConnection()
 *      method that opens the connection with the server and the makeRequest() method
 *      that sends requests to the server. The rest of the controller is a bunch
 *      of actionListener handlers that directly call the model to update the view.
 *      
 *      Strategy:
 *      
 *          1) Test new file
 *          
 *          2) Test insert regular
 *          
 *          3) Test user defined file name
 *          
 *          4) Add more untitled files by calling new
 *          
 *          5) Test the list request
 *          
 *          6) Test the switch request by switching, inserting text
 *             and reading what was inserted
 *             
 *          7) Test the view request
 *          
 *          8) Test the delete for a regular case
 *          
 *          9) Test the hello message. Make sure that the server
 *             knows the correct amount of clients connected
 *             
 *          10) Test replaceOne request
 *          
 *          11) Test replaceAll request
 *          
 *          12) Test inserting spaces
 *          
 *          13) Test inserting newlines
 *          
 *          14) Test inserting crazy ASCII characters
 *          
 *          15) Test deleting with out of bounds
 *          
 *          16) Test deleting one character
 *          
 *          17) Test deleting zero characters
 *          
 *          18) Test that non ASCII text returns invalid request
 *          
 *          19) Test the set and get IP Adress Methods
 */

public class RTCE_ControllerTest {   
    
    @Test
    public void makeRequestNewAndInsertTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("new");
        String returnInsert = RTCE_Controller.makeRequest("insert 0 madam im adam");
        assertEquals(returnInsert, "madam im adam");    
    }
    
    @Test
    public void makeRequestNewCheckListTest () throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("new");
        RTCE_Controller.makeRequest("new");
        RTCE_Controller.makeRequest("new hahah.txt");
        String returnList = RTCE_Controller.makeRequest("list");
        assertEquals(returnList, "untitled1.txt untitled2.txt untitled3.txt hahah.txt ");
    }
    
    @Test
    public void makeRequestSwitchInsertTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch untitled3.txt");
        String returnInsert = RTCE_Controller.makeRequest("insert 3 hello world!");
        assertEquals(returnInsert, "   hello world!");
    }
    
    @Test
    public void makeRequestSwitchDeleteTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch untitled1.txt");
        String returnDelete = RTCE_Controller.makeRequest("delete 0 6");
        assertEquals(returnDelete, "im adam");
    }
    
    @Test
    public void makeRequesrViewRequestTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch untitled1.txt");
        String view = RTCE_Controller.makeRequest("view");
        assertEquals(view, "im adam");
    }
    
    @Test
    public void makeRequestHelloTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        String hello = RTCE_Controller.makeRequest("hello");
        assertEquals(hello, "Welcome to RTCE 6 people are currently " +
        		"connected. Type help for help.");
    }
    
    @Test
    public void makeRequestReplaceTest() throws IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch untitled2.txt");
        RTCE_Controller.makeRequest("insert 0 hello hello hello");
        String replaced = RTCE_Controller.makeRequest("replaceOne hello bye");
        assertEquals(replaced, "bye hello hello");
    }
    
    @Test
    public void makeRequestReplaceAllTest() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch untitled2.txt");
        String replacedAll = RTCE_Controller.makeRequest("replaceAll hello bye");
        assertEquals(replacedAll, "bye bye bye");
    }
    
    
    // I put these all in one because I don't want to overload
    // the server with so many clients.
    @Test
    public void makeRequestEdgeCases() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("switch hahah.txt");
        String spaceInserted = RTCE_Controller.makeRequest("insert 0 " + String.format(" "));
        String newLineInserted = RTCE_Controller.makeRequest("insert 1 "+ String.format("%n"));
        String deletedAllWithOutOfBounds = RTCE_Controller.makeRequest("delete 0 1000");
        String crazyCharacterInsert = RTCE_Controller.makeRequest("insert 1 !@#$%^&*()_QWERTYUIOP{}ASDFGHJKL:ZXCVBNM<>?");
        String deleteInMiddle = RTCE_Controller.makeRequest("delete 11 16" );
        String deleteOne = RTCE_Controller.makeRequest("delete 5 6");
        String deleteZero = RTCE_Controller.makeRequest("delete 5 5");
        
        assertEquals(spaceInserted, " ");
        assertEquals(newLineInserted, " " + String.format("%n"));
        assertEquals(deletedAllWithOutOfBounds, "");
        assertEquals(crazyCharacterInsert, " !@#$%^&*()_QWERTYUIOP{}ASDFGHJKL:ZXCVBNM<>?");
        assertEquals(deleteInMiddle, " !@#$%^&*()TYUIOP{}ASDFGHJKL:ZXCVBNM<>?" );
        assertEquals(deleteOne, " !@#$^&*()TYUIOP{}ASDFGHJKL:ZXCVBNM<>?");
        assertEquals(deleteZero, " !@#$^&*()TYUIOP{}ASDFGHJKL:ZXCVBNM<>?");
    }
    
    @Test
    public void makeRequestOFNonASCIIChar() throws UnknownHostException, IOException {
        RTCE_Controller.openConnection();
        RTCE_Controller.makeRequest("new");
        String nonASCIIreturn = RTCE_Controller.makeRequest("insert 0 ×©×œ×•×�");
        assertEquals(nonASCIIreturn, "Invalid Request.");
    }
    
    public void setIPAddressTest() {
        RTCE_View view = new RTCE_View();
        RTCE_Model model = new RTCE_Model(view);
        RTCE_Controller controller = new RTCE_Controller(view, model);
        controller.setIPAddress("localhost");
        assertEquals(controller.getIPAddress(), "localhost");
        
        controller.setIPAddress("127.0.0.1");
        assertEquals(controller.getIPAddress(), "localhost");
        
        controller.setIPAddress("192.168.1.100");
        assertEquals(controller.getIPAddress(),"192.168.1.100" );

        
    }
    

}
