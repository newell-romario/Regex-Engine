package Engine;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import automaton.State;
import automaton.StateType;
import parser.Parser;

public class Engine{
        private Parser parser; 
        private State  start;
        private State  accept;
        private Submatch submatches;
        private byte [] flags;

        private class Submatch{
                private int [][] matches;
                private int [] index;
                private int groups;

                public Submatch()
                {
                        groups  = parser.getGroups();
                        matches = new int[groups][2];
                        index   = new int[groups];
                }
                
                public void setMatch(int group, int pos)
                {
                        matches[group][index[group]] = pos;
                        index[group] = (index[group]+1)%2; 
                }

                public Submatch copy()
                {
                        Submatch match = new Submatch();
                        int [][] m = match.getMatches();
                        for(int i = 0; i < m.length; ++i){
                                for(int j = 0; j < m[i].length; ++j)
                                        m[i][j] = matches[i][j];
                        }
                        return match;
                }

                public int [][] getMatches(){return matches;}                
        }

        public Engine(String pat, byte [] f)
        {
                try{
                        flags  = f;
                        parser = new Parser(pat, flags);
                        start  = parser.compile(); 
                        accept = start.getAccept();
                        submatches = new Submatch();
                } catch (Exception e) {
                       System.err.println(e.getMessage());
                }
        }

        public boolean match(String text)
        {
                int pos = 0; 
                Set<State> states = match(text, pos);
                return states.contains(accept);
        }

        
        private Set<State> match(String text, int pos)
        {
                Set<State> cur  = epsilonClosure(start);
                Set<State> post = new HashSet<>();
                State [] next   = null;
                boolean inc     = false;

                while(pos < text.length()){
                        inc = true;
                        for(State state : cur){
                                switch(state.getStateType()){
                                        case NORMAL:
                                                next = state.move(text.codePointAt(pos));
                                        break;
                                        case CHARACTER_CLASS:
                                                next = state.move(text.codePointAt(pos));
                                        break;
                                        case SUBMATCH:
                                                submatches.setMatch(state.getSubMatch(), pos);
                                                next = state.move();
                                                inc = false;
                                        break;
                                        case ANCHOR:
                                                next = state.assertion(text, pos);
                                                inc = false;
                                        default:
                                                break;
                                }
                                
                                for(int i = 0; i < next.length; ++i){
                                        if(next[i] != null)
                                                post.addAll(epsilonClosure(next[i]));
                                }
                        }  
                         
                        if(inc)
                                ++pos;

                        cur  = post;
                        post = new HashSet<>();
                        
                }

                for(State state: cur){
                        switch(state.getStateType()){
                                case SUBMATCH:
                                        submatches.setMatch(state.getSubMatch(), pos);
                                        next = state.move();
                                break;
                                case ANCHOR:
                                        next = state.assertion(text, pos);
                                break;
                                default:
                        }
                        if(next != null)
                                for(int i = 0; i < next.length; ++i){
                                        if(next[i] != null)
                                                post.addAll(epsilonClosure(next[i]));
                        }
                }
                cur.addAll(post);
                return cur;
        }

        public Set<State> epsilonClosure(State state)
        {
                final int PROCESSING = 0; 
                final int DONE = 1;
                Deque<State> stack = new ArrayDeque<>();
                Hashtable<State, Integer> onStack = new Hashtable<>();
                State [] next = null;
                stack.push(state);
                onStack.put(state, PROCESSING); 
                boolean exit;
                while(!stack.isEmpty()){
                        for(;;){
                                state = stack.peek();
                                next  = state.move();
                                exit  = true;
                                if(state.getStateType() == StateType.NORMAL)
                                        for(int i = 0; i < next.length; ++i){
                                                if(next[i] != null){
                                                        if(!onStack.containsKey(next[i])){
                                                                stack.push(next[i]);
                                                                onStack.put(next[i], PROCESSING);
                                                                exit = false;
                                                                break;
                                                        }
                                                }
                                        }
                                
                                if(exit)
                                        break;
                        }
                        state = stack.pop();
                        onStack.put(state, DONE);
                }
             
                return new HashSet<>(onStack.keySet());
        }

        public State getStart(){return start;}

}
