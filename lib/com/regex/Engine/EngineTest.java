package Engine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import org.junit.Test;
import automaton.State;

public class EngineTest{
        byte [] flags = new byte[3];

        @Test
        public void testEpsilonClosureOr()
        {
                String pattern = "foo|bar";
                Engine engine = new Engine(pattern, flags);
                Set<State> s = engine.epsilonClosure(engine.getStart());        
                assertEquals(1, s.size());                
                for(State state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonClosureStar()
        {
                String pattern = "a*";
                Engine engine  = new Engine(pattern, flags);
                Set<State> s   = engine.epsilonClosure(engine.getStart());
                assertEquals(1, s.size());                
                for(State state: s)
                        System.out.println(state.getRegex());
        }
        

        @Test
        public void testEpsilonPlus()
        {
                String pattern = "a+";
                Engine engine  = new Engine(pattern, flags);
                Set<State> s = engine.epsilonClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(State state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonQuestion()
        {
                String pattern = "a?";
                Engine engine  = new Engine(pattern, flags);
                Set<State> s = engine.epsilonClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(State state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonConcatenation()
        {
                String pattern = "ab";
                Engine engine  = new Engine(pattern, flags);
                Set<State> s = engine.epsilonClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(State state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonRange()
        {
                String [] pattern = {"a{,3}", "a{1,}", "a{1,4}", "a{4}"};
                int [] count = {1, 1, 1, 1};
                int j = 0;
                for(String pat: pattern){
                        Engine engine  = new Engine(pat, flags);
                        Set<State> s = engine.epsilonClosure(engine.getStart()); 
                        assertEquals(count[j++], s.size()); 
                        for(State state: s)
                                System.out.println(state.getRegex());
                }                
        }

        @Test
        public void testMatchConcatenation()
        {
                
                String  pattern = "Romario Newell";
                String  [] text    = {"Romario Newell","Romario Newel" }; 
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length; ++i){
                        engine = new Engine(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testMatchStar()
        {
                String pattern  = "a*b";
                String [] text     = {"aaaaaab", "aaaaaaaa"};
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Engine(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testMatchPlus()
        {
                String pattern  = "a+";
                String [] text     = {"aaaaaaaa", "aaaaaaaab"};
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Engine(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testMatchOr()
        {
                String pattern  = "romario|newell";
                String [] text     = {"newell","romario", "fail"};
                boolean [] found = {true, true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Engine(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testMatchCharClass()
        {
                String pattern = "[romario\\d]";
                int [] text = {'r', 'o', 'm', 'a', 'r', 'i', 'o', '9', 'A'};
                boolean [] found = {true, true, true, true, true, true, true, true, false};
                Engine engine; 
                for(int i = 0; i < text.length;++i){
                        engine = new Engine(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(Character.toString(text[i]))), 
                        Boolean.valueOf(found[i]));
                }

        }

        @Test
        public void testMatchRange()
        {
                String pattern [] = {"a{5}", "a{2,5}", "a{3,}", "a{0,3}", "a{,3}", "a{5}"};
                String [] text    = { "aaaaa", "aaa", "aaaaaaaaa", "", "a", "b"};
                boolean [] found  = {true, true, true, true, true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Engine(pattern[i], flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testSubmatch()
        {
               String pattern  = "(ab)+";
               String text     = "ababc";
                
               
                //String pattern  = "[[:digit:]]{2,4}-(0[1-9]|1[0-2])-(0[1-9]|[12][[:digit:]]|3[01])";
                //String text = "2024-05-15";
                Engine engine   = new Engine(pattern, flags);
                boolean found   = false;
                found = engine.match(text);
                assertTrue(found);
        }
}
