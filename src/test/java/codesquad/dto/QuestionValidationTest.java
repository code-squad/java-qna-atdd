package codesquad.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import codesquad.dto.QuestionDto;

public class QuestionValidationTest {
    private static Validator validator;
    private User user;
    private Question question;
    private QuestionDto questionDto;
    private User user2;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Before
    public void setData() {
        user = new User("test11", "테스트11", "테스트11", "테스트11");
        user2 = new User("test22", "테스트22", "테스트22", "테스트22");
        question = new Question("수정 전", "수정 전");
        questionDto = new QuestionDto("수정 후",  "수정 후");
    }
    @Test
    public void titleWhenIsEmpty() throws Exception {
        QuestionDto question = new QuestionDto("", "당근 엄청 의미있는 활동이고 말고..");
        Set<ConstraintViolation<QuestionDto>> constraintViolcations = validator.validate(question);
        assertThat(constraintViolcations.size(), is(1));
    }

    @Test
    public void 수정_테스트() throws Exception {
        question.writeBy(user);
        question.update(user, questionDto);
        assertThat("수정 후", is(question.getTitle()));
    }

}
