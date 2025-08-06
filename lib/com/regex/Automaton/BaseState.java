package automaton;

public class BaseState {
        /*Unique key representing each state */
        private final long  key;

        /*Key of the state that this copy came from*/
        private long cid;
        
        /*Next states*/
        private BaseState []  next;
        
        /*State type*/
        private StateType type;

        /*Characters to transition on*/
        int [] vals;

        /*Accept State */
        private BaseState accept;

        /*Regex representation of NFA that starts here*/
        private String regex;

        /*Dead State*/
        private static final BaseState [] dead = new NormalState[2];

        /*We can set flags for regex such as:
         * i - case-insensitive
         * s - match \n
         * U - ungreedy
        */
        private byte [] flags;
        
        public BaseState(StateType t)
        {
                type    = t;
                key     = System.nanoTime();
                cid     = key;
                next    = new BaseState[2];
                regex   = "";
        }
        
        public void setStateType(StateType type){this.type = type;}
        public void setNext(NormalState []next){this.next = next;}
        public void setVals(int [] vals){this.vals = vals;}
        public void setCid(long id){cid = id;}
        public long getKey(){return key;}
        public BaseState [] getStates(){return next;}
        public StateType getStateType(){return type;}
        public int [] getVals(){return vals;}
        public long getCid(){return cid;}
        public BaseState [] getDeadState(){return dead;}
        public void setAccept(BaseState state){accept = state;}
        public BaseState getAccept(){return accept;}
        public String getRegex(){return regex;}
        public void setRegex(String r){regex = r;}
        public byte [] getFlags(){return flags;}
        public void setFlags(byte [] f)
        {
                if(f == null )
                        return;
                if(flags == null)
                        flags = f;
                String fString = "isU";
                for(int i = 0; i < f.length; ++i){
                        if(fString.indexOf(f[i]) != -1)
                                flags[i] = f[i];
                }          
        }
        @Override
        public int hashCode()
        {
                int result = Long.hashCode(key);
                result = 31*result + type.hashCode();
                result = 31*result + next.hashCode();
                return result;
        }

        @Override
        public boolean equals(Object o)
        {
                if(o == this)
                        return true;
                if(!(o instanceof NormalState))
                        return false;
                NormalState k = (NormalState) o;
                return k.getKey() == key;
        }

        public BaseState copy()
        {
                BaseState s = new BaseState(type);
                s.setRegex(regex);
                s.setAccept(accept);
                s.setCid(key);
                s.setFlags(flags);
                BaseState [] snext = s.getStates();
                snext[0] = next[0];
                snext[1] = next[1];
                return s;
        }

        protected void setBase(BaseState base)
        {
                type   = base.getStateType();
                regex  = base.getRegex();
                accept = base.getAccept();
                cid    = base.getCid();
                flags  = base.getFlags();
                BaseState [] snext = base.getStates();
                next[0] = snext[0];
                next[1] = snext[1];
        }

        public BaseState [] move(){return next;}
        public BaseState [] move(int val){return next;}
        public BaseState [] move(String text, int pos){return next;}
        public String toString(){return regex;}
}
