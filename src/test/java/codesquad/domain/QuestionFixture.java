package codesquad.domain;

public class QuestionFixture {
    public static final Question QUESTION = new Question("타이틀1", "내용1", UserFixture.USER);
    public static final Question QUESTION_2 = new Question("타이틀2", "내용2", UserFixture.USER_2);
    public static final Question ERROR_QUESTION = new Question("타", "내용1", UserFixture.USER);
}
