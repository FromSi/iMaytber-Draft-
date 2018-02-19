package kz.imaytber.sgq.imaytber.retrofit;

/**
 * Created by fromsi on 15.01.18.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DialogGet {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("idincoming")
    @Expose
    private int idincoming;
    @SerializedName("idchats")
    @Expose
    private int idchats;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("idmessage")
    @Expose
    private int idmessage;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("idpartner")
    @Expose
    private int idpartner;
    @SerializedName("photo")
    @Expose
    private String photo;

    public int getIdpartner() {
        return idpartner;
    }

    public void setIdpartner(int idpartner) {
        this.idpartner = idpartner;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIdincoming() {
        return idincoming;
    }

    public void setIdincoming(int idincoming) {
        this.idincoming = idincoming;
    }

    public int getIdchats() {
        return idchats;
    }

    public void setIdchats(int idchats) {
        this.idchats = idchats;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIdmessage() {
        return idmessage;
    }

    public void setIdmessage(int idmessage) {
        this.idmessage = idmessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
