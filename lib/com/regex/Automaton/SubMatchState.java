package automaton;

public class SubMatchState extends BaseState{ 
        private int group;
        private String regex;

        public SubMatchState(int id)
        {
                super(StateType.SUBMATCH);
                group = id;
        }
        
        public int getGroup(){return group;}
        public void setGroup(int id){group = id;}
        public String getRegex(){return regex;}
        public void   setRegex(String r){regex = r;}
}
