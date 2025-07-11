public class Repetition{
        private double min;
        private double max;

        public Repetition()
        {
                this.min = Double.POSITIVE_INFINITY; 
                this.max = Double.POSITIVE_INFINITY;
        }

        public void setMin(double min) throws ArithmeticException
        {
                if(min > max)
                        throw new ArithmeticException("Out of range");
                this.min = min; 
        }


        public void setMax(double max) throws ArithmeticException
        {
                if(max < min)
                        throw new ArithmeticException("Out of range");
                this.max = max;
        }

        public double getMin(){return min;}
        public double getMax(){return max;}
}
