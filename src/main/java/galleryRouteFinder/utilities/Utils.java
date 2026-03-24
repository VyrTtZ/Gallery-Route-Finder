package galleryRouteFinder.utilities;

import javafx.scene.control.Label;

public class Utils {
    public static String commaStringExtraction(String str, int start)
    {
        String s="";
        while (start<str.length() && str.charAt(start)!=',')
        {
            s+=str.charAt(start);
            start++;
        }
        return s;
    }

    public static boolean checkStringInvalidInteger(String s)
    {
        s=s.trim();
        if (s.isEmpty())
            return true;
        int start=0;
        if (s.startsWith("-") || s.startsWith("+"))
            start=1;
        for (int i=start;i<s.length();i++)
            if (!Character.isDigit(s.charAt(i)))
                return true;
        return false;
    }
}
