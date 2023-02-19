import java.util.LinkedList;

public class BlockState implements Node
{
    private LinkedList<Node> statements;

    public BlockState(LinkedList<Node> statements)
    {
        this.statements = statements;
    }

    public void prettyPrint(StringBuilder s)
    {
        if(statements.isEmpty())
        {
            s.append("{}");
        }
        else
        {
            s.append("\n{\n");
            for (Node statement : statements)
            {
                s.append("\t");
                statement.prettyPrint(s);
                s.append("\n");
            }
            s.append("}");
        }
    }
}