import java.util.Map;

public class InfoExpr implements Expr
{
    private Player player;
    private String expr, dir;
    private Long loc;

    public InfoExpr(Player player, String expr, String dir)
    {
        this.player = player;
        this.expr = expr;
        this.dir = dir;
    }

    public Long eval(Map<String, Long> bindings) throws EvalError
    {
        return null;
//        if(expr.equals("opponent"))
//            return player.opponent();
//        else
//            return player.nearby(dir);
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append(expr);
        if(dir != null)
            s.append(dir);
    }
}
