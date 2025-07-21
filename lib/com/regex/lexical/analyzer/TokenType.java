public enum TokenType {
        /*METACHARACTERS*/
        ALTERNATION,
        STAR,
        PLUS,
        PERIOD,
        COLON,
        QUESTION_MARK,
        LEFT_PAREN,
        RIGHT_PAREN,


        /*ESCAPE SEQUENCE*/
        DIGITS,
        NON_DIGITS,
        WHITESPACE,
        NON_WHITESPACE,
        WORD,
        NON_WORD,

        /*ASSERTIONS*/
        DOLLAR_SIGN,
        CARET,
        WORD_BOUNDARY,
        NON_WORD_BOUNDARY,
        STRICT_QUESTION_MARK,
        STRICT_CARET,
        

        /*CHARACTER*/
        CHARACTER,

        /*BACK_REFERENCE*/
        BACK_REFERENCE,

        /*CHARACTER_CLASS */
        CHARACTER_CLASS,

        /*RANGE*/
        RANGE,

        /*OTHER*/
        ERROR,
        EOF;
}
