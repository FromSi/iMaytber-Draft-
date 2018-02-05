package kz.imaytber.sgq.imaytber;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private FragmentManager fm;
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
        holder.nick.setText("ID: " + list.get(position).getIdfriend());
        holder.trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getApplicationContext(), MessageActivity.class);
                intent.putExtra("idUser", db.getProfileDao().getProfile().getIduser());
                intent.putExtra("idFriend", db.getFriendsDao().getFriends().get(position).getIdfriend());
                context.startActivity(intent);
            }
        });
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
        public FriendAdapter(View itemView) {
            super(itemView);
            trigger = itemView.findViewById(R.id.trigger);
            nick = itemView.findViewById(R.id.nick);
        }
    }
}
