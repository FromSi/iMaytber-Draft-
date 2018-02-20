package kz.imaytber.sgq.imaytber;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kz.imaytber.sgq.imaytber.dialog.DeleteMessageDialog;
import kz.imaytber.sgq.imaytber.retrofit.RestService;
import kz.imaytber.sgq.imaytber.room.AppDatabase;
import kz.imaytber.sgq.imaytber.room.DialogRoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fromsi on 26.01.18.
 */

public class RecyclerViewAdapterMessage extends RecyclerView.Adapter<RecyclerViewAdapterMessage.HolderRC> {
    private List<DialogRoom> list = new ArrayList<>();
    private List<Boolean> booleanList;
    private RestService restService;
    private AppDatabase db;
    private int idUser;
    private Context context;


    public RecyclerViewAdapterMessage(Context context,
                                      int idUser, RestService restService,
                                      AppDatabase db) {
        this.context = context;
        this.idUser = idUser;
        this.restService = restService;
        this.db = db;
    }

    public void updateList(List<DialogRoom> list) {
        this.list = list;
        booleanList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            booleanList.add(false);
        }
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
    public void onBindViewHolder(final HolderRC holder, final int position) {
        SimpleDateFormat newTimeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        holder.content.setText(list.get(position).getContent());
        try {
            holder.time.setText(newTimeFormat.format(new SimpleDateFormat("hh:mm:ss", Locale.getDefault())
                    .parse(list.get(position)
                            .getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (idUser == list.get(position).getIdincoming()) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int) holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginStart));
            layoutParams.setMarginStart((int) holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginEnd));
            holder.l_content.setLayoutParams(layoutParams);
            holder.l_content.setGravity(Gravity.START);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginEnd((int) holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginEnd));
            layoutParams.setMarginStart((int) holder.itemView.getResources().getDimension(R.dimen.f_content_layout_marginStart));
            holder.l_content.setLayoutParams(layoutParams);
            holder.l_content.setGravity(Gravity.END);
        }

//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 0; i < booleanList.size(); i++) {
//                    if (booleanList.get(i)) {
//                        if (!booleanList.get(position)) {
//                            holder.item.setBackgroundColor(Color.parseColor("#80d8ff"));
//                            booleanList.set(position, true);
//                        } else {
//                            holder.item.setBackgroundColor(Color.parseColor("#fafafa"));
//                            booleanList.set(position, false);
//                        }
//                        return;
//                    }
//                }
//                holder.item.setBackgroundColor(Color.parseColor("#fafafa"));
//                booleanList.set(position, false);
//                Log.d("ClickTest", "ClickMessage " + booleanList.get(position));
//            }
//        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                holder.item.setBackgroundColor(Color.parseColor("#80d8ff"));
//                booleanList.set(position, true);
//                Log.d("LongTest", "LongMessage " + booleanList.get(position));


                restService.deleteMessage(db.getProfileDao().getProfile().getIduser(),
                        list.get(position).getIdmessage()).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        db.getDialogDao().deleteMessage(list.get(position).getIdmessage());
                        list.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

                return true;
            }
        });
        if (list.get(position).getPhoto() != null){
            holder.photo.setVisibility(View.VISIBLE);
        }

        if (list.get(position).getPhoto() != null){
            holder.photo.setVisibility(View.VISIBLE);
            File file = new File(context.getCacheDir(), list.get(position).getPhoto().substring(73)+".jpg");
            if (file.isFile()){
                holder.photo.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            } else {
                Picasso.with(context.getApplicationContext())
                        .load(list.get(position).getPhoto())
                        .into(holder.photo, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                chashImg(holder.photo, list.get(position).getPhoto());
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        } else {
            holder.photo.setVisibility(View.GONE);
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
        LinearLayout item;
        View view;
        ImageView photo;

        public HolderRC(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            l_content = itemView.findViewById(R.id.l_content);
            item = itemView.findViewById(R.id.item);
            photo = itemView.findViewById(R.id.photo);
            this.view = itemView;
        }
    }

    private void chashImg(ImageView img, String name) {
        try {
            OutputStream outputStream = new FileOutputStream(new File(context.getCacheDir(), name.substring(73) + ".jpg"));
            Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}