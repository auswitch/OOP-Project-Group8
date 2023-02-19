public class Assignment implements Node
{
    private String var, op;
    private Expr num;

    public Assignment(String name, String op, Expr num)
    {
        this.var = name;
        this.op = op;
        this.num = num;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append(var + op);
        num.prettyPrint(s);
    }
}
