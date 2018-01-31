package codesquad;

public class QuestionNotFoundException extends NotFoundException {

    public QuestionNotFoundException() {
        super("해당 질문을 찾을 수 없습니다");
    }

    public QuestionNotFoundException(String message) {
        super(message);
    }
}
