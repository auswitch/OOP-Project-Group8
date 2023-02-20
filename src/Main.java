import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class Main
{
    public static void main(String[] args)
    {
        Charset charset = Charset.forName("UTF-8");

        long m = 0;
        long n = 0;
        long init_plan_min = 0;
        long init_plan_sec = 0;
        long init_budget = 0;
        long init_center_dep = 0;
        long plan_rev_min = 0;
        long plan_rev_sec = 0;
        long rev_cost = 0;
        long max_dep = 0;
        long interest_pct = 0;
        Path ficVar = Paths.get("src/configuration_file.txt");
        try (BufferedReader reader = Files.newBufferedReader(ficVar, charset))
        {
            m = Integer.parseInt(reader.readLine().replace("m=",""));
            n = Integer.parseInt(reader.readLine().replace("n=",""));
            init_plan_min = Integer.parseInt(reader.readLine().replace("init_plan_min=",""));
            init_plan_sec = Integer.parseInt(reader.readLine().replace("init_plan_sec=",""));
            init_budget = Integer.parseInt(reader.readLine().replace("init_budget=",""));
            init_center_dep = Integer.parseInt(reader.readLine().replace("init_center_dep=",""));
            plan_rev_min = Integer.parseInt(reader.readLine().replace("plan_rev_min=",""));
            plan_rev_sec = Integer.parseInt(reader.readLine().replace("plan_rev_sec=",""));
            rev_cost = Integer.parseInt(reader.readLine().replace("rev_cost=",""));
            max_dep = Integer.parseInt(reader.readLine().replace("max_dep=",""));
            interest_pct = Integer.parseInt(reader.readLine().replace("interest_pct=",""));
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage() + " : file not found");
        }

        Map<String, Long> var = new HashMap<>();
        var.put("rows", m);
        var.put("cols", n);
        var.put("currow", 0L);
        var.put("curcol", 0L);
        var.put("budget", init_budget);
        var.put("deposit", init_center_dep);
        var.put("int", interest_pct);
        var.put("maxdeposit", max_dep);

        Player p1 = new Player("player1", var);
        Path file = Paths.get("src/construction_plan.txt");
        try
        {
            String content = Files.readString(file, charset);
            Tokenizer tkz = new PlanTokenizer(content);
            PlanParser plan = new PlanParser(p1, tkz);
            Node p = plan.parse();
            StringBuilder s = new StringBuilder();
            p.prettyPrint(s);
            System.out.println(s);
        }
        catch (NoSuchElementException | LexicalError | SyntaxError | EvalError e)
        {
            System.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage() + " : file not found");
        }

    }
}