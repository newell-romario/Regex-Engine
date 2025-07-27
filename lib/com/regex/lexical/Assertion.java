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
}
