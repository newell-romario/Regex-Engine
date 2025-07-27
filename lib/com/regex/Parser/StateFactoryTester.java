package parser;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import automaton.State;



public class StateFactoryTester
{
        @Test
        public void testStar()
        {
                int [] vals   = {109}; 
                State start   = StateFactory.normal(vals);
                State old     = start.getAccept();
                State star    = StateFactory.star(start);
                State accept  = star.getAccept();
                State [] next = star.getStates();
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
                int [] vals   = {109}; 
                State start   = StateFactory.normal(vals);
                State old     = start.getAccept();
                State plus    = StateFactory.plus(start);
                State accept  = plus.getAccept();
                State [] next = accept.getStates();
                assertEquals(start.getVals(), vals);
                assertNull(next[0]);
                assertNull(next[1]);
                next = old.getStates();
                assertEquals(next[0], start);
                assertEquals(next[1], accept);
                next = start.getStates();
                assertEquals(next[0], old);
        }

        @Test
        public void testQuestion()
        {
                int [] vals   = {109}; 
                State start   = StateFactory.normal(vals);
                State quest   = StateFactory.question(start);
                State accept  = quest.getAccept();
                State [] next = accept.getStates();
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
                State a = StateFactory.normal(vals[0]); 
                State b = StateFactory.normal(vals[1]);
                State or = StateFactory.or(a, b);
                assertNull(or.getVals());
                State [] next = or.getStates();
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
                State start   = StateFactory.normal(vals);
                State dup = StateFactory.duplicate(start);
                assertEquals(start.getStateType(), dup.getStateType());
                assertEquals(start.getRegex(), dup.getRegex());
                assertEquals(start.getAssertion() , dup.getAssertion());
                assertEquals(start.getSubMatch(), dup.getSubMatch());
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
                State start   = StateFactory.normal(vals);
                State star    = StateFactory.star(start);
                State dup     = StateFactory.duplicate(star);
                assertEquals(star.getStateType(), dup.getStateType());
                assertEquals(star.getRegex(), dup.getRegex());
                assertEquals(star.getAssertion() , dup.getAssertion());
                assertNull(dup.getVals());
                assertNull(star.getVals());
                assertEquals(star.getSubMatch(), dup.getSubMatch());
                assertArrayEquals(star.getFlags(), dup.getFlags());
                
                
                assertEquals(star.getKey(), dup.getCid());
                assertEquals(star.getAccept().getKey(), dup.getAccept().getCid());
                State [] next  = star.getStates();
                State [] dnext = dup.getStates(); 
                assertEquals(next[0].getKey(), start.getKey());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertEquals(start.getKey(), dnext[0].getCid());

                next  = start.getStates();
                dnext = dnext[0].getStates();
                assertArrayEquals(next[0].getVals(), dnext[0].getVals());
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertEquals(next[1], dnext[1]);
                State accept  = start.getAccept();
                assertEquals(accept.getKey(), dnext[0].getCid());

                dnext = dnext[0].getStates();
                assertEquals(start.getKey(), dnext[0].getCid());
                assertEquals(star.getAccept().getKey(), dnext[1].getCid());
                
        }

        @Test
        public void testDuplicatePlus()
        {
                int [] vals   = {109}; 
                State start   = StateFactory.normal(vals);
                State plus    = StateFactory.plus(start);
                State dup     = StateFactory.duplicate(plus);
                assertEquals(plus.getStateType(), dup.getStateType());
                assertEquals(plus.getRegex(), dup.getRegex());
                assertEquals(plus.getAssertion() , dup.getAssertion());
                assertEquals(plus.getSubMatch(), dup.getSubMatch());
                assertArrayEquals(plus.getFlags(), dup.getFlags());
                assertArrayEquals(plus.getVals(), dup.getVals());
                
                assertEquals(plus.getKey(), dup.getCid());
                assertEquals(plus.getAccept().getKey(), dup.getAccept().getCid());
                State [] next  = plus.getStates(); 
                State [] dnext = dup.getStates();
                
                assertEquals(next[0].getKey(), dnext[0].getCid());
                assertNull(next[0].getVals());
                assertNull(dnext[0].getVals());
                assertNull(next[1]);
                assertNull(dnext[1]);
                next  = next[0].getStates();
                dnext = dnext[0].getStates();
                assertEquals(next[0].getKey(), start.getKey());
                assertEquals(start.getKey(), dnext[0].getCid());
                assertEquals(next[1].getKey(), dnext[1].getCid());
                assertEquals(dnext[1].getCid(), plus.getAccept().getKey());
        }

        @Test
        public void testDuplicateQuestion()
        {
                int [] vals   = {109}; 
                State start   = StateFactory.normal(vals);
                State quest   = StateFactory.question(start);
                State dup     = StateFactory.duplicate(quest);
                assertEquals(quest.getStateType(), dup.getStateType());
                assertEquals(quest.getRegex(), dup.getRegex());
                assertEquals(quest.getAssertion() , dup.getAssertion());
                assertEquals(quest.getSubMatch(), dup.getSubMatch());
                assertArrayEquals(quest.getFlags(), dup.getFlags());
                assertEquals(quest.getVals(), dup.getVals());

                assertEquals(quest.getKey(), dup.getCid());
                assertEquals(quest.getAccept().getKey(), dup.getAccept().getCid());
                State [] next = quest.getStates();
                State [] dnext = dup.getStates();
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
                State a = StateFactory.normal(vals[0]); 
                State b = StateFactory.normal(vals[1]);
                State or = StateFactory.or(a, b);
                State dup = StateFactory.duplicate(or);
                assertEquals(or.getStateType(), dup.getStateType());
                assertEquals(or.getRegex(), dup.getRegex());
                assertEquals(or.getAssertion() , dup.getAssertion());
                assertEquals(or.getSubMatch(), dup.getSubMatch());
                assertArrayEquals(or.getFlags(), dup.getFlags());
                assertEquals(or.getVals(), dup.getVals());

                assertEquals(or.getKey(), dup.getCid());
                assertEquals(or.getAccept().getKey(), dup.getAccept().getCid());
                State [] next = or.getStates(); 
                State [] dnext = dup.getStates();
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
                State a = StateFactory.normal(vals[0]);
                State b = StateFactory.normal(vals[1]);
                State accept  = a.getAccept();
                a = StateFactory.join(a, b);
                assertEquals(a.getAccept(), b.getAccept());
                State [] next = accept.getStates();
                assertEquals(next[0], b);
                assertArrayEquals(vals[0], a.getVals());
                assertArrayEquals(vals[1], b.getVals());
        }

        @Test
        public void testJoinStates()
        {
                int [][] vals = {{109}, {100}, {80}};
                State [] states = new State[vals.length];
                for(int i = 0; i < vals.length;++i)
                        states[i] = StateFactory.normal(vals[i]);
                State accept = states[0].getAccept();
                State start = StateFactory.join(states);
                assertEquals(start, states[0]);
                assertEquals(start.getAccept(), states[vals.length-1].getAccept());
                State [] next = accept.getStates();
                for(int i=1; i < vals.length;++i)
                        assertEquals(next[0], states[i]);
                        next =  next[0].getAccept().getStates();
        }

        
}
