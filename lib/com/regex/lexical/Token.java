package lexical;
public class Token {
        private TokenType type; 
        private int [] values;
        private Range  range;
        private CharacterClass set;
        
        public Token(TokenType type, int [] values, Range range, CharacterClass set)
        {
                this.type   = type;
                this.values = values;
                this.range  = range;
                this.set    = set;
        }
     
        
        public TokenType getTokenType(){return type;}
        public CharacterClass getCharacterClass(){return set;}
        public Range getRange(){return range;}
        public int getValue()
        {       if(type == TokenType.BACK_REFERENCE)
                        return values[1];
                return values[0];
        }
        
        @Override
        public String toString()
        {
                String rep = "";
                if(type == TokenType.CHARACTER_CLASS)
                        rep = set.toString();
                else if(type == TokenType.RANGE)
                        rep = range.toString();
                else{
                        if(values[0] != 0)
                                rep  += Character.toString(values[0]); 
                        if(values[1] != 0)
                                rep  += Character.toString(values[1]);
                        return rep;
                } 
                return rep;
        }
}
