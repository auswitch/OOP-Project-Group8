public class Region implements Node
{
    private String command;
    private Expr cost;

    public Region(String command, Expr cost)
    {
        this.command = command;
        this.cost = cost;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append(command);
        cost.prettyPrint(s);
    }
}
