package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

/**
 * Created by fromsi on 15.01.18.
 */
@Entity
public class UsersRoom {
    @PrimaryKey private int iduser;
    private String avatar;
    private String nick;

    public int getIduser() {
        return iduser;
    }

    public void setIduser(int iduser) {
        this.iduser = iduser;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
