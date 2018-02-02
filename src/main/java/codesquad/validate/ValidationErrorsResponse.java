package codesquad.validate;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ValidationErrorsResponse extends RestStatus {
    private List<ValidationError> errors;

    public ValidationErrorsResponse() {
        super(false);
        errors = new ArrayList<>();
    }

    public void addValidationError(ValidationError error) {
        errors.add(error);
    }

    public void addAllValidationError(List<FieldError> fieldErrors) {
        this.errors = fieldErrors.stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
                .collect(toList());
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
