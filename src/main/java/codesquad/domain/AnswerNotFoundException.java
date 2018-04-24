package codesquad.domain;

public class AnswerNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "삭제되었거나 존재하지 않는 답변입니다.";

    public AnswerNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
