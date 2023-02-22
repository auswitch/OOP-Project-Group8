public class State implements Node
{
    private Node current, next;

    public State(Node current, Node next)
    {
        this.current = current;
        this.next = next;
    }

    public Node getNext()
    {
        return next;
    }

    public void nextState(Node next)
    {
        this.next = next;
    }

    public void prettyPrint(StringBuilder s)
    {
        current.prettyPrint(s);
        s.append('\n');
        if(next != null)
        {
            next.prettyPrint(s);
        }
    }
}
