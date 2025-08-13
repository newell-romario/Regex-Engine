import java.util.ArrayList;
import Engine.*;

public class Regex {
        private Engine backTracking;

        public Regex(String pattern) throws Exception
        {
                this(pattern, "");
        }

        public Regex(String pattern, String flags) throws Exception
        { 
                if(flags.length() > 3)
                        throw new Exception("Invalid flags");

                byte [] f = new byte[3];
                f[0] = (byte)(flags.contains("i") == true? 1: 0);
                f[1] = (byte)(flags.contains("U") == true? 1: 0);
                f[2] = (byte)(flags.contains("s") == true? 1: 0);
                backTracking = new BackTracking(pattern, f);
        }


        public boolean match(String text)
        {
                return backTracking.match(text);
        }

        public ArrayList<Match> matchAll(String text)
        {
                return backTracking.allMatches(text);
        }

        public ArrayList<Match> getMatches()
        {
                return backTracking.getMatches();
        }
        
}
