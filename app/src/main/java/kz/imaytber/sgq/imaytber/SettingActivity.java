package kz.imaytber.sgq.imaytber;

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

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    //    private ImageView avatar;
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

    MorphyToolbar morphyToolbar;
    FloatingActionButton fabPhoto;
    AppBarLayout appBarLayout;

    int primary2;
    int primaryDark2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        avatar = findViewById(R.id.avatar);
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

        r_russian.setOnClickListener(this);
        r_english.setOnClickListener(this);

        t_notification.setOnClickListener(this);
        t_theme.setOnClickListener(this);


        primary2 = getResources().getColor(R.color.primary2);
        primaryDark2 = getResources().getColor(R.color.primary_dark2);

        appBarLayout = (AppBarLayout) findViewById(R.id.layout_app_bar);
        toolbar = findViewById(R.id.toolBar);
        fabPhoto = (FloatingActionButton) findViewById(R.id.fab_photo);
        disableAppBarDrag();
        hideFab();

        morphyToolbar = MorphyToolbar.builder(this, toolbar)
                .withToolbarAsSupportActionBar()
                .withTitle("Work [not so] serious talk")
                .withSubtitle("160 participants")
                .withPicture(R.drawable.ic_launcher_background)
                .withHidePictureWhenCollapsed(false)
                .build();

        morphyToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (morphyToolbar.isCollapsed()) {
                    morphyToolbar.expand(primary2, primaryDark2, new MorphyToolbar.OnMorphyToolbarExpandedListener() {
                        @Override
                        public void onMorphyToolbarExpanded() {
                            showFab();
                        }
                    });
                } else {
                    hideFab();
                    morphyToolbar.collapse();
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_TITLE
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        }
    }

    private void disableAppBarDrag() {
        // see http://stackoverflow.com/questions/34108501/how-to-disable-scrolling-of-appbarlayout-in-coordinatorlayout
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        params.setBehavior(behavior);
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }

    /** To hide fab, you need to remove its anchor */
    private void hideFab() {
        // Ugly bug makes the view go to bottom|center of screen before hiding, seems like you need to implement your own fab behavior...
        fabPhoto.setVisibility(View.GONE);
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabPhoto.getLayoutParams();
        layoutParams.setAnchorId(View.NO_ID);
        fabPhoto.requestLayout();
        fabPhoto.hide();
    }

    private void showFab() {
        final CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fabPhoto.getLayoutParams();
        layoutParams.setAnchorId(R.id.layout_app_bar);
        layoutParams.anchorGravity = Gravity.RIGHT | Gravity.END | Gravity.BOTTOM;
        fabPhoto.requestLayout();
        fabPhoto.show();
    }

}
