package misc;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;
import lexical.Range;
public class IntervalTreeTest {

        @Test
        public void testCreateIntervalTree()
        {
                ArrayList<Range> ranges = new ArrayList<>();
                Range [] r = {  new Range(2, 4), 
                                new Range(6, 10), 
                                new Range(9, 20),
                                new Range(16, 19),
                                new Range(3, 5),
                                new Range(7,  11),
                                new Range(12, 18),
                                new Range(13, 15),
                                new Range(21, 25),
                                new Range(30, 45)
                };

                for(Range range: r)
                        ranges.add(range);
                
                IntervalTree tree = new IntervalTree(ranges);
                tree.displayTree();
                int val; 
                Random ran = new Random();
                for(int i = 0; i < 4;++i){
                        val = ran.nextInt(46);
                        ArrayList<Range> results = tree.getRanges(val);
                        for(Range range: results){
                                assertTrue(range.getMin() <= val);
                                assertTrue(range.getMax() >= val);
                        }
                }
        }
}
