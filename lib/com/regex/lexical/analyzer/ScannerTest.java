import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ScannerTest {

        @Test
        public void testOperators()
        {
                String operators = "^.$|()*+?";
                TokenType  [] types = { TokenType.CARET, 
                                        TokenType.PERIOD,
                                        TokenType.DOLLAR_SIGN,
                                        TokenType.ALTERNATION,
                                        TokenType.LEFT_PAREN,
                                        TokenType.RIGHT_PAREN,
                                        TokenType.STAR,
                                        TokenType.PLUS,
                                        TokenType.QUESTION_MARK,
                                        TokenType.EOF};
                Scanner scanner = new Scanner(operators);
                for(TokenType type : types){
                        try{
                                assertEquals(type, scanner.nextToken().getTokenType());
                        }catch(Exception e){}        
                }   
        }


        @Test
        public void testCharacters()
        {
                String alpha    = "abcdefghijklmnopqrstuvwxyz";
                String digits   = "0123456789";
                String symbols  = "`~@#%:;'\"/<>/=_-&]";
                String escaped  = "\\^\\.\\$\\|\\(\\)\\[\\{\\*\\+\\?";
                String pattern = alpha + digits + symbols + escaped; 
                Scanner scanner = new Scanner(pattern); 
                try{
                        Token token     = scanner.nextToken(); 
                        while(token.getTokenType() != TokenType.EOF){
                                assertEquals(TokenType.CHARACTER, token.getTokenType());
                                token = scanner.nextToken(); 
                        }   
                }catch(Exception e){}
        }

        @Test
        public void testBackReferences()
        {
                String pattern  = "a\\12bf\\[+";
                TokenType [] types = {  TokenType.CHARACTER,
                                        TokenType.BACK_REFERENCE,
                                        TokenType.CHARACTER,
                                        TokenType.CHARACTER,
                                        TokenType.CHARACTER,
                                        TokenType.PLUS,
                                        TokenType.EOF
                };
                Token token = null;
                Scanner scanner = new Scanner(pattern);
                try{
                        for(TokenType type : types){
                                token = scanner.nextToken();
                                assertEquals(type, token.getTokenType());
                                if(token.getTokenType() == TokenType.BACK_REFERENCE)
                                        assertEquals(token.getValue(), 12);
                        }
                }catch(Exception e){}
        }

        @Test
        public  void testMinRepetition()
        {
                String [] pattern  = {"{1,}","{12,}"}; 
                double [] min = {1, 12};
                Scanner scanner;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                assertEquals(scanner.nextToken().getTokenType(), TokenType.REPETITION);
                                assertEquals(min[i], scanner.getRepetition().getMin(), 0.0);
                                assertEquals(scanner.getRepetition().getMax(), Double.POSITIVE_INFINITY, 0.0);
                        }catch(Exception e){}
                }       
        }
        

        @Test
        public void testMaxRepetition()
        {
                String [] pattern = { "{,1}", "{,12}"}; 
                double [] max = {1, 12};
                Scanner scanner;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                assertEquals(scanner.nextToken().getTokenType(), TokenType.REPETITION);
                                assertEquals(max[i], scanner.getRepetition().getMax(), 0.0);
                                assertEquals(scanner.getRepetition().getMin(), Double.POSITIVE_INFINITY, 0.0);
                        }catch(Exception e){}
                } 
        }

        @Test
        public void testMinMaxRepetition()
        {
                String [] pattern = {"{1,2}","{11,22}"};
                double [][] range = {{1, 2}, {11, 22}};       
                Scanner scanner;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                assertEquals(scanner.nextToken().getTokenType(), TokenType.REPETITION);
                                assertEquals(range[i][0], scanner.getRepetition().getMin(), 0.0);
                                assertEquals(range[i][1], scanner.getRepetition().getMax(), 0.0);
                        }catch(Exception e){}
                } 
        }

        @Test
        public void testInvalidRepetition()
        {
                String [] pattern = {"{}","{a}", "{a}","{1","{1,3", "{1,a}"};
                String [] message = {   "Empty quantifier.", 
                                        "Invalid token.",
                                        "Invalid token.", 
                                        "Missing }.",
                                        "Missing }.",
                                        "Invalid token."};
                Scanner scanner = null; 
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                scanner.nextToken();
                        }catch(Exception e)
                        {
                                assertEquals(message[i], e.getMessage());
                        }
                }
        }


        @Test
        public void testCharacterClassWithOnlyMembers()
        {
                String pattern = "[abcdefghijklmnopqrstuvwxyz\\]\\@!`*&^|{}.%()~#*[\'\"]";
                String mem = "abcdefghijklmnopqrstuvwxyz]\\@!`*&^|{}.%()~#[\'\"";
                Scanner scanner = new Scanner(pattern); 
                try{    
                        Token token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                        assertEquals(scanner.getCharacterClass().stringRepSet(), mem);
                }catch(Exception e){}
        }

        @Test
        public void testCharacterClassWithDuplicateMembers()
        {
                String pattern = "[abcdabcd***]";
                String mem = "abcd*"; 
                Scanner scanner = new Scanner(pattern); 
                try{    
                        Token token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                        assertEquals(scanner.getCharacterClass().stringRepSet(), mem);
                }catch(Exception e){}
        }

        @Test 
        public void testCharacterClassWithRange()
        {
                String pattern = "[a-zA-Zab`1@#$0-9]";
                String mem     = "ab`1@#$";
                int [][] range = {{'a', 'z'}, {'A', 'Z'}, {'0','9'}};
                Scanner scanner = new Scanner(pattern); 
                try{
                     Token token = scanner.nextToken();
                     assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                     for(int i = 0; i < range.length; ++i){
                        assertEquals(scanner.getCharacterClass().getRanges().get(i).getLow(), range[i][0]);
                        assertEquals(scanner.getCharacterClass().getRanges().get(i).getHigh(), range[i][1]);
                        assertEquals(mem, scanner.getCharacterClass().stringRepSet());
                     }
                }catch(Exception e){}
        }

        @Test
        public void testCharacterClassWithEscapeSequence()
        {
                String pattern = "[a\\dz[:alpha:]\\Wfg]";
                String mem = "azfg"; 
                String escape = "dW";
                Scanner scanner = new Scanner(pattern);
                try{
                        Token token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                        assertEquals(mem, scanner.getCharacterClass().stringRepSet());
                        assertEquals(scanner.getCharacterClass().getPosix().get(0), "alpha");
                        for(int i = 0; i < pattern.length();++i)
                                assertEquals((int)scanner.getCharacterClass().getEscape().get(i), escape.charAt(i));      
                } catch (Exception e){}
        }


        @Test
        public void testCharacterClassWithRangeAndMembersAndPosix()
        {
                String pattern  = "[A-Z`~@#$%^&*(0-9)[:digit:]\\][:alpha:][:digit:@z]";
                String mem      = "`~@#$%^&*()][:digtz";
                String [] posix = {"digit", "alpha"};
                int [][] range = {{'A', 'Z'}, {'0','9'}};
                Scanner scanner = new Scanner(pattern); 
                try{
                     Token token = scanner.nextToken();
                     assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                     for(int i = 0; i < range.length; ++i){
                        assertEquals(scanner.getCharacterClass().getRanges().get(i).getLow(), range[i][0]);
                        assertEquals(scanner.getCharacterClass().getRanges().get(i).getHigh(), range[i][1]);
                        assertEquals(scanner.getCharacterClass().getPosix().get(i), posix[i]);
                        assertEquals(mem, scanner.getCharacterClass().stringRepSet());
                     }
                }catch(Exception e){System.out.println(e.getMessage());}
        }

        
        @Test
        public void testInvalidCharacterClass()
        {
                String invalid [] = {
                        "[]", 
                        "[", 
                        "[\\qaa]", 
                        "[\\d-a]", 
                        "[a-\\d]",
                        "[]",
                        "[[:alphaa:]]",
                        "[z-a]"
                };

                Scanner scanner = null; 
                for(int i = 0; i < invalid.length;++i){
                        scanner = new Scanner(invalid[i]);
                        try{
                                scanner.nextToken();
                        }catch(Exception e){System.out.println(e.getMessage());}
                }
        }
}
