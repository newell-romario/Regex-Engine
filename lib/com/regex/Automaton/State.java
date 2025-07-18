public abstract class State{
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
        private byte   flags;

        /* We can set anchors which ensure the 
         * asserts properties about the match. 
         *^ - match must start at the beginning of line.
         *$ - match must end at the end of line.   
         */
        private byte  anchors;

        /*Stores the  accept state*/
        private State   accept;

        /*Stores the old accept state*/
        private State   oldAccept;

        public State()
        {
                key     =   System.currentTimeMillis();
                regex   = "";
                flags   = 0; 
                anchors = 0;
                accept  = oldAccept = null;
        }
        
        public void setFlags(byte flags){this.flags = flags;}
        public void setAnchors(byte anchors){this.anchors = anchors;}
        public void setAccept(State accept){this.accept = accept;}
        public byte getFlags(){return flags;}
        public byte getAnchors(){return anchors;}
        public long getKey(){return key;}
        public String getRegex(){return regex;}
        public State getAccpet(){return accept;}
        public State getOldAcceppt(){return oldAccept;}
}
