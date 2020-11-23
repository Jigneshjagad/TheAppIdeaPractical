package com.theappIdea.practical.utils;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidString(String string) {
        if (string != null && string.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String getStringFromEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isValidEmailAddress(EditText argEditText) {

        try {
            if (isValidString(getStringFromEditText(argEditText))) {
                argEditText.setError(null);
                Pattern pattern = Pattern.compile
                        ("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
                Matcher matcher = pattern.matcher(argEditText.getText());
                if (matcher.matches()) {
                    argEditText.setError(null);
                    return true;
                } else {
//                    argEditText.setError("Please Enter valid email address.");
                    return false;
                }
            } else {
//                argEditText.setError("Please Enter email address.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
