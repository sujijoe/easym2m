/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m.sms;

import com.siemens.icm.io.ATCommandFailedException;
import em2m.sms.SMS;
import com.siemens.icm.io.ATCommandResponseListener;
import com.siemens.icm.io.ATStringConverter;
import em2m.FailedInitException;
import em2m.response_listener;
import em2m.util.Em2mATCommand;
import em2m.util.Em2mString;
import em2m.util.Logger;

/**
 *
 * @author DaJohn
 */
public class SMSManager{

    private response_listener rl;

    public void  Constructor(String servCenter)throws ATCommandFailedException{
           Em2mATCommand.sendATC("at+cscs=\"GSM\""); //TE character set
           Em2mATCommand.sendATC("at+csca="+servCenter);
           Em2mATCommand.sendATC("at+cmgf=1");
           Em2mATCommand.sendATC("at+cpms=\"mt\",\"mt\",\"mt\"");
           rl = new response_listener();
           //Em2mATCommand.sendATC("at+cnmi=2,1,0,0,1");
           Em2mATCommand.getATCommand().send("at+cnmi=2,1,0,0,1\r", rl);
           Em2mATCommand.sendATC("at+creg?", "0,1");
    }
    public SMSManager(String servCenter) throws FailedInitException{
        if(!Em2mATCommand.isCommandDone("at+cpin?\r", "READY"))
            throw new FailedInitException("PIN required!");
        try{
            Constructor(servCenter);
        }catch (Exception e){
            try {
                Em2mATCommand.relaseATCommand();
            } catch (ATCommandFailedException ex) { }
            new FailedInitException("Could not initialize SMS Manager");
        }
    }
        public SMSManager(String pin, String servCenter) throws FailedInitException{
        try{
           Em2mATCommand.sendATC("\rat+cpin="+pin);
           Constructor(servCenter);
        }catch (Exception e){
            new FailedInitException("Could not initialize SMS Manager");
        }
    }
    public boolean sendMessage(SMS message){
        try {
            Em2mATCommand.sendATC("at+cmgs=\"" + message.getDestinNumber() + "\"", ">");
            return Em2mATCommand.sendATC(message.getMessage()+"\032").indexOf("+CMGS:")>0;
        } catch (ATCommandFailedException e) {
            return false;
        }

    }

    public boolean sendUcs2Message(SMS message){
        try {
            Em2mATCommand.sendATC("at+cscs=\"UCS2\"");
            Em2mATCommand.sendATC("at+cmgs=\"" + ATStringConverter.Java2UCS2Hex(message.getDestinNumber()) + "\"", ">");
            boolean res = Em2mATCommand.sendATC(ATStringConverter.Java2UCS2Hex
                    (message.getMessage())+"\032").indexOf("+CMGS:")>0;
            Em2mATCommand.sendATC("at+cscs=\"GSM\"");
            return res;
        } catch (ATCommandFailedException e) {
            return false;
        }

    }
    public SMS readMessage(int index){
        try{
            String atres = Em2mATCommand.sendATC("at+cmgr="+index,"+CMGR:");
            //first split lines
            String tmp[] = Em2mString.split(atres, "\r\n");
            if(tmp.length<2)//if empty message received, return null
                return null;
            //then split first line to get message source numb. date etc.
            String header[] = Em2mString.split(tmp[1],",");
            tmp = Em2mString.split(tmp[1],",");
            SMS res = new SMS();
            res.setSourceNumber(header[2]);
            //res.setMessage(tmp[]);
            return res;
        }catch(Exception e){
            return null;
        }
    }
    public SMS readUcs2Message(int index){
        return null;
    }
}
