package codesquad.domain;

public class AnswerFixture {
    public static final Answer TEST_ANSWER = new Answer(UserFixture.JAVAJIGI_USER, "Contents Of Answer");

    public static final Answer TEST_ANSWER_SANJIGI = new Answer(UserFixture.SANJIGI_USER, "Contents Of Answer");

    public static final Answer ERROR_ANSWER = new Answer(UserFixture.JAVAJIGI_USER, "er");
}
