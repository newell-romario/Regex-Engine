package automaton;

public class BackReferenceState extends SubMatchState{
        private BaseState start;
        private BaseState end;
        public BackReferenceState (BaseState start, BaseState end, int ref)
        {
                super(ref, 0);
                start = end = null;
        }

       
        public BaseState getStart(){return this.start;}
        public void setStart(BaseState start){this.start = start;}
        public BaseState getEnd(){return this.end;}
        public void setEnd(BaseState end){this.end = end;}
        
        public BackReferenceState copy()
        {
                BackReferenceState ref = new BackReferenceState(start, end, super.getGroup());
                ref.setBase(this);
                return ref;
        }
}
