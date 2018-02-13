package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

import java.util.List;

/**
 * Created by fromsi on 15.01.18.
 */
@Dao
public interface UsersDao {
    @Insert
    void insert(UsersRoom usersRooms);

    @Query("DELETE FROM usersroom")
    void deleteAll();

    @Query("SELECT * FROM usersroom where iduser=:iduser")
    UsersRoom getUser(int iduser);

    @Query("SELECT * FROM usersroom")
    List<UsersRoom> getUsers();
}
