package automaton;
import java.util.ArrayList;
import java.util.Arrays;
import lexical.CharacterClass;
import lexical.Escape;
import lexical.Posix;
import misc.IntervalTree;

public class CharClassState  extends NormalState{
        IntervalTree members;
        ArrayList<Posix> posixes; 
        ArrayList<Escape> escapes;
        boolean negated;
        CharacterClass c;

        public CharClassState(CharacterClass c)
        {
                c.compactSet();
                int [] vals = new int[c.getSet().size()];
                for(int i = 0; i < vals.length;++i)
                        vals[i] = c.getSet().get(i);
                super(vals);
               
                members = new IntervalTree(c.getRanges());
                posixes = c.getPosix();
                escapes = c.getEscape();
                negated = c.isNegated();
                this.c = c;
        }

        @Override
        public BaseState [] move(){return super.getDeadState();}

        @Override
        public BaseState[] move(int val)
        {
                boolean e = false;
                boolean p = false;
                boolean m = false;
                boolean v = false;

                for(Escape escape : escapes)
                        e = e || Escape.evaluate(val, escape);
                        
                for(Posix posix: posixes)
                        p = p || Posix.evaluate(val, posix);
                
                m = members.getRanges(val).size() > 0;
                int [] vals = super.getVals();
                if(vals != null && vals.length != 0){
                        byte [] flags = super.getFlags();
                        if(flags != null){
                                if(flags[0] == 'i'){
                                        val = Character.toLowerCase(val);
                                        for(int i = 0; i < vals.length; ++i){
                                                vals[i] = Character.toLowerCase(vals[i]);
                                        }    
                                }
                        }
                        int loc = Arrays.binarySearch(vals, val);
                        if(loc >= 0)
                                v =  vals[loc] == val;
                }
                        
                boolean t = m || e || p || v;
                if(negated)
                        t = !t;
                if(t)
                        return super.getStates();
            
                return super.getDeadState();
        }

        @Override
        public CharClassState copy()
        {
                CharClassState s = new CharClassState(c);
                s.setBase(this);
                return s;  
        }


        public IntervalTree getMembers()
        {
                return this.members;
        }

        public ArrayList<Posix> getPosixes()
        {
                return this.posixes;
        }

        public ArrayList<Escape> getEscapes()
        {
                return this.escapes;
        }

        public boolean isNegated()
        {
                return this.negated;
        }

        public CharacterClass getCharacterClass()
        {
                return this.c;
        }
}
