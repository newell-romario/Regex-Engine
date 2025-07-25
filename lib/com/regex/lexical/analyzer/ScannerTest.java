import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ScannerTest {

        @Test
        public void testOperators()
        {
                String operators = "^.$|()*+?:";
                TokenType  [] types = { TokenType.CARET, 
                                        TokenType.PERIOD,
                                        TokenType.DOLLAR_SIGN,
                                        TokenType.ALTERNATION,
                                        TokenType.LEFT_PAREN,
                                        TokenType.RIGHT_PAREN,
                                        TokenType.STAR,
                                        TokenType.PLUS,
                                        TokenType.QUESTION_MARK,
                                        TokenType.COLON,
                                        TokenType.EOF};
                Scanner scanner = new Scanner(operators);
                for(TokenType type : types){
                        try{
                                assertEquals(type, scanner.nextToken().getTokenType());
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}        
                }   
        }


        @Test
        public void testCharacters()
        {
                String alpha    = "abcdefghijklmnopqrstuvwxyz";
                String digits   = "0123456789";
                String symbols  = "`~@#%;'\"/<>/=_-&]";
                String escaped  = "\\^\\.\\$\\|\\(\\)\\[\\{\\*\\+\\?";
                String pattern = alpha + digits + symbols + escaped; 
                Scanner scanner = new Scanner(pattern); 
                try{
                        Token token     = scanner.nextToken(); 
                        while(token.getTokenType() != TokenType.EOF){
                                assertEquals(TokenType.CHARACTER, token.getTokenType());
                                token = scanner.nextToken(); 
                        }   
                }catch(InvalidTokenException e){System.err.println(e.getMessage());}
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
                }catch(InvalidTokenException e){System.err.println(e.getMessage());}
        }

        @Test
        public  void testMinRepetition()
        {
                String [] pattern  = {"{1,}","{12,}"}; 
                double [] min = {1, 12};
                Scanner scanner;
                Token  token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(min[i], token.getRange().getMin(), 0.0);
                                assertEquals(token.getRange().getMax(), Double.POSITIVE_INFINITY, 0.0);
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                }       
        }
        

        @Test
        public void testMaxRepetition()
        {
                String [] pattern = { "{,1}", "{,12}"}; 
                double [] max = {1, 12};
                Scanner scanner;
                Token token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(max[i], token.getRange().getMax(), 0.0);
                                assertEquals(token.getRange().getMin(), Double.POSITIVE_INFINITY, 0.0);
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                } 
        }

        @Test
        public void testMinMaxRepetition()
        {
                String [] pattern = {"{1,2}","{11,22}"};
                double [][] range = {{1, 2}, {11, 22}};       
                Scanner scanner;
                Token token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(range[i][0], token.getRange().getMin(), 0.0);
                                assertEquals(range[i][1], token.getRange().getMax(), 0.0);
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                } 
        }

        @Test
        public void testExactRepetition()
        {
                String pattern  = "{1}";
                double range = 1; 
                Scanner scanner = new Scanner(pattern); 
                Token token;
                try{
                        token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.RANGE);
                        assertEquals(range, token.getRange().getMin(), 0.0);
                        assertEquals(range, token.getRange().getMax(), 0.0);
                }catch(InvalidTokenException e){System.err.println(e.getMessage());}
        }

        @Test
        public void testInvalidRepetition()
        {
                String [] pattern = {"{}","{a}", "{a}","{1","{1,3", "{1,a}"};
                Scanner scanner = null; 
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                scanner.nextToken();
                        }catch(InvalidTokenException e){
                                System.out.println(e.getMessage());
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
                        assertEquals(token.getCharacterClass().stringRepSet(), mem);
                }catch(InvalidTokenException e){System.err.println(e.getMessage());}
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
                        assertEquals(token.getCharacterClass().stringRepSet(), mem);
                }catch(InvalidTokenException e){System.err.println(e.getMessage());}
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
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMin(), range[i][0], 0.0);
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMax(), range[i][1],  0.0);
                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                     }
                }catch(InvalidTokenException e){System.out.println(e.getMessage());}
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
                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                        assertEquals(token.getCharacterClass().getPosix().get(0), "alpha");
                        for(int i = 0; i < escape.length();++i)
                                assertEquals((int)token.getCharacterClass().getEscape().get(i), escape.charAt(i));      
                } catch (InvalidTokenException e){System.out.println(e.getMessage());}
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
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMin(), range[i][0], 0);
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMax(), range[i][1], 0);
                        assertEquals(token.getCharacterClass().getPosix().get(i), posix[i]);
                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                     }
                }catch(InvalidTokenException e){System.out.println(e.getMessage());}
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
                        }catch(InvalidTokenException e){System.out.println(e.getMessage());}
                }
        }

        @Test 
        public void testRegex()
        {
                String pattern = "^abc[a-z[:digit:]defg\\d]{1,2}(a){5}b*?a+$\\1@";
                TokenType [] types = {
                        TokenType.CARET, TokenType.CHARACTER, TokenType.CHARACTER,
                        TokenType.CHARACTER, TokenType.CHARACTER_CLASS, 
                        TokenType.RANGE, TokenType.LEFT_PAREN, TokenType.CHARACTER,
                        TokenType.RIGHT_PAREN, TokenType.RANGE, TokenType.CHARACTER,
                        TokenType.STAR, TokenType.QUESTION_MARK, TokenType.CHARACTER,
                        TokenType.PLUS, TokenType.DOLLAR_SIGN, TokenType.BACK_REFERENCE,
                        TokenType.CHARACTER, TokenType.EOF
                };
                
                /*Elements of the character class*/
                String mem = "defg";
                String posix = "digit";
                int [] range = {'a', 'z'};
                double [] minmax = {1, 2, 5, 5};
                int i = 0; 
                Scanner scanner = new Scanner(pattern);
                Token token = null; 
                for (TokenType type : types) {
                        try{
                                token = scanner.nextToken();
                                assertEquals(type, token.getTokenType());
                                if(type == TokenType.RANGE){
                                        assertEquals(minmax[i], token.getRange().getMin(), 0.0);
                                        assertEquals(minmax[i+1], token.getRange().getMax(), 0.0);
                                        i+=2;
                                }
                                if(type == TokenType.CHARACTER_CLASS){
                                        for(int j = 0; j < token.getCharacterClass().getRanges().size();++j){
                                                assertEquals(token.getCharacterClass().getRanges().get(j).getMin(), range[0], 0.0);
                                                assertEquals(token.getCharacterClass().getRanges().get(j).getMax(),  range[1], 0.0);
                                        }
                                        for(String s: token.getCharacterClass().getPosix()){
                                                assertEquals(s, posix);
                                        }
                                        for(Integer escape : token.getCharacterClass().getEscape()){
                                                assertEquals('d', escape.intValue());
                                        }
                                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                                }
                        }catch(Exception e){System.out.println(e.getMessage());}
                }
        }
}