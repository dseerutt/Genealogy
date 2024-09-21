package Genealogy.Model.Exception;

/**
 * URLException class : used when an exception is found connecting to internet
 */
public class URLException extends Exception {

    /**
     * URLException constructor
     *
     * @param message
     */
    public URLException(String message) {
        super(message);
    }
}
