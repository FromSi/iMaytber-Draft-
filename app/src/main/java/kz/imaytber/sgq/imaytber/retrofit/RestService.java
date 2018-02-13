package kz.imaytber.sgq.imaytber.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by fromsi on 15.01.18.
 */

public interface RestService {
    @POST("user")
    Call<LoginGet> addUser(@Query("avatar") String avatar, @Query("nick") String nick,
                         @Query("login") String login, @Query("password") String password);
    @POST("dialog")
    Call<DialogGet> addDialog(@Query("iduser_1") int iduser_1, @Query("iduser_2") int iduser_2,
                         @Query("content") String content,@Query("date") String date, @Query("time") String time);
    @POST("friend")
    Call<FriendGet> addFriend(@Query("iduser") int iduser, @Query("idfriend") int idfriend);
    @GET("user")
    Call<UserGet> getUser(@Query("iduser") int iduser);
    @GET("dialog")
    Call<List<DialogGet>> getAllDialog(@Query("iduser") int iduser);
    @GET("dialog-check")
    Call<List<DialogGet>> getCheckDialog(@Query("iduser") int iduser);
    @GET("dialog-notif")
    Call<DialogGet> getNotifDialog(@Query("iduser") int iduser);
    @GET("chats")
    Call<List<ChatsGet>> getAllChats(@Query("iduser") int iduser);
    @GET("friend")
    Call<List<FriendGet>> getAllFriend(@Query("iduser") int iduser);
    @GET("profile")
    Call<List<ProfileGet>> getAllUsers(@Query("iduser") int iduser);
    @GET("login")
    Call<LoginGet> getLogin(@Query("login") String login, @Query("password") String password);
    @DELETE("friend")
    Call<Void> deleteFriend(@Query("idfriends") int infriends);
}
