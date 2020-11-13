package com.dietmanager.chef.utils;

import java.util.regex.Pattern;

/**
 * Created by Tamil on 9/21/2017.
 */

public class TextUtils {

    private static String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()?<>]).{6,15})";

           /*(			                #   Start of group
               (?=.*\d)		            #   must contains one digit from 0-9
               (?=.*[a-z])		        #   must contains one lowercase characters
               (?=.*[A-Z])		        #   must contains one uppercase characters
               (?=.*[!@#$%^&*()?<>])    #   must contains one special symbols in the list "!@#$%^&*()?<>"
                        .		        #     match anything with previous condition checking
                        {6,15}	        #        length at least 6 characters and maximum of 15
)			# End of group*/

    //Check empty edit text
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    //check Valid Mail address
    public final static boolean isValidEmail(String strText) {
        return strText != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strText).matches();
    }
    //check Valid Mail address
    public final static boolean isValidPassword(String strText) {
        return strText != null && Pattern.compile(PASSWORD_PATTERN).matcher(strText).matches();
    }

}
