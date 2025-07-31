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

        /*Dead State*/
        private static final BaseState [] dead = new NormalState[2];

        public BaseState(StateType t)
        {
                type    = t;
                key     = System.nanoTime();
                cid     = key;
                next    = new BaseState[2];
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



        public BaseState [] move(){return next;}
        
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
}
