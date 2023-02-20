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

    public static boolean isIdentifier(String s)
    {
        // Check that the string is not a reserved word
        switch (s)
        {
            case "collect":
            case "done":
            case "down":
            case "downleft":
            case "downright":
            case "else":
            case "if":
            case "invest":
            case "move":
            case "nearby":
            case "opponent":
            case "relocate":
            case "shoot":
            case "then":
            case "up":
            case "upleft":
            case "upright":
            case "while":
                return false;
            default:
                break;
        }

        // Check that the string starts with a letter
        char firstChar = s.charAt(0);
        if (!Character.isLetter(firstChar))
        {
            return false;
        }
        return true;
    }


        public Node parse() throws SyntaxError, LexicalError, EvalError
    {
        Node n = parsePlan();
        if (tkz.hasNextToken())                         // if true have next word so convert tokenizer not all
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
            return parseBlock();
        }
        else if (tkz.peek("if"))
        {
            return parseIf();
        }
        else if (tkz.peek("while"))
        {
            return parseWhile();
        }
        else
        {
            return parseCommand();
        }
    }

    // Command -> AssignmentStatement | ActionCommand

    private Node parseCommand() throws SyntaxError, LexicalError
    {
        if(isIdentifier(tkz.peek()))
        {
            return parseAssignment();
        }
        else
        {
            return parseAction();
        }
    }
    // ActionCommand -> done | relocate | MoveCommand | RegionCommand | AttackCommand
    private Node parseAction() throws LexicalError, SyntaxError
    {
        if(tkz.peek("done"))
        {
            tkz.consume();
            return new Done();
        }
        else if (tkz.peek("relocate"))
        {
            tkz.consume();
            return new Relocate();
        }
        else if (tkz.peek("move"))
        {
            tkz.consume();
            return parseMove();
        }
        else if (tkz.peek("invest") || tkz.peek("collect"))
        {
            return parseRegion();
        }
        else if (tkz.peek("shoot"))
        {
            tkz.consume();
            return parseAttack();
        }
        else
        {
            throw new SyntaxError("Unknown command: " + tkz);
        }
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
    private Node parseDir() throws LexicalError
    {
        String direction = tkz.consume();
        switch (direction) {
            case "up":
                return Direction.up;
            case "down":
                return Direction.down;
            case "upleft":
                return Direction.upleft;
            case "upright":
                return Direction.upright;
            case "downleft":
                return Direction.downleft;
            case "downright":
                return Direction.downright;
            default:
                throw new AssertionError("Unknown direction: " + direction);
        }
    }

    // BlockStatement -> { Statement* }
    private Node parseBlock() throws SyntaxError, LexicalError, EvalError
    {
        LinkedList<Node> statements = new LinkedList<>();
        tkz.consume("{");
        while(!tkz.peek("}"))
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
        Node condition = parseE();
        tkz.consume(")");
        tkz.consume("then");
        Node thenStatement = parseStatement();
        tkz.consume("else");
        Node elseStatement = parseStatement();
        return new IfState(condition, thenStatement, elseStatement);
    }

    // WhileStatement -> while ( Expression ) Statement
    private Node parseWhile() throws SyntaxError, LexicalError, EvalError
    {
        tkz.consume("while");
        tkz.consume("(");
        Node condition = parseE();
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
        else if(tkz.peek("opponent"))
        {
            tkz.consume();
            return new InfoExpr(player, "opponent", null);
        }
        else if (tkz.peek("nearby"))
        {
            tkz.consume();
            return new InfoExpr(player, "nearby", parseDir());
        }
        else
        {
            throw new SyntaxError("Unexpected token: " + tkz.peek());
        }
    }

    // InfoExpression -> opponent | nearby Direction
//    private Expr parseInfo() throws LexicalError, SyntaxError
//    {
//        return new InfoExpr(player, tkz.consume(), tkz.hasNextToken() ? parseDir() : null);
//    }
}
