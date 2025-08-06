package Engine;
public class Submatch{
                private int [][] matches;
                private int groups;

                public Submatch(int num)
                {
                        groups  = num;       
                        matches = new int[groups][2];
                        for(int i = 0; i < matches.length; ++i){
                                for(int j = 0; j < matches[i].length; ++j)
                                        matches[i][j] = -1;
                        }
                }
                
                public void setMatch(int group, int index, int pos){matches[group][index] = pos;}

                public Submatch copy()
                {
                        Submatch match = new Submatch(groups);
                        int [][] m = match.getMatches();
                        for(int i = 0; i < m.length; ++i){
                                for(int j = 0; j < m[i].length; ++j)
                                        m[i][j] = matches[i][j];
                        }
                        return match;
                }

                public int [][] getMatches(){return matches;}   
                
                public int[] getMatches(int group){return matches[group];}

                public int getGroup(){return groups;}
}
