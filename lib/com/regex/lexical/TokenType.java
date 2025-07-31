package lexical;
public enum TokenType {
        /*METACHARACTERS*/
        ALTERNATION,
        STAR,
        PLUS,
        QUESTION_MARK,
        LEFT_PAREN,
        RIGHT_PAREN,

        /*ASSERTIONS*/
        ASSERTIONS,
        
        /*COLON*/
        COLON,

        /*CHARACTER*/
        CHARACTER,

        /*BACK_REFERENCE*/
        BACK_REFERENCE,

        /*CHARACTER_CLASS */
        CHARACTER_CLASS,

        /*RANGE*/
        RANGE,

        /*ESCAPE*/
        ESCAPE,

        /*OTHER*/
        ERROR,
        EOF;
}
