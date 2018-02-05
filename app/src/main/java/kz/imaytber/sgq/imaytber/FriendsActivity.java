package kz.imaytber.sgq.imaytber;

import android.arch.persistence.room.Room;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kz.imaytber.sgq.imaytber.dialog.AddFriendDialog;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity {
    private RestService restService;
    private Gson gson;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";

    private Toolbar toolbar;
    private FloatingActionButton add;
    private RecyclerView friends;
    private RecyclerViewAdapterFriend adapter;
    private LinearLayoutManager linearLayoutManager;
    private Paint p = new Paint();
    private AppDatabase db;
    private final String URL_ROOM = "local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friiends);
        friends = findViewById(R.id.friends);
        add = findViewById(R.id.add);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();
        friends.setHasFixedSize(true);
        friends.setLayoutManager(linearLayoutManager);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Friends");
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restService = retrofit.create(RestService.class);
        adapter = new RecyclerViewAdapterFriend(restService, db, this);
        adapterTest();
        friends.setAdapter(adapter);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFriendDialog(db, adapter).show(getSupportFragmentManager(), "AddFriend");
            }
        });
        initSwipe();
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
        Log.d("SettingLOG", "True");
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        if (direction == ItemTouchHelper.LEFT) {
                            adapter.removeItem(position);
                        }
                    }

                    @Override
                    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                            View itemView = viewHolder.itemView;
                            p.setColor(Color.parseColor("#D32F2F"));
                            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                            c.drawRect(background, p);
                        }
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(friends);
    }

    public void adapterTest() {
//        List<FriendsRoom> list = new ArrayList<>();
//        for (int i = 0; i < db.getFriendsDao().getFriends().size(); i++) {
//            FriendsRoom friendsRoom = new FriendsRoom();
//            friendsRoom.setIduser();
//            friendsRoom.setIdfriend();
//            friendsRoom.setIdfriends();
//            list.add();
//        }
        adapter.updateList(db.getFriendsDao().getFriends());
        adapter.notifyDataSetChanged();
    }
}
