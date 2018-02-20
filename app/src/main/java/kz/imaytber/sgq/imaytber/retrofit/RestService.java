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
                         @Query("login") String login, @Query("password") String password,
                           @Query("api") String api);
    @POST("dialog")
    Call<DialogGet> addDialog(@Query("iduser_1") int iduser_1, @Query("iduser_2") int iduser_2,
                              @Query("content") String content, @Query("date") String date, @Query("time") String time, @Query("photo") String photo);
    @POST("dialog")
    Call<DialogGet> addDialog(@Query("iduser_1") int iduser_1, @Query("iduser_2") int iduser_2,
                              @Query("content") String content, @Query("date") String date, @Query("time") String time, @Query("key") String key, @Query("photo") String photo);
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
    @GET("chat")
    Call<ChatsGet> getChat(@Query("idchats") int idchats);
    @GET("friend")
    Call<List<FriendGet>> getAllFriend(@Query("iduser") int iduser);
    @GET("profile")
    Call<List<ProfileGet>> getAllUsers(@Query("iduser") int iduser);
    @GET("login")
    Call<LoginGet> getLogin(@Query("login") String login, @Query("password") String password);
    @DELETE("friend")
    Call<Void> deleteFriend(@Query("idfriends") int infriends);
    @DELETE("dialog")
    Call<Void> deleteMessage(@Query("iduser") int iduser, @Query("idmessage") int idmessage);
    @PUT("nick")
    Call<Void> putNick(@Query("iduser") int iduser, @Query("nick") String nick);
    @PUT("avatar")
    Call<Void> putAvatar(@Query("iduser") int iduser, @Query("avatar") String avatar);
    @PUT("password")
    Call<Void> putPassword(@Query("iduser") int iduser, @Query("password") String password);
}
