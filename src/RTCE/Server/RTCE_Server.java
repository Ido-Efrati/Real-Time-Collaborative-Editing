package RTCE.Server;

import java.io.*;
import java.net.*;
import java.util.*;

import RTCE.Document.RTCE_Document;
/*Thread Safety Argument:
 * RTCE_Server is a server in which each user can access documents to edit.  
 * Clients make requests of the form serve. which creates a new socket connection for the client.
 * and for each request, the server creates a specific thread for the user. so no two threads can interleave
 * In addition the client is create and not stored so we can't mutate it.
 * 
 * the counter for the amount of people on the server is synchronized so we won't try to increment or decrement 
 * the number at the same time.
 * 
 * all of the messages that the server can send to the client, hello or help are also synchronized 
 * to prevent interleaving 
 * 
 *  all of the document methods are synchronized (the document is thread safe). 
 *  By calling any of them from handleRequest won't cause concurrency issues,
 *  because it is not possible for two invocations of synchronized methods on the same object to interleave. 
 *  When one thread is executing a synchronized method for an object, 
 *  all other threads that invoke synchronized methods for the same object block (suspend execution)
 *  until the first thread is done with the object.
 */


/**
 * Server class for the RTCE. The server has the following tasks:
 * 
 * 1. Activate server by doing:
 * 
 *      (a) Opening a port.
 *      (b) Listen for clients.
 *      (c) Accept as many clients as want to connect via sockets.
 *      
 *      
 * 2. Handle requests from clients. This entails:
 * 
 *      (a) Parsing the request and returning appropriate.
 *          result according to our protocol.
 *      (b) Making sure that the request follows protocol.
 *          If it doesn't the server lets the client know that
 *          the request made does not follow protocol.
 *      (c) Return the result of the request to the server.
 *      (d) If a user makes an "edit" request (either 'insert' or
 *          'delete' the server will modify the document.
 *          
 *          
 * 3. Keeping track of states on server side by:
 * 
 *      (a) Recording and tracking all users (or clients) that have connected    
 *          the server.
 *      (b) Keeping a record of what document is being edited at any given moment.
 *          Only one file can be edited by server at a time. It is the clients that
 *          edit files simultaneously. 
 *      (c) Keeping track of which client is editing at any given time. Only one file can
 *          be edited on server side at a time. It is the clients that edit files
 *          simultaneously.
 *          
 *          
 *  4. General bookkeeping:
 *  
 *      (a) Giving default names to files.
 *      (b) Serializing requests from clients.
 *     
 */
public class RTCE_Server {

    private ServerSocket serverSocket = null;
    private static int numberOfUsers = 0;
    private static int numberOfUntitledDocs = 1;
    private static Vector<RTCE_Document> docs = new Vector<RTCE_Document>();
    //private static RTCE_Document RTCEDocument;

    private int userName = 1;

    private static Vector<RTCE_User> clients = new Vector<RTCE_User>();

    /**
     * Make an RTCE server that listens for connections on port.
     * @param port port number, requires 0 <= port <= 65535.
     */

