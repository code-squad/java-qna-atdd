package codesquad.dto;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import codesquad.domain.User;
import org.junit.BeforeClass;
import org.junit.Test;

import codesquad.dto.QuestionDto;

public class QuestionValidationTest {
    private static Validator validator;
    private static User writer;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void titleWhenIsEmpty() throws Exception {
        QuestionDto question = new QuestionDto("title", "당근 엄청 의미있는 활동이고 말고..", writer);
        Set<ConstraintViolation<QuestionDto>> constraintViolcations = validator.validate(question);
        assertThat(constraintViolcations.size(), is(1));
    }
}
