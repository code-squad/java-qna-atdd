package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Test
    public void write() {

        User loginUser = findByUserId(DEFAULT_LOGIN_USER);

        QuestionDto questionDto = new QuestionDto(0, "타이틀", "컨텐츠");

        String location = this.createResource("/api/questions/", questionDto, loginUser);

        Question question1 = this.getResource(location, Question.class, loginUser);

        assertThat(question1.isContentsEquals(questionDto.toQuestion()), is(true));
    }

    @Test
    public void update() {

        User loginUser = findByUserId(DEFAULT_LOGIN_USER);

        QuestionDto questionDto = new QuestionDto(0, "타이틀", "컨텐츠");
        QuestionDto questionDto2 = new QuestionDto(0, "타이틀2", "컨텐츠2");

        String location = this.createResource("/api/questions/", questionDto, loginUser);

        basicAuthTemplate(loginUser).put(location, questionDto2);

        QuestionDto questionDto1 = this.getResource(location, QuestionDto.class, loginUser);
        assertThat(questionDto1.isSameContents(questionDto2), is(true));

    }
}
