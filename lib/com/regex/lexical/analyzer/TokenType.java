public enum TokenType {
        /*METACHARACTERS*/
        ALTERNATION,
        STAR,
        PLUS,
        PERIOD,
        QUESTION_MARK,
        CARET,
        DOLLAR_SIGN,
        LEFT_PAREN,
        RIGHT_PAREN,

        /*ESCAPE SEQUENCE*/
        DIGITS,
        NON_DIGITS,
        WHITESPACE,
        NON_WHITESPACE,
        WORD,
        NON_WORD,

        /*CHARACTER*/
        CHARACTER,

        /*BACK_REFERENCE*/
        BACK_REFERENCE,

        /*CHARACTER_CLASS */
        CHARACTER_CLASS,

        /*REPETITION*/
        REPETITION,

        /*OTHER*/
        ERROR,
        EOF;
}
