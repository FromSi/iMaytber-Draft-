package kz.imaytber.sgq.imaytber;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kz.imaytber.sgq.imaytber.room.DialogRoom;

/**
 * Created by fromsi on 26.01.18.
 */

public class RecyclerViewAdapterMessage extends RecyclerView.Adapter<RecyclerViewAdapterMessage.HolderRC> {
    private List<DialogRoom> list;
    private int idUser;

    public RecyclerViewAdapterMessage(int idUser) {
        this.idUser = idUser;
    }

    public void updateList(List<DialogRoom> list) {
        this.list = list;
    }

    public void updateList(DialogRoom dialogRoom) {
        list.add(dialogRoom);
    }

    @Override
    public HolderRC onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new HolderRC(view);
    }

    @Override
    public void onBindViewHolder(HolderRC holder, int position) {
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        holder.content.setText(list.get(position).getContent());
        try {
            holder.time.setText(newTimeFormat.format(new SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                    .parse(list.get(position)
                            .getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (idUser == list.get(position).getIdincoming()){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int)holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginStart));
            layoutParams.setMarginStart((int)holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginEnd));
            holder.l_content.setLayoutParams(layoutParams);
            holder.l_content.setGravity(Gravity.START);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int)holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginEnd));
            layoutParams.setMarginStart((int)holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginStart));
            holder.l_content.setLayoutParams(layoutParams);
            holder.l_content.setGravity(Gravity.END);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<DialogRoom> getList() {
        return list;
    }

    public void addItem(DialogRoom item) {
        list.add(0, item);
    }

    public class HolderRC extends RecyclerView.ViewHolder {
        TextView content;
        TextView time;
        LinearLayout l_content;
        public HolderRC(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            l_content = itemView.findViewById(R.id.l_content);
        }
    }

}