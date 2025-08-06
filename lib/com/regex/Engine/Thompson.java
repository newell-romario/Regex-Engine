package Engine;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import automaton.BaseState;
import automaton.SubMatchState;


public class Thompson extends Engine{
        public Thompson(String pattern, byte []  flags)
        {
                super(pattern, flags);
        }


        @Override
        public boolean match(String text)
        {
                ArrayList<String> matches = new ArrayList<>();
                super.setMatch(matches);
                BaseState cur    = super.getStart();
                BaseState accept = super.getAccept();
                Configuration config = new Configuration(0, null, getSubmatches(), 0);
                List<Configuration> configs = createConfigurations(eClosure(cur), config);
                BaseState [] next = null;
                BaseState [] dead = cur.getDeadState();
                int pos = 0; 
                for(;;++pos){
                        ListIterator<Configuration> iter    = configs.listIterator(configs.size());
                        ArrayList<Configuration> nextConfig = new ArrayList<>();
                        while(iter.hasPrevious()){
                                config = iter.previous();
                                cur    = config.state;
                                switch(cur.getStateType()){
                                        case NORMAL:
                                                if(pos < text.length()){
                                                        next = cur.move(text.codePointAt(pos));
                                                        nextConfig.addAll(createConfigurations(next, config));
                                                }
                                        break;
                                        case BASE:
                                                next = cur.move();
                                                createConfigurations(iter, createConfigurations(next, config));
                                                if(cur == accept && pos >= text.length()){
                                                        setSubmatch(config.matches);
                                                        storeMatches(text);
                                                        return true;
                                                }
                                        break;
                                        case ANCHOR:
                                                next = cur.move(text, pos);
                                                createConfigurations(iter, createConfigurations(next, config));
                                        break; 
                                        case SUBMATCH_START:
                                        case SUBMATCH_END:
                                                SubMatchState sub = (SubMatchState) cur;
                                                setMatch(sub.getGroup(), sub.getIndex(), pos, config.matches);
                                                next = cur.getStates();
                                                createConfigurations(iter, createConfigurations(next, config));
                                        break;
                                        case RANGE:
                                        case STAR:
                                        case ALTERNATION:
                                        case QUESTION:
                                                next = cur.move();
                                                Set<BaseState> states = new HashSet<>();
                                                for(BaseState state: next){
                                                        if(state != null){
                                                                Set<BaseState> e = eClosure(state);
                                                                states.addAll(e);
                                                        }
                                                }
                                                for(BaseState state : states)
                                                        iter.add(new Configuration(pos, state, config.matches.copy(), config.pv)); 
                                        break;
                                        default:
                                        break;
                                }
                        }
                        
                        configs = nextConfig;
                        if(configs.size() == 0)
                                break;
                }
                return config.state == accept;
        }

        @Override
        protected int match(String text, int pos)
        {
                ArrayList<Configuration> end = new ArrayList<>();
                BaseState cur     = super.getStart();
                BaseState accept  = super.getAccept();
                Configuration config = new Configuration(pos, accept, getSubmatches(), 0);
                List<Configuration> configs = createConfigurations(eClosure(cur), config);
                Set<BaseState> work = new HashSet<>(eClosure(cur));
                BaseState [] next = null;
                BaseState [] dead = cur.getDeadState();
                int beg = pos;
                Set<BaseState> w = new HashSet<>();
                for(;;++pos){
                        ListIterator<Configuration> iter = configs.listIterator(configs.size());
                        ArrayList<Configuration> nextConfig = new ArrayList<>();
                        while(iter.hasPrevious()){
                                config = iter.previous();
                                cur    = config.state;
                                switch(cur.getStateType()){
                                        case NORMAL:
                                                if(pos < text.length()){
                                                        next = cur.move(text.codePointAt(pos));
                                                        System.out.print(text.charAt(pos));
                                                        for(BaseState s: next){
                                                                if(s != null && !w.contains(s)){
                                                                        nextConfig.add(new Configuration(pos, s, config.matches, 0));
                                                                        w.add(s);
                                                                }
                                                                       
                                                        }
                                                }
                                        break;
                                        case BASE:
                                                next = cur.move();
                                                createConfigurations(iter, work, createConfigurations(next, config));
                                                if(cur == accept){
                                                        config.pos = pos; 
                                                        end.add(config);
                                                }  
                                        break;
                                        case ANCHOR:
                                                next = cur.move(text, pos);
                                                createConfigurations(iter, createConfigurations(next, config));
                                        break; 
                                        case SUBMATCH_START:
                                        case SUBMATCH_END:
                                                SubMatchState sub = (SubMatchState) cur;
                                                setMatch(sub.getGroup(), sub.getIndex(), pos, config.matches);
                                                next = cur.getStates();
                                                createConfigurations(iter, createConfigurations(next, config));
                                        break;
                                        case RANGE:
                                        case STAR:
                                        case ALTERNATION:
                                        case QUESTION:
                                                next = cur.move();
                                                Set<BaseState> states = new HashSet<>();
                                                for(BaseState state: next){
                                                        if(state != null){
                                                                Set<BaseState> e = eClosure(state);
                                                                for(BaseState s: e){
                                                                        if(states.add(s)){
                                                                                iter.add(new Configuration(pos, state, config.matches.copy(), 0));
                                                                                work.add(s);
                                                                        }
                                                                }
                                                        }
                                                }                                                        
                                        break;
                                        default:
                                        break;
                                }
                        }
                        
                        configs = nextConfig;
                        work = new HashSet<>();
                        w = new HashSet<>();
                        for(Configuration c: configs)
                                work.add(c.state);
                        if(configs.size() == 0)
                                break;
                }

                if(!end.isEmpty()){
                        config = end.getLast();
                        setSubmatch(config.matches);
                        storeMatches(text);
                        beg = config.pos;
                }
                        
                return beg;                
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

        private List<Configuration> createConfigurations(Set<BaseState> states, Configuration config)
        {
                ArrayList<Configuration> configurations = new ArrayList<>();
                for(BaseState state: states)
                        configurations.add(new Configuration(0, state, config.matches.copy(), config.pv));
                return configurations;
        }
        
        private List<Configuration> createConfigurations(BaseState [] next, Configuration config)
        {
                ArrayList<Configuration> configs = new ArrayList<>();
                for(BaseState s: next){
                        if(s != null)
                                configs.add(new Configuration(0, s, config.matches.copy(), config.pv));    
                }

                return configs;
        }

        private void createConfigurations(ListIterator<Configuration> list, List<Configuration> elem)
        {
                for(Configuration c: elem){
                        list.add(c);
                }  
        }

        private void createConfigurations(ListIterator<Configuration> list, Set<BaseState> working, List<Configuration> elem)
        {
                for(Configuration c: elem){
                        if(!working.contains(c.state))
                                list.add(c);
                }  
        }

        private List<Configuration> createConfigurations(BaseState [] next, Set<BaseState> work, Configuration config)
        {
                ArrayList<Configuration> configs = new ArrayList<>();
                for(BaseState s: next){
                        if(s != null && !work.contains(s))
                                configs.add(new Configuration(0, s, config.matches.copy(), config.pv));                
                }

                return configs;
        }
}
