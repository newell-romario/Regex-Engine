package automaton;
import lexical.Assertion;
import lexical.Posix;

public class AnchorState extends NormalState{
        private Assertion anchor;
        public AnchorState(Assertion assertion)
        {
                super(StateType.ANCHOR, null);
                anchor = assertion;
        }

        public BaseState [] assertion(String pattern, int pos)
        {
                switch(anchor){
                        case START_OF_LINE:
                                if(pos == 0 || pattern.charAt(pos-1) == '\n' || pattern.charAt(pos-1) == '\r')
                                        return super.getStates();  
                                return super.getDeadState();
                        case END_OF_LINE:
                                if(pos == pattern.length())
                                        return super.getStates();
                                if(pattern.charAt(pos) == '\n' || pattern.charAt(pos) == '\r')
                                        return super.getStates();
                                return super.getDeadState();
                        case WORD_BOUNDARY:
                                if(pos-1 == 0 && Posix.asciiIsWord(pattern.charAt(pos)))
                                        return super.getStates();
                                if(pos+1 == pattern.length() && Posix.asciiIsWord(pattern.charAt(pos)))
                                        return super.getStates();
                                if(!Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(pos+1 < pattern.length() &&
                                         Posix.asciiIsWord(pattern.charAt(pos+1)))
                                                return super.getStates();
                                if(Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(pos+1 < pattern.length() &&
                                                !Posix.asciiIsWord(pattern.charAt(pos+1)))
                                                return super.getStates();
                                return super.getDeadState();
                        case NON_WORD_BOUNDARY:
                                if(!(pos-1 == 0 && Posix.asciiIsWord(pattern.charAt(pos))))
                                        return super.getStates();
                                if(!(pos+1 == pattern.length() && Posix.asciiIsWord(pattern.charAt(pos))))
                                        return super.getStates();
                                if(Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(!(pos+1 < pattern.length() &&
                                         Posix.asciiIsWord(pattern.charAt(pos+1))))
                                                return super.getStates();
                                if(!Posix.asciiIsWord(pattern.charAt(pos)))
                                        if(!(pos+1 < pattern.length() &&
                                                !Posix.asciiIsWord(pattern.charAt(pos+1))))
                                                return super.getStates();
                        return super.getDeadState();
                        case START_OF_FILE:
                                if(pos == 0)
                                        return super.getStates();
                                return super.getDeadState();
                        case END_OF_FILE:
                                if(pos == pattern.length())
                                        return super.getStates();
                                return super.getDeadState();
                        default:
                        break;
                }
                return super.getDeadState();
        }
}
