package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

/**
 * Created by fromsi on 15.01.18.
 */
@Entity
public class FriendsRoom {
    @PrimaryKey private int idfriends;
    private int iduser;
    private int idfriend;

    public int getIdfriends() {
        return idfriends;
    }

    public void setIdfriends(int idfriends) {
        this.idfriends = idfriends;
    }

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public int getIdfriend() {
        return idfriend;
    }

    public void setIdfriend(int idfriend) {
        this.idfriend = idfriend;
    }
}
