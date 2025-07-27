package lexical;

public enum Escape {
        DIGITS,
        NON_DIGITS,
        WHITESPACE,
        NON_WHITESPACE,
        WORD,
        NON_WORD,
        ERROR;

        public static Escape getType(int val)
        {
                Escape  e = ERROR;
                switch(val){
                        case 'd':
                                e = DIGITS;
                        break;
                        case 'D':
                                e = NON_DIGITS;
                        break;
                        case 'w':
                                e = WORD;
                        break;
                        case 'W':
                                e = NON_WORD;
                        break;
                        case 's': 
                                e = WHITESPACE;
                        break;
                        case 'S':
                                e = NON_WHITESPACE;
                }
                return e;
        }

        public static boolean digit(int val)
        {
                return Posix.asciiIsDigit(val);
        }


        public static boolean nonDigit(int val)
        {
                return !digit(val);
        }


        public static boolean word(int val)
        {
                return Posix.asciiIsWord(val);
        }

        public static boolean nonWord(int val)
        {
                return !word(val);
        }

        public static boolean space(int val)
        {
                return Posix.asciiIsSpace(val);
        }

        public static boolean nonSpace(int val)
        {
                return !space(val);
        }

        public static boolean evaluate(int val, Escape e)
        {
                switch(e){
                        case DIGITS:
                                return digit(val);
                        case NON_DIGITS:
                                return nonDigit(val);
                        case WORD:
                                return word(val);
                        case NON_WORD:
                                return nonWord(val);
                        case WHITESPACE: 
                                return space(val);
                        case NON_WHITESPACE:
                                return nonSpace(val);
                        default:
                }
                return false;
        }

}
