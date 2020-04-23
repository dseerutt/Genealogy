package Genealogy.Model.Exception;

/**
 * RepublicanDateOutOfRangeException class : used a RepublicanDate is out of range of the Republican calendar
 */
public class RepublicanDateOutOfRangeException extends Exception {
    /**
     * ParsingException constructor
     *
     * @param errorMessage
     */
    public RepublicanDateOutOfRangeException(String errorMessage) {
        super(errorMessage);
    }
}
