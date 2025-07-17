public class Parser {
        private Scanner scanner;
        private Token   token;
        private String  pattern = "";
        int groups = 0;

        public Parser(String pattern)
        {
                scanner = new Scanner(pattern);
        }

        public void compile() throws InvalidTokenException
        {
                regex();
                token = scanner.nextToken(); 
                if(token.getTokenType() != TokenType.EOF){
                        throw new InvalidTokenException("Invalid token: " + Character.toString(token.getValue()));
                }    
        }

        private void regex() throws InvalidTokenException
        {
                token = scanner.peek();
                if(token.getTokenType() == TokenType.CARET){
                        pattern+= Character.toString(token.getValue());
                        token = scanner.nextToken();
                }

                union();
                token = scanner.peek();
                if(token.getTokenType() == TokenType.DOLLAR_SIGN){
                        pattern+= Character.toString(token.getValue());
                        token = scanner.nextToken();
                }
        }

        private void union() throws InvalidTokenException
        {
                concatenation();
                token = scanner.peek();
                if(token.getTokenType() == TokenType.DOLLAR_SIGN){
                        pattern+= Character.toString(token.getValue());
                        token = scanner.nextToken();  
                }

                token = scanner.peek();
                if(token.getTokenType() == TokenType.ALTERNATION){
                        pattern+=Character.toString(token.getValue());
                        token = scanner.nextToken();
                        regex();
                }
        }

        private void concatenation() throws InvalidTokenException
        {
                basicRegex();
                token = scanner.peek();
                switch(token.getTokenType()){
                        case DIGITS:
                        case NON_DIGITS:
                        case WHITESPACE:
                        case NON_WHITESPACE:
                        case WORD:
                        case NON_WORD:
                        case CHARACTER:
                        case COLON:
                        case PERIOD:
                        case LEFT_PAREN:
                        case CHARACTER_CLASS:
                        case BACK_REFERENCE:
                                concatenation();
                        break;
                        default:
                }
        }

        private void basicRegex() throws InvalidTokenException
        {
                atom();
                quantifiers();
        }
        
        private void atom() throws InvalidTokenException
        {
                token = scanner.nextToken();
                switch(token.getTokenType()){
                        case DIGITS:
                        case NON_DIGITS:
                        case WHITESPACE:
                        case NON_WHITESPACE:
                        case WORD:
                        case NON_WORD:
                                pattern+="\\";
                                pattern+=Character.toString(token.getValue());
                        break;
                        case BACK_REFERENCE:
                                if(token.getValue() > groups)
                                        throw new InvalidTokenException("Invalid token: invalid back reference.");
                                pattern+="\\";
                                pattern+=Character.toString(token.getValue());
                        break;
                        case CHARACTER:
                        case COLON:
                                pattern+=printMetaCharacter();
                        break;
                        case PERIOD:
                                pattern+=Character.toString(token.getValue());
                        break;
                        case LEFT_PAREN:
                                pattern+=Character.toString(token.getValue());
                                group();
                        break;
                        case CHARACTER_CLASS:
                                CharacterClass c = scanner.getCharacterClass();
                                pattern+=c.getRepresentation();
                        break;
                        default:
                                throw new InvalidTokenException("Invalid token: "+ Character.toString(token.getValue()));
                }
        }

        private void quantifiers()
        {
                try {
                        token = scanner.peek();
                        switch(token.getTokenType()){
                                case STAR:
                                case QUESTION_MARK:
                                case PLUS:
                                        pattern+=Character.toString(token.getValue());
                                        token = scanner.nextToken();
                                break;
                                case REPETITION:
                                        Repetition repetition = scanner.getRepetition();
                                        pattern+=repetition;
                                        token = scanner.nextToken();
                                break;
                                default:
                                break;
                        }       
                }catch(Exception e){
                    System.err.println(e.getMessage());
                }
        }

        private void group() throws InvalidTokenException
        {
                boolean exit = true;
                String flags = "";
                ++groups;
                /*TO DO
                 * Take care of nonmatch sub group and flags
                 */
                token = scanner.peek();
                if(token.getTokenType() == TokenType.QUESTION_MARK){
                        pattern+=Character.toString(token.getValue());
                        token = scanner.nextToken();
                        token = scanner.peek();
                        if(token.getTokenType() == TokenType.CHARACTER){
                                /*Get flags*/
                                while(exit){
                                        token = scanner.nextToken();
                                        switch(token.getValue()){
                                                case 'i':
                                                case 'm':
                                                case 's':
                                                case 'U':
                                                        flags+=Character.toString(token.getValue());
                                                break;
                                                default:
                                                        exit = false;
                                        }
                                }
                                if("imsU".indexOf(token.getValue()) == -1
                                && token.getTokenType() != TokenType.COLON 
                                && token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: unknow flag.");
                                pattern+= flags;

                                if(token.getTokenType() == TokenType.COLON){
                                        /*Turn off non capturing*/
                                        pattern+=Character.toString(token.getValue());
                                        regex();
                                        token = scanner.nextToken();
                                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                                throw new InvalidTokenException("Invalid token: missing ).");
                                                /*take care of group */
                                                pattern+=Character.toString(token.getValue());

                                }else if(token.getTokenType() == TokenType.RIGHT_PAREN){
                                        /*take care of group */
                                        pattern+=Character.toString(token.getValue());
                                }else 
                                        throw new InvalidTokenException("Invalid token: unknow flag.");
                        }else if(token.getTokenType() == TokenType.COLON){
                                pattern+=Character.toString(token.getValue());
                                token = scanner.nextToken();
                                regex();
                                if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: missing ).");
                                /*take care of group */
                                pattern+=Character.toString(token.getValue());
                        }else throw new InvalidTokenException("Invalid token: unknow flag.");
                }else{
                        regex();
                        token = scanner.nextToken();
                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                throw new InvalidTokenException("Invalid token: missing ).");
                        pattern+=Character.toString(token.getValue());
                }
        }

        public String getPattern(){return pattern;}
        private String printMetaCharacter()
        {
                String rep = "";
                switch(token.getValue()){
                        case '*':
                        case '|':
                        case '?':
                        case '{':
                        case '[':
                        case '(':
                        case ')':
                        case '+':
                        case '.':
                        case '^':
                        case '$':
                                rep+="\\";
                        default:
                }
                rep += Character.toString(token.getValue());
                return rep;
        }
}