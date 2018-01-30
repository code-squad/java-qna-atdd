package codesquad.dto;

import javax.persistence.Lob;
import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {
    private Long questionId;

    @Size(min = 5)
    @Lob
    private String contents;

    public AnswerDto() {
    }

    public AnswerDto(Long questionId, String contents) {
        this.questionId = questionId;
        this.contents = contents;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public AnswerDto setQuestionId(Long questionId) {
        this.questionId = questionId;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public AnswerDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(questionId, answerDto.questionId) &&
                Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, contents);
    }
}
