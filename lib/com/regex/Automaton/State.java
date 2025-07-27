package automaton;
import java.util.Arrays;
import lexical.Assertion;
import lexical.Posix;

public class State{
        /*Unique key representing each state */
        private final long  key;

        /*Key of the state that this copy came from*/
        private long cid;
        
        /*String representation of current regex*/
        private String regex;

        /*We can set flags for regex such as:
         * i - case-insensitive
         * m - multi-line
         * s - match \n
         * U - ungreedy
        */
        private byte [] flags;

        /*Type of anchor*/
        private Assertion anchor;

        /*Stores the  accept state*/
        private State accept;


        /*Next states*/
        private State []  next;

        
        /*State type*/
        private StateType type;

        /*Characters to transition on*/
        int [] vals;

        /*Dead State*/
        private static final State [] dead = new State[2];

        /*Submatch number */
        private int submatch;

        public State(StateType t, int [] v)
        {
                vals    = v;
                type    = t;
                key     = System.nanoTime();
                cid     = key;
                regex   = "";
                flags   = new byte[4]; 
                accept  = null;
                next    = new State[2];
        }
        
        public void setStateType(StateType type){this.type = type;}
        public void setAccept(State accept){this.accept = accept;}
        public void setNext(State []next){this.next = next;}
        public void setAssertion(Assertion anchor){this.anchor = anchor;}
        public void setVals(int [] vals){this.vals = vals;}
        public void setRegex(String r){regex = r;}
        public void setSubMatch(int n){submatch = n;}
        public int  getSubMatch(){return submatch;}
        public void setCid(long id){cid = id;}
        public byte [] getFlags(){return flags;}
        public long getKey(){return key;}
        public String getRegex(){return regex;}
        public State getAccept(){return accept;}
        public State [] getStates(){return next;}
        public StateType getStateType(){return type;}
        public Assertion getAssertion(){return anchor;}
        public int [] getVals(){return vals;}
        public long getCid(){return cid;}
        protected State [] getDeadState(){return dead;}


        public void setFlags(byte [] f)
        {
                if(flags == null)
                        return;
                
                for(int i = 0; i < f.length; ++i)
                        flags[i] = f[i];
        }

        
        public State [] move(int val)
        {
                if(vals != null){                               
                        if(vals[0] == val)
                                return next;
                }
         
                return dead;
        }

        public State [] assertion(String pattern, int pos)
        {
                switch(anchor){
                        case START_OF_LINE:
                                if(pos == 0 || pattern.charAt(pos-1) == '\n' || pattern.charAt(pos-1) == '\r')
                                        return next;  
                                return dead;
                        case END_OF_LINE:
                                if(pos == pattern.length())
                                        return next;
                                if(pattern.charAt(pos) == 13 || pattern.charAt(pos) == 10)
                                        return next;
                                return dead;
                        case WORD_BOUNDARY:
                                if(pos-1 == 0 && Posix.asciiIsWord(pattern.charAt(pos)))
                                        return next;
                                if(pos+1 == pattern.length() && Posix.asciiIsWord(pattern.charAt(pos)))
                                        return next;
                                if(!Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(pos+1 < pattern.length() &&
                                         Posix.asciiIsWord(pattern.charAt(pos+1)))
                                                return next;
                                if(Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(pos+1 < pattern.length() &&
                                                !Posix.asciiIsWord(pattern.charAt(pos+1)))
                                                return next;
                                return dead;
                        case NON_WORD_BOUNDARY:
                                if(!(pos-1 == 0 && Posix.asciiIsWord(pattern.charAt(pos))))
                                        return next;
                                if(!(pos+1 == pattern.length() && Posix.asciiIsWord(pattern.charAt(pos))))
                                        return next;
                                if(Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(!(pos+1 < pattern.length() &&
                                         Posix.asciiIsWord(pattern.charAt(pos+1))))
                                                return next;
                                if(!Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(!(pos+1 < pattern.length() &&
                                                !Posix.asciiIsWord(pattern.charAt(pos+1))))
                                                return next;
                        return dead;
                        case START_OF_FILE:
                                if(pos == 0)
                                        return next;
                                return dead;
                        case END_OF_FILE:
                                if(pos == pattern.length())
                                        return next;
                                return dead;
                        default:
                        break;
                }
                return null;
        }

       
        public State copy()
        {
                State s = new State(type, vals == null?null: Arrays.copyOf(vals, vals.length));
                s.setRegex(regex);
                s.setFlags(Arrays.copyOf(flags, flags.length));
                s.setAssertion(anchor);
                s.setAccept(accept);
                s.setSubMatch(submatch);
                s.setCid(key);
                State [] n = s.getStates();
                n[0] = next[0];
                n[1] = next[1];
                return s;
        }

        @Override
        public int hashCode()
        {
                int result = Long.hashCode(key);
                result = 31*result + type.hashCode();
                result = 31*result + next.hashCode();
                result = 31*result + flags.hashCode();
                return result;
        }

        @Override
        public boolean equals(Object o)
        {
                if(o == this)
                        return true;
                if(!(o instanceof State))
                        return false;
                State k = (State) o;
                return k.getKey() == key;
        }
}
