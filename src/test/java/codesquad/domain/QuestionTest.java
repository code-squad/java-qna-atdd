package codesquad.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QuestionTest {

    public static final Question QUESTION = new Question("test title", "test content");

    public static final User WRITER = new User(3L, "writer", "password", "name", "javajigi@slipp.net");
    public static final User ANSWER = new User(4L, "answer", "password", "name", "javajigi@slipp.net");

    @Test
    public void writeBy() {
        Question question = new Question("글쓴이 제목 테스트", "글쓴이 내용 테스트");
        question.writeBy(WRITER);

        assertThat(question.getWriter())
                .isEqualTo(WRITER);
    }

    @Test
    public void addAnswer() {
        Question question = new Question("글쓴이 제목 테스트", "글쓴이 내용 테스트");
        Answer answer = new Answer(ANSWER, "답변 테스트");

        question.addAnswer(answer);

        assertThat(question.getAnswers())
                .contains(answer);
    }

    @Test
    public void isOwner() {
        Question question = new Question("글쓴이 제목 테스트", "글쓴이 내용 테스트");
        question.writeBy(WRITER);

        assertThat(question.isOwner(WRITER))
                .isTrue();
    }

    @Test
    public void generateUrl() {
        assertThat(QUESTION.generateUrl())
                .isEqualTo("/questions/0");
    }
}