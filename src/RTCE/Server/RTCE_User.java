package RTCE.Server;

import java.net.Socket;

import RTCE.Document.RTCE_Document;

/*
 * Thread safety argument
 * The user class represent data about each client that is connected to the server. Since each user 
 * is allocated its own port and its own thread we can claim concurrency safety by confinement.
 * by confinement - all of the method in this class are performed on one thread that is localized to the user
 * and are not shared with any other clients. Furthermore, there is no actual race condition because each
 * method is responsible to update a different JComponent , so there is no concern that their order of
 * execution will interfere with the view.  

 */

/**
 * User class for the RTCE. This is useful to differentiate
 * between users on the server. Every User has four (4) 
 * fields: 1) The thread it is on. 2) its name. 3) its socket.
 * 4) the document it is currently working on.
 *
 */
public class RTCE_User {
    Thread thread;
    RTCE_Document doc;
    String name;
    Socket socket;

    /**
     * Constructor for RTCE_Client. Takes three of the fields
     * as parameters. Takes the thread, its name and the socket.
     * @param Thread t, the Thread the client is on.
     * @param String name, the Client's name.
     * @param Socket socket, the socket the client is using.
     */
    public RTCE_User(Thread t, String name, Socket socket) {
        this.thread = t;
        this.name = name;
        this.socket = socket;
    }


    /**
     * Method to set the Client's Thread.
     * @param Thread t, the thread you want to give the Client.
     */
    public void setThread(Thread t) {
        this.thread = t;
    }


    /**
     * Method to set the Client's document.
     * @param RTCE_Docuemtn doc, the document you want to give it.
     */
    public void setDoc(RTCE_Document doc) {
        this.doc = doc;
    }

    /**
     * Method to set the Client's name.
     * @param String name, the name you want to give the document.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method to set the Client's socket.
     * @param Socket socket, the socket you want to assign to the user.
     * Usually this should not be changed.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Method to get the Thread that the Client is on.
     * @return Thread. The thread the Client is on.
     */
    public Thread getThread() {
        return this.thread;
    }

    /**
     * Method to get the Document object the Client is currently
     * focused on.
     * @return RTCE_Document. The RTCE_Document object that the Client
     * is currently on.
     */
    public RTCE_Document getDoc() {
        return this.doc;
    }

    /**
     * Method to get the name of the Client.
     * @return String, the name of the Client.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method to get the the Socket the client is using
     * to connect to the server.
     * @return Socket, the socket the client is using to 
     * connect to the server.
     */
    public Socket getSocket() {
        return this.socket;
    }
}