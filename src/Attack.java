public class Attack implements Node
{
    private Node dir;
    private Expr cost;

    public Attack(Node dir, Expr cost)
    {
        this.dir = dir;
        this.cost = cost;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("shoot ");
        cost.prettyPrint(s);
        dir.prettyPrint(s);
    }
}
