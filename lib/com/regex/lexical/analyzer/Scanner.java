
public class Scanner{
        private int loc;
        private String pattern;     
        private int value;
        private CharacterClass characterClass;
        private Repetition repetition;
        private TokenType type;

        public Scanner(String pattern)
        {
                this.loc = 0; 
                this.pattern = pattern;
        }

        public Token nextToken() throws InvalidTokenException
        {
                value = 0; 
                type  = TokenType.EOF; 
                if(loc < pattern.length())
                        switch(pattern.charAt(loc)){
                                case '|':
                                case '*':
                                case '+':
                                case '.':
                                case '?':       
                                case '^':
                                case '(':
                                case ')':
                                case '$':
                                case ':':
                                        loc = processMetaCharacters(loc);
                                break;
                                case '{':
                                        loc = processQuantifier(loc);
                                break;
                                case '[':
                                        type = TokenType.CHARACTER_CLASS;
                                        loc  = processCharacterClass(loc);
                                break; 
                                case '\\':
                                        type  = TokenType.CHARACTER; 
                                        value = pattern.charAt(loc); 
                                        if(loc+1 < pattern.length());
                                                loc = processEscapeSequence(loc+1);
                                break;
                                default:
                                        type  = TokenType.CHARACTER; 
                                        value = pattern.charAt(loc);
                                        ++loc;
                        }
                return new Token(type, value);
        }

        
        private int processEscapeSequence(int peek) throws InvalidTokenException
        {
                switch(pattern.charAt(peek)){
                        case 'd':
                                type  = TokenType.DIGITS;
                        break;
                        case 'D': 
                                type  = TokenType.NON_DIGITS; 
                        break;
                        case 's':
                                type  = TokenType.WHITESPACE;
                        break; 
                        case 'S':
                                type  = TokenType.NON_WHITESPACE;
                        break;
                        case 'w':
                                type = TokenType.WORD; 
                        break; 
                        case 'W':
                                type = TokenType.NON_WORD;      
                        break;
                        case '|':
                        case '*':
                        case '+':
                        case '?': 
                        case '^':
                        case '.':
                        case '$':
                        case '{':
                        case '[':
                        case '(':
                        case ')':
                                value = pattern.charAt(peek);
                                ++peek;
                        break;
                        default:
                                value = pattern.charAt(peek);
                                if(Character.isDigit(value))
                                        peek = processBackReference(peek);
                                else if(Character.isLetter(value))
                                        throw new InvalidTokenException("Invalid token: unknown escape sequence.");
                }
                return peek;
        }


        private int processBackReference(int peek)
        {
                type  = TokenType.BACK_REFERENCE; 
                String rep = ""; 
                while(peek < pattern.length() 
                && Character.isDigit(pattern.charAt(peek))){
                        rep += pattern.charAt(peek);
                        ++peek;
                }
                value = Integer.parseInt(rep);
                return peek;
        }

        private int processMetaCharacters(int peek)
        {
                String temp = "|*+.?^()$:";
                TokenType [] types = {
                        TokenType.ALTERNATION, TokenType.STAR, TokenType.PLUS,
                        TokenType.PERIOD, TokenType.QUESTION_MARK,  TokenType.CARET,
                        TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN, TokenType.DOLLAR_SIGN, 
                        TokenType.COLON
                };
                type  = types[temp.indexOf(pattern.charAt(peek))];
                value = pattern.charAt(peek); 
                return ++peek;
        }

        private int processCharacterClass(int peek) throws InvalidTokenException
        {
                int start = peek;
                ++peek;
                if(peek < pattern.length() && pattern.charAt(peek) == ']')
                        throw new InvalidTokenException("Invalid token: [].");
                else if(peek >= pattern.length())
                        throw new InvalidTokenException("Invalid token: [.");

                characterClass = new CharacterClass();
                if(pattern.charAt(peek) == '^'){
                        characterClass.negate();
                        ++peek;
                }
                
                while(peek < pattern.length() && 
                pattern.charAt(peek) != ']'){
                        switch(pattern.charAt(peek)){
                                case '[':
                                        int rollback = processPosixClass(peek);
                                        /**
                                         * We failed at processing the character class.
                                         * Add the character to the set instead.
                                         */
                                        if(peek == rollback)
                                                characterClass.addToSet(pattern.charAt(peek));
                                        peek = rollback;
                                break;
                                case '\\':
                                        if(peek+1 < pattern.length()){ 
                                                //Testing a valid escape sequence
                                                if(validEscapeSequence(pattern.charAt(peek+1)) == true){
                                                        ++peek;
                                                        characterClass.addToEscape(pattern.charAt(peek));
                                                }
                                                else if(pattern.charAt(peek+1) == ']') {
                                                        ++peek;
                                                        characterClass.addToSet(pattern.charAt(peek));
                                                }else characterClass.addToSet(pattern.charAt(peek));
                                        }
                                break;
                                default:

                                      int range = processRange(peek);
                                      if(range == peek)
                                        characterClass.addToSet(pattern.charAt(peek));
                                        else peek = range;
                        }
                        ++peek;
                }
                if(peek == pattern.length())
                        throw new InvalidTokenException("Invalid token: missing closing bracket ]");
                int end = ++peek;
                characterClass.setRepresentation(pattern.substring(start, end));
                return end;
        }

