public class Range {
       private int low;
       private int high;
       public Range(int low, int high) throws Exception
       {
                if(low > high)
                        throw new Exception("Out of range.");
                this.low  = low; 
                this.high = high; 
       }
       public int getLow(){return low;}
       public int getHigh(){return high;}
}


