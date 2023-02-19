public class Move implements Node
{
    private String dir;

    public Move(String dir)
    {
        this.dir = dir;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append("move " + dir);
    }
}
