import java.util.ArrayList;

public class CharacterClass{
        private String rep;
        private ArrayList<Range>  range;/*represents character range*/
        private ArrayList<String> posix;/*represents  posix class*/
        private ArrayList<Integer> set;/*all characters in the set*/
        private ArrayList<Integer> escape;/*escape sequences*/
        private boolean negate;

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
        
        public void addPosix(String posixClass)
        {
                if(this.posix.indexOf(posixClass) == -1)
                        this.posix.add(posixClass);
        }


        public void addToSet(int mem)
        {
                Integer elem = mem;
                if(set.indexOf(elem) == -1)
                        set.add(elem);
        }

        public void addToEscape(int mem)
        {
                Integer elem = mem;
                if(escape.indexOf(elem) == -1)
                        escape.add(elem);
        }



        public void setRepresentation(String s){rep = s;}
        public void negate(){negate = !negate;}
        public ArrayList<Range> getRanges(){return range;}
        public ArrayList<String> getPosix(){return posix;}
        public ArrayList<Integer> getSet(){return set;}
        public ArrayList<Integer> getEscape(){return escape;}
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
}
