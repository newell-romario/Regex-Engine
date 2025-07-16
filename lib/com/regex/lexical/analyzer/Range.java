public class Range {
       private int low;
       private int high;
       public Range(int low, int high) throws InvalidTokenException
       {
                if(low > high)
                        throw new InvalidTokenException("Invalid token: out of range.");
                this.low  = low; 
                this.high = high; 
       }
       public int getLow(){return low;}
       public int getHigh(){return high;}
}


