package lexical;
import exceptions.*;

public class Scanner{
        private int loc;
        private String pattern;     
        private int [] values;
        private CharacterClass set;
        private Range range;
        private TokenType type;
        private final static Long NEGATIVE_INFINITY = Double.doubleToLongBits(Double.NEGATIVE_INFINITY);
        private final static Long POSITIVE_INFINITY = Double.doubleToLongBits(Double.POSITIVE_INFINITY);


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
                        case 'D': 
                        case 's':
                        case 'S':
                        case 'w':
                        case 'W':
                                set = new CharacterClass();
                                set.addEscape(Escape.getType(pattern.charAt(peek)));
                                type = TokenType.CHARACTER_CLASS;
                                set.setRepresentation("\\"+pattern.charAt(peek));
                                ++peek;    
                        break;
                        case 'b':
                        case 'B':
                        case 'A':
                        case 'Z':
                                values[1] = pattern.charAt(peek);
                                type = TokenType.ASSERTIONS;
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
                String temp = "|*+?^()$:";
                TokenType [] types = {
                        TokenType.ALTERNATION, TokenType.STAR, TokenType.PLUS, 
                        TokenType.QUESTION_MARK,  TokenType.ASSERTIONS,
                        TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN, 
                        TokenType.ASSERTIONS, TokenType.COLON
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
                                                set.addMembers(pattern.charAt(peek));
                                        peek = rollback;
                                break;
                                case '\\':
                                        if(peek+1 < pattern.length()){ 
                                                //Testing a valid escape sequence
                                                if(validEscapeSequence(pattern.charAt(peek+1)) == true){
                                                        ++peek;
                                                        set.addEscape(Escape.getType(pattern.charAt(peek)));
                                                }
                                                else if(pattern.charAt(peek+1) == ']') {
                                                        ++peek;
                                                        set.addMembers(pattern.charAt(peek));
                                                }else set.addMembers(pattern.charAt(peek));
                                        }
                                break;
                                default:
                                      int range = processRange(peek);
                                      if(range == peek)
                                                set.addMembers(pattern.charAt(peek));
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
                                range = new Range(NEGATIVE_INFINITY, Long.parseLong(max));
                        else if(min != "" && max == "")
                                range = new Range(Long.parseLong(min), POSITIVE_INFINITY);   
                        else range = new Range(Long.parseLong(min), Long.parseLong(max));
                        if(range.getMin() > range.getMax())
                                throw new InvalidTokenException("Out of range");
                }else  throw new InvalidTokenException("Invalid token: {.");

                type = TokenType.RANGE;
                return ++peek;
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
