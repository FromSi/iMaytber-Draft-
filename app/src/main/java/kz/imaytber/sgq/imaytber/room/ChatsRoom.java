package kz.imaytber.sgq.imaytber.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by fromsi on 29.01.18.
 */
@Entity
public class ChatsRoom {
    @PrimaryKey
    private int idchats;
    private int iduser_1;
    private int iduser_2;

    public int getIdchats() {
        return idchats;
    }

    public void setIdchats(int idchats) {
        this.idchats = idchats;
    }

    public int getIduser_1() {
        return iduser_1;
    }

    public void setIduser_1(int iduser_1) {
        this.iduser_1 = iduser_1;
    }

    public int getIduser_2() {
        return iduser_2;
    }

    public void setIduser_2(int iduser_2) {
        this.iduser_2 = iduser_2;
    }
}
