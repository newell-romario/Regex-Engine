package Engine;
import java.util.ArrayList;


public class Match {
        private ArrayList<String> groups;
        private String match;

        public Match(Submatch matches, String text)
        {
                match = null;
                int [][] m = matches.getMatches();
                groups = new ArrayList<>();
                for(int i = 0; i < m.length;++i){
                        if(m[i][0] <  m[i][1])
                                        groups.add(text.substring(m[i][0], m[i][1]));
                        else if (m[i][0] ==  m[i][1] && m[i][0] != -1)
                                        groups.add(new String());  
                        if(match == null)
                                        match = groups.getLast();         
                }
                if(!groups.isEmpty())
                        groups.remove(0);
        }

        public  String getMatch(){return match;}
        public  ArrayList<String> getGroups(){return groups;}
}
