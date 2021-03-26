package game.evgeha.logicalquiz;

public class LevelInfo {

    private String name = ""; //Название уровня
    private int cost = 0; //Стоимость уровня
    private Boolean locked = true; //Состояние уровня(закрыт/открыт)

    public LevelInfo(String name, int cost, boolean locked){
        this.name = name;
        this.cost = cost;
        this.locked = locked;
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
}
