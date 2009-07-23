/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m.sms;

import com.siemens.icm.io.ATCommandFailedException;
import com.siemens.icm.io.ATStringConverter;
import em2m.FailedInitException;
import em2m.util.Em2mATCommand;
import em2m.util.Em2mString;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is used to manage SM services.
 * @author Jan Melník, MEL108
 */
public class SMSManager{

    private void  Constructor(String servCenter)throws ATCommandFailedException{
           Em2mATCommand.sendATC("at+csca="+servCenter);
           Em2mATCommand.sendATC("at+cmgf=1");
           Em2mATCommand.sendATC("at+cpms=\"SM\",\"SM\",\"SM\"");
           Em2mATCommand.sendATC("at+cnmi=2,1,0,0,1");
           Em2mATCommand.sendATC("at+creg?", "0,1");
    }

    /**
     * Create new instance of SMSManager, which allows user to send and read SMS.
     * @param servCenter This number you get from provider
     * @throws em2m.FailedInitException
     */
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

    /**
     * Create new instance of SMSManager, which allows user to send and read SMS.
     * @param pin PIN1
     * @param servCenter This number you get from provider
     * @throws em2m.FailedInitException
     */
    public SMSManager(String pin, String servCenter) throws FailedInitException{
        try{
           Em2mATCommand.sendATC("\rat+cpin="+pin);
           Constructor(servCenter);
        }catch (Exception e){
            new FailedInitException("Could not initialize SMS Manager");
        }
    }

    /**
     * This method send mesage in standard GSM character set.
     * @param message
     * @return result (true if message was sent)
     */
    public boolean sendMessage(SMS message){
        try {
            Em2mATCommand.sendATC("at+cscs=\"GSM\"");
            Em2mATCommand.sendATC("at+csmp=17,167,0,0");
            Em2mATCommand.sendATC("at+cmgs=\""
                    + message.getDestinNumber() + "\"", ">");
            return Em2mATCommand.sendATC(message.getMessage()+"\032").indexOf("+CMGS:")>0;
        } catch (ATCommandFailedException e) {
            return false;
        }

    }

    /**
     * This method send mesage in UCS2 character set, alow user to use localized characters.
     * For more info see http://www.unicode.org/Public/MAPPINGS/ETSI/GSM0338.TXT
     * @param message
     * @return result (true if message was sent)
     */
    public boolean sendUcs2Message(SMS message){
        try {
            Em2mATCommand.sendATC("at+cscs=\"UCS2\"");
            Em2mATCommand.sendATC("at+csmp=17,167,0,8");
            Em2mATCommand.sendATC("at+cmgs=\""
                    + ATStringConverter.Java2UCS2Hex(message.getDestinNumber())
                    + "\"", ">");
            boolean res = Em2mATCommand.sendATC(ATStringConverter.Java2UCS2Hex
                    (message.getMessage())+"\032").indexOf("+CMGS:")>0;
            return res;
        } catch (ATCommandFailedException e) {
            return false;
        }

    }

    public SMS readMessage(int index){
        try{
            Em2mATCommand.sendATC("at+cscs=\"UCS2\"");
            Em2mATCommand.sendATC("at+csmp=17,167,0,8");
            String atres = Em2mATCommand.sendATC("at+cmgr="+index,"+CMGR:");

            //first split lines, separate header and message body
            String tmp[] = Em2mString.split(atres, "\r\n");

            //then split first line to get message source numb. date etc.
            String header[] = Em2mString.split(tmp[1],",");

            //no message stored in this position, return null
            if(header[1].equalsIgnoreCase(""))
                return null;

            SMS res = new SMS();

            //remove extra quotes contained in string
            String temporary = header[1];
            temporary = temporary.substring(1,temporary.length()-1);
            //store source num
            res.setSourceNumber(ATStringConverter.UCS2Hex2Java(temporary));
            //store text
            res.setMessage(ATStringConverter.UCS2Hex2Java(tmp[2]));

            //convert date from GSM 03.40 time string format
            //("yy/MM/dd,hh:mm:ss+zz", where "z" is used for timezone)
            java.util.Calendar c = java.util.Calendar.getInstance();

            try{
                //remove extra quote
                header[3]=header[3].substring(1);

                int val;

                //year
                val = Integer.parseInt(header[3].substring(0,1));
                c.set(Calendar.YEAR, val);
                //month
                val = Integer.parseInt(header[3].substring(3,4));
                c.set(Calendar.MONTH, val);
                //day
                val = Integer.parseInt(header[3].substring(6,7));
                c.set(Calendar.DAY_OF_MONTH, val);
                //hour
                val = Integer.parseInt(header[4].substring(0,1));
                c.set(Calendar.HOUR_OF_DAY, val);
                //minute
                val = Integer.parseInt(header[4].substring(3,4));
                c.set(Calendar.MINUTE, val);
                //second
                val = Integer.parseInt(header[4].substring(6,7));
                c.set(Calendar.SECOND, val);

                res.setReceived(c.getTime());

            }catch(NumberFormatException e) {}

            return res;

        }catch(Exception e){
            return null;
        }
    }
}
