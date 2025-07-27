package parser;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import automaton.State;
import automaton.StateCharClass;
import automaton.StateType;
import lexical.Assertion;
import lexical.CharacterClass;
import lexical.Range;

public class StateFactory{
        public static State charClass(CharacterClass c)
        {
                State state     = new StateCharClass(c);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAccept(accept);
                return state;
        }

        public static State assertion(Assertion a)
        {
                State state     = new State(StateType.ANCHOR, null);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAssertion(a);
                state.setAccept(accept);
                return state;
        }

        public static State normal(int [] vals)
        {
                State state     = new State(StateType.NORMAL, vals);
                State accept    = new State(StateType.NORMAL, null);
                State [] next   = state.getStates();
                next[0] = accept;
                state.setAccept(accept);
                return state;
        }

        public static State star(State state)
        {
                State start   = normal(null);
                State accept  = normal(null);
                start.setAccept(accept);
                State [] next = start.getStates();
                next[0] = state;
                next[1] = accept;
                next = state.getAccept().getStates();
                next[0] = state;
                next[1] = accept;
                return start;
        }

        public static State plus(State state)
        {
                State accept  = new State(StateType.NORMAL, null);
                State [] next = state.getAccept().getStates();
                next[0] = state;
                next[1] = accept;
                state.setAccept(accept);
                return state;
        }


        public static State question(State state)
        {
                State start = new State(StateType.NORMAL, null);
                State [] next = start.getStates();
                next[0] = state;
                next[1] = state.getAccept();
                start.setAccept(state.getAccept());
                return start;
        }

        public static State or(State a, State b)
        {
                State start  = new State(StateType.NORMAL, null);
                State accept = new State(StateType.NORMAL, null); 
                start.setAccept(accept);
                State [] next = start.getStates();
                next[0] = a; 
                next[1] = b;
                a.getAccept().getStates()[0] = accept;
                b.getAccept().getStates()[0] = accept;
                return start;
        }


        public static State range(State state, Range range)
        {
                State [] min = null;
                State [] max = null;
                State start  = null;
                if(range.getMin() == range.getMax()){
                        min   = duplicate(state, (int)range.getMin()-1);
                        start = join(state, join(min));
                }else if(range.getMin() != Double.NEGATIVE_INFINITY){
                        min   = duplicate(state, (int)range.getMin());
                        start = join(min);
                        if(range.getMax() != Double.POSITIVE_INFINITY){
                                max   =  duplicate(state, (int)(range.getMax()-range.getMin()));
                                start = join(start, minToMax(max));
                        }else
                                start = join(start, plus(state));
                }else{
                        max = duplicate(state, (int)range.getMax());
                        start = minToMax(max);
                }
                return start;
        }

        private static State minToMax(State [] states)
        {
                State state   = join(states);
                State start   = normal(null); 
                State accept  = normal(null);
                start.setAccept(accept);
                State [] next = start.getStates();
                next[0] = state;
                next[1] = accept;
                for(State a : states){
                        accept  = a.getAccept();
                        next    = accept.getStates();
                        next[1] = start.getAccept();
                }
                return start;
        }
        
     
        public static State join(State a, State b)
        {
                State [] next = a.getAccept().getStates();
                next[0] = b;
                a.setAccept(b.getAccept());
                return a;
        }

        public static State join(State [] b)
        {
                State start = b[0];
                int num = 1;
                do{
                        start = join(start, b[num]);
                        ++num;
                }while(num < b.length);
                return start;
        }


        private static State [] duplicate(State state, int num)
        {
                State [] copies = new State[num];
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
}
