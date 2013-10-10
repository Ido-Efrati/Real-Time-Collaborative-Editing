package RTCE.Document;

import java.io.IOException;
import java.io.PrintWriter;
/*
 * Thread Safety argument * 
 * by confinement - all of the writing is being performed on a single thread that is only related to the
 * current user that is calling a local instance of the file writer on his or her system,
 * therefore there is no race condition or concurrency issues.
 */
/**
 * The file writer class will enable the user to export (save) RTCE files (file.txt) from the editor to his
 * or her system. The class is using a file writer to export the file from the text editor to the user's.
 * @author Ido Efrati
 *
 */
public class RTCE_FileWriter {
    /**
     * The writeData method will enable the user to export a file from RTCE to a file.txt in his or her system.
     * @param fileTowrite - a valid file  and path name (/path/file.txt) of the file we want to create on
     * the user's system. 
     * @param out - a printWriter that insert the text from the document to the file that was created
     * on the user's system  
     */
    public void writeData(String fileTowrite, PrintWriter out) throws IOException { 
        out.println(fileTowrite);  // insert the text from the document to a file.txt on the user's system
    } 
}
