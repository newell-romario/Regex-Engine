import java.util.ArrayList;
import Engine.Match;

public class Driver {
        public static void main(String [] args)
        {
                String pattern = "<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)<\\/\\1>";
                String text = "<span class=\"x\">Text</span>";

                try{
                        System.out.println("----------------------------------------------");
                        Regex regex = new Regex(pattern);
                        System.out.println(regex.match(text));
                        ArrayList<Match> matches = regex.getMatches();
                        ArrayList<String> groups = null;
                        int i = 0;
                        for(Match match: matches){
                                System.out.println("Match " + ++i + ":" + match.getMatch());
                                groups = match.getGroups();
                                for(int j = 0; j < groups.size(); ++j){
                                        System.out.println("Submatch" + (j+1) + ":" + groups.get(j));
                                }
                        }
                        System.out.println("----------------------------------------------");
                        pattern = "\\b([A-Za-z]+)\\b(?:\\s+\\b[A-Za-z]+\\b){0,3}\\s+\\1\\b";
                        regex   = new Regex(pattern);
                        text    = "hello hello\nhello there world hello\ntest one two three test\ndog runs fast dog\nready rooster";
                        matches = regex.matchAll(text);
                        i = 0;
                        for(Match match: matches){
                                System.out.println("Match " + ++i + ":" + match.getMatch());
                                groups = match.getGroups();
                                for(int j = 0; j < groups.size(); ++j){
                                        System.out.println("Submatch" + (j+1) + ":" + groups.get(j));
                                }
                        }
                        System.out.println("----------------------------------------------");
                        pattern = "^(?:[A-Za-z0-9]+([-_.][A-Za-z0-9]+)*@[A-Za-z0-9]+(?:[-][A-Za-z0-9]+)*\\.[A-Za-z]{2,6})$";
                        text = "john.doe@example.com\nalice_bob-123@sub-domain.example\nuser123@domain.co\n@b.io";
                        regex   = new Regex(pattern);
                        matches = regex.matchAll(text);
                        i = 0;
                        for(Match match: matches){
                                System.out.println("Match " + ++i + ":" + match.getMatch());
                                groups = match.getGroups();
                                for(int j = 0; j < groups.size(); ++j){
                                        System.out.println("Submatch" + (j+1) + ":" + groups.get(j));
                                }
                        }

                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
}
