public class WhileState implements Node
{
    private Node condition;
    private Node body;

    public WhileState(Node condition, Node body)
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