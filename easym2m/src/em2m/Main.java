/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m;

import em2m.io.GPIO;
import em2m.sms.SMS;
import em2m.sms.SMSManager;
import em2m.util.Em2mString;
import em2m.util.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;


/**
 * @author DaM3s3lf
 */
public class Main extends EM2Mlet{

    EventListener list;
    SMSManager m;

    GPIO io;

    //middle and top layer
    static String destHost = "gerrys.webz.cz/handlecords.php?";
    static String destPort = "80";

    //interenet settings for T-mobile
    static String connProfile = "bearer_type=gprs;access_point=internet.t-mobile.cz";


    String openParam;
    
    HttpConnection      hc  = null;
    InputStream         is  = null;
    OutputStream        os  = null;


    public void startApp() {

        try {
            Logger.printlnTS("App initialization...");

            // T-mobile CZ
            m = new SMSManager("+420603052000");

            /*// Vodafone CZ
            //m = new SMSManager("+420608005681");*/

            Logger.printlnTS("SMS manager initialized");

            //create new instance of EventListener
            list = new EventListener();
            list.addSMListener(this, "+420603052000");

            Logger.printlnTS("SMS Listener set");



            /*list.addCallListener(this);
            System.out.println("Call Listener set");*/

            //Em2mATCommand.sendATC("at+cind=0,0,,0,,,0,,0\r");
            //Em2mATCommand.sendATC("at+cmer=2,0,0,2\r");
            //System.out.println("Event Reporting set");
            //System.out.println(Em2mATCommand.sendATC("at&f");)

            /*SMS s = m.readMessage(4);
            System.out.println(s);*/

            io = new GPIO();
            io.openPin(0, true);
            Logger.printlnTS("GPIO initialized");

            list.addGPSListener(this, 120);
            //Logger.printlnTS
            Logger.printlnTS("GPS Location Listener set");
            
        } catch (Exception e) {
            Logger.printlnTS(e.getMessage());
            notifyDestroyed();
        }
        //Logger.printlnTS
        Logger.printlnTS("App initialized and running");
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        if(list!=null)
            list.relase();
    }

    public void handleEvent(int event, Object data) {

        //incoming SMS
        if (event==EventListener.NEW_SMS_EVENT) {

            SMS message = (SMS)data;
            if(message.getSourceNumber().equalsIgnoreCase("+420608732168"))
            {
                //handle command
                if(message.getMessage().indexOf("start")>=0)
                    io.setVal(0, true);
                if(message.getMessage().indexOf("stop")>=0)
                    io.setVal(0, false);
                if(message.getMessage().indexOf("exit")>=0)
                    notifyDestroyed();
            }

        }else if (event==EventListener.GPS_LOCATION_UPDATE){

            //parse data
            String nmea = (String) data;
            String[] parts = Em2mString.split(nmea, ",");

            //gps neni zamerena
            if (parts[9].indexOf("0")>=0)
                return;

            try {
                //blink
                io.setVal(0,true);

                //Open Connection and send data
                openParam = "http://" + destHost+"lon="+parts[2] + "&lat="+parts[4]+";" + connProfile;

                hc = (HttpConnection) Connector.open(openParam,Connector.READ);

                int res = hc.getResponseCode();

                //spatna odpoved
                if (res!=200) return;

                /* Read Data */
                StringBuffer str = new StringBuffer();
                int ch;
                while ((ch = is.read()) != -1) {
                    str.append((char) ch);
                }

                /* Close all */
                is.close();
                hc.close();

                //log result
                Logger.printlnTS(str.toString());
                io.setVal(0, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public SMSManager getSMSManager() {
        return m;
    }
}
