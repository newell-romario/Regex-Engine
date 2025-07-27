package lexical;

public class Range{
        private long min;
        private long max;
        public Range(long min, long max)
        {     
                this.min = min;
                this.max = max;
        }

        public void setMin(long min){this.min = min;}
        public void setMax(long max){this.max = max;}

        public long getMin(){return min;}
        public long getMax(){return max;}
        
        @Override
        public String toString()
        {
                String rep = "{"; 
                if(min == max)
                        rep+=(int)max+"}";
                else{
                        String smin = "";
                        String smax = "";
                        smin += min == Double.doubleToLongBits(min)? "" : min;
                        smax += max == Double.doubleToLongBits(max)? "" : max; 
                        rep+=smin+","+smax+"}";
                }       
                return rep; 
        }
}