    public RTCE_Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Problem creating port.");
        }
    }

    /**
     * Run the server, listening for client connections and handling them.  
     * Never returns unless an exception is thrown.
     * @throws IOException if the main server socket is broken
     * (IOExceptions from individual clients do *not* terminate serve()).
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects

            final Socket socket = serverSocket.accept();         
            updateUsers(1);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    Thread.yield();  // give the other threads a chance to start too, so it's a fair race
                    try {
                        handleConnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace(); // but don't terminate serve()
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }            
                }
            });
            clients.add(new RTCE_User(t, String.valueOf(userName), socket));
            userName++;
            t.start();// don't forget to start the thread!
        }
    }

    /**
     * Handle a single user connection.  Returns when client disconnects.
     * @param socket socket where the user is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    @SuppressWarnings("unchecked")
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        try {
            RTCE_User curClient = null;
            for (RTCE_User c : clients) {
                if (c.getSocket().equals(socket)) curClient = c;
            }
            for (String line = in.readLine(); line != null; line = in.readLine()) {

                String output = handleRequest(line, curClient);
                if(output.equals("exit" + String.format("%n") + "EOF")) {
                    break;
                }
                if(output != null) {
                    out.println(output);
                }
                out.flush();
            }
        } catch (SocketException se) { 
            socket.close();
        }
        finally {   
            Vector<RTCE_User> cloned = new Vector<RTCE_User>();
            cloned = (Vector<RTCE_User>) clients.clone();
            for (RTCE_User c : cloned) {
                if (c.getSocket().equals(socket)) {
                    c.getSocket().close();
                    clients.removeElement(c);
                }
            }
            updateUsers(-1);
            out.close();
            in.close();
        }
    }

    /**
     * Handler for client input.
     * Parses the request that comes from the server and does appropriate
     * action to change server state. Returns the result of each request
     * to the client based on our protocol.
     * 
     * @param String userInput, the String representing the request coming
     * from a client.
     * @return String, the response to the given request. See Design.pdf
     * for complete description of response for each request.
     * @throws UnsupportedEncodingException if a client uses a bad encoding
     * to send request over connection.
     */
    public static String handleRequest(String userInput, RTCE_User curClient) throws UnsupportedEncodingException {
        String regex = "(view)|(insert \\d+ \\p{ASCII}*)|(delete \\d+ \\d+)|(help)|(list)|" +
                "(switch \\w+\\.txt)|(new \\w+\\.txt)|(new)|(exit)|(replaceAll \\p{ASCII}+\\s\\p{ASCII}+$)|" +
                "(replaceOne \\p{ASCII}+\\s\\p{ASCII}+$)|(style \\d+ \\d+ \\d+ \\d+)|(giveStyle)|(hello)";
        String input = URLDecoder.decode(userInput, "UTF-8");
        if(!input.matches(regex)) {
            //invalid input
            return "Invalid Request."
            + String.format("%n") + "EOF";
        }
        String[] tokens = input.split(" ");
        String[] editTokens= input.split(" ",3);
        String[] replaceTokens = userInput.split(" ");

        if (tokens[0].equals("view")) {
            // 'view' request
            return viewRequest(curClient);

        } else if (tokens[0].equals("hello")) {
            // 'hello' request
            return helloRequest();

        } else if (tokens[0].equals("replaceAll")) {
            //replaceAll request
            return replaceAllRequest(replaceTokens, curClient);

        } else if (tokens[0].equals("replaceOne")) {
            //replaceOne request
            return replaceOneRequest(replaceTokens, curClient);

        } else if (tokens[0].equals("insert")) {
            // 'insert' request
            return insertRequest(editTokens, curClient);

        } else if (tokens[0].equals("delete")) {
            // 'delete' request
            return deleteRequest(editTokens, curClient);

        } else if (tokens[0].equals("help")) {
            // 'help' request
            return helpRequest();

        } else if (tokens[0].equals("list")) {
            // 'list' request
            return listRequest();

        } else if (tokens[0].equals("switch")) {
            // 'switch' request
            return switchRequest(tokens, curClient);

        } else if (tokens[0].equals("new")) {
            // 'new' request
            return newRequest(tokens, curClient);

        } else if (tokens[0].equals("style")) {
            // 'style' request
            return styleUpdateRequest(tokens,curClient);

        } else if (tokens[0].equals("giveStyle") ) {
            // 'giveStyle' request
            return giveStyleRequest(curClient);

        } else if (tokens[0].equals("exit")) {
            // 'exit' request
            return exitRequest();

        } else {
            throw new RuntimeException("End of handleRequest. Should no have gotten here");
        }
    }

    /**
     * Main method to start an RTCE_Server running on the default port (4444).
     */
    public static void main(String[] args) {
        final int port;
        String portProp = System.getProperty("RTCE.customport");
        if (portProp == null) {
            port = 4444; // Default port; do not change.
        } else {
            port = Integer.parseInt(portProp);
        }
        try {
            runRTCEServer(port);
        } catch (IOException e) {
            throw new RuntimeException("Problem in opening server.");
        }
    }

    /**
     * Method to start the server in reality. The main method
     * calls this to start the server with the desired port #.
     * @param int port, the port number you want to serve from.
     * @throws IOException
     */
    public static void runRTCEServer(int port)
            throws IOException {
        RTCE_Server server = new RTCE_Server(port);
        server.serve();
    }

    /**
     * Helper method for view request. Gives the user a view of the
     * current state of the document.
     * @param RTCE_User, the user that made the request.
     * @return String. What the Document looks like at the time
     * of the 'view' request.
     * @throws UnsupportedEncodingException 
     */
    public static String viewRequest(RTCE_User curClient) throws UnsupportedEncodingException {
        return URLEncoder.encode(curClient.getDoc().getAllText(),"UTF-8") 
                +String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the giveStyle request. Gives the user the current style
     * of the document it is currently working on.
     * @param RTCE_User curClient, the user that made the request.
     * @return String. The current style setting for the given current document.
     * @throws UnsupportedEncodingException
     */
    public static String giveStyleRequest(RTCE_User curClient) throws UnsupportedEncodingException {
        return URLEncoder.encode(curClient.getDoc().getStyle(),"UTF-8") 
                +String.format("%n") 
                + "EOF";
    }


    /**
     * Helper method for the style request. Changes the style of the current
     * document that the client is working on.
     * @param String[] tokens, the String array of the numbers representing the
     * the style settings.
     * @param RTCE_User curClient, the client that made the request.
     * @return the new style setting of the given document.
     * @throws UnsupportedEncodingException
     */
    public static String styleUpdateRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {
        curClient.getDoc().setStyle(tokens[1] + " " + tokens[2] + " " + tokens[3] + " " +tokens[4]);
        return URLEncoder.encode(curClient.getDoc().getStyle(),"UTF-8") 
                +String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for insert request. The first of the two "edits" supported
     * by our protocol. This method does the actual editing of the file.
     * @param String[] tokens the array with the request, 
     * @param RTCE_User curClient, the user that made the request
     * @return String, what the Document looks like after the insert.
     * @throws UnsupportedEncodingException 
     */
    public static String insertRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {
        int pos = Integer.valueOf(tokens[1]);
        String text = tokens[2];
        curClient.getDoc().getQueue().addRequest(new RTCE_Request("insert", pos, text, curClient.getDoc()));
        curClient.getDoc().getQueue().resolveRequest();
        return URLEncoder.encode(curClient.getDoc().getAllText(), "UTF-8"  
                )+String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for delete request. The second of the two "edits" supported
     * by our protocol. This method does the actual editing of the file
     * @param String[] tokens the array with the request, 
     * @param RTCE_User curClient, the current user that made the request
     * @return String, what the Document looks like after the delete.
     * @throws UnsupportedEncodingException 
     */
    public static String deleteRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {

        int beginPos=Integer.valueOf(tokens[1]);
        int endPos=Integer.valueOf(tokens[2]);
        curClient.getDoc().getQueue().addRequest(new RTCE_Request("delete", beginPos, endPos, curClient.getDoc()));
        curClient.getDoc().getQueue().resolveRequest();
        return URLEncoder.encode(curClient.getDoc().getAllText(),"UTF-8")
                +String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the replaceAll request. Takes the tokens representing the
     * request from the user and calls the necessary methods to replace all the
     * occurrences of a word with another one.
     * @param String[] tokens, the tokens built from the user request. 
     * Has all the information needed to call the replaceAll methods.
     * @param RTCE_Client curClient, the client whose document you are changing.
     * @return String, the new view of what the document looks like after the 
     * the replacements were made.
     * @throws UnsupportedEncodingException
     */
    public static String replaceAllRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {
        String replaceFromInitial = tokens[1];
        String replaceToInitial = tokens[2];
        String replaceFrom = URLDecoder.decode(replaceFromInitial, "UTF-8");
        String replaceTo = URLDecoder.decode(replaceToInitial, "UTF-8");

        curClient.getDoc().replaceAll(replaceFrom, replaceTo);
        String result = URLEncoder.encode(curClient.getDoc().getAllText(),"UTF-8");
        return result  +String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the replaceOne request. Takes the tokens representing the
     * request from the user and calls the necessary methods to replace the first
     * of the occurrences of a word with another one.
     * @param String[] tokens, the tokens built from the user request. 
     * Has all the information needed to call the replaceOne methods.
     * @param RTCE_Client curClient, the client whose document you are changing.
     * @return String, the new view of what the document looks like after the 
     * the replacement was made.
     * @throws UnsupportedEncodingException
     */
    public static String replaceOneRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {
        String replaceFromInitial = tokens[1];
        String replaceToInitial = tokens[2];
        String replaceFrom = URLDecoder.decode(replaceFromInitial, "UTF-8");
        String replaceTo = URLDecoder.decode(replaceToInitial, "UTF-8");

        curClient.getDoc().replaceOne(replaceFrom, replaceTo);
        String result = URLEncoder.encode(curClient.getDoc().getAllText(), "UTF-8");
        return result + String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the list request to get a list of the all the files
     * that are currently on the server.
     * @return String, the names of all the files currently
     * stored on the server.
     */
    public static String listRequest() {
        String listOfDocs = "";
        for(int i = 0; i<docs.size();i++) {
            listOfDocs += docs.get(i).getName()+" "+ String.format("%n");
        }
        if (listOfDocs.equals("")) {
            return "There are no existing files on the server." 
                    +String.format("%n") 
                    + "EOF";
        }
        return listOfDocs 
                + String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the switch request. Allows a user two switch the
     * focus to another document. All edits and style requests will from now
     * on be sent to the switched-to document and any views will represent the 
     * data in this switched-to document.
     * @param String[] tokens, the tokens representing the request from the client.
     * @param RTCE_Client curClient, the client that wants to switch.
     * @return String, the latest view of the switched-to document.
     * @throws UnsupportedEncodingException 
     */
    public static String switchRequest(String[] tokens, RTCE_User curClient) throws UnsupportedEncodingException {
        String fileName = tokens[1];
        for (RTCE_Document doc: docs){
            if(doc.getName().equals(fileName)) {
                //put a client to the doc list
                doc.setUser(curClient);
                //if the client used to have a document remove the client from that document list

                if(curClient.getDoc() != null) {
                    curClient.getDoc().getList().remove(curClient);
                }
                //set the client to a new doc
                curClient.setDoc(doc);
                return URLEncoder.encode(curClient.getDoc().getAllText(),"UTF-8")                     
                        + String.format("%n") 
                        + "EOF";
            }
        }
        return "File does not exist, can't swtich" 
        + String.format("%n") 
        + "EOF";
    }

    /**
     * Helper method for the new request. When a client connects a new file
     * must be created. There is no theoretical limit to the number of new 
     * documents that can be created. The user can give any name 
     * when they created a new file. The .txt suffix will be added automatically.
     * If the user does not specify a name , a generic name will be
     * assigned by the server ("untitled[file number].txt".  
     * Adds the new document to the document list kept by the server.
     * @param tokens
     * @param curClient
     * @return String. What the new document looks like. Probably will be empty.
     */
    public static String newRequest(String[] tokens, RTCE_User curClient) {

        if (tokens.length == 1) {
            String name = "untitled" + String.valueOf(numberOfUntitledDocs) + ".txt";
            RTCE_Document doc = new RTCE_Document(name);

            //put a client to the doc list
            doc.setUser(curClient);
            //if the client used to have a document remove the client from that document list
            if(curClient.getDoc() != null) {
                curClient.getDoc().getList().remove(curClient);
            }
            //set the client to a new doc
            curClient.setDoc(doc);
            updateDocumentCounter(1);

        }
        else {

            String name = tokens[1];
            // we might want to consider checking for .txt in the end of the file 
            RTCE_Document documentName =new RTCE_Document(name);
            //put a client to the doc list
            documentName.setUser(curClient);
            //if the client used to have a document remove the client from that document list
            if(curClient.getDoc() != null){
                curClient.getDoc().getList().remove(curClient);
            }

            //set the client to a new doc
            curClient.setDoc(documentName);
        }
        docs.add(curClient.getDoc());
        return curClient.getDoc().getName()
                + String.format("%n") 
                + "EOF";
    }

    /**
     * Helper method for the hello request. This is mostly for the
     * terminal use of the RTCE so that you can see how many users
     * are connected.
     * @return String, the hello message.
     */
    private synchronized static String helloRequest() {
        String helloMessage = "Welcome to RTCE " + Integer.toString(getNumberOfUsers()) +
                " people are currently connected. Type help for help."
                + String.format("%n") + "EOF";
        return helloMessage;
    }

    /**
     * Method to build the help message to the user when
     * user makes help request.
     * @return String help massage.
     */
    private synchronized static String helpRequest() {
        final String helpMessage = "Welcome to RTCE. Please use one of the follwoing commands:"
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
                + "EOF";
        return helpMessage;
    }

    /**
     * Helper method to handle exit request.
     * @return String exit, tells client side to disconnect.
     */
    public synchronized static String exitRequest() {
        return "exit" + String.format("%n") + "EOF";
    }

    /**
     * Helper method to increment or decrement the number 
     * of users that are connected to the RTCE bye updateNum
     * so that we can keep track of them and give helpful user feedback.
     * @param int updateNum, either positive or negative integer.
     */
    public synchronized static void updateUsers(int updateNum) {
        numberOfUsers += updateNum;
    }

    

    /**
     * Method to get the number of users connected to the server.
     * @return int, representing the number of users connected to the server
     */
    public synchronized static int getNumberOfUsers() {
        return numberOfUsers;
    }
    
    /**
     * Helper method for incrementing the number of documents there 
     * are on the server by num. Used mainly for giving distinct default
     * names to documents.
     * @param int num, the number by how much you want to update 
     * numberOfUntitledDocs.
     */
    public synchronized static void updateDocumentCounter(int num) {
        numberOfUntitledDocs += num;
    }
    
    
    /**
     * Method to get the current state of the document counter
     * @return int the integer representing the number of the 
     * document currently on the server.
     * 
     */
    public synchronized static int getDocumentCounter() {
       return numberOfUntitledDocs;
    }
}