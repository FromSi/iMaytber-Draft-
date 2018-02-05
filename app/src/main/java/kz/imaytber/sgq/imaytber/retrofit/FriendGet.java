package kz.imaytber.sgq.imaytber.retrofit;

/**
 * Created by fromsi on 16.01.18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FriendGet {

    @SerializedName("iduser")
    @Expose
    private String iduser;
    @SerializedName("idfriends")
    @Expose
    private String idfriends;
    @SerializedName("idfriend")
    @Expose
    private String idfriend;

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getIdfriends() {
        return idfriends;
    }

    public void setIdfriends(String idfriends) {
        this.idfriends = idfriends;
    }

    public String getIdfriend() {
        return idfriend;
    }

    public void setIdfriend(String idfriend) {
        this.idfriend = idfriend;
    }

}
