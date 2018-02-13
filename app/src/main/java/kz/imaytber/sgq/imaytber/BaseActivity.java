package kz.imaytber.sgq.imaytber;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kz.imaytber.sgq.imaytber.dialog.LogOutDialog;
import kz.imaytber.sgq.imaytber.retrofit.ChatsGet;
import kz.imaytber.sgq.imaytber.retrofit.DialogGet;
import kz.imaytber.sgq.imaytber.retrofit.FriendGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.retrofit.UserGet;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ChatsRoom;
import kz.imaytber.sgq.imaytber.room.DialogRoom;
import kz.imaytber.sgq.imaytber.room.FriendsRoom;
import kz.imaytber.sgq.imaytber.room.UsersRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Url;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private DialogFragment logout;
    private TextView nick;
    private TextView id_user;
    private ImageView avatar;
    private AppDatabase db;
    private RestService restService;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private Intent intent;
    private RecyclerView dialogs;
    private RecyclerViewAdapterDialog adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<DialogRoom> chats;
    private boolean checkBool = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        init();
        new BaseThread().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkBool = false;
        db.getDialogDao().deleteAll();
        db.getChatsDao().deleteAll();
        db.getFriendsDao().deleteAll();
        db.getUsersDao().deleteAll();
        db.getProfileDao().deleteAll();
        Log.d("LogTets", "Stop Activity");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friends:
                intent = new Intent(this, FriendsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                drawerLayout.closeDrawers();
                break;
            case R.id.setting:
                intent = new Intent(this, SettingActivity.class);
                drawerLayout.closeDrawers();
                break;
            case R.id.log:
                logout.show(getSupportFragmentManager(), "LogOut");
                break;
            default:
                intent = null;
                break;
        }
        return false;
    }


    class BaseThread extends Thread {
        private String checkAvatar = "default";

        @Override
        public void run() {
            super.run();
            while (checkBool) {
                try {
                    Thread.sleep(1500);
                    if (checkBool){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str = db.getProfileDao().getProfile().getAvatar();
                                if (!checkAvatar.equals(str)) {
                                    checkAvatar = db.getProfileDao().getProfile().getAvatar();
                                    Picasso.with(getApplicationContext()).load(checkAvatar).into(avatar);
                                }
                            }
                        });

                        chats = new ArrayList<>();

                        createDialog();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkMS() {
        restService.getCheckDialog(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<DialogGet>>() {
            @Override
            public void onResponse(Call<List<DialogGet>> call, Response<List<DialogGet>> response) {
                if (response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {

                        if (db.getDialogDao().getDialog_2(response.body().get(i).getIdmessage()) == null) {

                                DialogRoom dialogRoom = new DialogRoom();
                                dialogRoom.setIdmessage(response.body().get(i).getIdmessage());
                                dialogRoom.setDate(response.body().get(i).getDate());
                                dialogRoom.setIdincoming(response.body().get(i).getIdincoming());
                                dialogRoom.setIdchats(response.body().get(i).getIdchats());
                                dialogRoom.setContent(response.body().get(i).getContent());
                                dialogRoom.setTime(response.body().get(i).getTime());
                                db.getDialogDao().insert(dialogRoom);

                                if (db.getChatsDao().getChat_2(response.body().get(i).getIdchats()) == null) {
                                    ChatsRoom chatsRoom = new ChatsRoom();
                                    chatsRoom.setIdchats(response.body().get(i).getIdchats());
                                    chatsRoom.setIduser_1(response.body().get(i).getIdpartner());
                                    chatsRoom.setIduser_2(db.getProfileDao().getProfile().getIduser());
                                    db.getChatsDao().insert(chatsRoom);
                                    if (response.body().get(i).getIdpartner() !=
                                            db.getProfileDao().getProfile().getIduser()) {
                                        initUserDB(db.getChatsDao().getChatAll().get(i).getIduser_1());
                                    } else {
                                        initUserDB(db.getChatsDao().getChatAll().get(i).getIduser_2());
                                    }
                                }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<DialogGet>> call, Throwable t) {

            }
        });
    }

    private void createDialog() {
        checkMS();
        for (int i = 0; i < db.getDialogDao().getChats().size(); i++) {
            DialogRoom local = db.getDialogDao().getChats().get(i);
            DialogRoom dialogRoom = new DialogRoom();
            dialogRoom.setIdmessage(local.getIdmessage());
            dialogRoom.setDate(local.getDate());
            dialogRoom.setIdincoming(local.getIdincoming());
            dialogRoom.setIdchats(local.getIdchats());
            dialogRoom.setContent(local.getContent());
            dialogRoom.setTime(local.getTime());
            chats.add(dialogRoom);
        }
        if (db.getDialogDao().getChats() != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.updateList(chats);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void initUserDB(int idUser){
        if (db.getUsersDao().getUser(idUser) == null)
        restService.getUser(idUser).enqueue(new Callback<UserGet>() {
            @Override
            public void onResponse(Call<UserGet> call, Response<UserGet> response) {
                if (db.getUsersDao().getUser(response.body().getIduser()) == null) {
                    UsersRoom usersRoom = new UsersRoom();
                    usersRoom.setNick(response.body().getNick());
                    usersRoom.setAvatar(response.body().getAvatar());
                    usersRoom.setIduser(response.body().getIduser());
                    db.getUsersDao().insert(usersRoom);
                    Log.d("Test228", "Norm");
                }
            }

            @Override
            public void onFailure(Call<UserGet> call, Throwable t) {
                Log.d("Test228", "Field");
            }
        });
    }

    private void init(){
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        restService = retrofit.create(RestService.class);
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        logout = new LogOutDialog(db);
        toggle = new ActionBarDrawerToggle(BaseActivity.this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (intent != null){
                    startActivity(intent);
                    intent = null;
                }
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navDrawer);
        nick = navigationView.getHeaderView(0).findViewById(R.id.nick);
        id_user = navigationView.getHeaderView(0).findViewById(R.id.id_user);
        nick.setText(db.getProfileDao().getProfile().getNick());
        id_user.setText("#"+db.getProfileDao().getProfile().getIduser());
        avatar = navigationView.getHeaderView(0).findViewById(R.id.avatar);
        navigationView.setNavigationItemSelectedListener(this);
        linearLayoutManager = new LinearLayoutManager(this);
        dialogs = findViewById(R.id.dialogs);
        adapter = new RecyclerViewAdapterDialog(this, db);
        chats = new ArrayList<>();
        adapter.updateList(chats);
        dialogs.setLayoutManager(linearLayoutManager);
        dialogs.setAdapter(adapter);
    }
}