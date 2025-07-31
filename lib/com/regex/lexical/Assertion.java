package lexical;

public enum Assertion {
        END_OF_LINE,
        START_OF_LINE,
        WORD_BOUNDARY,
        NON_WORD_BOUNDARY,
        START_OF_FILE,
        END_OF_FILE,
        ERROR;

        public static Assertion getType(int val)
        {
                Assertion a = ERROR;
                switch(val){
                        case '^':
                                a = START_OF_LINE;
                        break;
                        case '$':
                                a = END_OF_LINE;
                        break;
                        case 'b':
                                a = WORD_BOUNDARY;
                        break; 
                        case 'B':
                                a = NON_WORD_BOUNDARY;
                        break;
                        case 'A':
                                a = START_OF_FILE;
                        break;
                        case 'Z':
                                a = END_OF_FILE; 
                        break;
                        default:
                                a = ERROR;
                }
                return a;
        }

        public static String stringRepresentation(Assertion a)
        {
                switch(a){
                        case START_OF_LINE:
                                return "^";
                        case END_OF_LINE:
                                return "$";
                        case WORD_BOUNDARY:
                                return "\\b";
                        case NON_WORD_BOUNDARY:
                                return "\\B";
                        case START_OF_FILE:
                                return "\\A";
                        case END_OF_FILE:
                                return "\\Z"; 
                        default:
                                a = ERROR;
                }
                return "";
        }
}
