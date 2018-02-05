package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by fromsi on 29.01.18.
 */
@Dao
public interface ChatsDao {
    @Insert
    void insert(ChatsRoom chatsRoom);

    @Query("DELETE FROM chatsroom")
    void deleteAll();

    @Query("SELECT * FROM chatsroom where iduser_1=:idFriend or iduser_2=:idFriend")
    ChatsRoom getChat_1(int idFriend);

    @Query("SELECT * FROM chatsroom where idchats=:idChat LIMIT 1")
    ChatsRoom getChat_2(int idChat);

    @Query("SELECT * FROM chatsroom")
    List<ChatsRoom> getChatAll();
}
