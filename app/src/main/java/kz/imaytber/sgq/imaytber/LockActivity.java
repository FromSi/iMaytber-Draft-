package kz.imaytber.sgq.imaytber;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import kz.imaytber.sgq.imaytber.crypto.FS_RC4;
import kz.imaytber.sgq.imaytber.crypto.KeyGen;
import kz.imaytber.sgq.imaytber.retrofit.ChatsGet;
import kz.imaytber.sgq.imaytber.retrofit.DialogGet;
import kz.imaytber.sgq.imaytber.retrofit.FriendGet;
import kz.imaytber.sgq.imaytber.retrofit.LoginGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.retrofit.UserGet;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ChatsRoom;
import kz.imaytber.sgq.imaytber.room.DialogRoom;
import kz.imaytber.sgq.imaytber.room.FriendsRoom;
import kz.imaytber.sgq.imaytber.room.ProfileRoom;
import kz.imaytber.sgq.imaytber.room.UsersRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LockActivity extends AppCompatActivity implements View.OnClickListener {
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private Button done;
    private Button selector;
    private EditText nick;
    private EditText login;
    private EditText password;
    private ImageView avatar;
    private TextInputLayout layoutNick;
    private AppDatabase db;
    private RestService restService;
    private Retrofit retrofit;
    private Intent base;
    private StorageReference mStorageRef;
    private Uri uri;
    private boolean checkBool = true;
    private boolean state = true;
    private boolean checkDB_Profile = false;
    private boolean checkDB_Friends = false;
    private boolean checkDB_Chats = false;
    private boolean checkDB_Dialogs = false;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        init();
        new LogInThread().start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                if (!state) {
                    if (uri != null) {
                        StorageReference storageReference = mStorageRef.child("Avatars").child(uri.getLastPathSegment());
                        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                loading.show();
                                doneWrite(String.valueOf(taskSnapshot.getDownloadUrl()));
                            }
                        });
                    } else {
                        loading.show();
                        doneWrite("default");
                    }
                } else {
                    restService.getLogin(login.getText().toString(), password.getText().toString()).enqueue(new Callback<LoginGet>() {
                        @Override
                        public void onResponse(Call<LoginGet> call, Response<LoginGet> response) {

                            if (response.body() != null) {
                                loading.show();
                                Toast.makeText(LockActivity.this, "Connect", Toast.LENGTH_SHORT).show();
                                db.getProfileDao().insert(new ProfileRoom(response.body().getIduser(), response.body().getAvatar(),
                                        response.body().getNick(), response.body().getLogin(), response.body().getPassword(),
                                        response.body().getApi()));
                                checkDB_Profile = true;
                                check();
                            } else {
                                Toast.makeText(LockActivity.this, "Nononon", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<LoginGet> call, Throwable t) {
                            Toast.makeText(LockActivity.this, "No connect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.selector:
                if (state) {
                    state = false;
                    layoutNick.setVisibility(View.VISIBLE);
                    avatar.setVisibility(View.VISIBLE);
                } else {
                    state = true;
                    layoutNick.setVisibility(View.GONE);
                    avatar.setVisibility(View.GONE);
                }
                break;
            case R.id.avatar:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
                break;
            default:
                break;
        }
    }

    class LogInThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (checkBool) {
                    Thread.sleep(500);
                    if (checkDB_Chats && checkDB_Dialogs && checkDB_Friends && checkDB_Profile) {
                        loading.dismiss();
                        startActivity(base.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK));
                        checkBool = false;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            Picasso.with(getApplicationContext()).load(uri).into(avatar);
        }
    }

    private void doneWrite(final String uri) {
        restService.addUser(uri, nick.getText().toString(), login.getText().toString(), password.getText().toString(),
                new KeyGen().generate(40))
                .enqueue(new Callback<LoginGet>() {
                    @Override
                    public void onResponse(Call<LoginGet> call, Response<LoginGet> response) {
                        if (response.body() != null) {
                            Toast.makeText(LockActivity.this, "Connect", Toast.LENGTH_SHORT).show();
                            db.getProfileDao().insert(new ProfileRoom(response.body().getIduser(), uri,
                                    response.body().getNick(), response.body().getLogin(), response.body().getPassword(),
                                    response.body().getApi()));
                            loading.dismiss();
                            startActivity(base.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            Toast.makeText(LockActivity.this, "Nononon", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginGet> call, Throwable t) {
                        Toast.makeText(LockActivity.this, "No connect", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void check() {
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
                        initUserDB(Integer.parseInt(response.body().get(i).getIdfriend()));
                    }
                }
                checkDB_Friends = true;
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
                        if (db.getChatsDao().getChat_2(response.body().get(i).getIdchats()) == null) {
                            ChatsRoom chatsRoom = new ChatsRoom();
                            chatsRoom.setIdchats(response.body().get(i).getIdchats());
                            chatsRoom.setIduser_1(response.body().get(i).getIduser_1());
                            chatsRoom.setIduser_2(response.body().get(i).getIduser_2());
                            chatsRoom.setKey(response.body().get(i).getKey());
                            if (db.getProfileDao().getProfile().getIduser() != response.body().get(i).getIduser_1()) {
                                initUserDB(response.body().get(i).getIduser_1());
                            } else {
                                initUserDB(response.body().get(i).getIduser_2());
                            }
                            db.getChatsDao().insert(chatsRoom);
                        }
                    }
                    for (int i = 0; i < db.getChatsDao().getChatAll().size(); i++) {
                        if (db.getChatsDao().getChatAll().get(i).getIduser_1() !=
                                db.getProfileDao().getProfile().getIduser()) {
                            initUserDB(db.getChatsDao().getChatAll().get(i).getIduser_1());
                        } else {
                            initUserDB(db.getChatsDao().getChatAll().get(i).getIduser_2());
                        }
                    }
                }
                checkDB_Chats = true;
            }

            @Override
            public void onFailure(Call<List<ChatsGet>> call, Throwable t) {

            }
        });
        restService.getAllDialog(db.getProfileDao().getProfile().getIduser()).enqueue(new Callback<List<DialogGet>>() {
            @Override
            public void onResponse(Call<List<DialogGet>> call, Response<List<DialogGet>> response) {
                if (response.body().size() != 0) {
                    final List<DialogGet> list = response.body();
                    for (int i = 0; i < list.size(); i++) {
                        if (db.getDialogDao().getDialog_2(list.get(i).getIdmessage()) == null) {
                            if (db.getChatsDao().getChat_2(list.get(i).getIdchats()) == null) {
                                final int j = i;
                                restService.getChat(list.get(i).getIdchats()).enqueue(new Callback<ChatsGet>() {
                                    @Override
                                    public void onResponse(Call<ChatsGet> call, Response<ChatsGet> response) {
                                        ChatsRoom chatsRoom = new ChatsRoom();
                                        chatsRoom.setIdchats(response.body().getIdchats());
                                        chatsRoom.setIduser_1(response.body().getIduser_1());
                                        chatsRoom.setIduser_2(response.body().getIduser_2());
                                        chatsRoom.setKey(response.body().getKey());
                                        db.getChatsDao().insert(chatsRoom);
                                        if (response.body().getIduser_1() !=
                                                db.getProfileDao().getProfile().getIduser()) {
                                            initUserDB(db.getChatsDao().getChatAll().get(j).getIduser_1());
                                        } else {
                                            initUserDB(db.getChatsDao().getChatAll().get(j).getIduser_2());
                                        }

                                        String key = response.body().getKey();
                                        String text = new FS_RC4(key, list.get(j).getContent()).start();
                                        DialogRoom dialogRoom = new DialogRoom();
                                        dialogRoom.setIdmessage(list.get(j).getIdmessage());
                                        dialogRoom.setDate(list.get(j).getDate());
                                        dialogRoom.setIdincoming(list.get(j).getIdincoming());
                                        dialogRoom.setIdchats(list.get(j).getIdchats());
                                        dialogRoom.setContent(text);
                                        dialogRoom.setTime(list.get(j).getTime());
                                        if (list.get(j).getPhoto() != null)
                                            dialogRoom.setPhoto(list.get(j).getPhoto());
                                        db.getDialogDao().insert(dialogRoom);
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
                checkDB_Dialogs = true;
                Log.d("MyTag", String.valueOf(response.body().size()));
            }

            @Override
            public void onFailure(Call<List<DialogGet>> call, Throwable t) {
                Log.d("MyTag", "Nononoonnoo");
            }
        });
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

    private void init() {
        loading = new ProgressDialog(this);
        loading.setMessage("Loading");
        done = findViewById(R.id.done);
        selector = findViewById(R.id.selector);
        nick = findViewById(R.id.nick);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        avatar = findViewById(R.id.avatar);
        layoutNick = findViewById(R.id.layoutNick);
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        restService = retrofit.create(RestService.class);
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();
        base = new Intent(this, BaseActivity.class);
        base.putExtra("cheack_db", true);
        base.putExtra("checkBool", true);
        done.setOnClickListener(this);
        selector.setOnClickListener(this);
        avatar.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }
}
