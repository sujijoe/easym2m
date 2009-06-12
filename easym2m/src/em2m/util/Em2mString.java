
package em2m.util;
import java.util.Vector;

/**
 *
 * @author DaM3s3lf
 */
public class Em2mString{

    /**
     * This method implements String.split() function,
     * missing in IMP-NG profile.
     * 
     * @param String original String
     * @param String String used as delimiter
     *
     * @return String[] array of String objects
     *
    **/
    public static String[] split(String str, String delimiter){
        Vector v = new Vector();
        int pointer = str.indexOf(delimiter);
        while (pointer>=0)
        {
            v.addElement(str.substring(0, pointer));
            str=str.substring(pointer+delimiter.length());
            pointer = str.indexOf(delimiter);
        }
        //store last part
        v.addElement(str);

        //crate new string in according size
        String[] res = new String[v.size()];

        //fill new string
        for (int i=0;i<v.size();i++)
        {
            res[i]=(String)v.elementAt(i);
        }
        return res;
    }

}
