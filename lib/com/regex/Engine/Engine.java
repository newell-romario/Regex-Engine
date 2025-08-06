package Engine;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import automaton.BaseState;
import parser.Parser;

public abstract class Engine{
        private Parser    parser; 
        private BaseState start;
        private BaseState accept;
        private Submatch  submatches;
        private ArrayList<Match> matches;
        private byte [] flags;


        public Engine(String pat, byte [] f)
        {
                try{
                        flags  = f;
                        parser = new Parser(pat, flags);
                        start  = parser.compile(); 
                        accept = start.getAccept();
                        submatches = new Submatch(parser.getGroups());
                } catch (Exception e) {
                       System.err.println(e.getMessage());
                }
        }

        

        public Set<BaseState> eClosure(BaseState state)
        {
                Deque<BaseState> stack = new ArrayDeque<>();
                Hashtable<BaseState, BaseState> onStack = new Hashtable<>();
                BaseState [] next = null;
                stack.push(state);
                onStack.put(state, state); 
                boolean exit;
                while(!stack.isEmpty()){
                        for(;;){
                                state = stack.peek();
                                next  = state.move();
                                exit  = true;                                
                                for(int i = 0; i < next.length; ++i){
                                        if(next[i] != null){
                                                if(!onStack.containsKey(next[i])){
                                                        stack.push(next[i]);
                                                        onStack.put(next[i], state);
                                                        exit = false;
                                                        break;
                                                }
                                        }
                                }  
                                if(exit)
                                        break;
                        }
                        state = stack.pop();
                        onStack.put(state, state);
                }
             
                return new HashSet<>(onStack.keySet());
        }

        protected class Configuration{
                int pos; 
                BaseState state;
                Submatch  matches;
                Configuration(int pos, BaseState state, Submatch matches)
                {
                        this.pos     = pos; 
                        this.state   = state;
                        this.matches = matches; 
                }
        }


        public abstract boolean match(String text);
        protected abstract int match(String text, int pos);
        public abstract ArrayList<Match> allMatches(String text);
        public BaseState getStart(){return start;}
        public BaseState getAccept(){return accept;}
        protected Submatch  getSubmatches(){return submatches;}
        protected void setSubmatch(Submatch match){submatches = match;}
        protected byte []   getFlags(){return flags;}
        protected void setMatch(int group,  int index, int pos){submatches.setMatch(group, index, pos);}
        protected void setMatch(ArrayList<Match> m){matches = m;}
        public ArrayList<Match> getMatches(){return matches;}
}
