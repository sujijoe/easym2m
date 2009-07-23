
package em2m.util;

import com.siemens.icm.io.ATCommand;
import com.siemens.icm.io.ATCommandFailedException;
import com.siemens.icm.io.ATCommandListener;

/**
 * @author DaJohn
 */
public class Em2mATCommand {

    private static ATCommand atc;
    private ATCommandListener list;

    public static void init(){
        try {
            atc = new ATCommand(false);
        } catch(Exception e){}

    }

    public static String sendATC(String cmd, String response) throws ATCommandFailedException {
        if(atc == null) init();
        try {
            String resp = atc.send(cmd + "\r");
            if(resp.indexOf(response) < 0)
            {
                System.out.println(resp);
                throw new ATCommandFailedException("Incorrect command response");
            }
            return resp;
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String sendATC(String cmd) throws ATCommandFailedException {
            return sendATC(cmd,"OK");
    }

    /*public static String getATC(String cmd) {
        try {
            String res = atc.send(cmd);
            String simpleRes[] = Em2mString.split(res,"\r\n");
            if (simpleRes!=null)
                //vysledek ma vice radku
                if(simpleRes.length>=2)
                    return simpleRes[1];
        } catch (ATCommandFailedException ex) {
            return "ATERR";
        } catch (IllegalStateException ex) {
            return "ILLSTAT";
        } catch (IllegalArgumentException ex) {
            return "ILLAR";
        }
            return "";
    }*/
    /**
     * This method send ATCommand and check response,
     * in case of expected response returns true, oterwise false;
     * 
     **/
    public static boolean isCommandDone(String cmd, String response){
        if(atc == null) init();
        try {
            String resp = atc.send(cmd + "\r");
            if(resp.indexOf(response) < 0)
            {
                System.out.println(resp);
                return false;
            }
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /********************SHIZZL!****************************/
    public static ATCommand getATCommand(){
        if(atc==null) init();
        return atc;
    }

    public static void relaseATCommand() throws ATCommandFailedException{
        atc.release();
    }

    public static void registerListener(ATCommandListener list){
        atc.addListener(list);
    }
}
