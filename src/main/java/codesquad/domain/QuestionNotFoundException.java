package codesquad.domain;

public class QuestionNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "삭제되었거나 존재하지 않는 질문입니다.";

    public QuestionNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
