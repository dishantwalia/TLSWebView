package com.leeewy.sslwebview.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.leeewy.sslwebview.R;

/**
 * Created by dlewsza on 2016-08-25.
 */
public class MessageDialogFragment extends DialogFragment {

    private static int title;
    private static String message;

    public static MessageDialogFragment getInstance(int title, String message) {
        MessageDialogFragment.title = title;
        MessageDialogFragment.message = message;
        return new MessageDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        Dialog dialog = builder.create();

        dialog.setCancelable(false);
        setCancelable(false);

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
