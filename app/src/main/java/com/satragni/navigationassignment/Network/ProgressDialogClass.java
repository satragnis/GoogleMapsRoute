package com.satragni.navigationassignment.Network;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.satragni.navigationassignment.R;

/**
 * Created by dell on 21/12/17.
 */

public class ProgressDialogClass {
    //Start showing progress

    public static Dialog mProgressDialog;
    public static void showProgressDialog(final Activity activity) {
        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null) {
                        if (mProgressDialog.isShowing()) cancelProgressDialog();
                    }

                    mProgressDialog = new Dialog(activity);
                    mProgressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    mProgressDialog.setContentView(R.layout.progress_indicator);
                    mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mProgressDialog.show();
                    mProgressDialog.setCancelable(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    //Stop showing progress
    public static void cancelProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
