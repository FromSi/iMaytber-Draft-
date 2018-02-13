package kz.imaytber.sgq.imaytber;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.ChatsRoom;
import kz.imaytber.sgq.imaytber.room.DialogRoom;

/**
 * Created by fromsi on 21.01.18.
 */

public class RecyclerViewAdapterDialog extends RecyclerView.Adapter<RecyclerViewAdapterDialog.DialogAdapter> {
    private List<DialogRoom> list;
    private Context context;
    private AppDatabase db;
    public void updateList(List<DialogRoom> list) {
        this.list = list;
    }

    public RecyclerViewAdapterDialog(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
    }

    @Override
    public DialogAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog, parent, false);
        return new DialogAdapter(view);
    }

    @Override
    public void onBindViewHolder(DialogAdapter holder, final int position) {
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        final String DEFAULT_AVATAR = "default";
        holder.content.setText(list.get(position).getContent());
        ChatsRoom chatsRoom;
        try {
            holder.time.setText(newTimeFormat.format(new SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                    .parse(list.get(position)
                            .getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idUser = db.getProfileDao().getProfile().getIduser();
                int idUser_1 = db.getChatsDao().getChat_2(list.get(position).getIdchats()).getIduser_1();
                int idUser_2 = db.getChatsDao().getChat_2(list.get(position).getIdchats()).getIduser_2();
                Intent intent = new Intent(context.getApplicationContext(), MessageActivity.class);
                intent.putExtra("idUser", idUser);
                if (idUser_1 != idUser){
                    intent.putExtra("idFriend", idUser_1);
                } else {
                    intent.putExtra("idFriend", idUser_2);
                }
                context.startActivity(intent);
            }
        });
//        Picasso.with(context).load(db.getUsersDao().getUser(list.get(position).getIdpartner()).getAvatar()).into(holder.avatar);

        chatsRoom = db.getChatsDao().getChat_2(list.get(position).getIdchats());
        if (chatsRoom.getIduser_1() != db.getProfileDao().getProfile().getIduser()){
            holder.nick.setText(db.getUsersDao().getUser(chatsRoom.getIduser_1()).getNick());
            if (!DEFAULT_AVATAR.equals(db.getUsersDao().getUser(chatsRoom.getIduser_1()).getAvatar()))
            Picasso.with(context).load(db.getUsersDao().getUser(chatsRoom.getIduser_1()).getAvatar()).into(holder.avatar);
        } else {
            holder.nick.setText(db.getUsersDao().getUser(chatsRoom.getIduser_2()).getNick());
            if (!DEFAULT_AVATAR.equals(db.getUsersDao().getUser(chatsRoom.getIduser_2()).getAvatar()))
            Picasso.with(context).load(db.getUsersDao().getUser(chatsRoom.getIduser_2()).getAvatar()).into(holder.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class DialogAdapter extends RecyclerView.ViewHolder  {
        ConstraintLayout trigger;
        TextView nick;
        TextView content;
        TextView time;
        ImageView avatar;
        public DialogAdapter(View itemView) {
            super(itemView);
            trigger = itemView.findViewById(R.id.trigger);
            nick = itemView.findViewById(R.id.nick);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
