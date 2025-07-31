package parser;
import automaton.NormalState;
import automaton.StateFactory;
import exceptions.InvalidTokenException;
import lexical.*;

public class Parser{
        private Scanner scanner;
        private Token   token;
        private int     groups;
        private String  pattern; 
        private byte [] flags;

        public Parser(String pat, byte [] flags)
        {
                this.flags = flags;
                groups  = 1;
                pattern = pat;
                scanner = new Scanner(pat);
        }

        public NormalState compile() throws InvalidTokenException
        {
                NormalState start = regex(flags);
                NormalState submatch = StateFactory.subMatch(0);
                StateFactory.join(start, submatch);
                submatch = StateFactory.subMatch(0);; 
                submatch = StateFactory.join(submatch, start);
                token = scanner.nextToken(); 
                if(token.getTokenType() != TokenType.EOF)
                        throw new InvalidTokenException("Invalid token: " + token.toString());  
                return submatch; 
        }

        private NormalState regex(byte [] flags) throws InvalidTokenException
        {
                NormalState start = union(flags);
                return start;
        }

        private NormalState union(byte [] flags) throws InvalidTokenException
        {
                NormalState a = concatenation(flags);
                token = scanner.peek();
                if(token.getTokenType() == TokenType.ALTERNATION){
                        token = scanner.nextToken();
                        NormalState b = regex(flags);
                        a = StateFactory.or(a, b, flags);
                }
                return a;
        }

        private NormalState concatenation(byte [] flags) throws InvalidTokenException
        {
                NormalState a = basicRegex(flags);
                NormalState b = null;
                token = scanner.peek();
                switch(token.getTokenType()){
                        case CHARACTER:
                        case COLON:
                        case LEFT_PAREN:
                        case CHARACTER_CLASS:
                        case BACK_REFERENCE:
                        case ASSERTIONS:
                        case ESCAPE:
                              b = concatenation(flags);
                              a = StateFactory.join(a, b);
                        break;
                        default:
                }

                return a;
        }

        private NormalState basicRegex(byte [] flags) throws InvalidTokenException
        {
                NormalState state = atom(flags);
                state = quantifiers(state);
                return state;
        }
        
        private NormalState atom(byte [] flags) throws InvalidTokenException
        {       
                NormalState start      = null;
                CharacterClass c = null;
                token = scanner.nextToken();
                switch(token.getTokenType()){
                        case ASSERTIONS:
                               start = StateFactory.assertion(Assertion.getType(token.getValue()), flags);
                        break;
                        case BACK_REFERENCE:
                                if(token.getValue() > groups || token.getValue() == 0)
                                        throw new InvalidTokenException("Invalid token: invalid back reference.");
                                start = StateFactory.backReference(token.getValue(), flags);
                        break;
                        case CHARACTER:
                        case COLON:
                        case ESCAPE:
                                int [] vals = new int[1];
                                vals[0]     = token.getValue();
                                start       = StateFactory.normal(vals, flags);
                                start.setRegex(token.toString());
                        break;
                        case LEFT_PAREN:
                                start = group(flags);
                        break;
                        case CHARACTER_CLASS:
                                c = token.getCharacterClass();
                                start = StateFactory.charClass(c, flags); 
                        break;
                        default:
                                throw new InvalidTokenException("Invalid token: "+ token.toString());
                }
                return start;
        }

        private NormalState quantifiers(NormalState state)
        {
                TokenType type;
                boolean greedy = true;
                Range range = null;
                try{
                        token = scanner.peek();
                        type  = token.getTokenType();
                        range = token.getRange();
                        switch(token.getTokenType()){
                                case STAR:
                                case QUESTION_MARK:
                                case PLUS:
                                case RANGE:  
                                        token = scanner.nextToken();
                                        token = scanner.peek();
                                        if(token.getTokenType() == TokenType.QUESTION_MARK){
                                                greedy = false;
                                                token  = scanner.nextToken();
                                        }else{
                                                byte [] flags = state.getFlags();
                                                if(flags != null){
                                                        for(byte flag: flags){
                                                                /**
                                                                 * If the state has the ungreedy flag set
                                                                 * then quantifier should be ungreedy as well.
                                                                 */
                                                                if(flag == 'U')
                                                                        greedy = false;
                                                        }
                                                }
                                        }
                                        state = StateFactory.quantifier(state, type, range, greedy);
                                break;
                                default:
                                break;
                        }  
                        
                }catch(Exception e){System.err.println(e.getMessage());}
                return state;  
        }

        private NormalState group(byte [] flags) throws InvalidTokenException
        {
                NormalState start  = null;
                boolean exit = true;
                String f = "";
                NormalState submatch = null;
            
                /*TO DO
                 * Take care of  sub group and flags
                 */
                token = scanner.peek();
                if(token.getTokenType() == TokenType.QUESTION_MARK){
                        token = scanner.nextToken();
                        token = scanner.peek();
                        if(token.getTokenType() == TokenType.CHARACTER){
                                /*Get flags*/
                                while(exit){
                                        token = scanner.nextToken();
                                        switch(token.getValue()){
                                                case 'i':
                                                case 's':
                                                case 'U':
                                                        f+=token.toString();
                                                break;
                                                default:
                                                        exit = false;
                                        }
                                }
                                if("isU".indexOf(token.getValue()) == -1
                                && token.getTokenType() != TokenType.COLON 
                                && token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: unknown flag.");
                                
                                if(token.getTokenType() == TokenType.COLON){
                                        /*Turn off non capturing*/
                                        start = regex(f.getBytes());
                                        start.setRegex("(?" + f + ":" + start.getRegex() + ")");
                                        token = scanner.nextToken();
                                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                                throw new InvalidTokenException("Invalid token: missing ).");
                                }else if(token.getTokenType() == TokenType.RIGHT_PAREN){
                                        start = regex(f.getBytes());
                                        start.setRegex("(?" + f + ")" + start.getRegex());
                                }else 
                                        throw new InvalidTokenException("Invalid token: unknown flag.");
                        }else if(token.getTokenType() == TokenType.COLON){
                                /*Turn off submatching*/
                                token = scanner.nextToken();
                                start  = regex(flags);
                                start.setRegex("(" + start.getRegex() + ")");
                                token = scanner.nextToken();
                                if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: missing ).");
                                
                        }else throw new InvalidTokenException("Invalid token: unknown flag.");
                }else{
                        int pos =  groups++;
                        /*Creating submatch start*/
                        
                        submatch = StateFactory.subMatch(pos);
                        submatch.setRegex("(");
                        start = regex(flags);
                        token = scanner.nextToken();
                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                throw new InvalidTokenException("Invalid token: missing ).");
                                
                        start = StateFactory.join(submatch, start);
                        /*Create submatch ending*/
                        submatch = StateFactory.subMatch(pos);
                        submatch.setRegex(")");
                        start = StateFactory.join(start, submatch);
                        
                }
                return start;
        }

        public String getPattern(){return pattern;}
        public int getGroups(){return groups;}
}