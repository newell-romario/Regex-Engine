package test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import automaton.BaseState;
import exceptions.*;
import parser.Parser;

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
                                "(^foo$)|(^bar$)", 
                                "^foo$|^bar$",
                                "a(?i:hello)b",
                                "<([[:alpha:]]+)([[:space:]]+[[:alnum:]-]+(=\"[^\"]*\")?)*>.*<\\/\\1>",
                                "([[:alpha:]]-?[[:alpha:]]+)=\"([^\"]*)\"[^>]*\\>.*<\\/\\1\\>",
                                "a{0,0}",
                                "a{0,1}",
                                "a{0,5}"
                        };
                
                for(String pattern : patterns){
                        Parser parser = new Parser(pattern, null); 
                        try{
                                BaseState state =  parser.compile();
                                assertEquals(state.getRegex(), pattern);
                                System.out.println(pattern);
                        }catch(InvalidTokenException e){
                                System.err.println(e.getMessage());
                        }
                }
        }
}
