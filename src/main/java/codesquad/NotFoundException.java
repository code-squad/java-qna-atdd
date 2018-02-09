package codesquad;

/**
 * Created by hoon on 2018. 2. 9..
 */
public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }
}
