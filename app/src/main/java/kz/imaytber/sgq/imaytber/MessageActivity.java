package kz.imaytber.sgq.imaytber;

import android.arch.persistence.room.Room;
import android.graphics.Color;
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

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

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

public class MessageActivity extends AppCompatActivity {
    private AppDatabase db;
    private RestService restService;
    private Gson gson;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private RecyclerView message;
    private LinearLayoutManager linearLayoutManager;
    private EditText content;
    private Button send;
    private RecyclerViewAdapterMessage adapter;
    private int idUser;
    private int idFriend;
    private MessageThread messageThread;
    private boolean check = true;
    private Toolbar toolbar;

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

        MorphyToolbar morphyToolbar = MorphyToolbar.builder(this, toolbar)
//                .withToolbarAsSupportActionBar()
                .withTitle("Nick")
                .withSubtitle("Title")
                .withTitleColor(Color.BLACK)
                .withSubtitleColor(Color.BLACK)
                .withPicture(R.drawable.ic_launcher_background)
                .withHidePictureWhenCollapsed(false)
                .build();

//        morphyToolbar.expand();
//        morphyToolbar.collapse();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        message = findViewById(R.id.message);
        message.setLayoutManager(linearLayoutManager);
//        adapter = new RecyclerViewAdapterMessage();
//        message.setAdapter(adapter);
        content = findViewById(R.id.content);
        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()));
                String time = String.valueOf(android.text.format.DateFormat.format("hh:mm:ss", new java.util.Date()));
                restService.addDialog(db.getProfileDao().getProfile().getIduser(), idFriend,
                        content.getText().toString(), date, time).enqueue(new Callback<DialogGet>() {
                    @Override
                    public void onResponse(Call<DialogGet> call, Response<DialogGet> response) {
                        if (db.getChatsDao().getChat_1(idFriend) == null) {
                            DialogRoom dialogRoom = new DialogRoom();
                            dialogRoom.setIdmessage(response.body().getIdmessage());
                            dialogRoom.setDate(response.body().getDate());
                            dialogRoom.setIdincoming(response.body().getIdincoming());
                            dialogRoom.setIdchats(response.body().getIdchats());
                            dialogRoom.setContent(response.body().getContent());
                            dialogRoom.setTime(response.body().getTime());
                            dialogRoom.setIdpartner(response.body().getIdpartner());
                            db.getDialogDao().insert(dialogRoom);
                            ChatsRoom chatsRoom = new ChatsRoom();
                            chatsRoom.setIdchats(response.body().getIdchats());
                            chatsRoom.setIduser_1(idUser);
                            chatsRoom.setIduser_2(idFriend);
                            adapter.updateList(dialogRoom);
                            adapter.notifyDataSetChanged();
                        } else {
                            DialogRoom dialogRoom = new DialogRoom();
                            dialogRoom.setIdmessage(response.body().getIdmessage());
                            dialogRoom.setDate(response.body().getDate());
                            dialogRoom.setIdincoming(response.body().getIdincoming());
                            dialogRoom.setIdchats(response.body().getIdchats());
                            dialogRoom.setContent(response.body().getContent());
                            dialogRoom.setTime(response.body().getTime());
                            dialogRoom.setIdpartner(response.body().getIdpartner());
                            db.getDialogDao().insert(dialogRoom);
                            adapter.addItem(dialogRoom);
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<DialogGet> call, Throwable t) {

                    }
                });
                content.setText(null);
            }
        });

        adapter = new RecyclerViewAdapterMessage(db.getProfileDao().getProfile().getIduser());
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
//        messageThread.stop();
        check = false;
        Log.d("MyThread", "Stop");
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
                            if (sizeMS < db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()).size()) {
                                sizeMS = db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()).size();
                                adapter.updateList(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat_1(idFriend).getIdchats()));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });


//                    if (sizeMS < db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).size()){
//                        sizeMS = db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).size();
//                        adapter.updateList(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()));
//                        adapter.notifyDataSetChanged();


//                        for (int i = 0; i < db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).size(); i++) {
//                            DialogRoom dialogRoom = new DialogRoom();
//                            dialogRoom.setIdmessage(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getIdmessage());
//                            dialogRoom.setDate(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getDate());
//                            dialogRoom.setIdincoming(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getIdincoming());
//                            dialogRoom.setIdchats(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getIdchats());
//                            dialogRoom.setContent(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getContent());
//                            dialogRoom.setTime(db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getTime());
//                            if (db.getDialogDao().getAllDialogs(db.getChatsDao().getChat(idFriend).getIdchats()).get(i).getIdincoming() != idFriend) {
//                                adapter.addItem(dialogRoom);
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("MyThread", "Sec");
            }
        }

}
}
