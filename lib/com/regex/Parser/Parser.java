package parser;
import automaton.State;
import exceptions.InvalidTokenException;
import lexical.*;

public class Parser {
        private Scanner scanner;
        private Token   token;
        private String  pattern = "";
        private int groups = 0;
        public Parser(String pattern)
        {
                scanner = new Scanner(pattern);
        }

        public void compile() throws InvalidTokenException
        {
                regex();
                token = scanner.nextToken(); 
                if(token.getTokenType() != TokenType.EOF){
                        throw new InvalidTokenException("Invalid token: " + token.toString());
                }    
        }

        private void regex() throws InvalidTokenException
        {
                union();
                token = scanner.peek();
        }

        private void union() throws InvalidTokenException
        {
                concatenation();
                token = scanner.peek();
                if(token.getTokenType() == TokenType.ALTERNATION){
                        pattern+=token.toString();
                        token = scanner.nextToken();
                        regex();
                }
        }

        private void concatenation() throws InvalidTokenException
        {
                basicRegex();
                token = scanner.peek();
                switch(token.getTokenType()){
                        case CHARACTER:
                        case COLON:
                        case LEFT_PAREN:
                        case CHARACTER_CLASS:
                        case BACK_REFERENCE:
                        case ASSERTIONS:
                                concatenation();
                        break;
                        default:
                }
        }

        private void basicRegex() throws InvalidTokenException
        {
                State state = atom();
                quantifiers(state);
        }
        
        private State atom() throws InvalidTokenException
        {       
                /*Start state*/
                State start = null;
                CharacterClass c= null;
                token = scanner.nextToken();
                switch(token.getTokenType()){
                        case ASSERTIONS:
                               start = StateFactory.assertion(Assertion.getType(token.getValue()));
                               pattern += token.toString();
                        break;
                        case BACK_REFERENCE:
                                if(token.getValue() > groups)
                                        throw new InvalidTokenException("Invalid token: invalid back reference.");
                                pattern+="\\";
                                pattern+=token.toString();
                        break;
                        case CHARACTER:
                        case COLON:
                                int [] vals = new int[1];
                                vals[0] = token.getValue();
                                start = StateFactory.normal(vals);
                                pattern+=token.toString();
                        break;
                        case LEFT_PAREN:
                                pattern+=token.toString();
                                group();
                        break;
                        case CHARACTER_CLASS:
                                c = token.getCharacterClass();
                                start = StateFactory.charClass(c);
                                pattern+=token.toString();
                        break;
                        default:
                                throw new InvalidTokenException("Invalid token: "+ token.toString());
                }

                /*Accept State*/
                State accept  = StateFactory.normal(null);

                /*Connect start state to accept*/
                State [] next = start.getStates();
                next[0] = accept;
                start.setAccept(accept);
                return start;
        }

        private State quantifiers(State state)
        {
                try {
                        token = scanner.peek();
                        switch(token.getTokenType()){
                                case STAR:
                                        state = StateFactory.star(state);
                                        token = scanner.nextToken();
                                        pattern+=token.toString();
                                break;
                                case QUESTION_MARK:
                                        state = StateFactory.question(state);
                                        pattern+=token.toString();
                                        token = scanner.nextToken();
                                break;
                                case PLUS:
                                        state = StateFactory.plus(state); 
                                        pattern+=token.toString();
                                        token = scanner.nextToken();
                                break;
                                case RANGE:
                                        pattern+=token.toString();
                                        token = scanner.nextToken();
                                break;
                                default:
                                break;
                        }  
                        
                }catch(Exception e){System.err.println(e.getMessage());}
                
                return state;  
        }

        private void group() throws InvalidTokenException
        {
                boolean exit = true;
                String flags = "";
                ++groups;
                /*TO DO
                 * Take care of  sub group and flags
                 */
                token = scanner.peek();
                if(token.getTokenType() == TokenType.QUESTION_MARK){
                        pattern+=token.toString();
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
                                                        flags+=token.toString();
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
                                        pattern+=token.toString();
                                        regex();
                                        token = scanner.nextToken();
                                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                                throw new InvalidTokenException("Invalid token: missing ).");
                                                /*take care of group */
                                                pattern+=token.toString();

                                }else if(token.getTokenType() == TokenType.RIGHT_PAREN){
                                        /*take care of group */
                                        pattern+=token.toString();
                                }else 
                                        throw new InvalidTokenException("Invalid token: unknow flag.");
                        }else if(token.getTokenType() == TokenType.COLON){
                                pattern+=token.toString();
                                token = scanner.nextToken();
                                regex();
                                if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: missing ).");
                                /*take care of group */
                                pattern+=token.toString();
                        }else throw new InvalidTokenException("Invalid token: unknow flag.");
                }else{
                        regex();
                        token = scanner.nextToken();
                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                throw new InvalidTokenException("Invalid token: missing ).");
                        pattern+=token.toString();
                }
        }

        public String getPattern(){return pattern;}



}