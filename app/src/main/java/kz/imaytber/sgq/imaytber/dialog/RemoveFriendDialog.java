package kz.imaytber.sgq.imaytber.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kz.imaytber.sgq.imaytber.LockActivity;
import kz.imaytber.sgq.imaytber.R;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fromsi on 23.01.18.
 */

@SuppressLint("ValidFragment")
public class RemoveFriendDialog extends DialogFragment {
    private int idFriends;
    private AppDatabase db;
    private RestService restService;
    private Gson gson;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";

    @SuppressLint("ValidFragment")
    public RemoveFriendDialog(AppDatabase db, int idFriends) {
        this.db = db;
        this.idFriends = idFriends;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restService = retrofit.create(RestService.class);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_remove)
                .setPositiveButton(R.string.dialog_remove_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restService.deleteFriend(idFriends).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                db.getFriendsDao().delete(idFriends);
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.d("Test", "No Connect");
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_remove_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
