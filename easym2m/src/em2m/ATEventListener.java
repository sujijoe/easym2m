
package em2m;

import com.siemens.icm.io.ATCommandListener;

public class ATEventListener implements ATCommandListener {

    private EM2Mlet listener;

    public void ATEvent(String Event) {
        if(Event.indexOf("+CIEV: message,1")>=0)
            listener.handleEvent("SMS");
    }

    public void RINGChanged(boolean SignalState){
    int i;
    }

    public void DCDChanged(boolean SignalState){
    int i;
    }

    public void DSRChanged(boolean SignalState){
    int i;
    }

    public void CONNChanged(boolean SignalState){
    int i;
    }

    public void setListener(EM2Mlet instance){
        this.listener = instance;
    }

}
