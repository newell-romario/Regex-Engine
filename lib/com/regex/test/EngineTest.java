package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;
import org.junit.Test;

import Engine.BackTracking;
import Engine.Engine;
import Engine.Match;
import automaton.BaseState;

public class EngineTest{
        byte [] flags = new byte[3];

        @Test
        public void testEpsilonClosureOr()
        {
                String pattern = "foo|bar";
                Engine engine  = new BackTracking(pattern, flags);
                Set<BaseState> s = engine.eClosure(engine.getStart());        
                assertEquals(1, s.size());                
                for(BaseState state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonClosureStar()
        {
                String pattern     = "a*";
                Engine engine      = new BackTracking(pattern, flags);
                Set<BaseState> s   = engine.eClosure(engine.getStart());
                assertEquals(1, s.size());                
                for(BaseState state: s)
                        System.out.println(state.getRegex());
        }
        

        @Test
        public void testEpsilonPlus()
        {
                String pattern = "a+";
                Engine engine  = new BackTracking(pattern, flags);
                Set<BaseState> s = engine.eClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(BaseState state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonQuestion()
        {
                String pattern = "a?";
                Engine engine  = new BackTracking(pattern, flags);
                Set<BaseState> s = engine.eClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(BaseState state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonConcatenation()
        {
                String pattern = "ab";
                Engine engine  = new BackTracking(pattern, flags);
                Set<BaseState> s = engine.eClosure(engine.getStart());  
                assertEquals(1, s.size());                
                for(BaseState state: s)
                        System.out.println(state.getRegex());
        }

        @Test
        public void testEpsilonRange()
        {
                String [] pattern = {"a{,3}", "a{1,}", "a{1,4}", "a{4}"};
                int [] count = {1, 1, 1, 1};
                int j = 0;
                for(String pat: pattern){
                        Engine engine  = new BackTracking(pat, flags);
                        Set<BaseState> s = engine.eClosure(engine.getStart()); 
                        assertEquals(count[j++], s.size()); 
                        for(BaseState state: s)
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
                        engine = new BackTracking(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testMatchStar()
        {
                String pattern    = "a*b";
                String [] text    = {"aaaaaab", "aaaaaaaa"};
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new BackTracking(pattern, flags);
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
                        engine = new BackTracking(pattern, flags);
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
                        engine = new BackTracking(pattern, flags);
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
                        engine = new BackTracking(pattern, flags);
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
                        engine = new BackTracking(pattern[i], flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }

        @Test
        public void testSubmatch()
        {
                String pattern  = "(?:\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\w+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|https?://(?:www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(?:/\\S*)?)";
                String text     = "help@company.com";
                Engine engine   = new BackTracking(pattern, flags);
                boolean found   = false;
                found = engine.match(text);
                assertTrue(found);
        }

        @Test
        public void testAllMatch()
        {
                String pattern  = "(?:\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\w+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|https?://(?:www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(?:/\\S*)?)"; 
                String text     = "Please contact support at help@company.com or call +1 (555) 123-4567 for assistance. Visit our website at https://company.com/docs for more information. Invalid entries like 'user@name@domain.org', 'tel:123-456', or 'htp:/broken.link' will not work. For international inquiries, reach us at +44 20 7946 0958 or sales@company.co.uk. Note that 'support@.com' and 'www.company' are invalid formats.";
                Engine engine   = new BackTracking(pattern, flags);
                ArrayList<Match> matches = engine.allMatches(text);
                String [] results = {"help@company.com", "+1 (555) 123-4567", "https://company.com/docs",  "name@domain.org", "sales@company.co.uk"};
                for(int i = 0; i < results.length; ++i){
                        assertTrue(results[i].equals(matches.get(i).getMatch()));
                }
        }

        @Test
        public void testBackReference()
        {
                String pattern = "(?:(([A-Z])([a-z])\\2\\3){2}|(\\d)([a-f])\\4\\5(?:\\4\\5){2}|((\\w)\\7){3}([^\\W\\d_])\\8)";
                String text = "AaAaBbBb";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<Match> matches = engine.allMatches(text); 
                String [] results = {"AaAaBbBb"};
                for(int i = 0; i < results.length; ++i){
                        assertTrue(results[i].equals(matches.get(i).getMatch()));
                }
        }

        @Test
        public void testAnchors()
        {
                String pattern = "<([a-z][a-z0-9]*)\\b[^>]*>.*?</\\1>";
                String text    = "<div>content</div>";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<Match> matches = engine.allMatches(text); 
                String [] results = {"<div>content</div>"};
                for(int i = 0; i < results.length; ++i){
                        assertTrue(results[i].equals(matches.get(i).getMatch()));
                }

        }


        @Test
        public void testEmptyMatches()
        {
                String pattern = "a*";
                String text = "bbbbbbbb";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<Match> matches = engine.allMatches(text); 
                String [] results = {"", "", "", "","", "", "", "", ""};
                for(int i = 0; i < results.length; ++i){
                        assertTrue(results[i].equals(matches.get(i).getMatch()));
                }

        }

        @Test
        public void testComplex()
        {
                String pattern = "<(\\w+)(\\s+\\w+=\"[^\"]*\")*>(.*?)</\\1>";
                String text    = "<div class=\"main\">Content</div>";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<Match> matches = engine.allMatches(text);  
                String [] results = {"<div class=\"main\">Content</div>"};
                for(int i = 0; i < results.length; ++i){
                        assertTrue(results[i].equals(matches.get(i).getMatch()));
                }
        }
}
