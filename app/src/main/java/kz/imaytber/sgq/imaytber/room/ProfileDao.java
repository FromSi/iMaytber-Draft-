package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

import java.util.List;

import retrofit2.http.PUT;

/**
 * Created by fromsi on 15.01.18.
 */
@Dao
public interface ProfileDao {
    @Insert
    void insert(ProfileRoom profileRoom);

    @Query("DELETE FROM profileroom")
    void deleteAll();

    @Query("SELECT * FROM profileroom LIMIT 1")
    ProfileRoom getProfile();

    @Query("UPDATE profileroom SET avatar=:avatar")
    void putAvatar(String avatar);

    @Query("UPDATE profileroom SET nick=:nick")
    void putNick(String nick);

    @Query("UPDATE profileroom SET password=:password")
    void putPassword(String password);

}
