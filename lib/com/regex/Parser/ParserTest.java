import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ParserTest {
        @Test
        public void parseValidExpression()
        {
                String [] patterns = {"^(([[:alpha:]]+://)?([[:alnum:]_!~*'().&=+$%-]+)(:([[:alnum:]_!~*'().&=+$%-]+))?@)?([[:alnum:]-]+(\\.[[:alnum:]-]+)*)(:([[:digit:]]{1,5}))?(/([[:alnum:]_!~*'().&=+$%-/]*))?(\\?([[:alnum:]_!~*'().&=+$%-/?]+))?$",
                                "^[[:alnum:]_.+-]+@([[:alnum:]-]+\\.)+[[:alpha:]]{2,}$",
                                "^[[:digit:]]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][[:digit:]]|3[01])$",
                                "^([[:upper:]][[:lower:]]+),[[:space:]]+([[:upper:]][[:lower:]]+):[[:space:]]+([[:digit:]]{4})[-/]([[:digit:]]{2})[-/]([[:digit:]]{2})[[:space:]]+([[:digit:]]{2}):([[:digit:]]{2}):([[:digit:]]{2})$",
                                "^[[:digit:]]{4}-[[:digit:]]{2}-[[:digit:]]{2}T[[:digit:]]{2}:[[:digit:]]{2}:[[:digit:]]{2}$",
                                "^[bcdlps-][r-][w-][xsStT-][r-][w-][xsStT-][r-][w-][xtT-]$",
                                "^#[[:xdigit:]]{6}$", 
                                "^[_[:alpha:]][_[:alnum:]]*$",
                                "^\\([^()]*\\)$|^\\([^()]*\\([^()]*\\)[^()]*\\)$",
                                "(^foo)|(bar$)", 
                                "a(?i:hello)b"
                        };
                for(String pattern : patterns){
                        Parser parser = new Parser(pattern); 
                        try{
                                parser.compile();
                                assertEquals(pattern, parser.getPattern());
                        }catch(Exception e){
                                System.err.println(e.getMessage());
                        }
                }
        }
}
