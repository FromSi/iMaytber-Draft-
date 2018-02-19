package kz.imaytber.sgq.imaytber;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.github.badoualy.morphytoolbar.MorphyToolbar;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import kz.imaytber.sgq.imaytber.dialog.EditProfilDialog;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    //    private ImageView avatar;
    private AppDatabase db;
    private RestService restService;
    private Retrofit retrofit;
    private final String URL_RETROFIT = "https://fs-messenger.herokuapp.com/";
    private final String URL_ROOM = "local";
    private Switch s_notification;
    private Switch s_theme;
    private ConstraintLayout l_language_1;
    private TextView t_language;
    private TextView t_russian;
    private TextView t_notification;
    private TextView t_theme;
    private TextView t_english;
    private ConstraintLayout l_language_2;
    private RadioButton r_russian;
    private RadioButton r_english;
    private Toolbar toolbar;

    private MorphyToolbar morphyToolbar;
    private FloatingActionButton fabPhoto;
    private AppBarLayout appBarLayout;

    int primary2;
    int primaryDark2;

    private boolean checkBool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();

        morphyToolbar = MorphyToolbar.builder(this, toolbar)
                .withToolbarAsSupportActionBar()
                .withAnimationDuration(1000)
                .withTitle(db.getProfileDao().getProfile().getNick())
                .withSubtitle("#"+db.getProfileDao().getProfile().getIduser())
                .withPicture(R.drawable.ic_launcher_background)
                .withHidePictureWhenCollapsed(false)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();
        restService = retrofit.create(RestService.class);

        if (!"default".equals(db.getProfileDao().getProfile().getAvatar())) {
            File file = new File(this.getCacheDir(), db.getProfileDao().getProfile().getAvatar().substring(73) + ".jpg");
            morphyToolbar.setPicture(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        new AnimationSleep().start();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new SettingThread().start();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s_notification:
                if (s_notification.isChecked()) {
                    Log.d("SettingLOG", "True");
                } else {
                    Log.d("SettingLOG", "False");
                }
                break;
            case R.id.s_theme:
                if (s_theme.isChecked()) {
                    Log.d("SettingLOG", "True");
                } else {
                    Log.d("SettingLOG", "False");
                }
                break;
            case R.id.l_language_1:
                if (l_language_2.getVisibility() == View.VISIBLE) {
                    l_language_2.setVisibility(View.GONE);
                } else {
                    l_language_2.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.t_russian:
                r_russian.setChecked(true);
                Log.d("SettingLOG", "True");
                break;
            case R.id.t_english:
                r_english.setChecked(true);
                Log.d("SettingLOG", "True");
                break;
            case R.id.r_russian:
                Log.d("SettingLOG", "True");
                break;
            case R.id.r_english:
                Log.d("SettingLOG", "True");
                break;
            case R.id.t_notification:
                if (s_notification.isChecked()) {
                    s_notification.setChecked(false);
                } else {
                    s_notification.setChecked(true);
                }
                Log.d("SettingLOG", "True");
                break;
            case R.id.t_theme:
                if (s_theme.isChecked()) {
                    s_theme.setChecked(false);
                } else {
                    s_theme.setChecked(true);
                }
                Log.d("SettingLOG", "True");
                break;
            case R.id.fab_photo:
                new EditProfilDialog(db, restService)
                        .show(getSupportFragmentManager(), "Edit_Profil");
                Log.d("SettingLOG", "True");
                break;
        }
    }

    private void init() {
        db = Room.databaseBuilder(this, AppDatabase.class, URL_ROOM).allowMainThreadQueries().build();

        appBarLayout = findViewById(R.id.layout_app_bar);
        toolbar = findViewById(R.id.toolBar);
        fabPhoto = findViewById(R.id.fab_photo);
        s_notification = findViewById(R.id.s_notification);
        s_theme = findViewById(R.id.s_theme);
        t_language = findViewById(R.id.t_language);
        l_language_1 = findViewById(R.id.l_language_1);
        l_language_2 = findViewById(R.id.l_language_2);
        t_russian = findViewById(R.id.t_russian);
        t_english = findViewById(R.id.t_english);
        t_english = findViewById(R.id.t_english);

        t_notification = findViewById(R.id.t_notification);
        t_theme = findViewById(R.id.t_theme);

        r_russian = findViewById(R.id.r_russian);
        r_english = findViewById(R.id.r_english);

        t_russian.setOnClickListener(this);
        t_english.setOnClickListener(this);
        s_notification.setOnClickListener(this);
        s_theme.setOnClickListener(this);
        l_language_1.setOnClickListener(this);

        fabPhoto.setOnClickListener(this);

        r_russian.setOnClickListener(this);
        r_english.setOnClickListener(this);

        t_notification.setOnClickListener(this);
        t_theme.setOnClickListener(this);


        primary2 = getResources().getColor(R.color.primary2);
        primaryDark2 = getResources().getColor(R.color.primary_dark2);


    }

    class AnimationSleep extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(500);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        morphyToolbar.expand();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
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
        final File file = new File(this.getCacheDir(), uri.substring(73) + ".jpg");
        if (file.isFile()) {
            morphyToolbar.setPicture(BitmapFactory.decodeFile(file.getAbsolutePath()));
        } else {
            final ImageView img = new ImageView(this);
            Picasso.with(getApplicationContext())
                    .load(uri)
                    .into(img, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            chashImg(img, uri);
                            morphyToolbar.setPicture(BitmapFactory.decodeFile(file.getAbsolutePath()));
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkBool = false;
    }

    class SettingThread extends Thread {
        private String checkAvatar = "default";
        private String checkNick = db.getProfileDao().getProfile().getNick();

        @Override
        public void run() {
            super.run();
            while (checkBool) {
                try {
                    Thread.sleep(1000);
                    if (checkBool) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                morphyToolbar.setTitle(checkNick);
                                if (!checkAvatar.equals(db.getProfileDao().getProfile().getAvatar())) {
                                    checkAvatar = db.getProfileDao().getProfile().getAvatar();
                                    loadImg(db.getProfileDao().getProfile().getAvatar());
                                }
                                if (!checkNick.equals(db.getProfileDao().getProfile().getNick())) {
                                    checkNick = db.getProfileDao().getProfile().getNick();
                                    morphyToolbar.setTitle(checkNick);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
