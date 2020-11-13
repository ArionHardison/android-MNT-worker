package com.dietmanager.chef.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.dietmanager.chef.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
    }
}
