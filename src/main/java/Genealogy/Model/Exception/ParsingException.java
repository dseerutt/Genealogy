package Genealogy.Model.Exception;

/**
 * ParsingException class : used when an exception is found parsing the file
 */
public class ParsingException extends Exception {
    /**
     * ParsingException constructor
     *
     * @param errorMessage
     */
    public ParsingException(String errorMessage) {
        super(errorMessage);
    }
}
