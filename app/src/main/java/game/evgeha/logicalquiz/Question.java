package game.evgeha.logicalquiz;

public class Question {
    private String name;
    private String[] vars = new String[4];

    public Question(String[] info, int id){
        this.name = info[id];
        String var1 = info[id + 1], var2= info[id + 2], var3 = info[id + 3], var4 = info[id + 4];
        int nmb = (int) (Math.random() * 4);
        vars[nmb] = var1;
        vars[(nmb + 1 ) % 4] = var2;
        vars[(nmb + 2 ) % 4] = var3;
        vars[(nmb + 3 ) % 4] = var4;
    }

    public String[] getVars() {
        return vars;
    }

    public String getName() {
        return name;
    }
}
