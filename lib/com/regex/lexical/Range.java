package lexical;

public class Range{
        private long min;
        private long max;
        private String rep;
        public Range(long min, long max)
        {     
                this.min = min;
                this.max = max;
        }

        public void setMin(long min){this.min = min;}
        public void setMax(long max){this.max = max;}
        public long getMin(){return min;}
        public long getMax(){return max;}
        public void setRep(String r){ rep = r;}

        @Override
        public String toString(){return rep; }
}
