package codesquad.dto;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswerValidationTest {
    private static Validator validator;

    @BeforeClass
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void contentsWhenIsEmpty() throws Exception {
        AnswerDto answer = new AnswerDto("", 0l);
        Set<ConstraintViolation<AnswerDto>> constraintViolcations = validator.validate(answer);
        assertThat(constraintViolcations.size(), is(1));
    }
}
