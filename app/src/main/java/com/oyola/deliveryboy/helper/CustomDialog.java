package com.oyola.deliveryboy.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.oyola.deliveryboy.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
    }
}
