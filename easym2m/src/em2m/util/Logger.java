
package em2m.util;

import com.siemens.icm.io.file.FileConnection;
import em2m.FailedInitException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.microedition.io.Connector;


/**
 * This class can be used for logging events, debuging etc.
 * Records are writen into file in root directory called logfile.txt
 * @author Jan Melník
 */
public class Logger {

    private static OutputStream out;
    private static FileConnection fc;
    private static boolean initialized;
    private static final String LOGFILE_PATH = "file:///a:/logfile.txt";
    //default file size 100 kB
    private static final long MAX_FILE_SIZE = 100000;

    public static void init(){
        try
        {
            //opens connection, store as fileconnection
            fc = (FileConnection)Connector.open(LOGFILE_PATH);

            long pos;

            //create file if doesn't exist, also open stream at
            //start of file, otherwise get file lenght and open
            //stream at the end of the file
            if(!fc.exists()) {
                fc.create();
                out = fc.openOutputStream();
            } else {
                pos = fc.fileSize();
                out = fc.openOutputStream(pos);
                out.write(("\n-------------\n").getBytes());
                out.flush();
            }
            initialized = true;
        }
        catch(Exception e)
        {
            initialized = false;
            new FailedInitException(e.getMessage());
        }
    }
    //print str into logfile
    public static void print(String str){
        if(!isInitialized())init();
        try {
            if((fc.fileSize()+str.length()) > MAX_FILE_SIZE)
                fc.truncate(0);
            out.write(str.getBytes());
            out.flush();
        }catch(IOException e){}
    }
    //print str with timestamp into logfile
    public static void printTS(String str){
        if(!isInitialized())init();
        try {
            if((fc.fileSize()+str.length()) > MAX_FILE_SIZE)
                fc.truncate(0);
            out.write((new Date()).toString().getBytes());
            out.write(str.getBytes());
            out.flush();
        }catch(IOException e){}
    }
    //print str into logfile, create new line
    public static void println(String str){
        if(!isInitialized())init();
        try {
            if((fc.fileSize()+str.length()) > MAX_FILE_SIZE)
                fc.truncate(0);
            out.write((str+"\n").getBytes());
            out.flush();
        }catch(IOException e){}
    }
    //print str with timestamp into logfile, create new line
    public static void printlnTS(String str){
        if(!isInitialized())init();
        try {
            if((fc.fileSize()+str.length()) > MAX_FILE_SIZE)
                fc.truncate(0);
            out.write((new Date()).toString().getBytes());
            out.write((str+"\n").getBytes());
            out.flush();
        }catch(IOException e){}
    }
    public static void close(){
        try {
            out.close();
            fc.close();
        } catch (IOException e) {}
    }

    /**
     * @return the initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
