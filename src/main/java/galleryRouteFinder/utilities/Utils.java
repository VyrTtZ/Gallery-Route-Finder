package galleryRouteFinder.utilities;

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
}
