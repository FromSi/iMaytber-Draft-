package kz.imaytber.sgq.imaytber;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;

import kz.imaytber.sgq.imaytber.crypto.FS_RC4;
import kz.imaytber.sgq.imaytber.crypto.KeyGen;
import kz.imaytber.sgq.imaytber.retrofit.ChatsGet;
import kz.imaytber.sgq.imaytber.retrofit.DialogGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ChatsRoom;
import kz.imaytber.sgq.imaytber.room.DialogRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private AppDatabase db;
    private RestService restService;
    private Gson gson;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private RecyclerView message;
    private LinearLayoutManager linearLayoutManager;
    private EditText content;
    private ImageView send;
    private ImageView photo;
    private Button clear;
    private RecyclerViewAdapterMessage adapter;
    private int idUser;
    private int idFriend;
    private MessageThread messageThread;
    private boolean check = true;
//    private boolean checkPhoto = false;
    private Toolbar toolbar;
    private MorphyToolbar morphyToolbar;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restService = retrofit.create(RestService.class);

        idFriend = getIntent().getIntExtra("idFriend", 0);
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();
        idUser = db.getProfileDao().getProfile().getIduser();
        toolbar = findViewById(R.id.toolBar);

        morphyToolbar = MorphyToolbar.builder(this, toolbar)
                .withTitle(db.getUsersDao().getUser(idFriend).getNick())
                .withSubtitle("#"+db.getUsersDao().getUser(idFriend).getIduser())
                .withTitleColor(Color.BLACK)
                .withSubtitleColor(Color.BLACK)
                .withPicture(R.drawable.ic_launcher_background)
                .withHidePictureWhenCollapsed(false)
                .build();

        if (!"default".equals(db.getUsersDao().getUser(idFriend).getAvatar())){
            File file = new File(this.getCacheDir(), db.getUsersDao().getUser(idFriend).getAvatar().substring(73)+".jpg");
            morphyToolbar.setPicture(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        message = findViewById(R.id.message);
        message.setLayoutManager(linearLayoutManager);
        content = findViewById(R.id.content);
        send = findViewById(R.id.send);
        photo = findViewById(R.id.photo);
        clear = findViewById(R.id.clear);
        send.setOnClickListener(this);
        photo.setOnClickListener(this);
        clear.setOnClickListener(this);
        adapter = new RecyclerViewAdapterMessage(this, db.getProfileDao().getProfile().getIduser(), restService, db);
        message.setAdapter(adapter);
        messageThread = new MessageThread();
        messageThread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        check = false;
        Log.d("MyThread", "Stop");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                if (content.getText().length() != 0){
                    addMessage();
                    clearPhoto();
                }
                break;
            case R.id.photo:
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
                Log.d("ButtonTestMessage","photo");
                break;
            case R.id.clear:
                clearPhoto();
                Log.d("ButtonTestMessage","clear");
                break;
        }
    }

    private void clearPhoto(){
        uri = null;
        photo.setScaleType(ImageView.ScaleType.CENTER);
        photo.setImageResource(R.drawable.ic_burst_mode_black_24dp);
        clear.setVisibility(View.GONE);
//        checkPhoto = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            Picasso.with(getApplicationContext()).load(uri).into(photo);
            clear.setVisibility(View.VISIBLE);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            checkPhoto = true;
        }
    }

    private void addMessage(){
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference storageReference = mStorageRef.child("Photos").child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    initMessage(String.valueOf(taskSnapshot.getDownloadUrl()));
                }
            });
            uri = null;
        } else {
            initMessage(null);
        }
    }

    private void initMessage(String photo){
        String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()));
        String time = String.valueOf(android.text.format.DateFormat.format("hh:mm:ss", new java.util.Date()));
        if (db.getChatsDao().getChat_1(idFriend) == null){
            final String key = new KeyGen().generate(20);
            String text = new FS_RC4(key,content.getText().toString()).start();
            restService.addDialog(db.getProfileDao().getProfile().getIduser(), idFriend,
                    text, date, time, key, photo).enqueue(new Callback<DialogGet>() {
                @Override
                public void onResponse(Call<DialogGet> call, Response<DialogGet> response) {
                    if (response.body() != null){
                        DialogRoom dialogRoom = new DialogRoom();
                        dialogRoom.setIdmessage(response.body().getIdmessage());
                        dialogRoom.setDate(response.body().getDate());
                        dialogRoom.setIdincoming(response.body().getIdincoming());
                        dialogRoom.setIdchats(response.body().getIdchats());
                        dialogRoom.setContent(content.getText().toString());
                        dialogRoom.setTime(response.body().getTime());
                        if (response.body().getPhoto() != null)
                            dialogRoom.setPhoto(response.body().getPhoto());
                        db.getDialogDao().insert(dialogRoom);
                        ChatsRoom chatsRoom = new ChatsRoom();
                        chatsRoom.setIdchats(response.body().getIdchats());
                        chatsRoom.setIduser_1(idUser);
                        chatsRoom.setIduser_2(idFriend);
                        chatsRoom.setKey(key);
                        db.getChatsDao().insert(chatsRoom);
                        adapter.updateList(dialogRoom);
                        adapter.notifyDataSetChanged();
                        content.setText(null);
                    }
                }

                @Override
                public void onFailure(Call<DialogGet> call, Throwable t) {

                }
            });
        } else {
            String key = db.getChatsDao().getChat_1(idFriend).getKey();
            String text = new FS_RC4(key,content.getText().toString()).start();
            restService.addDialog(db.getProfileDao().getProfile().getIduser(), idFriend,
                    text, date, time, photo).enqueue(new Callback<DialogGet>() {
                @Override
                public void onResponse(Call<DialogGet> call, Response<DialogGet> response) {
                    if (response.body() != null){
                        DialogRoom dialogRoom = new DialogRoom();
                        dialogRoom.setIdmessage(response.body().getIdmessage());
                        dialogRoom.setDate(response.body().getDate());
                        dialogRoom.setIdincoming(response.body().getIdincoming());
                        dialogRoom.setIdchats(response.body().getIdchats());
                        dialogRoom.setContent(content.getText().toString());
                        dialogRoom.setTime(response.body().getTime());
                        if (response.body().getPhoto() != null)
                            dialogRoom.setPhoto(response.body().getPhoto());
                        db.getDialogDao().insert(dialogRoom);
                        adapter.addItem(dialogRoom);
                        adapter.notifyDataSetChanged();
                        content.setText(null);
                    }
                }

                @Override
                public void onFailure(Call<DialogGet> call, Throwable t) {

                }
            });
        }
    }

    class MessageThread extends Thread {
        int sizeMS = 0;

        @Override
        public void run() {
            super.run();
            if (db.getChatsDao().getChat_1(idFriend) != null) {
                adapter.updateList(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()));
                adapter.notifyDataSetChanged();
            }

            while (check) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (db.getChatsDao().getChat_1(idFriend) != null)
                                if (sizeMS < db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()).size()) {
                                    sizeMS = db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()).size();
                                    adapter.updateList(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()));
                                    adapter.notifyDataSetChanged();
                                }
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("MyThread", "Sec");
            }
        }

    }
}
