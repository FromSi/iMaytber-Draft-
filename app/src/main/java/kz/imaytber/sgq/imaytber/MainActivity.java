package kz.imaytber.sgq.imaytber;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import kz.imaytber.sgq.imaytber.room.AppDatabase;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private Intent welcome;
    private Intent base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcome = new Intent(this, WelcomeActivity.class);
        base = new Intent(this, BaseActivity.class);
        base.putExtra("cheack_db", false);
        db = Room.databaseBuilder(this, AppDatabase.class, "local").allowMainThreadQueries().build();

        login();

    }

    private void login(){
            if (db.getProfileDao().getProfile() == null) {
                startActivity(welcome.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK));
            } else {
                startActivity(base.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK));
            }
    }
}
