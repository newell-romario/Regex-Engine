package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Set;
import org.junit.Test;

import Engine.BackTracking;
import Engine.Engine;
import Engine.Thompson;
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
                ArrayList<String> matches = engine.allMatches(text);
                String [] results = {"help@company.com", "+1 (555) 123-4567", "https://company.com/docs",  "sales@company.co.uk", "name@domain.org"};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }
        }

        @Test
        public void testBackReference()
        {
                String pattern = "(?:(([A-Z])([a-z])\\2\\3){2}|(\\d)([a-f])\\4\\5(?:\\4\\5){2}|((\\w)\\7){3}([^\\W\\d_])\\8)";
                String text = "AaAaBbBb";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<String> matches = engine.allMatches(text); 
                String [] results = {"AaAaBbBb", "BbBb", "B", "b"};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }
        }

        @Test
        public void testAnchors()
        {
                String pattern = "<([a-z][a-z0-9]*)\\b[^>]*>.*?</\\1>";
                String text    = "<div>content</div>";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<String> matches = engine.allMatches(text); 
                String [] results = {"<div>content</div>", "div"};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }

        }


        @Test
        public void testEmptyMatches()
        {
                String pattern = "a*?";
                String text = "bbbbbbbb";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<String> matches = engine.allMatches(text); 
                String [] results = {"", "", "", "","", "", "", "", ""};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }
        }

        @Test
        public void testComplex()
        {
                String pattern = "<(\\w+)(\\s+\\w+=\"[^\"]*\")*>(.*?)</\\1>";
                String text    = "<div class=\"main\">Content</div>";
                Engine engine  = new BackTracking(pattern, flags);
                ArrayList<String> matches = engine.allMatches(text); 
                String [] results = {"", "", "", "","", "", "", "", ""};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }
        }

        @Test
        public void testRegexDemo()
        {
                String pattern    = "^[A-Z][a-z]{2,}(?:-[A-Z][a-z]+)*\\d{2,4}:(?:[0-9A-F]{2}-){4}[0-9A-F]{2}(?:\\+[a-z]{3,6}\\.[a-z]{2,3})?$";
                String [] texts   = {"John-Doe42:AB-12-CD-34-EF", "Alice123:00-FF-00-FF-00+config.txt", "Xavier-Williams-Smith2024:1A-2B-3C-4D-5E", "Test99:AA-BB-CC-DD-EE+data.json"};
                Engine engine     = new BackTracking(pattern, flags);
                ArrayList<String> matches = null;
                for(String text: texts){
                        matches = engine.allMatches(text);
                        for(String match: matches){
                                System.out.println(match);
                        }
                }               
        }

        @Test
        public void testThompsonMatchConcatenation()
        {
                String  pattern = "Romario Newell";
                String  [] text    = {"Romario Newell","Romario Newel" }; 
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length; ++i){
                        engine = new Thompson(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }


        @Test
        public void testThompsonMatchStar()
        {
                String pattern    = "a*b";
                String [] text    = {"aaaaaab", "aaaaaaaa"};
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Thompson(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                } 
        }

        @Test
        public void testThompsonMatchPlus()
        {
                String pattern    = "a*b";
                String [] text    = {"aaaaaab", "aaaaaaaa"};
                boolean [] found  = {true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Thompson(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                } 

        }

        @Test
        public void testThompsonMatchOr()
        {
                String pattern  = "romario|newell";
                String [] text     = {"newell","romario", "fail"};
                boolean [] found = {true, true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Thompson(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }


        @Test
        public void testThompsonMatchCharClasss()
        {
                String pattern = "[romario\\d]";
                int [] text = {'r', 'o', 'm', 'a', 'r', 'i', 'o', '9', 'A'};
                boolean [] found = {true, true, true, true, true, true, true, true, false};
                Engine engine; 
                for(int i = 0; i < text.length;++i){
                        engine = new Thompson(pattern, flags);
                        assertEquals(Boolean.valueOf(engine.match(Character.toString(text[i]))), 
                        Boolean.valueOf(found[i]));
                }
        }


        @Test
        public void testThompsonMatchRange()
        {
                String pattern [] = {"a{5}", "a{2,5}", "a{3,}", "a{0,3}", "a{,3}", "a{5}"};
                String [] text    = { "aaaaa", "aaa", "aaaaaaaaa", "", "a", "b"};
                boolean [] found  = {true, true, true, true, true, false};
                Engine engine;
                for(int i = 0; i < text.length;++i){
                        engine = new Thompson(pattern[i], flags);
                        assertEquals(Boolean.valueOf(engine.match(text[i])), Boolean.valueOf(found[i]));
                }
        }


        @Test
        public void testThompsonSubmatch()
        {
                String pattern  = "(?:\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\w+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|https?://(?:www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(?:/\\S*)?)";
                String text     = "help@company.com";
                Engine engine   = new Thompson(pattern, flags);
                boolean found   = false;
                found = engine.match(text);
                assertTrue(found);
        }


        @Test
        public void testThompsonAllMatch()
        {
                String pattern  = "(?:\\+?\\d{1,3}[-.\\s]?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}|\\w+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|https?://(?:www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(?:/\\S*)?)"; 
                String text     = "Please contact support at help@company.com or call +1 (555) 123-4567 for assistance. Visit our website at https://company.com/docs for more information. Invalid entries like 'user@name@domain.org', 'tel:123-456', or 'htp:/broken.link' will not work. For international inquiries, reach us at +44 20 7946 0958 or sales@company.co.uk. Note that 'support@.com' and 'www.company' are invalid formats.";
                Engine engine   = new Thompson(pattern, flags);
                ArrayList<String> matches = engine.allMatches(text);
                String [] results = {"help@company.com", "+1 (555) 123-4567", "https://company.com/docs",  "sales@company.co.uk", "name@domain.org"};
                for(String result: results){
                        assertTrue(matches.contains(result));
                }
        }


        @Test
        public void testThompsonMatchGreedy()
        {
                String pattern = "<.+>";
                String text    = "<EM>first</EM> test";
                Engine engine  = new Thompson(pattern, flags);
                engine.allMatches(text);
        }

}
