public interface Parser
{
    Node parse() throws SyntaxError, LexicalError, EvalError;
}
