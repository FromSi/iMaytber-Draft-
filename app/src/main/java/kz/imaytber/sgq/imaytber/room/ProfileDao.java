package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

import java.util.List;

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
}
