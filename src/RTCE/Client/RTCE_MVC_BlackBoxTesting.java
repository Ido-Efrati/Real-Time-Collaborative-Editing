package RTCE.Client;


/**
 * Testing strategy for the RTCE GUI Side. (Black Box Testing):
 * 
 * 1) Test IP Adress:
 * 
 *          (a) Try an invalid IP Address format. Make sure the dialog box prompts
 *              the user for a new IP Address or hostname.
 *              
 *          (b) Try an invalid hostname format. Make sure the dialog box prompts 
 *              the user for a new IP Address or hostname.
 *              
 *          (c) Try a valid IP Address format but not one that is running the
 *              server. Make sure the dialog box informs the user that the
 *              connection could not be made and that they should try again.
 *              
 *          (d) Try a valid hostname format but not one that is running the
 *              server. Make sure the dialog box informs the user that the
 *              connection could not be made and that they should try again.
 *           
 *          (e) Try the correct IP Address or hostname and make sure the 
 *              RTCE Windows opens after a dialog box informing the user
 *              that the connection was made.
 *              
 *          (f) Make sure that all buttons besides the New button and
 *              the Switch button are disabled when the GUI is shown.
 * 
 * 2) Test the new button:
 * 
 *          (a) Click Cancel and make sure that nothing happens.
 *  
 *          (b) Click the New button and make sure the filename dialog
 *              box opens.
 *              
 *          (c) Click OK without giving a filename and make sure that
 *              "untitled1.txt" appears in the tree on right side of the GUI.
 *          
 *          (d) Press New again and give a filename now. Give a name with a .txt
 *              suffix and make sure that the it also appears in the tree.
 *              
 *          (e) Press New again and give a filename without a .txt suffix. Make 
 *              sure that it appears in the tree with a .txt suffix.
 *              
 *          (f) Press New again and try again without a filename by just clicking
 *              OK. Make sure that the next untitled is updated with the correct
 *              number.
 *              
 *          (g) Try to create a filename that already exists. Make sure that a new
 *              one does not get created. 
 *           
 *          (h) Try many new files with a name and many without a name interleaved
 *              and make sure all show up in tree.
 *              
 *  3) Test the Switch button:
 *      
 *          (a) Add some text (edit testing to follow in 4) ) to
 *              an open file.
 *              
 *          (b) Select another file in the tree and Click the switch button.
 *          
 *          (c) Make sure the windows switches to the new file. You can verify 
 *              this by checking the top of the window.
 *              
 *          (d) Add some different text to the switched-to file. 
 *          
 *          (e) Switch back to the first file. Make sure that the text that was
 *              added there is still there.
 *          
 *          (f) Switch again to the second file and make sure that the text that
 *              was added there is still there.
 *              
 *          (g) Do this with multiple files. Both named ones and "untitled" ones.
 *          
 *          (h) Make sure not text is lost anywhere when switching between any
 *              of the files.
 *              
 *              
 *  4) Test the edits:
 *  
 *      (i) Test Insert:
 *          
 *          (a) Insert text at the beginning of the file. Make sure it stays 
 *              there properly.
 *              
 *          (b) Insert text at the end of the file. Make sure all stays there 
 *              properly.
 *              
 *          (c) Insert text in the middle of some other text. Make sure it stays 
 *              in the right place.
 *              
 *          (d) Make sure the space character works as expected.
 *          
 *          (e) Make sure multiple space characters work as expected.
 *              
 *          (f) Move cursor with mouse and do all the above.
 *          
 *          (g) Move cursor with arrows and do all the above
 *          
 *          (h) Type a carriage return at the beginning of the document and
 *              make sure that everything gets shifted correctly.
 *              
 *          (i) Type a carriage return in the middle of the document's text
 *              and make sure that everything gets shifted properly.
 *          
 *          (j) Type a carriage return at the end and make sure cursor moves
 *              to a new line.
 *              
 *          (k) Type multiple carriage returns and make sure paragraphs get
 *              created properly.
 *              
 *          (l) Keep typing without a carriage return and make sure that
 *              the word wrapping works properly.
 *              
 *          (m) Try a Non-ASCII Character and make sure that Invalid Request 
 *              gets shown to the user.
 *              
 *      (ii) Test Delete:
 *      
 *           (a) Delete text from end of text. Make sure all that is wanted
 *               is deleted and the rest stays the same.
 *           
 *           (b) Delete text from the middle of text and make sure that what
 *               is wanted is deleted and that all the rest stays the same.
 *              
 *           (c) Keep your finger on the backspace (or delete) button and make
 *               sure that everything behind it is deleted and nothing in 
 *               front of it.
 *           
 *           (d) Select some text with the cursor and press backspace (or
 *               delete) make sure all that is selected is deleted and
 *               nothing else.
 *               
 *           (e) Try to delete when there is no text in the editor. Make sure
 *               that no errors get thrown and that everything stays stable.
 *               
 *  5) Test Document Tree:
 *          
 *           (a) Make sure all documents that were created in above steps stay
 *               in the tree.
 *               
 *           (b) Close the window and start a new one. Make sure that all
 *               the documents are still there in the Document Tree.
 *            
 *           (c) Select various documents in the tree and make sure that 
 *               when you switch between files they all stay there.
 *               
 *  6) Test Closing the Connection:
 *  
 *            (a) Make sure that the connection is broken without throwing
 *                any errors and that you can reconnect to the server.
 *            
 *            (b) Make sure the server doesn't die when a user disconnects.
 *            
 *  7) Test Multi-User Functionality (3 users):
 *              
 *            (a) After doing all the above, open a new client on another
 *                computer.
 *            
 *            (b) Put the servers IP Address in that the server is using 
 *                and try to connect.
 *            
 *            (c) Open yet another client on a third computer and put the IP
 *                Address that the server is using and try to connect.
 *                
 *            (d) Make sure that all the clients are connected.
 *            
 *            (e) First do all the above steps (1-6) and optional part testing
 *                on both the other clients.
 *                
 *            (f) Make sure that the documents that were already created by
 *                the first user are visible in the tree of both the new
 *                clients.    
 *                
 *            (g) Create new documents on each of the users and make sure that 
 *                they can all see all of the new documents that were added.
 *                
 *            (h) Do an insert text (with all the variations from steps 4) 
 *                on each of the other clients and make sure that both the other 
 *                clients can see the changes that are being made.
 *                
 *            (i) Do a delete text (with all the variations from step 4) 
 *                on each of the other client and make sure that both the other
 *                clients can see the changes that are being made.
 *                
 *            (j) Do all the above on all the different files and switch between them.
 *                Make sure that all the changes of each respective client is there
 *                correctly.
 *            
 *            (k) Delete the same letter at the the same time. Make sure that only the
 *                requested letter is deleted. ("Doag" issue)
 *                
 *            (l) Input letters one after another. Make sure that precedence is followed
 *                correctly.
 *                
 *            (m) Test all the above with three different OS's. Use one Windows, one Mac
 *                and oneLinux.
 *   
 *  8) Test disconnect:
 *            
 *            (a) Disconnect one client. Make sure that no data is lost to the other
 *                users.
 *                
 *            (b) Make sure that there are no exceptions or errors when disconnecting.
 *            
 *            (c) Make sure this client can reconnect.
 *            
 *            (d) Disconnect the other clients. Make sure none of the above issues appear.
 *            
 *            (e) Reconnect all. Make sure that they can all see the files in the last
 *                state.
 *                
 *                
 *                
 *  10) Test Different OS:
 *  
 *          (a) Test the application on MAC OS
 *          
 *          (b) Test the application on Windows
 *          
 *          (c) Test the application on Athena
 *          
 *          (d) Test that 3 users from 3 different OS can work on the same document 
 *          
 *          
 *  9) Test styling:
 *  
 *      Bold:
 *  
 *            (a) Open a new client and create a new file.
 *            
 *            (b) Insert text and click bold.
 *            
 *            (c) Test all edits (part 4) on the bolded text. And make sure that the
 *                styling stays there. 
 *            
 *            (d) Make a new file and insert text. Make sure that the text in the the 
 *                window is not bolded.
 *            
 *            (e) In the second file click the plain button and make sure that the text
 *                returns to plain text again.
 *                
 *            (f) Go back to the first file and make sure it's still bolded. Unbold that too
 *                and make sure that it works.
 *                
 *            (e) Go back to second file and make sure all is still there in plain text.
 *            
 *            (f) Connect a second user.
 *            
 *            (g) Have him switch to the first file and make sure he sees plain text.
 *            
 *            (h) Let second client bold the text. Make sure that first client actually
 *                sees the bold change that the second one did
 *            
 *            (i) Have the second one repeat all changes from (d) to (e) and make
 *                sure all these appear one first client's files.
 *                
 *      REPEAT THESE STEPS WITH ALL STYLING OPTIONS (ITALIC, FONT SIZE, FONT TYPE, FONT COLOR) AND ALL
 *      POSSIBLE COMBINATIONS OF THESE AND SEE IF THEY WORK.
 *      
 *      
 * 
 * 11) Other Extensions Testing:
 * 
 *     Copy:
 *     
 *          (a) Try to copy a word
 *          
 *          (b) Try to copy a sentence
 *          
 *          (c) Try to copy without selecting anything to copy- nothing should happen
 *
 *
 *     Cut:
 *      
 *          (a) Try to cut a word
 *          
 *          (b) Try to cut a sentence
 *          
 *          (c) Try to cut without selecting anything to cut  - nothing should happen
 *
 *     Paste:
 *     
 *          (a) Try to paste without anything in the clip board - nothing should happen
 *          
 *          (b) Paste after copy
 *          
 *          (c) Paste after cut
 *          
 *          (d) Paste several times
 *
 *     Replace:
 *     
 *          (a) Try to replace one occurrence of a word 
 *          
 *          (b) Try to replace all occurrences of a word 
 *          
 *          (c) Try to replace one occurrence of a sentence 
 *          
 *          (d) Try to replace all occurrences of a sentence
 *          
 *          (e) Close and open the replace window
 *          
 *          (f) Don't specify a word to replace with, but specify a word to replace
 *          
 *          (g) Replace a word with the same word
 *
 *     Save:
 *     
 *         (a) Save an empty file
 *         
 *         (b) Save a file with text
 *
 *     Open:
 *     
 *         (a) Open an empty file
 *         
 *         (b) Open a file with text
 *         
 *         (c) Try to open a file that is already in the tree- won't allow to bring this file to the system
*/
