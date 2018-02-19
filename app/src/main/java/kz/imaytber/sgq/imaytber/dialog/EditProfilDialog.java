package kz.imaytber.sgq.imaytber.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import kz.imaytber.sgq.imaytber.R;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fromsi on 16.02.18.
 */

@SuppressLint("ValidFragment")
public class EditProfilDialog extends DialogFragment {
    private EditText nick;
    private EditText password;
    private ImageView avatar;
    private AppDatabase db;
    private RestService restService;
    private Uri uri;
    private boolean checkAvatar = false;

    @SuppressLint("ValidFragment")
    public EditProfilDialog(AppDatabase db, RestService restService) {
        this.db = db;
        this.restService = restService;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_edit_profil, null);
        nick = v.findViewById(R.id.nick);
        password = v.findViewById(R.id.password);
        avatar = v.findViewById(R.id.avatar);
        nick.setText(db.getProfileDao().getProfile().getNick());
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
            }
        });
        loadImg(db.getProfileDao().getProfile().getAvatar(), avatar);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setPositiveButton(R.string.dialog_profile_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(R.string.dialog_profile_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }



    private void putNick() {
        if (!db.getProfileDao().getProfile().getNick()
                .equals(nick.getText().toString()) &&
                nick.getText().length() != 0) {
            restService.putNick(db.getProfileDao().getProfile().getIduser(),
                    nick.getText().toString()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    db.getProfileDao().putNick(nick.getText().toString());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
    }

    private void putAvatar() {
        if (uri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference storageReference = mStorageRef.child("Avatars").child(uri.getLastPathSegment());
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    restService.putAvatar(db.getProfileDao().getProfile().getIduser(),
                            String.valueOf(taskSnapshot.getDownloadUrl())).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            db.getProfileDao().putAvatar(String.valueOf(taskSnapshot.getDownloadUrl()));
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }
            });
        }
    }

    private void putPassword() {
        if (!db.getProfileDao().getProfile().getPassword()
                .equals(password.getText().toString()) &&
                password.getText().length() != 0) {
            restService.putPassword(db.getProfileDao().getProfile().getIduser(),
                    password.getText().toString()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    db.getProfileDao().putPassword(password.getText().toString());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
    }

    private void loadImg(final String uri, final ImageView avatar){
        if (uri.equals("default")){
            Picasso.with(getContext())
                    .load(R.drawable.ic_launcher_background)
                    .into(avatar);
        } else {
            File file = new File(getContext().getCacheDir(), uri.substring(73)+".jpg");
            if (file.isFile()){
                avatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                Picasso.with(getContext())
                        .load(uri)
                        .into(avatar, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                chashImg(avatar,uri);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }
    }

    private void chashImg(ImageView img, String name){
        try {
            OutputStream outputStream = new FileOutputStream(new File(getContext().getCacheDir(), name.substring(73)+".jpg"));
            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){
            uri = data.getData();
            Picasso.with(getContext()).load(uri).into(avatar);
            checkAvatar = true;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        putNick();
        putPassword();
        if (checkAvatar)
            putAvatar();
        Log.d("TagTest", db.getProfileDao().getProfile().getNick());
    }
}
