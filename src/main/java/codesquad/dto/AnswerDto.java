package codesquad.dto;

import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Optional;

public class AnswerDto {
    private long id;

    @Size(min = 3)
    private String contents;

    private Long questionId;

    public AnswerDto() {
    }

    public AnswerDto(String contents, Long questionId) {
        this(0, contents, questionId);
    }

    public AnswerDto(long id, String contents, Long questionId) {
        this.id = id;
        this.contents = contents;
        this.questionId = questionId;
    }

    public long getId() {
        return id;
    }

    public AnswerDto setId(long id) {
        this.id = id;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public AnswerDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public Long getQuestionId() {
        return Optional.of(questionId).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(contents, answerDto.contents) &&
                Objects.equals(questionId, answerDto.questionId);
    }
}
