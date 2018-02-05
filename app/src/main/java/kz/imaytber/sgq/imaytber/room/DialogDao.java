package kz.imaytber.sgq.imaytber.room;
import android.arch.persistence.room.*;

import java.util.List;

@Dao
public interface DialogDao {

    @Insert
    void insertAll(List<DialogRoom> dialogRooms);

    @Insert
    void insert(DialogRoom dialogRooms);

    @Query("DELETE FROM dialogroom")
    void deleteAll();

//    @Query("SELECT * FROM ( SELECT * FROM dialogroom ORDER BY date DESC, time DESC) AS orderedTable GROUP BY idpartner")
//    @Query("SELECT * FROM dialogroom where idpartner is 11 ORDER BY date DESC, time DESC ")
//    List<DialogRoom> getDialogs();

    @Query("SELECT * FROM dialogroom where idchats=:idchats ORDER BY idmessage DESC")
    List<DialogRoom> getAllDialogs(int idchats);

    @Query("SELECT * FROM dialogroom where idchats=:idchats ORDER BY idmessage DESC LIMIT 1")
    DialogRoom getDialog(int idchats);

    @Query("SELECT * FROM (select * from dialogroom order by dialogroom.idmessage) as messages GROUP BY messages.idchats ORDER BY messages.idmessage desc")
    List<DialogRoom> getChats();
}
