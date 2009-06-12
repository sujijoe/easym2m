package em2m.sms;

import java.util.Date;

public class SMS {

    private String destinNumber;
    private String sourceNumber;
    private Date received;
    private String message;

    /**
     * Create received short message instance
     */
    public void SMS(String sourceNumber, Date received, String message) {
        this.setSourceNumber(sourceNumber);
        this.setReceived(received);
        this.setMessage(message);
    }
    /**
     * Create short message to send 
     */
    public void SMS(String destNumber, String message)
    {
        this.setMessage(message);
        this.setDestinNumber(destinNumber);
    }

    /**
     * @return the destinNumber
     */
    public String getDestinNumber() {
        return destinNumber;
    }

    /**
     * @param destinNumber the destinNumber to set
     */
    public void setDestinNumber(String destinNumber) {
        this.destinNumber = destinNumber;
    }

    /**
     * @return the sourceNumber
     */
    public String getSourceNumber() {
        return sourceNumber;
    }

    /**
     * @param sourceNumber the sourceNumber to set
     */
    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    /**
     * @return the received
     */
    public Date getReceived() {
        return received;
    }

    /**
     * @param received the received to set
     */
    public void setReceived(Date received) {
        this.received = received;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
