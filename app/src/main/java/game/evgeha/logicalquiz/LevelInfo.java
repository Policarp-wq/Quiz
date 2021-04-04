package game.evgeha.logicalquiz;

public class LevelInfo {

    public static final String TYPE1 = "COMMON", TYPE2 = "GRAPHIC", TYPE3 = "SOUND";

    private String name = "", type; //Название уровня
    private int cost = 0; //Стоимость уровня
    private Boolean locked = true; //Состояние уровня(закрыт/открыт)

    public LevelInfo(String name, int cost, boolean locked, String type){
        this.name = name;
        this.cost = cost;
        this.locked = locked;
        this.type = type;
    }

    public int getCost() {
        return cost;
    }

    public String getName() {
        return name;
    }

    public boolean isLocked(){
        return locked;
    }

    public void setUnLocked() {
        locked = false;
    }

    public String getType(){
        return type;
    }
}
