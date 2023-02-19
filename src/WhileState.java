public class WhileState implements Node
{
    private Expr condition;
    private Node body;

    public WhileState(Expr condition, Node body)
    {
        this.condition = condition;
        this.body = body;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("while (");
        condition.prettyPrint(s);
        s.append(") ");
        body.prettyPrint(s);
    }
}