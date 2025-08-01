package automaton;
import lexical.Assertion;
import lexical.Posix;

public class AnchorState extends BaseState{
        private Assertion anchor;
        
        public AnchorState(Assertion assertion)
        {
                super(StateType.ANCHOR);
                anchor = assertion;
        }
        
        @Override
        public BaseState [] move(){return super.getDeadState();}

        @Override
        public BaseState [] move(String text, int pos)
        {
                switch(anchor){
                        case START_OF_LINE:
                                if(pos == 0 || text.charAt(pos-1) == '\n' || text.charAt(pos-1) == '\r')
                                        return super.getStates();  
                                return super.getDeadState();
                        case END_OF_LINE:
                                if(pos == text.length())
                                        return super.getStates();
                                if(text.charAt(pos) == '\n' || text.charAt(pos) == '\r')
                                        return super.getStates();
                                return super.getDeadState();
                        case WORD_BOUNDARY:
                                if(pos-1 == 0 && Posix.asciiIsWord(text.charAt(pos)))
                                        return super.getStates();
                                if(pos+1 == text.length() && Posix.asciiIsWord(text.charAt(pos)))
                                        return super.getStates();
                                if(!Posix.asciiIsWord(text.charAt(pos)))
                                        if(pos+1 < text.length() &&
                                         Posix.asciiIsWord(text.charAt(pos+1)))
                                                return super.getStates();
                                if(Posix.asciiIsWord(text.charAt(pos)))
                                        if(pos+1 < text.length() &&
                                                !Posix.asciiIsWord(text.charAt(pos+1)))
                                                return super.getStates();
                                return super.getDeadState();
                        case NON_WORD_BOUNDARY:
                                if(!(pos-1 == 0 && Posix.asciiIsWord(text.charAt(pos))))
                                        return super.getStates();
                                if(!(pos+1 == text.length() && Posix.asciiIsWord(text.charAt(pos))))
                                        return super.getStates();
                                if(Posix.asciiIsWord(text.charAt(pos)))
                                        if(!(pos+1 < text.length() &&
                                         Posix.asciiIsWord(text.charAt(pos+1))))
                                                return super.getStates();
                                if(!Posix.asciiIsWord(text.charAt(pos)))
                                        if(!(pos+1 < text.length() &&
                                                !Posix.asciiIsWord(text.charAt(pos+1))))
                                                return super.getStates();
                        return super.getDeadState();
                        case START_OF_FILE:
                                if(pos == 0)
                                        return super.getStates();
                                return super.getDeadState();
                        case END_OF_FILE:
                                if(pos == text.length())
                                        return super.getStates();
                                return super.getDeadState();
                        default:
                        break;
                }
                return super.getDeadState();
        }

        @Override
        public int hashCode()
        {
                int result = super.hashCode();
                result = 31*result + anchor.hashCode();
                return result;
        }

        @Override
        public AnchorState copy()
        {
                AnchorState a = new AnchorState(anchor);
                a.setBase(this);
                return a;
        }


}
