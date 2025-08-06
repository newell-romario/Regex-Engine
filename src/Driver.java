import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import Engine.Match;

public class Driver {
        public static void main(String [] args)
        {
                String text = ""; 
                Path file = Path.of("text.txt");
                try(BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"))){
                        String line = null; 
                        while((line = reader.readLine()) != null)
                                text+= line+"\n"; /*adding the newline character*/
                }catch(IOException e){System.err.println(e.getMessage());}
                
                try{    
                        System.out.println("");
                        System.out.println("--------------------------------Matches---------------------------------------");
                        Regex regex = new Regex("^[A-Z][a-z]{2,5}\\d{3}[a-zA-Z]{2}(?:[#@$%^&*][a-z0-9]{1,4})?$");
                        ArrayList<Match> matches = regex.matchAll(text);
                        for(Match m: matches){
                                System.out.println(m.getMatch());
                                System.out.println("----------Groups---------");
                                for(int i = 1; i < m.getGroups().size(); ++i){
                                        System.out.println("Group "+ i+ ": "+ m.getGroups().get(i));
                                }
                        }


                }catch(Exception e){}
               
        }
}
