public interface Tokenizer
{
    boolean hasNextToken();
    String peek();
    String consume() throws LexicalError;
}

