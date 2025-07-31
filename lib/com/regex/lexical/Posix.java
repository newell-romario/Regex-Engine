package lexical;
import java.util.Hashtable;

public enum Posix{
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
        NEG_WORD,
        NEG_XDIGIT,
        NEG_ASCII,
        PERIOD,
        ERROR;

        private static final Hashtable<String, Posix> set = new Hashtable<>();

        static{
                setPosixValues();
        }


        public static boolean asciiIsLower(int val)
        {
                return val >= 'a' && val <= 'z';
        }

        public static boolean asciiIsUpper(int val)
        {
                return val >= 'A' && val <= 'Z';
        }

        public static boolean asciiIsAlpha(int val)
        {
                return asciiIsLower(val) || asciiIsUpper(val);
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

        public static boolean asciiIsAscii(int val)
        {
                return val >= 0 && val <= 127;
        }

        public static boolean asciiIsCntrl(int val)
        {
                return !asciiIsPrint(val);
        }

        private static void setPosixValues()
        {
                if(!set.isEmpty())
                        return;
                        
                String [] names = {"upper", "lower", "alpha", "alnum", 
                                        "digit", "xdigit", "punct","blank",
                                        "space", "cntrl","graph", "print", 
                                        "^upper", "^lower",  "^alpha", "^alnum", 
                                        "^digit", "^xdigit", "^punct", "^blank",
                                        "^space", "^cntrl",  "^graph", "^print", "ascii", "^ascii"};
                Posix [] vals = {UPPER, LOWER, ALPHA, ALNUM, DIGIT, XDIGIT, PUNCT,
                                 BLANK, SPACE, CNTRL, GRAPH, PRINT, NEG_UPPER, 
                                 NEG_LOWER, NEG_ALPHA, NEG_ALNUM, NEG_DIGIT, 
                                 NEG_XDIGIT, NEG_PUNCT,NEG_SPACE,NEG_BLANK, 
                                 NEG_CNTRL, NEG_GRAPH, NEG_PRINT, ASCII, NEG_ASCII       
                };

                
                for(int i = 0; i < names.length; ++i){
                        set.put(names[i], vals[i]);
                }
        }

        public static Posix getPosix(String name)
        {
                Posix p = ERROR;
             
                if(set.containsKey(name))
                        p = set.get(name);

                return p;
        }

        public static boolean evaluate(int val, Posix p)
        {
              switch(p){
                case ALNUM:
                        return asciiIsAlnum(val);
                case ALPHA:
                        return asciiIsAlpha(val);
                case ASCII:
                        return asciiIsAscii(val);
                case BLANK:
                        return asciiIsBlank(val);
                case CNTRL:
                        return asciiIsCntrl(val);
                case DIGIT:
                        return asciiIsDigit(val);
                case GRAPH:
                        return asciiIsDigit(val);
                case LOWER:
                        return asciiIsLower(val);
                case PRINT:
                        return asciiIsPrint(val);
                case PUNCT:
                        return asciiIsPunct(val);
                case SPACE:
                        return asciiIsSpace(val);
                case UPPER:
                        return asciiIsUpper(val);
                case WORD:
                        return asciiIsWord(val);
                case XDIGIT:
                        return asciiIsXdigit(val);
                case NEG_ALNUM:
                        return !asciiIsAlnum(val);
                case NEG_ALPHA:
                        return !asciiIsAlpha(val);
                case NEG_ASCII:
                        return !asciiIsAscii(val);
                case NEG_BLANK:
                        return !asciiIsBlank(val);
                case NEG_CNTRL:
                        return !asciiIsCntrl(val);
                case NEG_DIGIT:
                        return !asciiIsDigit(val);
                case NEG_GRAPH:
                        return !asciiIsGraph(val);
                case NEG_LOWER:
                        return !asciiIsLower(val);
                case NEG_PRINT:
                        return !asciiIsPrint(val);
                case NEG_PUNCT:
                        return !asciiIsPunct(val);
                case NEG_SPACE:
                        return !asciiIsSpace(val);
                case NEG_UPPER:
                        return !asciiIsUpper(val);
                case NEG_WORD:
                        return !asciiIsWord(val);
                case NEG_XDIGIT:
                        return !asciiIsXdigit(val);
                default:
                        break;
              }
              return false;
        }
}
