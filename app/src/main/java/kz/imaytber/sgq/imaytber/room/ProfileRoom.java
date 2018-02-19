package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

/**
 * Created by fromsi on 15.01.18.
 */
@Entity
public class ProfileRoom {
    @PrimaryKey private int iduser;
    private String avatar;
    private String nick;
    private String login;
    private String password;
    private String api;

    public ProfileRoom(int iduser, String avatar, String nick, String login, String password, String api) {
        this.iduser = iduser;
        this.avatar = avatar;
        this.nick = nick;
        this.login = login;
        this.password = password;
        this.api = api;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
