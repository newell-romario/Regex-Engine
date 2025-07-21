package exceptions;

public class InvalidTokenException extends Exception {
        String message;

        public InvalidTokenException(String message)
        {
                super(message);
        }

        public InvalidTokenException(String message, Throwable err)
        {
                super(message, err);
        }
}
