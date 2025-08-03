package lexical;
import exceptions.*;

public class Scanner{
        private int loc;/*current location in the pattern*/
        private String pattern;/*pattern*/     
        private int [] values;/*holds the values of the current token*/
        private CharacterClass set;/*Represents a character set*/
        private Range range;/*Represents a range of the form a-z or {1, 5}*/
        private TokenType type;/*Stores the type of token*/
        private final static Long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);
        private final static Long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);

        public Scanner(String pattern)
        {
                this.loc = 0; 
                this.pattern = pattern;
        }

        /**
         * Reads the next available token. Reading the next available token changes the current state of the scanner.
         * 
         * @return Returns a token.
         * @throws InvalidTokenException
         */
        public Token nextToken() throws InvalidTokenException
        {
                range  = null;
                set    = null;
                values = new int[2]; 
                type   = TokenType.EOF; 
                if(loc < pattern.length())
                        switch(pattern.codePointAt(loc)){
                                case '?':       
                                case '^':
                                case '|':
                                case '*':
                                case '+':
                                case '(':
                                case ')':
                                case '$':
                                case ':':
                                        loc = processMetaCharacters(loc);
                                break;
                                case '.':
                                        type = TokenType.CHARACTER_CLASS;
                                        set  = new CharacterClass();
                                        set.addMembers('\r');
                                        set.addMembers('\n');
                                        set.negate();
                                        set.setRepresentation(".");
                                        ++loc;
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
                                        type  = TokenType.ESCAPE; 
                                        if(loc+1 < pattern.length());
                                                loc = processEscapeSequence(loc+1);
                                break;
                                default:
                                        type      = TokenType.CHARACTER; 
                                        values[0] = pattern.codePointAt(loc);
                                        ++loc;
                        }
                return new Token(type, values, range, set);
        }
        /**
         * Get's the next available token without changing the scanner's internal state.
         * 
         * @return Returns a token.
         * @throws InvalidTokenException
         */
        public Token peek() throws InvalidTokenException
        {
                Token token = null;
                int peek = loc;
                token = nextToken(); 
                loc = peek;
                return token;
        }

        
        /**
         * Processes all escape sequences accepted by our regular expression grammar.
         * 
         * @param peek Location processing should start from.
         * @return Returns the location of the first character after the escape sequence just read.
         * @throws InvalidTokenException
         */
        private int processEscapeSequence(int peek) throws InvalidTokenException
        {
                switch(pattern.codePointAt(peek)){
                        case 'd':
                        case 'D': 
                        case 's':
                        case 'S':
                        case 'w':
                        case 'W':
                                set = new CharacterClass();
                                set.addEscape(Escape.getType(pattern.codePointAt(peek)));
                                type = TokenType.CHARACTER_CLASS;
                                set.setRepresentation("\\"+ Character.toString(pattern.codePointAt(peek)));
                                ++peek;    
                        break;
                        case 'b':
                        case 'B':
                        case 'A':
                        case 'Z':
                                values[1] = pattern.codePointAt(peek);
                                type = TokenType.ASSERTIONS;
                                ++peek;
                        break;
                        case '\\':
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
                                values[1] = pattern.codePointAt(peek);
                                ++peek;
                        break;
                        default:
                                if(Character.isDigit(pattern.codePointAt(peek)))
                                        peek = processBackReference(peek);
                                else if(Character.isLetter(pattern.codePointAt(peek)))
                                        throw new InvalidTokenException("Invalid token: unknown escape sequence.");
                                else{
                                        /*We are an escaped symbol which is meaningless.*/
                                        values[1] = pattern.codePointAt(peek);
                                        ++peek;
                                }
                }
                return peek;
        }

        /**     
         * Processes a back-reference.
         * 
         * @param peek Location processing should start from.
         * @return Returns the location of the first character after the back-reference just read.
         */
        private int processBackReference(int peek)
        {
                type  = TokenType.BACK_REFERENCE; 
                String rep = ""; 
                while(peek < pattern.length() 
                && Character.isDigit(pattern.codePointAt(peek))){
                        rep +=  Character.toString(pattern.codePointAt(peek));
                        ++peek;
                }
                values[1] = Integer.parseInt(rep);
                return peek;
        }

        /**
         * Processes all meta-characters accepted by our regular expression grammar.
         * 
         * @param peek Location processing should start from.
         * @return Returns the location of the first character after the meta-character just read.
         */
        private int processMetaCharacters(int peek)
        {
                String temp = "|*+?^()$:";
                TokenType [] types = {
                        TokenType.ALTERNATION, TokenType.STAR, TokenType.PLUS, 
                        TokenType.QUESTION_MARK,  TokenType.ASSERTIONS,
                        TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN, 
                        TokenType.ASSERTIONS, TokenType.COLON
                };
                
                type = types[temp.indexOf(pattern.codePointAt(peek))];
                if(type != TokenType.ASSERTIONS)
                        values[0] = pattern.codePointAt(peek); 
                else 
                        values[1] = pattern.codePointAt(peek); 
                return ++peek;
        }

        /**
         * Processes a character class.
         * 
         * @param peek Location processing should start from.
         * @return Returns the location of the first character after the character class just read.
         * @throws InvalidTokenException
         */
        private int processCharacterClass(int peek) throws InvalidTokenException
        {
                int start = peek;
                ++peek;
                if(peek < pattern.length() && pattern.codePointAt(peek) == ']')
                        throw new InvalidTokenException("Invalid token: [].");
                else if(peek >= pattern.length())
                        throw new InvalidTokenException("Invalid token: [.");

                set = new CharacterClass();
                if(pattern.codePointAt(peek) == '^'){
                        set.negate();
                        ++peek;
                }
                
                while(peek < pattern.length() && pattern.codePointAt(peek) != ']'){
                        switch(pattern.codePointAt(peek)){
                                case '[':
                                        int rollback = processPosixClass(peek);
                                        /**
                                         * We failed at processing the posix character class.
                                         * Add the character to the set instead.
                                         */
                                        if(peek == rollback)
                                                set.addMembers(pattern.codePointAt(peek));
                                        peek = rollback;
                                break;
                                case '\\':
                                        if(peek+1 < pattern.length()){ 
                                                //Testing a valid escape sequence
                                                if(validEscapeSequence(pattern.codePointAt(peek+1)) == true){
                                                        ++peek;
                                                        set.addEscape(Escape.getType(pattern.codePointAt(peek)));
                                                }
                                                /* ] must be escaped as \\]. Checking for this possibility*/
                                                else if(pattern.codePointAt(peek+1) == ']') {
                                                        ++peek;
                                                        set.addMembers(pattern.codePointAt(peek));
                                                }else set.addMembers(pattern.codePointAt(peek));
                                        }
                                break;
                                default:
                                      int range = processRange(peek);
                                      if(range == peek)
                                                set.addMembers(pattern.codePointAt(peek));
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

        /**     
         * Process a range of the form a-z inside a character class.
         * 
         * @param peek Location processing should start from.
         * @return Returns location of the last character inside the range.
         * @throws InvalidTokenException
         */
        private int processRange(int peek) throws InvalidTokenException
        {
                int rollback = peek; 
                int low; 
                int high;
                if(peek+1 < pattern.length()){
                        low = pattern.codePointAt(peek);
                        ++peek;
                        /*Potential range low-high */
                        if(pattern.codePointAt(peek) == '-'){
                                if(pattern.codePointAt(rollback-1) == '\\'){
                                        if(validEscapeSequence(pattern.codePointAt(rollback)))
                                                throw new InvalidTokenException("Invalid token: escape sequence can not be apart of range.");
                                } 
                                if(peek+2 < pattern.length()){
                                        ++peek; 
                                        high = pattern.codePointAt(peek);
                                        if(high == '\\')          
                                                if(validEscapeSequence(pattern.codePointAt(peek+1)))
                                                        throw new InvalidTokenException("Invalid token: escape sequence can not be apart of range.");
                                        /*Closing ] can't be apart of a range unless it's escaped */               
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

        /**
         * Processes a posix character class.
         * 
         * @param peek Location processing should start from
         * @return Returns the location of the last character in the posix character class.
         * @throws InvalidTokenException
         */
        private int processPosixClass(int peek) throws InvalidTokenException
        {
                int rollback = peek;
                if(peek+1 < pattern.length() && pattern.codePointAt(peek+1) == ':'){
                        int start = peek+=2;
                        int end   = peek+7;
                        while(peek < pattern.length() && peek < end && pattern.codePointAt(peek) != ':')
                                ++peek;

                        if(peek < pattern.length() && pattern.codePointAt(peek) == ':'){
                                end = peek;
                                ++peek;
                                if(peek < pattern.length() && pattern.codePointAt(peek) == ']'){
                                        String posix = pattern.substring(start, end);
                                        Posix p = Posix.getPosix(posix);
                                        if(p == Posix.ERROR)
                                                throw new InvalidTokenException("Invalid token: unknown posix class.");
                                        set.addPosix(p);
                                        rollback = peek;
                                }
                        }
                }
                return rollback;
        }

        /**
         * Process a quantifier of the form {n, m}.
         * 
         * @param peek Location processing should start from
         * @return Returns the first character after the quantifier.
         * @throws InvalidTokenException
         */
        private int processRangeQuantifier(int peek) throws InvalidTokenException
        {
                int beg = peek;
                ++peek;
                if(peek < pattern.length() && pattern.codePointAt(peek) == '}')
                        throw new InvalidTokenException("Invalid token: empty {}.");
                
                String min    = "";
                String max    = "";
                int start     = peek;
                /*Reading minimum*/
                while(peek < pattern.length() && Character.isDigit(pattern.codePointAt(peek)))
                        ++peek;

                min = pattern.substring(start, peek);
                if(peek < pattern.length()){
                        switch(pattern.codePointAt(peek)){
                                case ',':
                                        ++peek; 
                                        start = peek;
                                        
                                        /*Reading the maximum */
                                        while(peek < pattern.length() && Character.isDigit(pattern.codePointAt(peek)))
                                                ++peek;

                                        if(peek < pattern.length() && 
                                        pattern.codePointAt(peek) != '}')
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
                                range = new Range(NEGATIVE_INFINITY, Long.parseLong(max));
                        else if(min != "" && max == "")
                                range = new Range(Long.parseLong(min), POSITIVE_INFINITY);   
                        else range = new Range(Long.parseLong(min), Long.parseLong(max));
                        
                        if(range.getMin() > range.getMax())
                                throw new InvalidTokenException("Out of range");
                        ++peek;
                        range.setRep(pattern.substring(beg, peek));
                }else  throw new InvalidTokenException("Invalid token: {.");

                type = TokenType.RANGE;
                return peek;
        }

        /**
         * Validates escape sequence.
         * 
         * @param val
         * @return Returns true if the escape sequence is valid.
         * @throws InvalidTokenException
         */
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
