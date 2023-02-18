import java.util.Map;

public class Number implements Expr
{
    private Long val;

    public Number(Long val)
    {
        this.val = val;
    }

    public Long eval(Map<String, Long> bindings)
    {
        return val;
    }

    public void prettyPrint(StringBuilder s)
    {
        s.append(val);
    }
}