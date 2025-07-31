package lexical;
import java.util.ArrayList;
import java.util.Collections;

public class CharacterClass{
        private String rep;
        private ArrayList<Range>   range;/*represents character range*/
        private ArrayList<Integer> set;/*all characters in the set*/
        private ArrayList<Posix>   posix; /*enum representation*/
        private ArrayList<Escape>  escape;/*enum representation of escape*/
        private boolean negate; /*represents the character class is negated*/

        public CharacterClass()
        {
                negate = false;
                rep    = "";
                range  = new ArrayList<>();
                posix  = new ArrayList<>();
                set    = new ArrayList<>();
                escape = new ArrayList<>();
        }

        public void addRange(Range r)
        {
                for (Range val : range)
                        if(val.getMin() == r.getMin() && r.getMax() == val.getMax())
                                return;
                this.range.add(r);
        }
        
        public void addPosix(Posix p)
        {
                if(posix.indexOf(p) == -1)
                        posix.add(p);
        }

        public void addEscape(Escape e)
        {
                if(escape.indexOf(e) == -1)
                        escape.add(e);
        }

        public void addMembers(int mem)
        {
                Integer elem = mem;
                if(set.indexOf(elem) == -1)
                        set.add(elem);
        }

        public void setRepresentation(String s){rep = s;}
        public void negate(){negate = !negate;}
        public ArrayList<Range> getRanges(){return range;}
        public ArrayList<Posix> getPosix(){return posix;}
        public ArrayList<Integer> getSet(){return set;}
        public ArrayList<Escape> getEscape(){return escape;}
        public boolean isNegated(){return negate;}

        public String stringRepSet()
        {
                String mem = ""; 
                for(int i = 0; i < set.size();++i)
                        mem += Character.toString(set.get(i));
                        
                return mem;
        }

        @Override
        public String toString()
        {
                return rep;
        }

        public void compactSet()
        {
                Collections.sort(set);
                byte [] vals = new byte[set.size()];
                for(int i = 0; i < vals.length-1; ++i){
                        if(set.get(i)+1 == set.get(i+1)){
                                vals[i]   = 1;
                                vals[i+1] = 1;
                        }
                }

                for(int i = 0, j=0; i < vals.length; ++i){
                        if(vals[i] == 1){
                                j = i;
                                while(j < vals.length-1 && vals[j] == 1){
                                        if(set.get(j)+1 != set.get(j+1))
                                                break;
                                        ++j;
                                }
                                range.add(new Range(set.get(i), set.get(j)));
                                i = j;
                        }
                }

                ArrayList<Integer> s = (ArrayList<Integer>)set.clone();
                for(int i = 0; i < vals.length; ++i)
                        if(vals[i] == 1)
                                set.remove(s.get(i));
        }
}
