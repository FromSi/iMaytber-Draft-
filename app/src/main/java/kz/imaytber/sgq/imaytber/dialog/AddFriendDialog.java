package kz.imaytber.sgq.imaytber.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kz.imaytber.sgq.imaytber.R;
import kz.imaytber.sgq.imaytber.RecyclerViewAdapterFriend;
import kz.imaytber.sgq.imaytber.retrofit.FriendGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.retrofit.UserGet;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.FriendsRoom;
import kz.imaytber.sgq.imaytber.room.UsersRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fromsi on 23.01.18.
 */

@SuppressLint("ValidFragment")
public class AddFriendDialog extends DialogFragment {
    private TextView idUser;
    private AppDatabase db;
    private RestService restService;
    private RecyclerViewAdapterFriend adapter;
    @SuppressLint("ValidFragment")
    public AddFriendDialog(AppDatabase db, RecyclerViewAdapterFriend adapter, RestService restService) {
        this.db = db;
        this.adapter = adapter;
        this.restService = restService;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_add_friend, null))
                .setPositiveButton(R.string.dialog_add_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        idUser = getDialog().findViewById(R.id.idUser);
                        restService.addFriend(db.getProfileDao().getProfile().getIduser(),
                                Integer.parseInt(idUser.getText().toString())).enqueue(new Callback<FriendGet>() {
                            @Override
                            public void onResponse(Call<FriendGet> call, Response<FriendGet> response) {
                                FriendsRoom friendsRoom = new FriendsRoom();
                                friendsRoom.setIduser(Integer.parseInt(response.body().getIduser()));
                                friendsRoom.setIdfriend(Integer.parseInt(response.body().getIdfriend()));
                                friendsRoom.setIdfriends(Integer.parseInt(response.body().getIdfriends()));
                                db.getFriendsDao().insert(friendsRoom);
                                adapter.addItem(friendsRoom);
                                if (db.getUsersDao().getUser(Integer.parseInt(response.body().getIdfriend())) == null){
                                    restService.getUser(Integer.parseInt(response.body().getIdfriend())).enqueue(new Callback<UserGet>() {
                                        @Override
                                        public void onResponse(Call<UserGet> call, Response<UserGet> response) {
                                            UsersRoom usersRoom = new UsersRoom();
                                            usersRoom.setNick(response.body().getNick());
                                            usersRoom.setAvatar(response.body().getAvatar());
                                            usersRoom.setIduser(response.body().getIduser());
                                            db.getUsersDao().insert(usersRoom);
                                            Log.d("Test228", "Norm");
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onFailure(Call<UserGet> call, Throwable t) {
                                            Log.d("Test228", "Field");
                                        }
                                    });
                                } else {
                                    adapter.notifyDataSetChanged();
                                }
                                Log.d("Test", "Connect");
                            }

                            @Override
                            public void onFailure(Call<FriendGet> call, Throwable t) {
                                Log.d("Test", "No Connect");
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.dialog_add_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
