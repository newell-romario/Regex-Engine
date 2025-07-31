package lexical;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import exceptions.*;

public class ScannerTest {

        long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);
        long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);

        @Test
        public void testOperators()
        {
                String operators = "^$|()*+?:";
                TokenType  [] types = { TokenType.ASSERTIONS, 
                                        TokenType.ASSERTIONS,
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
                String alpha       = "abcdefghijklmnopqrstuvwxyz";
                String digits      = "0123456789";
                String symbols     = "`~@#%;'\"/<>/=_-&]";
                String escaped     = "\\^\\.\\$\\|\\(\\)\\[\\{\\*\\+\\?";
                String [] pattern  = {alpha, digits, symbols, escaped};
                TokenType [] types = {TokenType.CHARACTER, TokenType.CHARACTER, TokenType.CHARACTER, TokenType.ESCAPE}; 
                Scanner scanner; 
                int i = 0;
                for(String pat: pattern){
                        scanner = new Scanner(pat); 
                        try{
                                Token token = scanner.nextToken(); 
                                while(token.getTokenType() != TokenType.EOF){
                                        assertEquals(types[i], token.getTokenType());
                                        token = scanner.nextToken(); 
                                }  
                                ++i; 
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                } 
             
        }

        @Test
        public void testBackReferences()
        {
                String pattern  = "a\\12bf\\[+";
                TokenType [] types = {  TokenType.CHARACTER,
                                        TokenType.BACK_REFERENCE,
                                        TokenType.CHARACTER,
                                        TokenType.CHARACTER,
                                        TokenType.ESCAPE,
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
                long [] min = {1, 12};
                Scanner scanner;
                Token  token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(min[i], token.getRange().getMin());
                                assertEquals(token.getRange().getMax(), POSITIVE_INFINITY);
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                }       
        }
        

        @Test
        public void testMaxRepetition()
        {
                String [] pattern = { "{,1}", "{,12}"}; 
                long [] max = {1, 12};
                Scanner scanner;
                Token token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(max[i], token.getRange().getMax());
                                assertEquals(token.getRange().getMin(), NEGATIVE_INFINITY);
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                } 
        }

        @Test
        public void testMinMaxRepetition()
        {
                String [] pattern = {"{1,2}","{11,22}"};
                long [][] range = {{1, 2}, {11, 22}};       
                Scanner scanner;
                Token token;
                for(int i = 0; i < pattern.length; ++i){
                        scanner = new Scanner(pattern[i]);
                        try{
                                token = scanner.nextToken();
                                assertEquals(token.getTokenType(), TokenType.RANGE);
                                assertEquals(range[i][0], token.getRange().getMin());
                                assertEquals(range[i][1], token.getRange().getMax());
                        }catch(InvalidTokenException e){System.err.println(e.getMessage());}
                } 
        }

        @Test
        public void testExactRepetition()
        {
                String pattern  = "{1}";
                long range = 1; 
                Scanner scanner = new Scanner(pattern); 
                Token token;
                try{
                        token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.RANGE);
                        assertEquals(range, token.getRange().getMin());
                        assertEquals(range, token.getRange().getMax());
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
                long [][] range = {{'a', 'z'}, {'A', 'Z'}, {'0','9'}};
                Scanner scanner = new Scanner(pattern); 
                try{
                     Token token = scanner.nextToken();
                     assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                     for(int i = 0; i < range.length; ++i){
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMin(), range[i][0]);
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMax(), range[i][1]);
                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                     }
                }catch(InvalidTokenException e){System.out.println(e.getMessage());}
        }

        @Test
        public void testCharacterClassWithEscapeSequence()
        {
                String pattern = "[a\\dz[:alpha:]\\Wfg]";
                String mem = "azfg"; 
                Escape [] escape = {Escape.DIGITS, Escape.NON_WORD};
                Scanner scanner = new Scanner(pattern);
                try{
                        Token token = scanner.nextToken();
                        assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                        assertEquals(token.getCharacterClass().getPosix().get(0), Posix.ALPHA);
                        for(int i = 0; i < escape.length;++i)
                                assertEquals(token.getCharacterClass().getEscape().get(i), escape[i]);      
                } catch (InvalidTokenException e){System.out.println(e.getMessage());}
        }


        @Test
        public void testCharacterClassWithRangeAndMembersAndPosix()
        {
                String pattern  = "[A-Z`~@#$%^&*(0-9)[:digit:]\\][:alpha:][:digit:@z]";
                String mem      = "`~@#$%^&*()][:digtz";
                Posix [] posix  = {Posix.DIGIT, Posix.ALPHA};
                long [][] range  = {{'A', 'Z'}, {'0','9'}};
                Scanner scanner = new Scanner(pattern); 
                try{
                     Token token = scanner.nextToken();
                     assertEquals(token.getTokenType(), TokenType.CHARACTER_CLASS);
                     for(int i = 0; i < range.length; ++i){
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMin(), range[i][0]);
                        assertEquals(token.getCharacterClass().getRanges().get(i).getMax(), range[i][1]);
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
                        TokenType.ASSERTIONS, TokenType.CHARACTER, TokenType.CHARACTER,
                        TokenType.CHARACTER, TokenType.CHARACTER_CLASS, 
                        TokenType.RANGE, TokenType.LEFT_PAREN, TokenType.CHARACTER,
                        TokenType.RIGHT_PAREN, TokenType.RANGE, TokenType.CHARACTER,
                        TokenType.STAR, TokenType.QUESTION_MARK, TokenType.CHARACTER,
                        TokenType.PLUS, TokenType.ASSERTIONS, TokenType.BACK_REFERENCE,
                        TokenType.CHARACTER, TokenType.EOF
                };
                
                /*Elements of the character class*/
                String mem = "defg";
                Posix posix = Posix.DIGIT;
                long [] range = {'a', 'z'};
                long [] minmax = {1, 2, 5, 5};
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
                                        for(Posix p: token.getCharacterClass().getPosix())
                                                assertEquals(p, posix);
                                        
                                        for(Escape escape : token.getCharacterClass().getEscape()){
                                                assertEquals(Escape.DIGITS, escape);
                                        }
                                        assertEquals(mem, token.getCharacterClass().stringRepSet());
                                }
                        }catch(Exception e){System.out.println(e.getMessage());}
                }
        }

        @Test
        public void testEscapeSequences()
        {
                String pattern = "\\d\\D\\s\\S\\w\\W\\b\\B\\A\\Z";
                TokenType [] types = {  TokenType.CHARACTER_CLASS,TokenType.CHARACTER_CLASS,
                                        TokenType.CHARACTER_CLASS, TokenType.CHARACTER_CLASS,
                                        TokenType.CHARACTER_CLASS,TokenType.CHARACTER_CLASS,
                                        TokenType.ASSERTIONS, TokenType.ASSERTIONS, 
                                        TokenType.ASSERTIONS,TokenType.ASSERTIONS, TokenType.EOF};
                Scanner scanner = new Scanner(pattern);
                for(TokenType type : types){
                        try{
                              Token token  = scanner.nextToken();
                              System.out.println(token.toString());
                              assertEquals(type, token.getTokenType()); 
                        }catch(InvalidTokenException e){System.err.println(e);}
                }
        }
}