package kz.imaytber.sgq.imaytber;


import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kz.imaytber.sgq.imaytber.crypto.FS_RC4;
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
        private String checkNick = db.getProfileDao().getProfile().getNick();

        @Override
        public void run() {
            super.run();
            while (checkBool) {
                try {
                    Thread.sleep(1500);
                    if (checkBool) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nick.setText(db.getProfileDao().getProfile().getNick());
                                if (!checkAvatar.equals(db.getProfileDao().getProfile().getAvatar())) {
                                    checkAvatar = db.getProfileDao().getProfile().getAvatar();
                                    loadImg(db.getProfileDao().getProfile().getAvatar());
                                }
                                if (!checkNick.equals(db.getProfileDao().getProfile().getNick())) {
                                    checkNick = db.getProfileDao().getProfile().getNick();
                                    nick.setText(checkNick);
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
                    final List<DialogGet> list = response.body();
                    for (int i = 0; i < list.size(); i++) {
                        if (db.getDialogDao().getDialog_2(list.get(i).getIdmessage()) == null) {
                            if (db.getChatsDao().getChat_2(list.get(i).getIdchats()) == null) {
                                final int j = i;
                                restService.getChat(list.get(i).getIdchats()).enqueue(new Callback<ChatsGet>() {
                                    @Override
                                    public void onResponse(Call<ChatsGet> call, Response<ChatsGet> response) {
                                        final ChatsRoom chatsRoom = new ChatsRoom();
                                        chatsRoom.setIdchats(response.body().getIdchats());
                                        chatsRoom.setIduser_1(response.body().getIduser_1());
                                        chatsRoom.setIduser_2(response.body().getIduser_2());
                                        chatsRoom.setKey(response.body().getKey());
                                        db.getChatsDao().insert(chatsRoom);

                                        String key = response.body().getKey();
                                        String text = new FS_RC4(key, list.get(j).getContent()).start();
                                        final DialogRoom dialogRoom = new DialogRoom();
                                        dialogRoom.setIdmessage(list.get(j).getIdmessage());
                                        dialogRoom.setDate(list.get(j).getDate());
                                        dialogRoom.setIdincoming(list.get(j).getIdincoming());
                                        dialogRoom.setIdchats(list.get(j).getIdchats());
                                        dialogRoom.setContent(text);
                                        dialogRoom.setTime(list.get(j).getTime());
                                        if (list.get(j).getPhoto() != null)
                                        dialogRoom.setPhoto(list.get(j).getPhoto());
                                        if (list.get(j).getPhoto() != null)
                                            dialogRoom.setPhoto(list.get(j).getPhoto());
                                        db.getDialogDao().insert(dialogRoom);
                                        if (list.get(j).getIdpartner() !=
                                                db.getProfileDao().getProfile().getIduser()) {
                                            initUserDB(db.getChatsDao().getChatAll().get(j).getIduser_1());
                                        } else {
                                            initUserDB(db.getChatsDao().getChatAll().get(j).getIduser_2());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChatsGet> call, Throwable t) {

                                    }
                                });

                            } else {
                                String key = db.getChatsDao().getChat_2(list.get(i).getIdchats()).getKey();
                                String text = new FS_RC4(key, list.get(i).getContent()).start();
                                DialogRoom dialogRoom = new DialogRoom();
                                dialogRoom.setIdmessage(list.get(i).getIdmessage());
                                dialogRoom.setDate(list.get(i).getDate());
                                dialogRoom.setIdincoming(list.get(i).getIdincoming());
                                dialogRoom.setIdchats(list.get(i).getIdchats());
                                dialogRoom.setContent(text);
                                dialogRoom.setTime(list.get(i).getTime());
                                if (list.get(i).getPhoto() != null)
                                dialogRoom.setPhoto(list.get(i).getPhoto());
                                if (list.get(i).getPhoto() != null)
                                    dialogRoom.setPhoto(list.get(i).getPhoto());
                                db.getDialogDao().insert(dialogRoom);
                            }
//                            String key = response.body().get(i).getKey();
//                            String text = new FS_RC4(key, response.body().get(i).getContent()).start();
//                            DialogRoom dialogRoom = new DialogRoom();
//                            dialogRoom.setIdmessage(response.body().get(i).getIdmessage());
//                            dialogRoom.setDate(response.body().get(i).getDate());
//                            dialogRoom.setIdincoming(response.body().get(i).getIdincoming());
//                            dialogRoom.setIdchats(response.body().get(i).getIdchats());
//                            dialogRoom.setContent(text);
//                            dialogRoom.setTime(response.body().get(i).getTime());
//                            db.getDialogDao().insert(dialogRoom);

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
            if (local.getPhoto() != null)
            dialogRoom.setPhoto(local.getPhoto());
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

    private void initUserDB(int idUser) {
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

    private void chashImg(ImageView img, String name) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(this.getCacheDir(), name.substring(73) + ".jpg"));
            Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImg(final String uri) {
        if (uri.equals("default")) {
            Picasso.with(getApplicationContext())
                    .load(R.drawable.ic_launcher_background)
                    .into(avatar);
        } else {
            File file = new File(this.getCacheDir(), uri.substring(73) + ".jpg");
            if (file.isFile()) {
                avatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                Picasso.with(getApplicationContext())
                        .load(uri)
                        .into(avatar, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                chashImg(avatar, uri);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

        }

    }

    private void init() {
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
                if (intent != null) {
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

        id_user.setText("#" + db.getProfileDao().getProfile().getIduser());
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