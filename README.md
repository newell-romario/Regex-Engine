# Background

Welcome to my implementation of a back-tracking regex engine in Java. I created this project has a portfolio project to demonstrate I can convert theory into a practical implementation. In creating this project I read theory relating to automata, context-free grammars and compiler creation. If one thinks about it, a regex engine is basically compiler that takes a regular expression and translates it to a nondeterministic finite automata which can be used for matching texts. 

I approached this project with no knowledge of how to create a lexer or parser. However, by the end I'm now able to create both a hand-written lexer and top-down recursive descent parser. This regex engine uses both a hand-written lexer and top-down recursive descent parser. The main workings of this regex engine is accomplished by four classes: Scanner.java, Parser.java, StateFactory.java and Backtracking.java. 

# Inner workings

As mentioned, this regex engine uses both a hand-written parser and lexer. The Parser.java class is where all the magic occurs but before discussing this class the Backtracking.java class must be discussed.  The Backtracking.java class controls the parser. It is responsible for receiving a regex pattern from the user and then passes it to the parser. The parser class then receives this regex pattern and passes it to the scanner which breaks the regex into it's to different tokens. The parser then builds the NFA using the StateFactory.java class which is responsible for implementing Thompson's regex NFA algorithm. That's simple overview of the inner workings. More can be garnered by looking at the source code.

# Usage

This regex engine is feature rich although it doesn't support entire PCRE features it does support a subset. It supports basic-regex, extended-regex, back-references, anchors, sub-match tracking, greedy and non-greedy evaluation. The engine offers two main functions for matching that is match and matchAll. The match function performs a match like a how an automaton would perform a match, that is, it only returns true if the entire string matches the regex. This means we can not use this function for substring search. 

Example 1:
```Java
try{
        String pattern = "Romario Newell";
        String text = "Romario Newell";
        Regex regex = new Regex(pattern);
        Sysmtem.out.println(regex.match(text)); //should print true
        ArrayList<Match> matches = regex.getMatches();
        System.out.println(matches.getMatch()); //should print Romario Newell
        text = "Romario Newel"
        Sysmtem.out.println(regex.match(text)); //should print false
}catch(Exception e){Sysmtem.out.println(e.getMessage());}

```
Example 2: 
```Java
String pattern = "<([A-Za-z][A-Za-z0-9]*)\\b[^>]*>(.*?)<\\/\\1>";
String text = "<span class=\"x\">Text</span>";

try{
        Regex regex = new Regex(pattern);
        System.out.println(regex.match(text));
        ArrayList<Match> matches = regex.getMatches();
        ArrayList<String> groups = null;
        int i = 0;
        for(Match match: matches){
                System.out.println("Match " + ++i + ":" + match.getMatch());
                groups = match.getGroups();
                for(int j = 0; j < groups.size(); ++j){
                        System.out.println("Submatch " + (j+1) + ":" + groups.get(j));
                }
        }

        /*      The above snipet should output the below:
         *      true
         *      Match 1:<span class="x">Text</span>
         *      Submatch 1:span
         *      Submatch 2:Text
        */

        pattern = "\\b([A-Za-z]+)\\b(?:\\s+\\b[A-Za-z]+\\b){0,3}\\s+\\1\\b";
        regex   = new Regex(pattern);
        text    = "hello hello\nhello there world hello\ntest one two three test\ndog runs fast dog\nready rooster";
        matches = regex.matchAll(text);
        i = 0;
        for(Match match: matches){
                System.out.println("Match " + ++i + ":" + match.getMatch());
                groups = match.getGroups();
                for(int j = 0; j < groups.size(); ++j){
                        System.out.println("Submatch " + (j+1) + ":" + groups.get(j));
                }
        }

        /*
        *
        *        Match 1:hello hello
        *        hello
        *        Submatch 1:hello
        *        Match 2:test one two three test
        *        Submatch 1:test
        *        Match 3:dog runs fast dog
        *        Submatch 1:dog
        */
} catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
}
```
Additionally, we can get the submatches by calling the getMatches function which returns a list of match objects. A match object represents a match and all the related submatches that accompany that match. To get the entire match you can call the getMatch function on a match object. To get the submatch we can getGroups function which returns all the submatches. We can compare the results of our regex engine to that regex101.com.

# Features 
This regex engine supports repetition, alternation, ascii character classes, character classes and backreferences. The above code snipet is indicative of the syntax accepted by our regex engine.
# Pitfalls

This is a backtracking implementation which means it's susceptible to catastrophic backtracking. We didn't take any precautions to prevent this. Additionally we didn't implement a full regex engine we  chose to leave out look-around assertions similar to how RE2 engine leaves them out. Our reason for leaving them out is not performance based but because this is only a portfolio project I didn't want it to get to complex. Adding look-assertions is future plan. We don't support Unicode. We only support ASCII. 