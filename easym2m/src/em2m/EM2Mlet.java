package em2m;

import em2m.sms.SMSManager;
import javax.microedition.midlet.MIDletStateChangeException;

public abstract class EM2Mlet extends javax.microedition.midlet.MIDlet{

    public abstract void startApp() throws MIDletStateChangeException;

    public abstract void pauseApp();

    public abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public abstract void handleEvent(int event, Object data);

    public abstract SMSManager getSMSManager();

}
