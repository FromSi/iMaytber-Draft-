package kz.imaytber.sgq.imaytber;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import kz.imaytber.sgq.imaytber.room.AppDatabase;
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
//        holder.nick.setText(list.get(position).getNick());
        holder.content.setText(list.get(position).getContent());
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
        public DialogAdapter(View itemView) {
            super(itemView);
            trigger = itemView.findViewById(R.id.trigger);
            nick = itemView.findViewById(R.id.nick);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
        }
    }
}
