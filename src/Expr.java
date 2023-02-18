import java.util.Map;

public interface Expr extends Node
{
    Long eval(Map<String, Long> bindings) throws EvalError;
}