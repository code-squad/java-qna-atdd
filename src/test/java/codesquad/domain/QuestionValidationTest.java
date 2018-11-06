package codesquad.domain;

import org.junit.BeforeClass;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class QuestionValidationTest extends BaseTest {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void titleWhenIsEmpty() throws Exception {
        Question question = new Question("", "당근 엄청 의미있는 활동이고 말고..");
        Set<ConstraintViolation<Question>> constraintViolcations = validator.validate(question);
        softly.assertThat(constraintViolcations).hasSize(1);
    }
}
