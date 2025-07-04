public class Scanner{
        /*regex string*/
        private String regex;

        /*start of current token*/
        private int pos;

        /*Back reference*/
        private int reference = -1;

        /*Token value*/
        private char value;
        public Scanner(String regex)
        {
                this.regex = regex; 
                this.pos = 0;
        }

        public int getPos(){return pos;}

        public Token nextToken()
        {
                value = '\0';
                reference = -1; /*set reference to -1 in case we're reading a reference*/
                Token token = Token.EOF;
                if(pos >= regex.length())
                        return token;
                
                switch(regex.charAt(pos)){
                        case '+':
                                token = Token.PLUS; 
                        break;

                        case '*':
                                token = Token.STAR; 
                        break; 

                        case '|':
                                token = Token.ALTERNATION; 
                        break;

                        case '.':
                                token = Token.PERIOD;
                        break; 

                        case '^':
                                token = Token.CARET; 
                        break; 

                        case '?':
                                token = Token.QUESTION_MARK; 
                        break;

                        case '$':
                                token = Token.DOLLAR_SIGN; 
                        break;

                        case '(':
                                token = Token.LEFT_PAREN; 
                        break; 

                        case ')':
                                token = Token.RIGHT_PAREN; 
                        break; 

                        case '{':
                                token = Token.LEFT_BRACES; 
                        break; 

                        case '}':
                                token = Token.RIGHT_BRACES;
                        break;
                        case '[':
                                token = Token.LEFT_BRACKET; 
                        break; 

                        case ']':
                                token = Token.RIGHT_BRACKET; 
                        break; 


                        case '-':
                                token = Token.MINUS; 
                        break; 

                        case '\0':
                                token = Token.CHARACTER; 
                        break; 

                        case '\n':
                                token = Token.CHARACTER; 
                        break;

                        case '\f':
                                token = Token.CHARACTER; 
                        break;

                        case '\t':
                                token = Token.CHARACTER; 
                        break;

                        case '\"':
                        case '\'':
                                token = Token.CHARACTER; 
                        break; 

                        case '\r':
                                token = Token.CHARACTER;
                        break;

                        case '\\':
                                token = escapeSequence();
                        break;

                        default:
                                token = Token.CHARACTER;
                        break;
                }
                value = regex.charAt(pos);
                ++pos;
                return token;
        }

        private Token escapeSequence()
        {
                Token token = Token.CHARACTER;
                if(pos + 1 < regex.length()){
                        /*finding the escape characters*/
                        switch(regex.charAt(pos + 1)){
                                case 'w':
                                        token = Token.WORD; 
                                        ++pos;
                                break; 

                                case 'W':
                                        token = Token.NON_WORD;
                                        ++pos;
                                break; 

                                case 's':
                                        token = Token.WHITESPACE; 
                                        ++pos;
                                break; 

                                case 'S':
                                        token = Token.NON_WHITESPACE;
                                        ++pos;
                                break; 

                                case 'd':
                                        token = Token.DIGITS; 
                                        ++pos;
                                break; 

                                case 'D':
                                        token = Token.NON_DIGITS;
                                        ++pos;
                                break;

                                case '^':
                                        token = Token.CHAR_CARET;
                                        ++pos;
                                break;

                                case '.':
                                        token = Token.CHAR_PERIOD;
                                        ++pos;
                                break; 

                                case '$':
                                        token = Token.CHAR_DOLLAR_SIGN;
                                        ++pos;
                                break;

                                case '|':
                                        token = Token.CHAR_ALTERNATION; 
                                        ++pos;
                                break;

                                case '(': 
                                        token = Token.CHAR_LEFT_PAREN;
                                        ++pos;
                                break; 

                                case ')':
                                        token = Token.CHAR_RIGHT_PAREN; 
                                        ++pos;
                                break; 
                                
                                case '[':
                                        token = Token.CHAR_LEFT_BRACKET; 
                                        ++pos; 
                                break; 

                                case ']': 
                                        token = Token.CHAR_RIGHT_BRACKET;
                                        ++pos; 
                                break; 

                                case '*':
                                        token = Token.CHAR_STAR;
                                        ++pos; 
                                break; 
                                
                                case '+':
                                        token = Token.CHAR_PLUS;
                                        ++pos; 
                                break; 

                                case '?':
                                        token = Token.CHAR_QUESTION_MARK; 
                                        ++pos; 
                                break; 

                                case '{':
                                        token = Token.CHAR_LEFT_BRACES;
                                        ++pos; 
                                break; 

                                case '}':
                                        token = Token.CHAR_RIGHT_BRACES;
                                        ++pos;
                                break;

                                case '-':
                                        token = Token.CHAR_MINUS; 
                                        ++pos;
                                break;
                                default:
                                        int peek = pos + 1; 
                                        if(Character.isDigit(regex.charAt(peek))){
                                                String digits = "";
                                                   while(peek < regex.length() && 
                                                   Character.isDigit(regex.charAt(peek))){
                                                        digits += regex.charAt(peek);
                                                        peek++;
                                                   } 
                                                reference = Integer.parseInt(digits);       
                                                token = Token.BACKREFERENCES;
                                                /*We add 1 to pos in the nextToken() function. 
                                                 * We substract one from the current pos so that we 
                                                 * don't miss a token.
                                                 * 
                                                */
                                                pos = --peek;        
                                        }
                        }
                }
                return token;
        }

        public int getReference(){return reference;}
        public char getValue(){return value;}
}
