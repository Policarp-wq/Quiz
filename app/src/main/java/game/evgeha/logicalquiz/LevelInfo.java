package game.evgeha.logicalquiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class LevelInfo implements Parcelable {

    public static final String TYPE_COMMON = "COMMON", TYPE_GRAPHIC = "GRAPHIC", TYPE_SOUND = "SOUND";

    private String name = "", type, code;
    private int cost = 0, stages;

    private int record = 0; //Стоимость уровня и рекорд пользователя
    private Boolean locked = true; //Состояние уровня(закрыт/открыт)

    public LevelInfo(String name, int cost, boolean locked, String type, String code, int record){
        this.name = name;
        this.cost = cost;
        this.locked = locked;
        this.type = type;
        this.code = code;
        this.record = record;
    }

    public int getCost() {
        return cost;
    }

    public int getRecord() {
        return record;
    }

    public String getName() {
        return name;
    }

    public boolean isLocked(){
        return locked;
    }

    public void setUnLocked(SharedPreferences.Editor editor){
        editor.putBoolean(getName(), false);
        editor.commit();
        locked = false;
    }

    public String getType(){
        return type;
    }

    public String getCode(){
        return code;
    }

    protected LevelInfo(Parcel in) {
        name = in.readString();
        type = in.readString();
        code = in.readString();
        cost = in.readInt();
        stages = in.readInt();
        record = in.readInt();
        byte tmpLocked = in.readByte();
        locked = tmpLocked == 0 ? null : tmpLocked == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(code);
        dest.writeInt(cost);
        dest.writeInt(stages);
        dest.writeInt(record);
        dest.writeByte((byte) (locked == null ? 0 : locked ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LevelInfo> CREATOR = new Creator<LevelInfo>() {
        @Override
        public LevelInfo createFromParcel(Parcel in) {
            return new LevelInfo(in);
        }

        @Override
        public LevelInfo[] newArray(int size) {
            return new LevelInfo[size];
        }
    };

}
