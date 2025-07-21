public class State{
        /*Unique key representing each state */
        private long key;
        
        /*String representation of current regex*/
        private String regex;

        /*We can set flags for regex such as:
         * i - case-insensitive
         * m - multi-line
         * s - match \n
         * U - ungreedy
        */
        private byte flags;


        /*Stores the  accept state*/
        private State accept;

        /*Stores the state type*/
        private StateType stateType;
        

        public State(StateType type)
        {
                type    = stateType;
                key     =   System.currentTimeMillis();
                regex   = "";
                flags   = 0; 
                accept  = null;
        }
        
        public void setFlags(byte flags){this.flags = flags;}
        public void setAccept(State accept){this.accept = accept;}
        public byte getFlags(){return flags;}
        public long getKey(){return key;}
        public String getRegex(){return regex;}
        public State getAccpet(){return accept;}
        public StateType getStateType(){return stateType;}
}
