public enum PosixClass{
        ALNUM,
        ALPHA,
        ASCII,
        BLANK,
        CNTRL,
        DIGIT,
        GRAPH,
        LOWER,
        PRINT, 
        PUNCT,
        SPACE,
        UPPER,
        WORD,
        XDIGIT,
        NEG_ALNUM,
        NEG_ALPHA,
        NEG_BLANK,
        NEG_CNTRL,
        NEG_DIGIT,
        NEG_GRAPH,
        NEG_LOWER,
        NEG_PRINT,
        NEG_PUNCT,
        NEG_SPACE,
        NEG_UPPER,
        NEG_WORD;

        public static boolean asciiIsLower(int val)
        {
                return val >= 'a' && val <= 'z';
        }

        public static boolean asciIsUpper(int val)
        {
                return val >= 'A' && val <= 'Z';
        }

        public static boolean asciiIsAlpha(int val)
        {
                return asciiIsLower(val) || asciIsUpper(val);
        }

        public static boolean asciiIsDigit(int val)
        {
                return val >= '0' && val <= '9';
        }

        public static boolean asciiIsAlnum(int val)
        {
                return asciiIsAlpha(val) || asciiIsDigit(val);
        }

        public static boolean asciiIsSpace(int val)
        {
                switch(val){
                        case ' ':
                        case '\n':
                        case '\t':
                        case '\r':
                        case '\f':
                                return true;
                        default:
                                if(val == 11)/*vertical tab */
                                        return true;
                                return false;
                }
        }

        public static boolean asciiIsBlank(int val)
        {
                switch(val){
                        case '\t':
                        case ' ':
                                return true;
                        default:
                                return false;
                }
        }

        public static boolean asciiIsWord(int val)
        {
                return asciiIsAlnum(val) || val == '_';
        }

        public static boolean asciiIsXdigit(int val)
        {
              return  (val >= 'A' && val <= 'F') || (val >= 'a' && val <= 'f') || asciiIsDigit(val);
        }

        public static boolean asciiIsPunct(int val)
        {
               String punct = "!\"\\#$%&'()*+,-./:;<=>?@[]^_`{|}~";
               return punct.indexOf(val) != -1;
        }


        public static boolean asciiIsPrint(int val)
        {
                return val >= ' ' && val <= '~';
        }

        public static boolean asciiIsGraph(int val)
        {
                return val > ' ' && val <= '~';
        }

}
