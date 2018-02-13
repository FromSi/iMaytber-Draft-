package kz.imaytber.sgq.imaytber.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fromsi on 05.02.18.
 */

public class UserGet {

    @SerializedName("nick")
    @Expose
    private String nick;
    @SerializedName("iduser")
    @Expose
    private int iduser;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
}
