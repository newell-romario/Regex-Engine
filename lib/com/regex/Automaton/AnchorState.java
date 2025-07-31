package automaton;
import lexical.Assertion;

public class AnchorState extends BaseState{
        private Assertion anchor;
        
        public AnchorState(Assertion assertion)
        {
                super(StateType.ANCHOR);
                anchor = assertion;
        }
}
