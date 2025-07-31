package automaton;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import lexical.Assertion;
import lexical.CharacterClass;
import lexical.Range;
import lexical.TokenType;

public class StateFactory{
        private static final long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);
        private static final long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);
        
        public static State charClass(CharacterClass c, byte [] flags)
        {
                if(flags != null){
                        if(c.isNegated()){
                                c.addMembers('\n');
                                c.addMembers('\r');
                        }
                }
                State state     = new CharClassState(c);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAccept(accept);
                state.setFlags(flags);
                state.setRegex(c.toString());
                return state;
        }

        public static State assertion(Assertion a, byte [] flags)
        {
                State state     = new State(StateType.ANCHOR, null);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAssertion(a);
                state.setAccept(accept);
                state.setFlags(flags);
                state.setRegex(Assertion.stringRepresentation(a));
                return state;
        }

        public static State normal(int [] vals, byte [] flags)
        {
                State state     = new State(StateType.NORMAL, vals);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAccept(accept);
                state.setFlags(flags);
                String rep = ""; 
                if(vals != null)
                        for(int val: vals)
                                rep += Character.toString(val);
                state.setRegex(rep);
                return state;
        }

        public static State star(State state, boolean greedy)
        {
                State start   = new State(StateType.NORMAL, null);
                State accept  = new State(StateType.NORMAL, null);
                start.setAccept(accept);
                State [] next = start.getStates();
                next[0] = state;
                next[1] = accept;
                if(!greedy){
                        next[0] = accept;
                        next[1] = state;
                }
                next = state.getAccept().getStates();
                next[0] = state;
                next[1] = accept;
                if(!greedy){
                        next[0] = accept;
                        next[1] = state;
                }
                String rep = state.getRegex() + "*" + (greedy == false? "?": "");
                start.setRegex(rep);
                return start;
        }

        public static State plus(State state, boolean greedy)
        {
                State accept  = new State(StateType.NORMAL, null);
                State [] next = state.getAccept().getStates();
                state.setAccept(accept);
                next[0] = state;
                next[1] = accept;
                if(!greedy){
                        next[0] = accept;
                        next[1] = state;
                }
              
                String rep = state.getRegex() +"+"+ (greedy == false? "?": "");
                state.setRegex(rep);
                return state;
        }


        public static State question(State state, boolean greedy)
        {
                State start = new State(StateType.NORMAL, null);
                State [] next = start.getStates();
                next[0] = state;
                next[1] = state.getAccept();
                if(!greedy){
                        next[0] = state.getAccept();
                        next[1] = state;
                }
                start.setAccept(state.getAccept());
                String rep = state.getRegex() + "?" + (greedy == false? "?": "");
                start.setRegex(rep);
                return start;
        }

        public static State or(State a, State b, byte [] flags)
        {
                State start  = new State(StateType.NORMAL, null);
                State accept = new State(StateType.NORMAL, null); 
                start.setAccept(accept);
                State [] next = start.getStates();
                next[0] = a; 
                next[1] = b;
                a.getAccept().getStates()[0] = accept;
                b.getAccept().getStates()[0] = accept;
                start.setRegex(a.getRegex() + "|" + b.getRegex());
                return start;
        }


        public static State range(State state, Range range, boolean greedy)
        {
                State [] min = null;
                State [] max = null;
                String regex = state.getRegex();
                State start  = null;
                State copy = duplicate(state);
                if(range.getMin() == range.getMax()){
                        min   = duplicate(state, range.getMin()-1);
                        start = join(state, join(min));
                }else if(range.getMin() != NEGATIVE_INFINITY && 
                        range.getMax()  != POSITIVE_INFINITY){
                        max = duplicate(state, (range.getMax()-range.getMin()));
                        if(range.getMin() != 1){
                                min   = duplicate(state, range.getMin()-1);
                                start = join(state, join(min));
                        }else start = state;
                        
                        start = join(start, minToMax(max, greedy));
                }else if(range.getMin()!= NEGATIVE_INFINITY && 
                        range.getMax() == POSITIVE_INFINITY){
                        if(range.getMin() != 1){
                                min   = duplicate(state, range.getMin()-1);
                                start = join(state, join(min));
                        }

                        State star = star(copy, greedy);
                        if(!greedy)
                                star.setRegex(star.getRegex().substring(0, star.getRegex().length()-2));
                        else
                                star.setRegex(star.getRegex().substring(0, star.getRegex().length()-1));
                        start = join(start, star);
                }else{
                        max = duplicate(state, range.getMax());
                        max[0] = state;
                        start = minToMax(max, greedy);
                }

                start.setRegex(regex + range + (greedy == false? "?": ""));
                return start;
        }

        private static State minToMax(State [] states, boolean greedy)
        {
                State [] accepts = new State[states.length-1];
                for(int i = 0; i < accepts.length; ++i)
                        accepts[i] = states[i].getAccept();
        
                State state   = join(states);
                State start   = new State(StateType.NORMAL, null); 
                State accept  = state.getAccept();
                start.setAccept(accept);
                start.setRegex(state.getRegex());
                State [] next = start.getStates();
                next[0] = state;
                next[1] = accept;
                if(!greedy){
                        next[0] = accept;
                        next[1] = state;
                }
                
                for(State a : accepts){
                        next    = a.getStates();
                        next[1] = accept;
                        if(!greedy){
                                next[1] = next[0]; 
                                next[0] = accept;
                        }
                }
                return start;
        }
        
     
        public static State join(State a, State b)
        {
                State [] next = a.getAccept().getStates();
                next[0] = b;
                a.setAccept(b.getAccept());
                a.setRegex(a.getRegex()+b.getRegex());
                return a;
        }

        public static State join(State [] b)
        {
                for(int num = b.length-2; num >= 0; --num){
                        b[num] = join(b[num], b[num+1]);
                }
                return b[0];
        }


        private static State [] duplicate(State state, long num)
        {
                State [] copies = new State[(int)num];
                for(int i = 0; i < num; ++i)
                        copies[i] = duplicate(state);
                
                return copies;
        }

        public static State duplicate(State state)
        {
                final byte PROCESSING  = 0;
                final byte FINISHED    = 1;
                Deque<State> stack  = new ArrayDeque<>();
                Hashtable<State, State>copies = new Hashtable<>();
                Hashtable<State, Byte> onStack = new Hashtable<>();
                State [] children = null;
                State [] next = null;
                State copy = state.copy();
                stack.push(state);
                copies.put(state, copy);
                onStack.put(state, PROCESSING);
                boolean exit = true;
                while(!stack.isEmpty()){
                        for(;;){
                                state = stack.peek();
                                copy  = copies.get(state);
                                next  = state.getStates();
                                children = copy.getStates();
                                exit = true;
                                for(int i = 0; i < next.length; ++i){
                                        if(next[i] != null && !onStack.containsKey(next[i])){
                                                children[i] = next[i].copy();
                                                onStack.put(next[i], PROCESSING);
                                                copies.put(next[i], children[i]);
                                                stack.push(next[i]);
                                                exit = false;
                                                break;
                                        }else if(next[i] != null && onStack.containsKey(next[i]))
                                                children[i] = copies.get(next[i]);
                                }
                                if(exit)
                                        break;
                        }
                        state = stack.pop();
                        copy  = copies.get(state);
                        if(state.getAccept() != null)
                                copy.setAccept(copies.get(state.getAccept()));
                        onStack.put(state, FINISHED);
                }
                return copy;
        }

        public static State quantifier(State state, TokenType type, Range range, boolean greedy) throws Exception
        {
                switch(type){
                        case STAR:
                                state = star(state, greedy);
                        break;
                        case QUESTION_MARK:
                                state = question(state, greedy);
                        break;
                        case PLUS:
                                state = plus(state, greedy);
                        break; 
                        case RANGE:
                                if(range.getMin() == 1 && range.getMax() == 1){
                                     state.setRegex(state.getRegex()+range);   
                                }else if(range.getMin() == 0 && range.getMax() == 0){
                                        State start = new State(StateType.NORMAL, null);
                                        start.getStates()[0] = state.getAccept();
                                        start.setAccept(state.getAccept());
                                        start.setRegex(state.getRegex()+range+ (greedy == false? "?": ""));
                                        state = start;  
                                }else if(range.getMin() == 0 && range.getMax() == 1){
                                        String  regex = state.getRegex();
                                        state = question(state, greedy);
                                        state.setRegex(regex+range);
                                }else{
                                        if(range.getMin() == 0)
                                                range.setMin(NEGATIVE_INFINITY);    
                                        state = range(state, range, greedy); 
                                }
                        break; 
                        default:
                                throw new Exception("Invalid quantifier");
                }

                return state; 
        }

        public static State subMatch(int group)
        {
                State start  = new State(StateType.SUBMATCH, null);
                State accept = new State(StateType.NORMAL,null);
                start.setAccept(accept);
                start.getStates()[0] = accept;
                start.setSubMatch(group);
                return start;
        }

        public static State backReference(int group, byte [] flags)
        {
                State start  = new State(StateType.BACK_REFERENCE, null);
                State accept = new State(StateType.NORMAL,null);
                start.setAccept(accept);
                start.getStates()[0] = accept;
                start.setSubMatch(group);
                start.setRegex("\\"+group);
                return start;
        }
}
