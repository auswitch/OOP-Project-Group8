enum Direction implements Node
{ up, upleft, upright, down, downleft, downright;
    public void prettyPrint(StringBuilder s)
    {
        s.append(" " + Direction.this);
    }
}


