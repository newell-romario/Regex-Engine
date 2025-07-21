package lexical;
public class Range{
        private double min;
        private double max;
        public Range(double min, double max)
        {     
                this.min = min;
                this.max = max;
        }

        public void setMin(double min){this.min = min;}
        public void setMax(double max){this.max = max;}

        public double getMin(){return min;}
        public double getMax(){return max;}
        
        @Override
        public String toString()
        {
                String rep = "{"; 
                if(min == max)
                        rep+=(int)max+"}";
                else{
                        String smin = "";
                        String smax = "";
                        smin += (int)min == (int)Double.POSITIVE_INFINITY? "" : (int)min;
                        smax += (int)max == (int)Double.POSITIVE_INFINITY? "" : (int)max; 
                        rep+=smin+","+smax+"}";
                }       
                return rep; 
        }
}


