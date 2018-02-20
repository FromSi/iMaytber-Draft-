package kz.imaytber.sgq.imaytber.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import kz.imaytber.sgq.imaytber.LockActivity;
import kz.imaytber.sgq.imaytber.R;
import kz.imaytber.sgq.imaytber.room.AppDatabase;

/**
 * Created by fromsi on 18.01.18.
 */

@SuppressLint("ValidFragment")
public class LogOutDialog extends DialogFragment {
    private Intent lock;
    private AppDatabase db;

    @SuppressLint("ValidFragment")
    public LogOutDialog(AppDatabase db) {
        this.db = db;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_log_out)
        .setPositiveButton(R.string.dialog_log_out_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lock = new Intent(getContext(), LockActivity.class);
                startActivity(lock.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK));
                dialog.dismiss();
            }
        })
        .setNegativeButton(R.string.dialog_log_out_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
