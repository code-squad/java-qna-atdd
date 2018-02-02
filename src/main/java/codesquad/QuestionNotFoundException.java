package codesquad;

public class QuestionNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 질문을 찾을 수 없습니다";

    public QuestionNotFoundException() {
        super(MESSAGE);
    }

    public QuestionNotFoundException(long id) {
        super(MESSAGE, id);
    }
}
