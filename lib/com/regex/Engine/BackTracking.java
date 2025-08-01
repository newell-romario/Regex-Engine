package Engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import automaton.BackReferenceState;
import automaton.BaseState;
import automaton.SubMatchState;

public class BackTracking extends Engine{
        
        public BackTracking(String pattern, byte [] flags)
        {
                super(pattern, flags);
        }

        @Override
        public boolean match(String text)
        {                
                ArrayList<String> matches = new ArrayList<>();
                super.setMatch(matches);
                ArrayDeque<Configuration>stack = new ArrayDeque<>();
                BaseState cur     = super.getStart();
                BaseState accept  = super.getAccept();
                BaseState [] next = null;
                Configuration config = null;
                int pos = 0;
                BaseState [] dead = cur.getDeadState();
                do{
                        switch(cur.getStateType()){
                                case NORMAL:
                                        if(pos < text.length()){
                                                next = cur.move(text.codePointAt(pos));
                                                if(next != dead)
                                                        ++pos;
                                        }else next = cur.move();
                                break;
                                case BASE:
                                        next   = cur.move();
                                break;
                                case ANCHOR:
                                        next = cur.move(text, pos);
                                break;
                                case SUBMATCH_START:
                                case SUBMATCH_END:
                                        SubMatchState sub = (SubMatchState)cur;
                                        setMatch(sub.getGroup(), sub.getIndex(), pos);
                                        next = cur.getStates();
                                break;
                                case RANGE:
                                case STAR:
                                case ALTERNATION:
                                case QUESTION:
                                        next   = cur.move();
                                        config = new Configuration(pos, next[1], getSubmatches().copy());
                                        stack.push(config);
                                break;
                                case BACK_REFERENCE:
                                        BackReferenceState ref = (BackReferenceState) cur;
                                        int [] m = super.getSubmatches().getMatches(ref.getGroup());
                                        String t = text.substring(m[0], m[1]);
                                        if(pos < text.length() && t.equals(text.substring(pos, pos+t.length()))){
                                                next = ref.getStates();
                                                pos = pos + t.length();
                                        }else if(pos == text.length())
                                                next = ref.getStates();
                                        else next = dead;
                                break;
                                default:
                        }

                        if(next != dead)
                                cur = next[0]; 
                        else{
                                if(!stack.isEmpty()){
                                        config = stack.pop();
                                        cur    = config.state;
                                        pos    = config.pos;
                                        setSubmatch(config.matches);
                                }else cur = null;
                        }
                        if(cur == accept && pos == text.length())
                                break;
                }while(cur != null);
                
                if(cur == accept)
                        storeMatches(text);
                return cur == accept; 
        }




        @Override
        public int match(String text, int pos)
        {
                ArrayDeque<Configuration>stack = new ArrayDeque<>();
                BaseState cur     = super.getStart();
                BaseState accept  = super.getAccept();
                BaseState [] next = null;
                Configuration config = null;
                BaseState [] dead = cur.getDeadState();
                do{
                        switch(cur.getStateType()){
                                case NORMAL:
                                        if(pos < text.length()){
                                                next = cur.move(text.codePointAt(pos));
                                                if(next != dead)
                                                        ++pos;
                                        }else next = cur.move();
                                break;
                                case BASE:
                                        next   = cur.move();
                                break;
                                case ANCHOR:
                                        next = cur.move(text, pos);
                                break;
                                case SUBMATCH_START:
                                case SUBMATCH_END:
                                        SubMatchState sub = (SubMatchState)cur;
                                        setMatch(sub.getGroup(), sub.getIndex(), pos);
                                        next = cur.getStates();
                                break;
                                case RANGE:
                                case STAR:
                                case ALTERNATION:
                                case QUESTION:
                                        next   = cur.move();
                                        config = new Configuration(pos, next[1], getSubmatches().copy());
                                        stack.push(config);
                                break;
                                case BACK_REFERENCE:
                                        BackReferenceState ref = (BackReferenceState) cur;
                                        int [] m = super.getSubmatches().getMatches(ref.getGroup());
                                        String t = text.substring(m[0], m[1]);
                                        if(pos < text.length() && t.equals(text.substring(pos, pos+t.length()))){
                                                next = ref.getStates();
                                                pos = pos + t.length();
                                        }else if(pos == text.length())
                                                next = ref.getStates();
                                        else next = dead;      
                                break;
                                default:
                                break;
                        }
                        
                        if(cur == accept)
                                break;
                        if(next != dead)
                                cur = next[0]; 
                        else{
                                if(!stack.isEmpty()){
                                        config = stack.pop();
                                        cur    = config.state;
                                        pos    = config.pos;
                                        setSubmatch(config.matches);
                                }else cur = null;
                        }
                        if(cur == accept && pos == text.length())
                                break;
                }while(cur != null);

                
                if(cur == accept)
                        storeMatches(text);
                return pos;               
        }

        @Override
        public ArrayList<String> allMatches(String text)
        {
                ArrayList<String> matches = new ArrayList<>();
                super.setMatch(matches);
                int cur = 0;
                for(int pos = 0; pos < text.length()+1;){
                        cur = match(text, pos);
                        if(pos == cur)
                                ++pos;
                        else pos = cur; 
                }

                return matches;
        }
}
