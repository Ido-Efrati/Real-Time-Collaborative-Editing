package RTCE.Document;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/*
 * Thread Safety argument * 
 * by confinement - all of the reading is being performed on a single thread that is only related to the
 * current user that is calling a local instance of the file reader on his or her system.
 * Therefore there is no race condition or concurrency issues.
 * Finally all of the methods that enables the user to create the file on RTCE are not part of this class
 * and are already thread safe.
 */
/**
 * The file reader class will enable the user to import RTCE files (file.txt) from his or her system
 * into the RTCE editor. The class is using a file reader to import the file from the system and convert it 
 * into a string.
 * 
 * behavior is not specified if the user tries to import a non supported file.
 * @author Ido Efrati
 *
 */
public class RTCE_FileReader {
    private static BufferedReader b;
    /**
     * The FileToString method convert a valid RTCE file to a string.
     * @param fileName - a valid file path to the file we want to import from the system.
     * @return  a String representation of a file for the document.
     */
    public String FileToString(String fileName) {
        String outValue = "";
        try {
            String eol = System.getProperty( "line.separator" );
            FileReader fin = new FileReader(fileName);
            b = new BufferedReader(fin);

            String currentLine; 
            while ((currentLine = b.readLine()) != null) {
                outValue= outValue + currentLine + eol;
            }
        }
        catch (FileNotFoundException ef) {
            throw new RuntimeException("File not found error in RTCE_FileReader.");
        }
        catch (IOException ei) {
            throw new RuntimeException("IO Exception: could not perform " +
            		"input/output correctly in RTCE_FileReader");
        }
        return outValue;
    }  
}