package kz.imaytber.sgq.imaytber;


import android.arch.persistence.room.Room;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import kz.imaytber.sgq.imaytber.dialog.LogOutDialog;
import kz.imaytber.sgq.imaytber.retrofit.ChatsGet;
import kz.imaytber.sgq.imaytber.retrofit.DialogGet;
import kz.imaytber.sgq.imaytber.retrofit.FriendGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ChatsRoom;
import kz.imaytber.sgq.imaytber.room.DialogRoom;
import kz.imaytber.sgq.imaytber.room.FriendsRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private DialogFragment logout;
    private TextView nick;
    private AppDatabase db;
    private RestService restService;
    private Gson gson;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private Intent intent;
    private RecyclerView dialogs;
    private RecyclerViewAdapterDialog adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<DialogRoom> chats;
    private boolean checkBool = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        checkBool = getIntent().getBooleanExtra("checkBool", true);
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(gson))
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
                if (intent != null)
                    startActivity(intent);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navDrawer);
        nick = navigationView.getHeaderView(0).findViewById(R.id.nick);
        nick.setText(db.getProfileDao().getProfile().getNick());
        navigationView.setNavigationItemSelectedListener(this);
        check();
        linearLayoutManager = new LinearLayoutManager(this);
        dialogs = findViewById(R.id.dialogs);
        adapter = new RecyclerViewAdapterDialog(this, db);
        chats = new ArrayList<>();
        adapter.updateList(chats);
        dialogs.setLayoutManager(linearLayoutManager);
        dialogs.setAdapter(adapter);
        new BaseThread().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkBool = false;
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

    private void check() {
        if (getIntent().getBooleanExtra("cheack_db", false)) {
            restService.getAllFriend(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<FriendGet>>() {
                @Override
                public void onResponse(Call<List<FriendGet>> call, Response<List<FriendGet>> response) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            FriendsRoom friendsRoom = new FriendsRoom();
                            friendsRoom.setIduser(Integer.parseInt(response.body().get(i).getIduser()));
                            friendsRoom.setIdfriend(Integer.parseInt(response.body().get(i).getIdfriend()));
                            friendsRoom.setIdfriends(Integer.parseInt(response.body().get(i).getIdfriends()));
                            db.getFriendsDao().insert(friendsRoom);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<FriendGet>> call, Throwable t) {

                }
            });
            restService.getAllChats(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<ChatsGet>>() {
                @Override
                public void onResponse(Call<List<ChatsGet>> call, Response<List<ChatsGet>> response) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            ChatsRoom chatsRoom = new ChatsRoom();
                            chatsRoom.setIdchats(response.body().get(i).getIdchats());
                            chatsRoom.setIduser_1(response.body().get(i).getIduser_1());
                            chatsRoom.setIduser_2(response.body().get(i).getIduser_2());
                            db.getChatsDao().insert(chatsRoom);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<ChatsGet>> call, Throwable t) {

                }
            });
            restService.getAllDialog(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<DialogGet>>() {
                @Override
                public void onResponse(Call<List<DialogGet>> call, Response<List<DialogGet>> response) {
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++) {
                            DialogRoom dialogRoom = new DialogRoom();
                            dialogRoom.setIdmessage(response.body().get(i).getIdmessage());
                            dialogRoom.setDate(response.body().get(i).getDate());
                            dialogRoom.setIdincoming(response.body().get(i).getIdincoming());
                            dialogRoom.setIdchats(response.body().get(i).getIdchats());
                            dialogRoom.setContent(response.body().get(i).getContent());
                            dialogRoom.setTime(response.body().get(i).getTime());
                            db.getDialogDao().insert(dialogRoom);
                        }
                    }
                    Log.d("MyTag", String.valueOf(response.body().size()));
                }

                @Override
                public void onFailure(Call<List<DialogGet>> call, Throwable t) {
                    Log.d("MyTag", "Nononoonnoo");
                }
            });

        } else {
//            restService.getAllFriend(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<FriendGet>>() {
//                @Override
//                public void onResponse(Call<List<FriendGet>> call, Response<List<FriendGet>> response) {
//                    if (response.body().size() != 0) {
//                        for (int i = 0; i < response.body().size(); i++) {
//                            FriendsRoom friendsRoom = new FriendsRoom();
//                            friendsRoom.setIduser(Integer.parseInt(response.body().get(i).getIduser()));
//                            friendsRoom.setIdfriend(Integer.parseInt(response.body().get(i).getIdfriend()));
//                            friendsRoom.setIdfriends(Integer.parseInt(response.body().get(i).getIdfriends()));
//                            db.getFriendsDao().insert(friendsRoom);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<List<FriendGet>> call, Throwable t) {
//
//                }
//            });
        }

    }


    class BaseThread extends Thread {
        //        List<DialogRoom> listDialog;
//        List<ChatsRoom> listChats;

        @Override
        public void run() {
            super.run();
//            listDialog = new ArrayList<>();
            while (checkBool) {
                try {
                    Thread.sleep(1500);
//                    if (listChats.equals(db.getChatsDao().getChats())){
                    chats = new ArrayList<>();
                    if (!checkBool)
                        return;
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
//                    }
                    if (db.getDialogDao().getChats() != null) {
//                        list = new ArrayList<>();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.updateList(chats);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private void checkMS(){

        restService.getCheckDialog(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<DialogGet>>() {
            @Override
            public void onResponse(Call<List<DialogGet>> call, Response<List<DialogGet>> response) {
                if (response.body() != null) {
                        for (int i = 0; i < response.body().size(); i++) {
                            boolean chatBool = true;
                            DialogRoom dialogRoom = new DialogRoom();
                            dialogRoom.setIdmessage(response.body().get(i).getIdmessage());
                            dialogRoom.setDate(response.body().get(i).getDate());
                            dialogRoom.setIdincoming(response.body().get(i).getIdincoming());
                            dialogRoom.setIdchats(response.body().get(i).getIdchats());
                            dialogRoom.setContent(response.body().get(i).getContent());
                            dialogRoom.setTime(response.body().get(i).getTime());
                            dialogRoom.setIdpartner(response.body().get(i).getIdpartner());
                            db.getDialogDao().insert(dialogRoom);
                            for (int j = 0; j < db.getChatsDao().getChatAll().size(); j++) {
                                if (db.getChatsDao().getChatAll().get(i).getIdchats() == response.body().get(i).getIdchats())
                                    chatBool = false;
                            }
                            if (chatBool){
                                ChatsRoom chatsRoom = new ChatsRoom();
                                chatsRoom.setIdchats(response.body().get(i).getIdchats());
                                chatsRoom.setIduser_1(response.body().get(i).getIdpartner());
                                chatsRoom.setIduser_2(db.getProfileDao().getProfile().getIduser());
                            }

                        }
                }
            }

            @Override
            public void onFailure(Call<List<DialogGet>> call, Throwable t) {

            }
        });
    }
}