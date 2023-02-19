import java.util.LinkedList;

public class PlanParser implements Parser
{
    private Player player;
    private Tokenizer tkz;

    public PlanParser(Player player, Tokenizer tkz)
    {
        this.player = player;
        this.tkz = tkz;
    }

    public Node parse() throws SyntaxError, LexicalError, EvalError
    {
        Node n = parsePlan();
        if (tkz.hasNextToken())
            throw new SyntaxError("leftover token");
        return n;
    }

    // Plan -> Statement+
    private Node parsePlan() throws SyntaxError, LexicalError, EvalError
    {
        if(!tkz.hasNextToken())
            throw new SyntaxError("You must have construction plan !");
        return parseStatement();
    }

    // Statement -> Command | BlockStatement | IfStatement | WhileStatement
    private Node parseStatement() throws SyntaxError, LexicalError, EvalError
    {
        Node n;
        if(tkz.peek("{"))
        {
            n = parseBlock();
        }
        else if (tkz.peek("if"))
        {
            n = parseIf();
        }
        else if (tkz.peek("while"))
        {
            n = parseWhile();
        }
        else
        {
            n = parseCommand();
        }
        return n;
    }

    // Command -> AssignmentStatement | ActionCommand
    // ActionCommand -> done | relocate | MoveCommand | RegionCommand | AttackCommand
    private Node parseCommand() throws SyntaxError, LexicalError, EvalError
    {
        Node n;
        if(tkz.peek("done"))
        {
            tkz.consume();
            n = new Done();
        }
        else if (tkz.peek("relocate"))
        {
            tkz.consume();
            n = new Relocate();
        }
        else if (tkz.peek("move"))
        {
            tkz.consume();
            n = parseMove();
        }
        else if (tkz.peek("invest") || tkz.peek("collect"))
        {
            n = parseRegion();
        }
        else if (tkz.peek("shoot"))
        {
            tkz.consume();
            n = parseAttack();
        }
        else
        {
            n = parseAssignment();
        }
        return n;
    }

    // AssignmentStatement -> <identifier> = Expression
    private Node parseAssignment() throws SyntaxError, LexicalError
    {
        String var = tkz.consume();
        if(!player.checkVar(var))
        {
            player.addVar(var, 0L);
        }
        return new Assignment(var, tkz.consume(), parseE());
    }

    // MoveCommand -> move Direction
    private Node parseMove() throws LexicalError
    {
        return new Move(parseDir());
    }

    // RegionCommand -> invest Expression | collect Expression
    private Node parseRegion() throws LexicalError, SyntaxError
    {
        return new Region(tkz.consume(), parseE());
    }

    // AttackCommand -> shoot Direction Expression
    private Node parseAttack() throws LexicalError, SyntaxError
    {
        return new Attack(parseDir(), parseE());
    }

    // Direction -> up | down | upleft | upright | downleft | downright
    private String parseDir() throws LexicalError
    {
        return tkz.consume();
    }

    // BlockStatement -> { Statement* }
    private Node parseBlock() throws SyntaxError, LexicalError, EvalError
    {
        LinkedList<Node> statements = new LinkedList<>();
        tkz.consume("{");
        while (!tkz.peek("}"))
        {
            statements.add(parseStatement());
        }
        tkz.consume("}");
        return new BlockState(statements);
    }

    // IfStatement -> if ( Expression ) then Statement else Statement
    private Node parseIf() throws SyntaxError, LexicalError, EvalError
    {
        tkz.consume("if");
        tkz.consume("(");
        Expr condition = parseE();
        tkz.consume(")");
        tkz.consume("then");
        Node thenStatement = parseStatement();
        Node elseStatement = null;
        if (tkz.peek("else"))
        {
            tkz.consume("else");
            elseStatement = parseStatement();
        }
        return new IfState(condition, thenStatement, elseStatement);
    }

    // WhileStatement -> while ( Expression ) Statement
    private Node parseWhile() throws SyntaxError, LexicalError, EvalError
    {
        tkz.consume("while");
        tkz.consume("(");
        Expr condition = parseE();
        tkz.consume(")");
        Node body = parseStatement();
        return new WhileState(condition, body);
    }

    // Expression -> Expression + Term | Expression - Term | Term
    private Expr parseE() throws LexicalError, SyntaxError
    {
        Expr e = parseT();
        while (tkz.peek("+"))
        {
            tkz.consume();
            e = new BinaryArithExpr(e, "+", parseT());
        }
        while (tkz.peek("-"))
        {
            tkz.consume();
            e = new BinaryArithExpr(e, "-", parseT());
        }
        return e;
    }

    // Term -> Term * Factor | Term / Factor | Term % Factor | Factor
    private Expr parseT() throws LexicalError, SyntaxError
    {
        Expr e = parseF();
        while ( tkz.hasNextToken() &&
                (tkz.peek("*") || tkz.peek("/") || tkz.peek("%")) )
        {
            if (tkz.peek("*"))
            {
                tkz.consume();
                e = new BinaryArithExpr(e, "*", parseF());
            }
            else if (tkz.peek("/"))
            {
                tkz.consume();
                e = new BinaryArithExpr(e, "/", parseF());
            }
            else if (tkz.peek("%"))
            {
                tkz.consume();
                e = new BinaryArithExpr(e, "%", parseF());
            }
        }
        return e;
    }

    // Factor -> Power ^ Factor | Power
    private Expr parseF() throws LexicalError, SyntaxError
    {
        Expr e = parseP();
        while (tkz.peek("^"))
        {
            tkz.consume();
            e = new BinaryArithExpr(e, "^", parseF());
        }
        return e;
    }

    // Power -> <number> | <identifier> | ( Expression ) | InfoExpression
    private Expr parseP() throws LexicalError, SyntaxError
    {
        if (tkz.peek().matches(".*[0-9].*"))
        {
            return new Number(Long.parseLong(tkz.consume()));
        }
        else if (tkz.peek().matches(".*[A-Za-z].*"))
        {
            return new Identifier(tkz.consume());
        }
        else if (tkz.peek("("))
        {
            tkz.consume();
            Expr e = parseE();
            tkz.consume(")");
            return e;
        }
        else
        {
            return parseInfo();
        }
    }

    // InfoExpression -> opponent | nearby Direction
    private Expr parseInfo() throws LexicalError, SyntaxError
    {
        return new InfoExpr(player, tkz.consume(), tkz.hasNextToken() ? parseDir() : null);
    }
}
