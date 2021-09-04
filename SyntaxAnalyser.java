
// Author: Dragos Bogdan Mihai 34816968

public class SyntaxAnalyser extends AbstractSyntaxAnalyser{
    int setSpace = 4;
    int currentSpace = 0;
    private String errorMessage="";

    public void _statementPart_(){
        
        acceptTerminal(Token.beginSymbol);
        myGenerate.commenceNonterminal("Statement Part");
        
        statementsList();
        acceptTerminal(Token.endSymbol);
        myGenerate.finishNonterminal("Statement Part");
    }

    //verify the nature of current token in comparison with the expected and throw an error if is the case
    public void acceptTerminal(int t){
        try{
            if(nextToken.symbol == t){
                keepSpace();
                myGenerate.insertTerminal(nextToken);
                nextToken = lex.getNextToken();
            }
            else{
                errorMessage = "error: expected " + nextToken +" but received " +Token.getName(t);
                myGenerate.reportError(nextToken, errorMessage);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //only for visualising purposes only -> indentation
    //is not perfect
    public void spacingPlus(){
        for(int i=0; i<currentSpace+setSpace;i++){
            System.out.print(" ");
            
        }
        currentSpace = currentSpace+setSpace;
    }
    public void spacingDown(){
        for(int x=0; x<currentSpace-setSpace; x++){
            System.out.print(" ");
        }
        currentSpace=currentSpace-setSpace;
    }
    public void keepSpace(){
        for(int y=0; y<currentSpace; y++){
            System.out.print(" ");
        }
    }

    //start reading the tokens in the current part of the code until reaching a semicolon
    public void statementsList(){
        spacingPlus();
        myGenerate.commenceNonterminal("Statement List");
        statementsAsign();
        while(nextToken.symbol ==Token.semicolonSymbol){
            acceptTerminal(Token.semicolonSymbol);
            statementsAsign();
        }
        spacingDown();
        myGenerate.finishNonterminal("Statement List");
    }
    //assign the tokens found from the list one by one into the specific categories
    public void statementsAsign(){
        spacingPlus();
        myGenerate.commenceNonterminal("Statement");
        switch(nextToken.symbol){
            case Token.callSymbol:  
                callStat();
                break;
            case Token.identifier:
                identifierStat();
                break;
            case Token.ifSymbol:
                ifStat();
                break;
            case Token.whileSymbol:
                whileStat();
                break;
            case Token.untilSymbol:
                untilStat();
                break;
            default:
                try{
                    myGenerate.reportError(nextToken, errorMessage);
                }catch(CompilationException e){
                    e.printStackTrace();
                }
            }
        
        spacingDown();
        myGenerate.finishNonterminal("Statement");
        
    }
    //process the call statements by checkingfirst if they start and end with parantesis
    //and also if there is data in between
    public void callStat(){
        spacingPlus();
        myGenerate.commenceNonterminal("Call Statement");

        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.leftParenthesis);
        argumentStat();
        acceptTerminal(Token.rightParenthesis);

        keepSpace();
        myGenerate.finishNonterminal("Call Statement");
    }
    //in case that there are more indentifiers within the previous statement process all of them 
    public void argumentStat(){   
        acceptTerminal(Token.identifier);
        while(nextToken.symbol==Token.commaSymbol){
            acceptTerminal(Token.commaSymbol);
            acceptTerminal(Token.identifier);
            argumentStat();
        }
    }
    //process the assignment statements
    public void identifierStat(){
        spacingPlus();
        myGenerate.commenceNonterminal("Assignment statement");
        acceptTerminal(Token.identifier);
        acceptTerminal(Token.becomesSymbol);
        if(nextToken.symbol == Token.stringConstant){
            acceptTerminal(Token.stringConstant);
            myGenerate.finishNonterminal("Assignment Statement");
        }else{
            expression();
            spacingDown();
            myGenerate.finishNonterminal("Assigment statement");
            
        }
    }
    //start processing the expresions
    // considering all the posibilities starting from the type of the token found in 
    //indivFactor then decisind what kind of symbol is after, checking first if it is pls or minus
    //going down to multiplication or division checked in term method
    public void expression(){
        spacingPlus();
        myGenerate.commenceNonterminal("Expression");
        terms();
        while(nextToken.symbol==Token.plusSymbol || nextToken.symbol == Token.minusSymbol){
            acceptTerminal(nextToken.symbol);
            terms();
        }
        spacingDown();
        myGenerate.finishNonterminal("Expression");
        
    }
    //check for multiplication and division
    public void terms(){
        spacingPlus();
        myGenerate.commenceNonterminal("Term");
        indivFactor();
        while(nextToken.symbol==Token.timesSymbol || nextToken.symbol ==Token.divideSymbol){
            acceptTerminal(nextToken.symbol);
            indivFactor();
        }
        spacingDown();
        myGenerate.finishNonterminal("Term");
    }
    //decide the nature of the token within the expresion
    public void indivFactor(){
        spacingPlus();
        myGenerate.commenceNonterminal("Factor");
        switch(nextToken.symbol){
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.leftParenthesis:
                acceptTerminal(Token.leftParenthesis);
                expression();
                acceptTerminal(Token.rightParenthesis);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
            default:
                try{
                    myGenerate.reportError(nextToken, errorMessage);
                }catch(CompilationException e){
                    e.printStackTrace();
                }
                break;
        }
        keepSpace();
        myGenerate.finishNonterminal("Factor");
    }
    //process the while statement 
    public void whileStat(){
        spacingPlus();
        myGenerate.commenceNonterminal("While Statement");
        acceptTerminal(Token.whileSymbol);
        cond();
        acceptTerminal(Token.loopSymbol);
        statementsList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        
        spacingDown();
        myGenerate.finishNonterminal("While Statement");
    }

    //process the nature of condition
    public void cond(){
        spacingPlus();
        myGenerate.commenceNonterminal("Condition");
        acceptTerminal(Token.identifier);
        operators();
        switch(nextToken.symbol){
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                try{
                    myGenerate.reportError(nextToken, errorMessage);
                }catch(CompilationException e){
                    e.printStackTrace();
            }
        }
        spacingDown();
        myGenerate.finishNonterminal("Condition");
    }
    //process the nature of the conditional operators
    public void operators(){
        spacingPlus();
        myGenerate.commenceNonterminal("ConditionalOperator");

        switch(nextToken.symbol){
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            default:
                try{
                    myGenerate.reportError(nextToken, errorMessage);
                }catch(CompilationException e){
                    e.printStackTrace();
                }
        }
        keepSpace();
        myGenerate.finishNonterminal("ConditionalOperator");
    }

    //process the do-until statements 
    public void untilStat(){
        spacingPlus();
        myGenerate.commenceNonterminal("Until Statement");
        
        acceptTerminal(Token.doSymbol);
        statementsList();
        acceptTerminal(Token.untilSymbol);
        cond();

        spacingDown();
        myGenerate.finishNonterminal("Until Statement");
    }
    //process the if statements
    public void ifStat(){
        spacingPlus();
        myGenerate.commenceNonterminal("If Statement");

        acceptTerminal(Token.ifSymbol);
        cond();
        acceptTerminal(Token.thenSymbol);
        statementsList();
        if(nextToken.symbol==Token.elseSymbol){
            acceptTerminal(Token.elseSymbol);
            statementsList();
        }
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
        spacingDown();
        myGenerate.finishNonterminal("If Statement");
    }
    
    public SyntaxAnalyser(String file){
        try{
            lex = new LexicalAnalyser(file);
        }catch(Exception e){

        }
    }
}