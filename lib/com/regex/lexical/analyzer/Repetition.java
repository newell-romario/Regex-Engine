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
                if(min > max)
                        throw new InvalidTokenException("".format("%.f > %.f. Did you mean {%.f,%.f} instead {%.f, %.f}?", min, max, max, min, min, max));
                this.min = min; 
        }


        public void setMax(double max) throws InvalidTokenException
        {
                if(max < min)
                        throw new InvalidTokenException("".format("%.f < %.f. Did you mean {%.f,%.f} instead of {%.f,%.f}?", max, min, max, min, min, max));
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