        private int processRange(int peek) throws InvalidTokenException
        {
                int rollback; 
                int low; 
                int high;
                rollback = peek;
                if(peek+1 < pattern.length()){
                        low = pattern.charAt(peek);
                        ++peek;
                        /*Potential range low-high */
                        if(pattern.charAt(peek) == '-'){
                                if(pattern.charAt(rollback-1) == '\\'){
                                        if(validEscapeSequence(pattern.charAt(rollback)))
                                                throw new InvalidTokenException("Invalid token: escape sequence can not be apart of range.");
                                } 
                                if(peek+2 < pattern.length()){
                                        ++peek; 
                                        high = pattern.charAt(peek);
                                        if(pattern.charAt(peek) == '\\')          
                                                if(validEscapeSequence(pattern.charAt(peek+1)))
                                                        throw new InvalidTokenException("Invalid token: escape sequence can not be apart of range.");
                                        if(high == ']')
                                                return rollback;
                                
                                        characterClass.addRange(new Range(low, high)); 
                                        rollback = peek; 
                                }
                        }
                }
                return rollback;
        }

        private int processPosixClass(int peek) throws InvalidTokenException
        {
                int rollback = peek;
                if(peek+1 < pattern.length() && 
                pattern.charAt(peek+1) == ':'){
                        peek+=2;
                        String posix = "";//
                        while(peek < pattern.length() &&
                        pattern.charAt(peek) != ':'){
                                posix+=pattern.charAt(peek);
                                ++peek;
                        }

                        if(peek+1 < pattern.length() &&
                        pattern.charAt(peek) == ':'){
                                ++peek;
                                if(peek < pattern.length()
                                && pattern.charAt(peek) == ']'){
                                        if(!validPosixName(posix))
                                                throw new InvalidTokenException("Invalid token: unknown posix class.");
                                        characterClass.addPosix(posix);
                                        rollback = peek;
                                }
                        }
                }
                return rollback;
        }

   
  

        private int processQuantifier(int peek) throws InvalidTokenException
        {
                ++peek;
                repetition    = new Repetition();
                String min    = "";
                String max    = "";
                if(peek < pattern.length() && pattern.charAt(peek) == '}')
                        throw new InvalidTokenException("Invalid token: empty {}.");
                
                while(peek < pattern.length() 
                && Character.isDigit(pattern.charAt(peek))){
                        min += pattern.charAt(peek);
                        ++peek;
                }

                if(peek < pattern.length()){
                        switch(pattern.charAt(peek)) {
                                case ',':
                                        ++peek; 
                                        if(peek >= pattern.length())
                                                throw new InvalidTokenException("Invalid token: missing }.");
                                        while(peek < pattern.length() 
                                        && Character.isDigit(pattern.charAt(peek))){
                                                max += pattern.charAt(peek);
                                                ++peek;
                                        }
                                        /*We exited the loop before seeing }
                                         *which is always the last character in the
                                         quantifier.
                                         */
                                        if(peek < pattern.length() && 
                                        pattern.charAt(peek) != '}')
                                                throw new InvalidTokenException("Invalid token: "+ Character.toString(pattern.codePointAt(peek)));
                                        else if(peek >= pattern.length())
                                                throw new InvalidTokenException("Invalid token: missing }.");
                                break;
                                case '}':
                                        max = min;
                                break;
                                default:        
                                        throw new InvalidTokenException("Invalid token: "+ Character.toString(pattern.codePointAt(peek)));
                        }
                        if(peek < pattern.length() && pattern.charAt(peek) !='}' 
                        || peek >= pattern.length())
                                 throw new InvalidTokenException("Invalid token: missing }.");

                        if(min != "")
                                repetition.setMin(Double.parseDouble(min));
                
                        if(max != "")
                                repetition.setMax(Double.parseDouble(max));   
                }else  throw new InvalidTokenException("Invalid token: missing }.");

                type = TokenType.REPETITION;
                return ++peek;
        }

        private boolean validPosixName(String name)
        {
                String [] posixNames = {"upper", "lower", "alpha", "alnum", 
                                        "digit", "xdigit", "punct","blank",
                                        "space", "cntrl","graph", "print", 
                                        "^upper", "^lower",  "^alpha", "^alnum", 
                                        "^digit", "^xdigit", "^punct", "^blank",
                                        "^space", "^cntrl",  "^graph", "^print", };
                boolean valid = false;
                for (String posix : posixNames) {
                        valid = name.equalsIgnoreCase(posix);
                        if(valid)
                                break;
                }
                return valid;
        }

        private boolean validEscapeSequence(int val) throws InvalidTokenException
        {
                String escape = "dDwWsS";
                if(Character.isLetter(val))
                        if(escape.indexOf(val) == -1)
                                throw new InvalidTokenException("Invalid token: unknown escape sequence.");
                        else return true; 
                return false;
        }

        public Token peek() throws InvalidTokenException
        {
                Token token = null;
                int peek = loc;
                token = nextToken(); 
                loc = peek;
                return token;
        }

        public CharacterClass getCharacterClass(){return characterClass;}
        public Repetition getRepetition(){return repetition;}
}
