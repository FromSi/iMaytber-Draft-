package kz.imaytber.sgq.imaytber.retrofit;

/**
 * Created by fromsi on 16.01.18.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileGet {

    @SerializedName("nick")
    @Expose
    private String nick;
    @SerializedName("iduser")
    @Expose
    private String iduser;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
