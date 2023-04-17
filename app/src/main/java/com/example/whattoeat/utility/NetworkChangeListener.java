package com.example.whattoeat.utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.whattoeat.R;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Common.isConnectedToInternet(context)) {// Internet is not connected
            AlertDialog.Builder build = new AlertDialog.Builder(context);
            View layout_dialog = LayoutInflater.from(context).inflate(R.layout.checkinternetdialog, null);
            build.setView(layout_dialog);

            AppCompatButton btnRetry = layout_dialog.findViewById(R.id.RetryBtn);

            //Show dialog
            AlertDialog dialog = build.create();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);

            // Retry connection button
            btnRetry.setOnClickListener(view -> {
                dialog.dismiss();
                onReceive(context, intent);
            });
        }
    }
}
