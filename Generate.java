// Author: Dragos Bogdan Mihai

public class Generate extends AbstractGenerate{
    
    public void reportError( Token token, String explanatoryMessage )throws CompilationException{
        throw new CompilationException(explanatoryMessage);
    }
}