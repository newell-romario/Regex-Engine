package misc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import lexical.Range;

public class IntervalTree{
        /*number of elements in the interval tree*/
        private long count;

        /*Stores left endpoints sorted in ascending order*/
        private Range [] low = new Range[1];

        /*Stores right endpoints sorted in descending order*/
        private Range [] high = new Range[1];

        /*Stores both endpoints in a sorted list.*/
        private long [] points;

        /*Comparators*/
        Comparator<Range>min=(Range a, Range b)->{return Long.compare(a.getMin(), b.getMin());};
        Comparator<Range>max=(Range a, Range b)->{return Long.compare(a.getMax(), b.getMax());};

        Node root;

        private ArrayList<Range> results; 
        private final static long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);
        private final static long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);

        private class Node{
                /*Median */
                long key;
                /*Sorted in ascending order by left endpoints*/
                ArrayList<Range>low;
                /*Sorted in descending order by right end points*/
                ArrayList<Range>high;
                /*Left sub tree */
                Node  left;
                /*Right sub tree */
                Node  right;

                public Node()
                {
                        key   = 0; 
                        low   = new ArrayList<>();
                        high  = new ArrayList<>();
                        left  = null;
                        right = null;
                }
        }

        public IntervalTree(ArrayList<Range> ranges)
        {
                low     =  ranges.toArray(low);
                high    =  ranges.toArray(high);
                count   = ranges.size();
                points  = new long[ranges.size()*2];

                for(int i = 0, j  = ranges.size(); i < ranges.size();++i, ++j){
                        points[i] = ranges.get(i).getMin();
                        points[j] = ranges.get(i).getMax();
                }

                Arrays.sort(points); 
                Arrays.sort(high, max.reversed());
                Arrays.sort(low, min);
                root = buildIntervalTree(ranges);
        }

        public Node buildIntervalTree(ArrayList<Range> ranges)
        {
                if(ranges.size() == 0)
                        return null;

                ArrayList<Range> smaller = new ArrayList<>(); 
                ArrayList<Range> greater = new ArrayList<>();
                long [] min = {POSITIVE_INFINITY, NEGATIVE_INFINITY};
                long [] max = {POSITIVE_INFINITY, NEGATIVE_INFINITY};
                long mid = getMid(ranges);

                for(Range range: ranges){
                        if(range.getMax() < mid)
                                smaller.add(range);
                        else if(range.getMin() > mid)
                                greater.add(range);
                        else if(range.getMin() <= mid && mid <= range.getMax()){
                                if(range.getMin() < min[0])
                                        min[0] = range.getMin(); 
                                if(range.getMin() > min[1])
                                        min[1] = range.getMin();
                                if(range.getMax() < max[0])
                                        max[0] = range.getMax();
                                if(range.getMax() > max[1])
                                        max[1] = range.getMax();
                        }
                }

                Node node = new Node(); 
                node.key  = mid;

                for(int start = binarySearch(low, min[0], 0), 
                end = binarySearch(low, min[1], 0); start <= end; ++start){
                        if(low[start].getMin() <= mid && mid <= low[start].getMax())
                        node.low.add(low[start]);
                }
                        
                for(int start = binarySearch(high, max[1], 1), 
                end = binarySearch(high, max[0], 1); start <= end; ++start)
                {
                        if(high[start].getMin() <= mid &&  mid <= high[start].getMax())
                                node.high.add(high[start]);
                }
                        

                node.left  = buildIntervalTree(smaller);
                node.right = buildIntervalTree(greater);
                return node; 
        }

       private long getMid(ArrayList<Range> ranges)
       {
                long min = ranges.get(0).getMin(); 
                long max = ranges.get(0).getMax();
                for(Range range: ranges){
                        if(range.getMin()< min)
                                min = range.getMin();
                        if(range.getMax() > max)
                                max = range.getMax(); 
                }
                
                int l = Arrays.binarySearch(points, min);
                int r = Arrays.binarySearch(points, max);
                return  points[l+(r-l)/2];
       } 

       private int binarySearch(Range [] values, double val, int field)
       {
                int start  = 0; 
                int len    = values.length-1;
                int mid    = start + (len - start)/2;
                double key; 
                while(start <= len){
                        if(field == 0){
                                key = values[mid].getMin();
                                if(key == val)
                                        return mid;
                                else if(val > key)
                                        start = mid+1;
                                else    len   = mid-1;
                        }else{
                                key = values[mid].getMax();
                                if(key == val)
                                        return mid;
                                else if(val < key)
                                        start = mid+1;
                                else    len   = mid-1;
                        }
                        mid    = start + (len - start)/2;
                }
                return mid;
       }

       public ArrayList<Range> getRanges(int val)
       {
                results = new ArrayList<>();
                intersectingRange(root, val);
                return results;
       }

       private void intersectingRange(Node root, long val)
       {
                if(root == null)
                        return; 
                
                if(val < root.key){
                        for(Range range: root.low){
                                if(range.getMin() <= val)
                                        results.add(range);
                                else break;
                        }
                        intersectingRange(root.left, val);
                }else{
                        for(Range range: root.high){
                                if(range.getMax() >= val)
                                        results.add(range);
                                else break;
                        }
                        intersectingRange(root.right, val);   
                }
       }

       public void displayTree()
       {
                display(root);
       }

       private void display(Node root)
       {
                        if(root == null)
                        return;
                
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.print("Key "+ root.key + ". Children:  ");
                for(Range range: root.low){
                        System.out.print("["+ (long)range.getMin()+ ","+ (long)range.getMax() +"] ");
                }
                System.out.println("");
                System.out.println("-----------------------------------------------------------------------------------------");
                display(root.left);
                display(root.right);
       }
       
       public long getCount(){return count;}
}