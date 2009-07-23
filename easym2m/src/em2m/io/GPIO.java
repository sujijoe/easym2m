/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m.io;

import com.siemens.icm.io.ATCommandFailedException;
import em2m.util.Em2mATCommand;
import em2m.util.Em2mString;

/**
 * This class is used to control General Purpose Input Output (GPIO) pins.
 * @author Jan Melník, MEL108
 */
public class GPIO {

    /**
     * Create new instance of GPIO, which allows user to work with GPIO pins.
     */
    public GPIO(){
        try
        {
            Em2mATCommand.sendATC("AT^SPIO=1");
        }
        catch(ATCommandFailedException e){}
    }

    /**
     * This method opens and configure GPIO pin for further use.
     * @param index  - GPIO pin identification
     * @param output - true means this pin will be used as output and values
     *        could be set by using setVal method
     * @param startHigh - startup value on GPIO pin. True for high/log 1. (works only in
     *        output mode)
     */
    public void openPin(int index, boolean output, boolean startHigh){
        int outputVal = (output) ? 1 : 0;
        int startVal = (startHigh) ? 1 : 0;
        try
        {
            Em2mATCommand.sendATC("AT^SCPIN=1," + index + "," + outputVal + "," + startVal);
        }
        catch (ATCommandFailedException ex) {
            // In case of failture, try to close pin first
            // and repeat this call.
            closePin(index);
            try {
                Em2mATCommand.sendATC("AT^SCPIN=1," + index + "," + outputVal + "," + startVal);
            } catch (ATCommandFailedException e) {}
        }
    }

    /**
     * This method opens and configure GPIO pin for further use.
     * @param index  - GPIO pin identification
     * @param output - true means this pin will be used as output and values
     *        could be set by using setVal method
     */
    public void openPin(int index, boolean output){
        int outputVal = (output) ? 1 : 0;
        try
        {
            Em2mATCommand.sendATC("AT^SCPIN=1," + index + "," + outputVal);
        }
        catch (ATCommandFailedException ex) {
            // In case of failture, close pin first
            // and repeat this call.
            closePin(index);
            try {
                Em2mATCommand.sendATC("AT^SCPIN=1," + index + "," + outputVal);
            } catch (ATCommandFailedException e) {}
        }
    }

    /**
     * Close GPIO pin.
     * @param index GPIO pin identification
     */
    public void closePin(int index){
        try {
            Em2mATCommand.sendATC("AT^SCPIN=0," + index);
        } catch (ATCommandFailedException ex) {}
    }

    /**
     * This method sets GPIO pin to log 1 (high) or log 0 (low).
     * Pin must be opened and set using openPin method first!
     * @param index GPIO pin identification
     * @param High pin value (true for high/log 1)
     */
    public void setVal(int index, boolean High){
        int val = (High) ? 1 : 0;
        try {
            Em2mATCommand.sendATC("AT^SSIO=" + index + "," + val);
        } catch (ATCommandFailedException ex) {}
    }

    /**
     * This method returns current logical value on GPIO pin.
     * Pin must be opened and set using openPin method first!
     * @param index GPIO pin identification
     * @return  0 ~ low/log 0
     *          1 ~ high/log 1
     *         -1 ~ error (not properly set or closed)
     */
    public int getVal(int index){
        try {
            String res = Em2mATCommand.sendATC("AT^SGIO=" + index);

            String [] tmp = Em2mString.split(res,"\r\n");

            res = tmp[1].substring(tmp[1].indexOf(": "));

            return (res.equalsIgnoreCase("1")) ? 1 : 0;

        } catch (ATCommandFailedException e) {
            return -1;
        }
    }
}
