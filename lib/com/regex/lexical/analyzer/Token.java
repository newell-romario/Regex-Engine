public class Token {
        private TokenType type; 
        private int value;

        public Token(TokenType type, int value)
        {
                this.type  = type;
                this.value = value; 
        }
        
        public int getValue(){return value;}
        public TokenType getTokenType(){return type;}
}
