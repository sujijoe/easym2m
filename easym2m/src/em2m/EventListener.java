package em2m;

import com.siemens.icm.io.ATCommandListener;
import java.util.Date;

public class EventListener implements ATCommandListener {

    public static final int INCOMING_CALL=2;
    public static final int NEW_SMS_EVENT=3;
    public static final int GPS_LOCATION_UPDATE=4;
    public static final int GPIO_VALUE_CHANGED=5;

    private EM2Mlet listener;

    public EventListener(EM2Mlet listener){
        this.listener = listener;
    }

    public void ATEvent(String Event) {
        System.out.println("ATEvent: "+Event);
        /*if(Event.indexOf("+CIEV: message,1")>=0)
            new paralelHandling(NEW_SMS_EVENT,"blablabla",listener).start();*/
    }

    public void RINGChanged(boolean SignalState){
        System.out.println("RINGChanged: "+SignalState);
    }

    public void DCDChanged(boolean SignalState){
        System.out.println("DCDChanged: "+SignalState);
    }

    public void DSRChanged(boolean SignalState){
        System.out.println("DSRChanged: "+SignalState);
    }

    public void CONNChanged(boolean SignalState){
        System.out.println("CONNChanged: "+SignalState);
    }

    /*public void setListener(EM2Mlet instance){
        this.listener = instance;
    }*/

    private class paralelHandling extends Thread {

        private EM2Mlet target;
        private String data;
        private int event;

        public paralelHandling (int event, String data, EM2Mlet target){
            this.target=target;
            this.event=event;
            this.data=data;
        }

        public void run() {
            System.out.println((new Date()) + " --- Událost ---\n"
                   + event + ":" + data);
            target.handleEvent(event, data);
        }

    }
}
