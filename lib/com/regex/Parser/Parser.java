public class Parser {
        private Scanner scanner;
        private Token   token;

        public Parser(String pattern)
        {
                scanner = new Scanner(pattern);
        }
        
        public void compile() throws Exception
        {
                try{
                        token = scanner.peek();
                        if(token.getTokenType() == TokenType.CARET)
                                System.out.println(token.getTokenType());
                        
                        token = scanner.nextToken(); 
                        if(token.getTokenType() == TokenType.DOLLAR_SIGN){
                                System.out.println(token.getTokenType());
                                token = scanner.nextToken();
                        }

                        if(token.getTokenType() != TokenType.EOF)
                                throw new Exception("Invalid token.");
                }catch(Exception e){
                        System.out.println(e.getMessage());
                }
        }      


        public void regex()
        {
               union(); 
        }

        public void union()
        {
                concatenation();
                unionTail();
        }

        public void unionTail()
        {
                token = scanner.peek();
                if(token.getTokenType() == TokenType.ALTERNATION){
                        try{
                                System.out.println(token.getTokenType());
                                token = scanner.nextToken();
                                union();
                        }catch(Exception e){System.out.println(e.getMessage());}
                }    
        }

        public void concatenation()
        {
                basicRegex();
                concatenationTail();
        }

        public void concatenationTail()
        {
                try{
                        token = scanner.peek();
                        if(token.getTokenType() == TokenType.CHARACTER){
                                concatenation();
                        }
                }catch(Exception e){System.out.println(e.getMessage());}
               

        }

        public void basicRegex()
        {
                try{
                        atom();
                        quantifiers();
                }catch(Exception e){System.out.println(e.getMessage());}
        }

        public void atom() throws Exception
        {
                token = scanner.nextToken();
                switch(token.getTokenType()){
                        case CHARACTER:
                        case PERIOD:
                        case DIGITS:
                        case NON_DIGITS:
                        case WORD:
                        case NON_WORD:
                        case WHITESPACE:
                        case NON_WHITESPACE:
                                /*Do functionality */
                                System.out.println("Atom: " + token.getTokenType());
                        break;
                        case BACK_REFERENCE: 
                                /*do functionality*/
                                System.out.println("Atom: " + token.getTokenType());
                        case CHARACTER_CLASS:
                                /*Do functionality*/
                                System.out.println("Atom: " + token.getTokenType());
                        break;

                        case LEFT_PAREN:
                                group();
                        default:
                                throw new Exception("Invalid token.");
                        
                }
        }

        public void quantifiers()
        {
                try{
                        token = scanner.nextToken();
                        switch(token.getTokenType()){
                                case QUESTION_MARK:
                                        /*do functionality*/
                                        System.out.println("Quantifier: " + token.getTokenType());
                                break; 
                                case STAR:
                                        /*do functionality*/
                                        System.out.println("Quantifier: " + token.getTokenType());
                                break; 
                                case PLUS:
                                        /*do functionality*/
                                        System.out.println("Quantifier: " + token.getTokenType());
                                break;
                                case REPETITION:
                                        /*do functionality*/
                                        System.out.println("Quantifier: " + token.getTokenType());
                                break;
                                default:

                        }
                }catch(Exception e){System.out.println(e.getMessage());}
        }


        public void group()
        {
                /*Parse optional flags*/
                System.out.println("Group");
                regex();
                try{
                        token = scanner.nextToken();
                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                throw new Exception("Missing closing bracket");
                }catch(Exception e){System.out.println(e.getMessage());}
        }

}
