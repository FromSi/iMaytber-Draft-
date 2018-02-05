package kz.imaytber.sgq.imaytber.room;
import android.arch.persistence.room.*;

import java.util.List;

/**
 * Created by fromsi on 15.01.18.
 */
@Dao
public interface FriendsDao {
    @Insert
    void insertAll(FriendsRoom... friendsRoom);

    @Insert
    void insert(FriendsRoom friendsRoom);

    @Query("DELETE FROM friendsroom WHERE idfriends=:id")
    void delete(int id);

    @Query("DELETE FROM friendsroom")
    void deleteAll();

    @Query("SELECT * FROM friendsroom")
    List<FriendsRoom> getFriends();
}
