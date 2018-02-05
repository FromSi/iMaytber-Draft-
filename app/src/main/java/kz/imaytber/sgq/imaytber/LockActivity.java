package kz.imaytber.sgq.imaytber;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kz.imaytber.sgq.imaytber.retrofit.LoginGet;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ProfileRoom;
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
    private Gson gson;
    private Retrofit retrofit;
    private Intent base;

    private boolean state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        done = findViewById(R.id.done);
        selector = findViewById(R.id.selector);
        nick = findViewById(R.id.nick);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        avatar = findViewById(R.id.avatar);
        layoutNick = findViewById(R.id.layoutNick);
        gson = new GsonBuilder().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        restService = retrofit.create(RestService.class);
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();
        base = new Intent(this, BaseActivity.class);
        base.putExtra("cheack_db", true);
        base.putExtra("checkBool", true);
        done.setOnClickListener(this);
        selector.setOnClickListener(this);
        avatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                if (!state){
                    restService.addUser("defaul", nick.getText().toString(), login.getText().toString(), password.getText().toString())
                            .enqueue(new Callback<LoginGet>() {
                                @Override
                                public void onResponse(Call<LoginGet> call, Response<LoginGet> response) {
                                    if (response.body() != null){
                                        Toast.makeText(LockActivity.this, "Connect", Toast.LENGTH_SHORT).show();
                                        db.getProfileDao().insert(new ProfileRoom(response.body().getIduser(),"default",
                                                response.body().getNick(),response.body().getLogin(),response.body().getPassword()));
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
                } else {
                    restService.getLogin(login.getText().toString(), password.getText().toString()).enqueue(new Callback<LoginGet>() {
                        @Override
                        public void onResponse(Call<LoginGet> call, Response<LoginGet> response) {

                            if (response.body() != null){
                                Toast.makeText(LockActivity.this, "Connect", Toast.LENGTH_SHORT).show();
                                db.getProfileDao().insert(new ProfileRoom(response.body().getIduser(),"default",
                                        response.body().getNick(),response.body().getLogin(),response.body().getPassword()));
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
                break;
            case R.id.selector:
                if (state){
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
                break;
            default:
                break;
        }
    }
}
