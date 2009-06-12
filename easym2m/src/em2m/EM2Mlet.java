package em2m;

import javax.microedition.midlet.MIDlet;


public abstract class EM2Mlet extends MIDlet{

    public abstract void startApp();

    public abstract void pauseApp();

    public abstract void destroyApp(boolean unconditional);

    public abstract void handleEvent(String event);

}
