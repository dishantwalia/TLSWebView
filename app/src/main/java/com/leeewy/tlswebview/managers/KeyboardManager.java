package com.leeewy.tlswebview.managers;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by dlewsza on 2016-08-25.
 */
public class KeyboardManager {

    public static void hideKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText() && v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
