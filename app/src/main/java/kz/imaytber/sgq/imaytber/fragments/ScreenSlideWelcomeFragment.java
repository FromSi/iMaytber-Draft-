package kz.imaytber.sgq.imaytber.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import kz.imaytber.sgq.imaytber.LockActivity;
import kz.imaytber.sgq.imaytber.R;

/**
 * Created by fromsi on 17.01.18.
 */

public class ScreenSlideWelcomeFragment extends Fragment {
    private String content;
    private TextView textView;
    private Button finish;
    //    private ImageView image;
    private Intent lock;
    private boolean selector;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_welcome, container, false);
        textView = viewGroup.findViewById(R.id.content);
        finish = viewGroup.findViewById(R.id.finish);
//        image = viewGroup.findViewById(R.id.image);

        if (selector)
            finish.setVisibility(View.VISIBLE);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File d = new File(Environment.getExternalStorageDirectory() + "iMaytber");
                d.mkdirs();
                lock = new Intent(getContext(), LockActivity.class);
                startActivity(lock.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        textView.setText(content);
        return viewGroup;
    }

    public void init(String content, boolean selector) {
        this.content = content;
        this.selector = selector;
    }
}
