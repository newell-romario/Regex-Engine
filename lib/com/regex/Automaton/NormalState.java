package automaton;
import java.util.Arrays;

public class NormalState extends BaseState{
        /*String representation of current regex*/
        private String regex;

        /*We can set flags for regex such as:
         * i - case-insensitive
         * s - match \n
         * U - ungreedy
        */
        private byte [] flags;

        /*Stores the  accept state*/
        private BaseState accept;

        /*Characters to transition on*/
        int [] vals;


        public NormalState(StateType t, int [] v)
        {
                super(t); 
                vals   = v;
                regex  = "";
                accept = null;
        }
        
        public void setAccept(BaseState accept){this.accept = accept;}
        public void setVals(int [] vals){this.vals = vals;}
        public void setRegex(String r){regex = r;}
        public byte [] getFlags(){return flags;}
        public String getRegex(){return regex;}
        public BaseState getAccept(){return accept;}
        public int [] getVals(){return vals;}


        public void setFlags(byte [] f)
        {
                if(f == null)
                        return;
                String fString = "isU";
                for(int i = 0; i < f.length; ++i){
                        if(fString.indexOf(f[i]) != -1)
                                flags[i] = f[i];
                }          
        }

        
        public BaseState [] move(int val)
        {
                if( flags != null && flags[0] == 'i'){
                        vals[0] = Character.toLowerCase(vals[0]);
                        val     = Character.toLowerCase(val);
                }
         
                if(vals[0] == val)
                        return super.getStates();
                
         
                return super.getDeadState();
        }


       
        public BaseState copy()
        {
                NormalState s = new NormalState(super.getStateType(), Arrays.copyOf(vals, vals.length));
                s.setRegex(regex);
                s.setFlags(flags);
                s.setAccept(accept);
                s.setCid(super.getKey());
                BaseState [] next = s.getStates();
                BaseState [] out  = super.getStates();
                next[0] = out[0];
                next[1] = out[1];
                return s;
        }

        @Override
        public int hashCode()
        {
                int result = super.hashCode();
                result = 31*result + regex.hashCode();
                result = 31*result + vals.hashCode();
                return result;
        }
}
