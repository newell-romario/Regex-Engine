package parser;
import java.util.ArrayList;

import automaton.BackReferenceState;
import automaton.BaseState;
import automaton.StateFactory;
import automaton.StateType;
import exceptions.InvalidTokenException;
import lexical.*;

public class Parser{
        private Scanner scanner;
        private Token   token;
        private int     groups;
        private String  pattern; 
        private byte [] flags;
        private ArrayList<BackReferenceState> references = new ArrayList<>();
        
        public Parser(String pat, byte [] flags)
        {
                this.flags = flags;
                groups  = 1;
                pattern = pat;
                scanner = new Scanner(pat);
                
        }

        public BaseState compile() throws InvalidTokenException
        {
                BaseState start = regex(flags);
                BaseState submatch = StateFactory.subMatch(0, StateType.SUBMATCH_END, 1);
                start = StateFactory.join(start, submatch);
                submatch = StateFactory.subMatch(0, StateType.SUBMATCH_START, 0); 
                submatch = StateFactory.join(submatch, start);
                token = scanner.nextToken(); 
                if(token.getTokenType() != TokenType.EOF)
                        throw new InvalidTokenException("Invalid token: " + token.toString());  
                return submatch; 
        }

        private BaseState regex(byte [] flags) throws InvalidTokenException
        {
                BaseState start = union(flags);
                return start;
        }

        private BaseState union(byte [] flags) throws InvalidTokenException
        {
                BaseState a = concatenation(flags);
                token = scanner.peek();
                if(token.getTokenType() == TokenType.ALTERNATION){
                        token = scanner.nextToken();
                        BaseState b = regex(flags);
                        a = StateFactory.or(a, b);
                }
                return a;
        }

        private BaseState concatenation(byte [] flags) throws InvalidTokenException
        {
                BaseState a = basicRegex(flags);
                BaseState b = null;
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

        private BaseState basicRegex(byte [] flags) throws InvalidTokenException
        {
                BaseState state = atom(flags);
                state = quantifiers(state);
                return state;
        }
        
        private BaseState atom(byte [] flags) throws InvalidTokenException
        {       
                BaseState start  = null;
                CharacterClass c = null;
                token = scanner.nextToken();
                switch(token.getTokenType()){
                        case ASSERTIONS:
                               start = StateFactory.assertion(Assertion.getType(token.getValue()));
                        break;
                        case BACK_REFERENCE:
                                if(token.getValue() > groups || token.getValue() == 0)
                                        throw new InvalidTokenException("Invalid token: invalid back reference.");
                                start = references.get(token.getValue()-1);
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

        private BaseState quantifiers(BaseState state)
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

        private BaseState group(byte [] flags) throws InvalidTokenException
        {
                BaseState start  = null;
                boolean exit = true;
                String f = "";
                BaseState submatch = null;

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
                                if("isU".indexOf(token.getValue()) == -1 && f.length() > 3 
                                && token.getTokenType() != TokenType.COLON 
                                && token.getTokenType() != TokenType.RIGHT_PAREN)
                                        throw new InvalidTokenException("Invalid token: unknown flag.");
                                byte [] g = new byte[flags.length];
                                g[0] = (f.contains("i") == true? 1: flags[0]);
                                g[1] = (f.contains("s") == true? 1: flags[1]);
                                g[2] = (f.contains("U") == true? 1: flags[2]);
                                if(token.getTokenType() == TokenType.COLON){
                                        /*Turn off sub matching*/
                                        start = regex(g);
                                        start.setRegex("(?" + f + ":" + start.getRegex() + ")");
                                        token = scanner.nextToken();
                                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                                throw new InvalidTokenException("Invalid token: missing ).");
                                }else if(token.getTokenType() == TokenType.RIGHT_PAREN){
                                        start = regex(g);
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
                        references.add(StateFactory.backReference(null, null, pos));
                        submatch = StateFactory.subMatch(pos, StateType.SUBMATCH_START, 0);
                        submatch.setRegex("(");
                        start = regex(flags);
                        token = scanner.nextToken();
                        if(token.getTokenType() != TokenType.RIGHT_PAREN)
                                throw new InvalidTokenException("Invalid token: missing ).");
                                
                        start = StateFactory.join(submatch, start);
                        /*Create submatch ending*/
                        submatch = StateFactory.subMatch(pos, StateType.SUBMATCH_END, 1);
                        submatch.setRegex(")");
                        submatch.setStateType(StateType.SUBMATCH_END);
                        start = StateFactory.join(start, submatch);
                        BackReferenceState ref = references.get(pos-1);
                        ref.setStart(start);
                        ref.setEnd(start.getAccept());
                }
                return start;
        }

        public String getPattern(){return pattern;}
        public int getGroups(){return groups;}
}