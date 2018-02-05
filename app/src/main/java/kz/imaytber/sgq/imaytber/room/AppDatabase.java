package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

/**
 * Created by fromsi on 15.01.18.
 */
@Database(entities = {ProfileRoom.class, UsersRoom.class,
        DialogRoom.class, FriendsRoom.class, ChatsRoom.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProfileDao getProfileDao();

    public abstract UsersDao getUsersDao();

    public abstract DialogDao getDialogDao();

    public abstract FriendsDao getFriendsDao();

    public abstract ChatsDao getChatsDao();
}
