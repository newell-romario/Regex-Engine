import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ScannerTest {
        
        @Test
        public void testMetaCharacters(){
                String pattern = "+*|.?^$()[]{}-\\";
                Scanner scanner = new Scanner(pattern);

                Token [] tokens = {Token.PLUS, 
                                   Token.STAR,
                                   Token.ALTERNATION,
                                   Token.PERIOD,
                                   Token.QUESTION_MARK,
                                   Token.CARET,
                                   Token.DOLLAR_SIGN,
                                   Token.LEFT_PAREN,
                                   Token.RIGHT_PAREN,
                                   Token.LEFT_BRACKET,
                                   Token.RIGHT_BRACKET,
                                   Token.LEFT_BRACES,
                                   Token.RIGHT_BRACES,
                                   Token.MINUS,
                                   Token.CHARACTER,
                                   Token.EOF
                };

                /*We should recognize every token*/
                for (Token token : tokens) {
                        assertEquals(scanner.nextToken(), token);
                        System.out.println(token);
                }
        }

        @Test
        public void testEscapeSequences()
        {
                String pattern = "\\w\\W\\s\\S\\d\\D\\^\\.\\$\\|\\(\\)\\[\\]\\{\\}\\*\\+\\?\\-";
                Scanner scanner = new Scanner(pattern);
    

                Token [] tokens = {
                        Token.WORD,
                        Token.NON_WORD,
                        Token.WHITESPACE,
                        Token.NON_WHITESPACE,
                        Token.DIGITS,
                        Token.NON_DIGITS,
                        Token.CHAR_CARET,
                        Token.CHAR_PERIOD,
                        Token.CHAR_DOLLAR_SIGN,
                        Token.CHAR_ALTERNATION,
                        Token.CHAR_LEFT_PAREN,
                        Token.CHAR_RIGHT_PAREN,
                        Token.CHAR_LEFT_BRACKET,
                        Token.CHAR_RIGHT_BRACKET,
                        Token.CHAR_LEFT_BRACES,
                        Token.CHAR_RIGHT_BRACES,
                        Token.CHAR_STAR,
                        Token.CHAR_PLUS,
                        Token.CHAR_QUESTION_MARK,
                        Token.CHAR_MINUS,
                        Token.EOF
                };

                /*We should recognize every token*/
                for (Token token : tokens) {
                        assertEquals(scanner.nextToken(), token);
                        System.out.println(token);
                }
        }
        
        @Test
        public void testBackrefrences()
        {
                String pattern = "\\1234";
                Scanner scanner = new Scanner(pattern);
                assertEquals(scanner.nextToken(), Token.BACKREFERENCES);
                assertEquals(1234, scanner.getReference());

        }

        @Test
        public void testChararcters()
        {
                String pattern = "Romario Newell@!#%&<>~`,:;'_= \"";
                Scanner scanner = new Scanner(pattern); 
                Token tok = scanner.nextToken(); 
                for(char ch : pattern.toCharArray()){
                        if(tok == Token.EOF)
                                break;

                        assertEquals(tok, Token.CHARACTER);
                        assertEquals(ch, scanner.getValue());
                        tok = scanner.nextToken();
                }
        }
}
