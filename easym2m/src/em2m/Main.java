/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m;

import com.siemens.icm.io.ATCommand;
import com.siemens.icm.io.ATCommandFailedException;
import com.siemens.icm.io.ATCommandResponseListener;
import com.siemens.icm.io.ATStringConverter;
import em2m.ATEventListener;
import em2m.util.Logger;
import java.util.Vector;
import javax.microedition.midlet.*;
import javax.microedition.*;
import em2m.*;
import em2m.sms.SMS;
import em2m.sms.SMSManager;
import em2m.util.Em2mATCommand;
import em2m.util.Em2mString;


/**
 * @author DaM3s3lf
 */
public class Main extends EM2Mlet{

    ATCommand atc;

        /*public class sendResponse extends Thread{

        SMSManager smsm;

        public void sendResponse() {
            runThreads = true;
        }

        public void run() {
            ;// deal with response
        }
        }*/

    public void startApp() {
        try {
            
            if(!Logger.isInitialized())
            {
                notifyDestroyed();
                return;
            }
            SMSManager m = new SMSManager("+420603052000");
            System.out.println("SMS manager initialized");

            /*SMS tmp = new SMS();
            tmp.setDestinNumber("608942017");
            tmp.setMessage("čeština");*/

            //if (m.sendUcs2Message(tmp))
                //return;


            ATEventListener list = new ATEventListener();
            list.setListener(this);
            Em2mATCommand.getATCommand().addListener(list);
            
            m.readMessage(1);
            //uloha
            //v.run();
            //m.sendMessage(tmp);


            /*atc = new ATCommand(false);
            

            //smse.setListener(this);
            //atc.addListener(smse);

            notifyDestroyed();


            String res = sendATC("AT+COPS?\r");

            //Wait for GSM logon
            while(res==null||res.length()==0)
            {
                res = sendATC("AT+COPS?\r");
            }
            Logger.println(res);

            String provider=res;


            //rozsirene hlaseni chyb (AT+CMEE=2)
            res = sendATC("AT+CMEE=2\r");
            Logger.println(res);
            if(!res.equalsIgnoreCase("OK"))
            {
                Logger.println("Nepovedlo se prepnout na rozsirene hlaseni chyb");
                notifyDestroyed();
            }

            //textovy mod (AT+CMGF=1)
            res = sendATC("AT+CMGF=1\r");
            Logger.println(res);
            if(!res.equalsIgnoreCase("OK"))
            {
                Logger.println("Nepovedlo se prejit do textoveho rezimu");
                notifyDestroyed();
            }

            //Unicode mode UCS2 (AT+CSCS="UCS2")
            res = sendATC("AT+CSCS=\"UCS2\"\r");
            if(!res.equalsIgnoreCase("OK"))
            {
                Logger.println("Nepovedlo se prejit do unicode rezimu");
                notifyDestroyed();
            }
            
            //res = atc.send("AT+CSCS=?");
            //System.out.println(res);
            //res = atc.send("AT+CSCS?");
            //System.out.println(res);
            atc.send("AT+CMGS=\"002B003400320030003600300038003700330032003100360038\"\r");
            atc.send("004A00300048004E0020006A00650020004B0049004E0047004A00300048004E0020006A00650020004B0049004E0047\u001A");
            Logger.println("sms odeslana odeslana");
            char ch = '\u0032';

            //res = atc.send(provider + "\u001A");
            

            Logger.println(res+"\nkoncim");
            Logger.close();
            notifyDestroyed();*/
        } catch (Exception e) {
            String tmp = e.getMessage();
            System.out.println(tmp);
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        try {
            atc.release();
        } catch (ATCommandFailedException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    //this method handles ATC data flow
    private String sendATC(String command){
        try {
            String res = atc.send(command);
            String simpleRes[] = Em2mString.split(res,"\r\n");
            if (simpleRes!=null)
                //vysledek ma vice radku
                if(simpleRes.length>=1)
                    return simpleRes[1];
        } catch (ATCommandFailedException ex) {
            return "ATERR";
        } catch (IllegalStateException ex) {
            return "ILLSTAT";
        } catch (IllegalArgumentException ex) {
            return "ILLAR";
        }
            return "";
    }

    public void handleEvent(String event) {
        System.out.println("handle method called!\n"+event+"\n");
    }
}
