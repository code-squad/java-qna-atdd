package codesquad.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionValidationTest {
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
        assertThat(constraintViolcations.size(), is(1));
    }
}
