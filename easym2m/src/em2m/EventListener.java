package em2m;

import com.siemens.icm.io.ATCommandFailedException;
import com.siemens.icm.io.ATCommandListener;
import em2m.sms.SMS;
import em2m.util.Em2mATCommand;

/**
 * Before use of this class, device should be configured,
 * ready (registered) in network
 * @author Family
 */
public class EventListener implements ATCommandListener {

    public static final int INCOMING_CALL=2;
    public static final int NEW_SMS_EVENT=3;
    public static final int GPS_LOCATION_UPDATE=4;
    public static final int GPIO_VALUE_CHANGED=5;

    private EM2Mlet smListener;
    private EM2Mlet gpsListener;
    private EM2Mlet callListener;
    private EM2Mlet gpioListener;

    public EventListener(){
        Em2mATCommand.registerListener(this);
    }

    public void addGPSListener(EM2Mlet gpsListener, int trackingInterval){
        try {
            //register listener
            this.gpsListener = gpsListener;
            //open driver
            Em2mATCommand.sendATC("at^sgpss=1,0");
            //set interval
            Em2mATCommand.sendATC("at^sgpsp="+trackingInterval);
        } catch (ATCommandFailedException e) {; }
    }

    /**
     * BEFORE USING THIS LISTENER, SMS MANAGER SHOULD BE INITIAIZED
     * @param smListener
     * @param servCenter
     */
    public void addSMListener(EM2Mlet smListener, String servCenter){
        try {
            //register listener
            this.smListener = smListener;
            //set serv. center num.
            Em2mATCommand.sendATC("at+csca="+servCenter);
            //set text mode
            Em2mATCommand.sendATC("at+cmgf=1");
            //setup URC representation for incomming SM
            Em2mATCommand.sendATC("at+cnmi=2,1,0,0,1");
        } catch (ATCommandFailedException e) {}
    }

    public void addCallListener(EM2Mlet callListener){
        //register listener
        this.callListener = callListener;
        try {
            //set URC representation of incoming call
            Em2mATCommand.sendATC("at+clip=1");
        } catch (ATCommandFailedException ex) {}
    }

    public void addGPIOListener(EM2Mlet gpioListener){
        this.gpioListener = gpioListener;
    }


    public void ATEvent(String urc) {
        System.out.println("ATEvent: "+urc);System.out.flush();

        if (smListener!=null && urc.indexOf("+CMT")>=0)
            new paralelHandling(urc, smListener).start();

        if (callListener!=null && urc.indexOf("+CLI")>=0)
            new paralelHandling(urc, callListener).start();

        if (gpsListener!=null && urc.indexOf("^SGPSP")>=0)
            new paralelHandling(urc, gpsListener).start();
    }

    public void RINGChanged(boolean SignalState){
        if(SignalState)
            try {
                Em2mATCommand.sendATC("AT+CHUP");
            } catch (ATCommandFailedException ex) { }
    }

    public void DCDChanged(boolean SignalState){
        //System.out.println("DCDChanged: "+SignalState);System.out.flush();
    }

    public void DSRChanged(boolean SignalState){
        //System.out.println("DSRChanged: "+SignalState);System.out.flush();
    }

    public void CONNChanged(boolean SignalState){
        //System.out.println("CONNChanged: "+SignalState);System.out.flush();
    }

    private class paralelHandling extends Thread {

        private EM2Mlet target;
        private String urc;

        public paralelHandling (String urc, EM2Mlet target){
            this.target=target;
            this.urc=urc;
        }

        public void run() {

            //handle incoming SMS
            if (urc.indexOf("+CMTI") >= 0)
            {
                String index = urc.substring(5);
                int i = Integer.parseInt(index);
                SMS message = (target.getSMSManager()).readMessage(i);
                target.handleEvent(EventListener.NEW_SMS_EVENT , message);
            }
            //handle position update
            if (urc.indexOf("^SGPSP") >= 0)
            {
                String nmea = urc.substring(urc.indexOf(":")+1);
                target.handleEvent(EventListener.GPS_LOCATION_UPDATE, nmea);
            }
            if (urc.indexOf("+CLIP") >= 0)
            {
                String numb = urc.substring(urc.indexOf("\"")+1,
                        urc.indexOf("\"", urc.indexOf("\"")-1));
                target.handleEvent(EventListener.INCOMING_CALL, numb);
            }
        }

    }

    public void relase(){
        Em2mATCommand.removeListener(this);
        Em2mATCommand.relaseATCommand();
    }
}
