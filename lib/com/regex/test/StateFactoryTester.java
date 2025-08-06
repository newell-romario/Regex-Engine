package test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import automaton.BaseState;
import automaton.StateFactory;
import lexical.CharacterClass;
import lexical.Range;



public class StateFactoryTester
{
        private static final long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);
        private static final long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);

        @Test
        public void testStar()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState old     = start.getAccept();
                BaseState star    = StateFactory.star(start, true);
                BaseState accept  = star.getAccept();
                BaseState [] next = star.getStates();
                assertEquals(next[0], start);
                assertEquals(next[1], accept);
                next = old.getStates();
                assertEquals(next[0], start);
                assertEquals(next[1], accept);
                next = start.getStates();
                assertEquals(next[0], old);
        }

        @Test
        public void testPlus()
        {
                int [] vals       = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState old     = start.getAccept();
                BaseState plus    = StateFactory.plus(start, true);
                BaseState accept  = plus.getAccept();
                BaseState [] next = accept.getStates();
                assertEquals(start.getVals(), vals);
                assertNull(next[0]);
                assertNull(next[1]);
                next = old.getStates();
                next = next[0].getStates();
                assertEquals(next[0].getCid(), start.getKey());
                assertEquals(next[1], accept);
        }

        @Test
        public void testQuestion()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState quest   = StateFactory.question(start, true);
                BaseState accept  = quest.getAccept();
                BaseState [] next = accept.getStates();
                assertNull(next[0]);
                assertNull(next[1]);
                next  = quest.getStates();
                assertEquals(next[0], start);
                assertEquals(next[1], accept);
                next = start.getStates();
                assertEquals(next[0], start.getAccept());
                assertNull(next[1]);
        }

        @Test
        public void testOr()
        {
                int [][] vals   = {{109}, {50}}; 
                BaseState a = StateFactory.normal(vals[0], null); 
                BaseState b = StateFactory.normal(vals[1], null);
                BaseState or = StateFactory.or(a, b);
                assertNull(or.getVals());
                BaseState [] next = or.getStates();
                assertEquals(next[0], a);
                assertEquals(next[1], b);
                assertArrayEquals(next[0].getVals(), vals[0]);
                assertArrayEquals(next[1].getVals(), vals[1]);
                assertEquals(a.getStates()[0].getStates()[0], or.getAccept());
                assertEquals(b.getStates()[0].getStates()[0], or.getAccept());
        }


        @Test
        public void testDuplicate()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState dup = StateFactory.duplicate(start);
                assertEquals(start.getStateType(), dup.getStateType());
                assertEquals(start.getRegex(), dup.getRegex());
                assertArrayEquals(start.getVals(), dup.getVals());
                assertArrayEquals(start.getFlags(), dup.getFlags());
                assertEquals(start.getKey(), dup.getCid());
                assertEquals(start.getAccept().getKey(), dup.getAccept().getCid());
                assertNotEquals(start.getKey(), dup.getKey());
                assertNotEquals(start.getAccept().getKey(), dup.getAccept().getKey());
        }

        @Test
        public void testDuplicateCharClass()
        {
                int [] vals   = {109}; 
                CharacterClass c = new CharacterClass();
                c.addMembers(vals[0]);
                BaseState start = StateFactory.charClass(c, null);
                BaseState dup   = StateFactory.duplicate(start);
                assertEquals(start.getStateType(), dup.getStateType());
                assertEquals(start.getRegex(), dup.getRegex());
                assertArrayEquals(start.getVals(), dup.getVals());
                assertArrayEquals(start.getFlags(), dup.getFlags());
                assertEquals(start.getKey(), dup.getCid());
                assertEquals(start.getAccept().getKey(), dup.getAccept().getCid());
                assertNotEquals(start.getKey(), dup.getKey());
                assertNotEquals(start.getAccept().getKey(), dup.getAccept().getKey()); 
        }

        @Test
        public void testDuplicateStar()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState star    = StateFactory.star(start, true);
                BaseState dup     = StateFactory.duplicate(star);
                assertEquals(star.getStateType(), dup.getStateType());
                assertEquals(star.getRegex(), dup.getRegex());
                assertNull(dup.getVals());
                assertNull(star.getVals());
                assertArrayEquals(star.getFlags(), dup.getFlags());
                
                
                assertEquals(star.getKey(), dup.getCid());
                assertEquals(star.getAccept().getKey(), dup.getAccept().getCid());
                BaseState [] next  = star.getStates();
                BaseState [] dnext = dup.getStates(); 
                assertEquals(next[0].getKey(), start.getKey());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertEquals(start.getKey(), dnext[0].getCid());

                next  = start.getStates();
                dnext = dnext[0].getStates();
                assertArrayEquals(next[0].getVals(), dnext[0].getVals());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1], dnext[1]);
                BaseState accept  = start.getAccept();
                assertEquals(accept.getKey(), dnext[0].getCid());

                dnext = dnext[0].getStates();
                assertEquals(start.getKey(), dnext[0].getCid());
                assertEquals(star.getAccept().getKey(), dnext[1].getCid());
                
        }

        @Test
        public void testDuplicatePlus()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState plus    = StateFactory.plus(start, true);
                BaseState dup     = StateFactory.duplicate(plus);
                
                assertEquals(plus.getStateType(), dup.getStateType());
                assertEquals(plus.getRegex(), dup.getRegex());
                assertArrayEquals(plus.getFlags(), dup.getFlags());
                assertArrayEquals(plus.getVals(), dup.getVals());
                assertEquals(plus.getKey(), dup.getCid());
                assertEquals(plus.getAccept().getKey(), dup.getAccept().getCid());


                BaseState [] next  = plus.getStates(); 
                BaseState [] dnext = dup.getStates();
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertNull(next[0].getVals());
                assertNull(dnext[0].getVals());
                assertNull(next[1]);
                assertNull(dnext[1]);

                next  = next[0].getStates();
                dnext = dnext[0].getStates();
                next  = next[0].getStates();
                dnext = dnext[0].getStates();
                assertEquals(next[0].getCid(), start.getKey());
                assertEquals(start.getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertEquals(dnext[1].getCid(), plus.getAccept().getKey());
        }

        @Test
        public void testDuplicateQuestion()
        {
                int [] vals   = {109}; 
                BaseState start   = StateFactory.normal(vals, null);
                BaseState quest   = StateFactory.question(start, true);
                BaseState dup     = StateFactory.duplicate(quest);
                assertEquals(quest.getStateType(), dup.getStateType());
                assertEquals(quest.getRegex(), dup.getRegex());
                assertArrayEquals(quest.getFlags(), dup.getFlags());
                assertEquals(quest.getVals(), dup.getVals());

                assertEquals(quest.getKey(), dup.getCid());
                assertEquals(quest.getAccept().getKey(), dup.getAccept().getCid());
                BaseState [] next = quest.getStates();
                BaseState [] dnext = dup.getStates();
                assertEquals(start.getKey(), next[0].getKey());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertArrayEquals(next[0].getVals(), dnext[0].getVals());
                next = next[0].getStates();
                dnext = dnext[0].getStates();
                assertEquals(dnext[0].getCid(), next[0].getKey());
                assertEquals(quest.getAccept().getKey(), dnext[0].getCid());
                assertNull(next[0].getVals());
                assertNull(dnext[0].getVals());
                assertArrayEquals(next[0].getStates(), dnext[0].getStates());
                assertNull(dnext[1]);
                assertNull(next[1]);
        }

        @Test
        public void testDuplicateOr()
        {
                int [][] vals   = {{109}, {50}}; 
                BaseState a = StateFactory.normal(vals[0], null); 
                BaseState b = StateFactory.normal(vals[1], null);
                BaseState or = StateFactory.or(a, b);
                BaseState dup = StateFactory.duplicate(or);
                assertEquals(or.getStateType(), dup.getStateType());
                assertEquals(or.getRegex(), dup.getRegex());
                assertArrayEquals(or.getFlags(), dup.getFlags());
                assertEquals(or.getVals(), dup.getVals());

                assertEquals(or.getKey(), dup.getCid());
                assertEquals(or.getAccept().getKey(), dup.getAccept().getCid());
                BaseState [] next = or.getStates(); 
                BaseState [] dnext = dup.getStates();
                assertEquals(next[0].getKey(), a.getKey());
                assertEquals(next[1].getKey(), b.getKey());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertArrayEquals(next[0].getVals(), dnext[0].getVals());
                assertArrayEquals(next[1].getVals(), dnext[1].getVals());
                next = dnext[0].getStates();
                dnext = dnext[1].getStates();
                assertEquals(next[0].getCid(), a.getAccept().getKey());
                assertEquals(dnext[0].getCid(), b.getAccept().getKey());
                assertEquals(next[1], dnext[1]);
                next =  dnext[0].getStates();
                dnext = dnext[0].getStates();
                assertEquals(next[0].getCid(),  or.getAccept().getKey());
                assertEquals(dnext[0].getCid(), or.getAccept().getKey());
        }

        @Test
        public void testJoin()
        {
                int [][] vals   = {{109}, {97}}; 
                BaseState a = StateFactory.normal(vals[0], null);
                BaseState b = StateFactory.normal(vals[1], null);
                BaseState accept  = a.getAccept();
                a = StateFactory.join(a, b);
                assertEquals(a.getAccept(), b.getAccept());
                BaseState [] next = accept.getStates();
                assertEquals(next[0], b);
                assertArrayEquals(vals[0], a.getVals());
                assertArrayEquals(vals[1], b.getVals());
        }

        @Test
        public void testJoinStates()
        {
                int [][] vals = {{109}, {100}, {80}};
                BaseState [] states = new BaseState[vals.length];
                for(int i = 0; i < vals.length;++i)
                        states[i] = StateFactory.normal(vals[i], null);
                BaseState accept = states[0].getAccept();
                BaseState start  = StateFactory.join(states);
                assertEquals(start, states[0]);
                assertEquals(start.getAccept(), states[vals.length-1].getAccept());
                BaseState [] next = accept.getStates();
                int count = 1;
                while (next[0] != null){
                        if(count < states.length && next[0] == states[count])
                                ++count;
                        next =  next[0].getStates();
                }
                       
                assertEquals(count, 3);
        }

        @Test
        public void testRange()
        {
                /*
                 * a{5}
                 * a{2,5}
                 * a{5,}
                 * a{,5}
                 */
                long min = 2;
                long max = 5;
                Range range;
                int [] vals = {109};
                BaseState accept;
                
                /*
                 * {5}
                 */
                range = new Range(max, max);
                BaseState start = StateFactory.normal(vals, null);
                accept = start.getAccept();
                BaseState quant = StateFactory.range(start, range, true);
                BaseState [] next = accept.getStates();
                assertEquals(start, quant);
                accept = quant.getAccept();
                int count = 0;
                int acount = 0;
                while(next[0] != null){
                        if(start.getKey() == next[0].getCid()){
                                assertArrayEquals(next[0].getVals(), vals);
                                ++count;
                                
                                if(next[0].getAccept() == accept)
                                        ++acount; 
                        }
                        next = next[0].getStates();
                }
                
                assertEquals(count, acount);

                range  = new Range(min, max);
                start  = StateFactory.normal(vals, null);
                accept = start.getAccept();
                quant  = StateFactory.range(start, range, true);
                count  = 0;
                acount = 0;
                next = accept.getStates();
                while(next[0] != null){
                        if(start.getKey() == next[0].getCid()){
                                assertArrayEquals(next[0].getVals(), vals);
                                ++count;
                        }
                        
                        if(next[1] == quant.getAccept())
                                ++acount;
                        next = next[0].getStates();
                }
                assertEquals(count, max-1);
                assertEquals(acount, max-min);
                range  = new Range(NEGATIVE_INFINITY, max);
                start  = StateFactory.normal(vals, null);
                accept = start.getAccept();
                quant  = StateFactory.range(start, range, true);
                count  = 0;
                acount = 0;
                next   = accept.getStates();
                while(next[0] != null){
                        if(start.getKey() == next[0].getCid()){
                                assertArrayEquals(next[0].getVals(), vals);
                                ++count;
                        }
                        
                        if(next[1] == start.getAccept())
                                ++acount;
                        next = next[0].getStates();
                }
                assertEquals(count, acount);
        }
}