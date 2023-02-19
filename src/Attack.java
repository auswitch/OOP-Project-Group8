public class Attack implements Node
{
    private String dir;
    private Expr cost;

    public Attack(String dir, Expr cost)
    {
        this.dir = dir;
        this.cost = cost;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("shoot " + dir + " ");
        s.append(cost);
    }
}
