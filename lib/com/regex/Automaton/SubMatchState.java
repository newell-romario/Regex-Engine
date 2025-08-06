package automaton;

public class SubMatchState extends BaseState{ 
        private int group;
        private int index;

        public SubMatchState(int id, int index)
        {
                super(StateType.BASE);
                group = id;
                this.index = index;
        }

        
        
        public BaseState [] move(){return super.getDeadState();}
        public int getGroup(){return group;}
        public int getIndex(){return index;}
        public void setGroup(int id){group = id;}

        @Override
        public SubMatchState copy()
        {
                SubMatchState sub = new SubMatchState(group, index);
                sub.setBase(this);
                return sub;
        }
}
