
public class Scanner{
        private int loc;
        private String pattern;     
        private int [] values;
        private CharacterClass set;
        private Range range;
        private TokenType type;

        public Scanner(String pattern)
        {
                this.loc = 0; 
                this.pattern = pattern;
        }

        public Token nextToken() throws InvalidTokenException
        {
                range  = null;
                set    = null;
                values = new int[2]; 
                type   = TokenType.EOF; 
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
                                        loc = processRangeQuantifier(loc);
                                break;
                                case '[':
                                        type = TokenType.CHARACTER_CLASS;
                                        loc  = processCharacterClass(loc);
                                break; 
                                case '\\':
                                        values[0] = '\\';
                                        type  = TokenType.CHARACTER; 
                                        if(loc+1 < pattern.length());
                                                loc = processEscapeSequence(loc+1);
                                break;
                                default:
                                        type      = TokenType.CHARACTER; 
                                        values[0] = pattern.charAt(loc);
                                        ++loc;
                        }
                return new Token(type, values, range, set);
        }

        public Token peek() throws InvalidTokenException
        {
                Token token = null;
                int peek = loc;
                token = nextToken(); 
                loc = peek;
                return token;
        }

        
        private int processEscapeSequence(int peek) throws InvalidTokenException
        {
                switch(pattern.charAt(peek)){
                        case 'd':
                                type  = TokenType.DIGITS;
                                ++peek;
                        break;
                        case 'D': 
                                type  = TokenType.NON_DIGITS; 
                                ++peek;
                        break;
                        case 's':
                                type  = TokenType.WHITESPACE;
                                ++peek;
                        break; 
                        case 'S':
                                type  = TokenType.NON_WHITESPACE;
                                ++peek;
                        break;
                        case 'w':
                                type = TokenType.WORD;
                                ++peek;
                        break; 
                        case 'W':
                                type = TokenType.NON_WORD;
                                ++peek;    
                        break;
                        case 'b':
                                type = TokenType.WORD_BOUNDARY;
                                ++peek;
                        break; 
                        case 'B':
                                type = TokenType.NON_WORD_BOUNDARY;
                                ++peek;
                        break;
                        case 'A':
                                type = TokenType.STRICT_CARET;
                                ++peek;
                        break;
                        case 'Z':
                                type = TokenType.STRICT_QUESTION_MARK;
                                ++peek;
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
                                values[1] = pattern.charAt(peek);
                                ++peek;
                        break;
                        default:
                                if(Character.isDigit(pattern.charAt(peek)))
                                        peek = processBackReference(peek);
                                else if(Character.isLetter(pattern.charAt(peek)))
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
                values[1] = Integer.parseInt(rep);
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
                type      = types[temp.indexOf(pattern.charAt(peek))];
                values[0] = pattern.charAt(peek); 
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

                set = new CharacterClass();
                if(pattern.charAt(peek) == '^'){
                        set.negate();
                        ++peek;
                }
                
                while(peek < pattern.length() && pattern.charAt(peek) != ']'){
                        switch(pattern.charAt(peek)){
                                case '[':
                                        int rollback = processPosixClass(peek);
                                        /**
                                         * We failed at processing the character class.
                                         * Add the character to the set instead.
                                         */
                                        if(peek == rollback)
                                                set.addToSet(pattern.charAt(peek));
                                        peek = rollback;
                                break;
                                case '\\':
                                        if(peek+1 < pattern.length()){ 
                                                //Testing a valid escape sequence
                                                if(validEscapeSequence(pattern.charAt(peek+1)) == true){
                                                        ++peek;
                                                        set.addToEscape(pattern.charAt(peek));
                                                }
                                                else if(pattern.charAt(peek+1) == ']') {
                                                        ++peek;
                                                        set.addToSet(pattern.charAt(peek));
                                                }else set.addToSet(pattern.charAt(peek));
                                        }
                                break;
                                default:
                                      int range = processRange(peek);
                                      if(range == peek)
                                                set.addToSet(pattern.charAt(peek));
                                        else peek = range;
                        }
                        ++peek;
                }
                
                if(peek == pattern.length())
                        throw new InvalidTokenException("Invalid token: missing closing bracket ]");
                
                int end = ++peek;
                set.setRepresentation(pattern.substring(start, end));
                return end;
        }

        private int processRange(int peek) throws InvalidTokenException
        {
                int rollback = peek; 
                int low; 
                int high;
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
                                        if(high == '\\')          
                                                if(validEscapeSequence(pattern.charAt(peek+1)))
                                                        throw new InvalidTokenException("Invalid token: escape sequence can not be apart of range.");
                                        /*Closing can't be apart of a range unless it's escaped */               
                                        if(high == ']')
                                                return rollback;
                                        if(low > high)
                                                throw new InvalidTokenException("Out of range");
                                        set.addRange(new Range(low, high)); 
                                        rollback = peek; 
                                }
                        }
                }
                return rollback;
        }

        private int processPosixClass(int peek) throws InvalidTokenException
        {
                int rollback = peek;
                if(peek+1 < pattern.length() && pattern.charAt(peek+1) == ':'){
                        int start = peek+=2;
                        int end   = peek+7;
                        while(peek < pattern.length() && peek < end && pattern.charAt(peek) != ':')
                                ++peek;

                        if(peek < pattern.length() && pattern.charAt(peek) == ':'){
                                end = peek;
                                ++peek;
                                if(peek < pattern.length() && pattern.charAt(peek) == ']'){
                                        String posix = pattern.substring(start, end);
                                        if(!validPosixName(posix))
                                                throw new InvalidTokenException("Invalid token: unknown posix class.");
                                        set.addPosix(posix);
                                        rollback = peek;
                                }
                        }
                }
                return rollback;
        }

        private int processRangeQuantifier(int peek) throws InvalidTokenException
        {
                ++peek;
                if(peek < pattern.length() && pattern.charAt(peek) == '}')
                        throw new InvalidTokenException("Invalid token: empty {}.");
                
                String min    = "";
                String max    = "";
                int start     = peek;
                while(peek < pattern.length() && Character.isDigit(pattern.charAt(peek)))
                        ++peek;

                min = pattern.substring(start, peek);
                if(peek < pattern.length()){
                        switch(pattern.charAt(peek)){
                                case ',':
                                        ++peek; 
                                        start = peek;
                                        while(peek < pattern.length() && Character.isDigit(pattern.charAt(peek)))
                                                ++peek;

                                        if(peek < pattern.length() && 
                                        pattern.charAt(peek) != '}')
                                                throw new InvalidTokenException("Invalid token: "+ Character.toString(pattern.codePointAt(peek)));
                                        else if(peek >= pattern.length())
                                                throw new InvalidTokenException("Invalid token: missing }.");
                                        max = pattern.substring(start, peek);
                                break;
                                case '}':
                                        max = min;
                                break;
                                default:        
                                        throw new InvalidTokenException("Invalid token: "+ Character.toString(pattern.codePointAt(peek)));
                        }
                        if(min == "" && max != "")
                                range = new Range(Double.POSITIVE_INFINITY, Double.parseDouble(max));
                        else if(min != "" && max == "")
                                range = new Range(Double.parseDouble(min), Double.POSITIVE_INFINITY);   
                        else range = new Range(Double.parseDouble(min), Double.parseDouble(max));
                }else  throw new InvalidTokenException("Invalid token: {.");

                type = TokenType.RANGE;
                return ++peek;
        }

        private boolean validPosixName(String name)
        {
                String [] posixNames = {"upper", "lower", "alpha", "alnum", 
                                        "digit", "xdigit", "punct","blank",
                                        "space", "cntrl","graph", "print", 
                                        "^upper", "^lower",  "^alpha", "^alnum", 
                                        "^digit", "^xdigit", "^punct", "^blank",
                                        "^space", "^cntrl",  "^graph", "^print"};
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
}
