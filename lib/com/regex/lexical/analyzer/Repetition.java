public class Repetition{
        private double min;
        private double max;

        public Repetition()
        {
                this.min = Double.POSITIVE_INFINITY; 
                this.max = Double.POSITIVE_INFINITY;
        }

        public void setMin(double min) throws InvalidTokenException
        {
                if(min > max && 
                max != Double.POSITIVE_INFINITY)
                        throw new InvalidTokenException(String.format("%1$.0f > %2$.0f. Did you mean {%2$.0f,%1$f} instead {%1$.0f, %2$.0f}?", min, max));
                this.min = min; 
        }


        public void setMax(double max) throws InvalidTokenException
        {
                if(max < min && 
                min != Double.POSITIVE_INFINITY)
                        throw new InvalidTokenException(String.format("%1$.0f < %2$.0f. Did you mean {%1$.0f,%2$.0f} instead of {%1$.0f,%2$.0f}?", max, min));
                this.max = max;
        }

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
