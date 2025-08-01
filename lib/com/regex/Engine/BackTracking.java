package Engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
                do{
                        switch(cur.getStateType()){
                                case NORMAL:
                                        if(pos < text.length()){
                                                next = cur.move(text.codePointAt(pos));
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
                                default:
                        }

                        if(next != cur.getDeadState())
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
                int start = pos;
                do{
                        switch(cur.getStateType()){
                                case NORMAL:
                                        if(pos < text.length()){
                                                next = cur.move(text.codePointAt(pos));
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
                                default:
                        }
                        
                        if(cur == accept)
                                break;
                        if(next != cur.getDeadState())
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

                
                storeMatches(text);
                return pos;               
        }

        @Override
        public ArrayList<String> allMatches(String text)
        {
                ArrayList<String> matches = new ArrayList<>();
                super.setMatch(matches);
                for(int pos = 0; pos < text.length();){
                        pos = match(text, pos); 
                }

                return matches;
        }
}
