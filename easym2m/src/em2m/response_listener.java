/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package em2m;

/**
 *
 * @author DaJohn
 */
public class response_listener implements com.siemens.icm.io.ATCommandResponseListener{

    public void ATResponse(String Response) {
        System.out.println("!");
    }

}
