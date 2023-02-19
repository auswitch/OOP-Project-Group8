import java.util.Map;

public class Player
{
    private String name;
    private Map<String, Long> var;

    public Player(String name, Map<String, Long> var)
    {
        this.name = name;
        this.var = var;
    }

    void addVar(String var, Long x)
    {
        if(this.var.containsKey(var))
        {
            this.var.replace(var, x);
        }
        else
        {
            this.var.put(var, 0L);
        }
    }

    Map<String, Long> getVar()
    {
        return var;
    }

    boolean checkVar(String var)
    {
        return this.var.containsKey(var);
    }
}