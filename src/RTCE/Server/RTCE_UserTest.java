package RTCE.Server;

import static org.junit.Assert.*;

import java.net.Socket;

import org.junit.Test;

import RTCE.Document.RTCE_Document;

/**
 * Test suite for the RTCE_User class to make sure all its
 * methods work properly.
 * 
 * Strategy: Test constructor
 *           Try all setter methods
 *           Try all getter methods
 *           Try a number of setter and getters in succession
 * @author Philippe
 *
 */
public class RTCE_UserTest {

    @Test
    public void testBasicConstruct() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_User client = new RTCE_User(t, test, socket);
        assertEquals(client.getThread(), t);
        assertEquals(client.getName(), test);
        assertEquals(client.getSocket(), socket);
    }
    
    @Test
    public void setThreadTest() {
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_User client = new RTCE_User(t1, test, socket);
        client.setThread(t2);
        assertEquals(client.getThread(), t2);
    }
    
    @Test
    public void setDocTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_Document doc = new RTCE_Document("test.txt");
        RTCE_User client = new RTCE_User(t, test, socket);
        client.setDoc(doc);
        assertEquals(client.getDoc(), doc);
    }
    
    @Test
    public void setDocAndChangeDocTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_Document doc = new RTCE_Document("test.txt");
        RTCE_User client = new RTCE_User(t, test, socket);
        client.setDoc(doc);
        client.getDoc().insert(1, "philippe");
        assertEquals(client.getDoc(), doc);
    }
    
    @Test
    public void setNameTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        @SuppressWarnings("unused")
        RTCE_User client = new RTCE_User(t, test, socket);
        
    }
    
    @Test
    public void setSocketTest() {
        Thread t = new Thread();
        String test = "test";
        Socket s1 = new Socket();
        Socket s2 = new Socket();
        RTCE_User client = new RTCE_User(t, test, s1);
        client.setSocket(s2);
        assertEquals(client.getSocket(), s2);
    }
    
    @Test
    public void getThreadTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_User client = new RTCE_User(t, test, socket);
        assertEquals(client.getThread(), t);
    }
    
    @Test
    public void getDocTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_Document doc = new RTCE_Document("test.txt");
        RTCE_User client = new RTCE_User(t, test, socket);
        client.setDoc(doc);
        assertEquals(client.getDoc(), doc);
    }
    
    @Test
    public void getNameTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_User client = new RTCE_User(t, test, socket);
        String name = client.getName();
        assertEquals(name, test);
    }
    
    @Test
    public void getSocketTest() {
        Thread t = new Thread();
        String test = "test";
        Socket socket = new Socket();
        RTCE_User client = new RTCE_User(t, test, socket);
        Socket soc = client.getSocket();
        assertEquals(soc, socket);
    }
}