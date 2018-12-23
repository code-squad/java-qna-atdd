package codesquad.domain;

public class QuestionFixture {
    public static final Question TEST_QUESTION = new Question("Title Title", "Content Content", UserFixture.JAVAJIGI_USER);

    public static final Question TEST_QUESTION_SANJIGI = new Question("Sanjigi", "Content Content", UserFixture.SANJIGI_USER);

    public static final Question ERROR_QUESTION = new Question("a", "a", UserFixture.JAVAJIGI_USER);

    public static final Question UPDATE_QUESTION = new Question("Modified Title", "Modified Content", UserFixture.JAVAJIGI_USER);
}
