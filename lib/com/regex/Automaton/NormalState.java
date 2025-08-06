package automaton;
import java.util.Arrays;

public class NormalState extends BaseState{


        /*Characters to transition on*/
        int [] vals;


        public NormalState(int [] v)
        {
                super(StateType.NORMAL); 
                vals   = v;
        }
        
        public void setVals(int [] vals){this.vals = vals;}  
        public int [] getVals(){return vals;}


        @Override
        public BaseState [] move(){return super.getDeadState();}
        
        @Override
        public BaseState [] move(int val)
        {
                byte [] flags = super.getFlags();
                if( flags != null && flags[0] == 'i'){
                        vals[0] = Character.toLowerCase(vals[0]);
                        val     = Character.toLowerCase(val);
                }
         
                if(vals[0] == val)
                        return super.getStates();
                
         
                return super.getDeadState();
        }


       
        public NormalState copy()
        {
                NormalState s = new NormalState(Arrays.copyOf(vals, vals.length));
                s.setBase(this);
                return s;
        }
}
