package codesquad.domain;

import org.junit.BeforeClass;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static codesquad.domain.UserTest.RED;

public class AnswerValidationTest extends BaseTest {
    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void contentsWhenIsEmpty() throws Exception {
        Answer answer = new Answer(RED,"");
        Set<ConstraintViolation<Answer>> constraintViolations = validator.validate(answer);
        softly.assertThat(constraintViolations).hasSize(1);

    }
}