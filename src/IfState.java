public class IfState implements Node
{
    private Expr condition;
    private Node thenStatement;
    private Node elseStatement;

    public IfState(Expr condition, Node thenStatement, Node elseStatement)
    {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("if (");
        condition.prettyPrint(s);
        s.append(") ");
        s.append("then ");
        thenStatement.prettyPrint(s);
        if (elseStatement != null) {
            s.append("\nelse ");
            elseStatement.prettyPrint(s);
        }
    }
}