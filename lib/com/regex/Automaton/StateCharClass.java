package automaton;

import java.util.ArrayList;
import java.util.Arrays;
import lexical.CharacterClass;
import lexical.Escape;
import lexical.Posix;
import misc.IntervalTree;

public class StateCharClass  extends State{
        IntervalTree members;
        ArrayList<Posix> posixes; 
        ArrayList<Escape> escapes;
        boolean negated;
        CharacterClass c;

        public StateCharClass(CharacterClass c)
        {
                c.compactSet();
                int [] vals = new int[c.getSet().size()];
                for(int i = 0; i < vals.length;++i)
                        vals[i] = c.getSet().get(i);
                super(StateType.CHARACTER_CLASS, vals);
               
                members = new IntervalTree(c.getRanges());
                posixes = c.getPosix();
                escapes = c.getEscape();
                negated = c.isNegated();
                this.c = c;
        }

        @Override
        public State [] move(int val)
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
                v =  vals[Arrays.binarySearch(vals, val)] == val;
        

                boolean t = m || e || p || v;
                if(negated)
                        t = !t;
                if(t)
                        return super.getStates();
            
                return super.getDeadState();
        }

        @Override
        public State copy()
        {
                State s = new StateCharClass(c);
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
