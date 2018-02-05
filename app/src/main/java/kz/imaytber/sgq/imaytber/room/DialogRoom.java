package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.*;

/**
 * Created by fromsi on 15.01.18.
 */
@Entity
public class DialogRoom {
    @PrimaryKey private int idmessage;
    private String date;
    private int idincoming;
    private int idchats;
    private String time;
    private String content;
    private int idpartner;

    public int getIdpartner() {
        return idpartner;
    }

    public void setIdpartner(int idpartner) {
        this.idpartner = idpartner;
    }

    public int getIdmessage() {
        return idmessage;
    }

    public void setIdmessage(int idmessage) {
        this.idmessage = idmessage;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
