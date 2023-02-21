public class Move implements Node
{
    private Node dir;

    public Move(Node dir)
    {
        this.dir = dir;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("move");
        dir.prettyPrint(s);
    }
}
