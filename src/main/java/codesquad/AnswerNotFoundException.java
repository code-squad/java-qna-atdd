package codesquad;

public class AnswerNotFoundException extends NotFoundException {

    private static final String MESSAGE = "해당 답변을 찾을 수 없습니다";

    public AnswerNotFoundException() {
        super(MESSAGE);
    }

    public AnswerNotFoundException(long id) {
        super(MESSAGE, id);
    }
}
