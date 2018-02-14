package kz.imaytber.sgq.imaytber;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.FriendsRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fromsi on 23.01.18.
 */

public class RecyclerViewAdapterFriend extends RecyclerView.Adapter<RecyclerViewAdapterFriend.FriendAdapter> {
    private List<FriendsRoom> list = new ArrayList<>();
    private AppDatabase db;
    private RestService restService;
    private Context context;
    public void updateList(List<FriendsRoom> list) {
        this.list = list;
    }

    public RecyclerViewAdapterFriend(RestService restService, AppDatabase db, Context context) {
        this.restService = restService;
        this.db = db;
        this.context = context;
    }

    public void addItem(FriendsRoom item){
        list.add(item);
    }

    @Override
    public RecyclerViewAdapterFriend.FriendAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendAdapter(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterFriend.FriendAdapter holder, final int position) {
        int idFriend = list.get(position).getIdfriend();
        holder.nick.setText(db.getUsersDao().getUser(idFriend).getNick());
        holder.idUser.setText("#" + db.getUsersDao().getUser(idFriend).getIduser());
        holder.trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), MessageActivity.class);
                intent.putExtra("idUser", db.getProfileDao().getProfile().getIduser());
                intent.putExtra("idFriend", db.getFriendsDao().getFriends().get(position).getIdfriend());
                context.startActivity(intent);
            }
        });
        loadImg(db.getUsersDao().getUser(idFriend).getAvatar(), holder.avatar);
    }

    public void removeItem(final int position) {
        final int idFriend = list.get(position).getIdfriends();
        restService.deleteFriend(idFriend).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                db.getFriendsDao().delete(idFriend);
                Log.d("Test", "Connect " + idFriend);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("Test", "No Connect");
            }
        });
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FriendAdapter extends RecyclerView.ViewHolder {
        ConstraintLayout trigger;
        TextView nick;
        TextView idUser;
        ImageView avatar;
        public FriendAdapter(View itemView) {
            super(itemView);
            trigger = itemView.findViewById(R.id.trigger);
            nick = itemView.findViewById(R.id.nick);
            idUser = itemView.findViewById(R.id.idUser);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }

    private void chashImg(ImageView img, String name){
        try {
            OutputStream outputStream = new FileOutputStream(new File(context.getCacheDir(), name.substring(73)+".jpg"));
            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImg(final String uri, final ImageView avatar){
        if (uri.equals("default")){
            Picasso.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .into(avatar);
        } else {
            File file = new File(context.getCacheDir(), uri.substring(73)+".jpg");
            if (file.isFile()){
                avatar.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                Picasso.with(context)
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
}
