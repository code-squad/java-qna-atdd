package codesquad.dto;

import javax.validation.constraints.Size;
import java.util.Objects;


public class AnswerDto {
    private long id;
    private long questionId;

    @Size(min = 3)
    private String contents;

    public AnswerDto() {
    }

    public AnswerDto(long questionId, String contents) {
        this.questionId = questionId;
        this.contents = contents;
    }

    public AnswerDto(long id, long questionId, String contents) {
        this.id = id;
        this.questionId = questionId;
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public long getQuestionId() {
        return questionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDto)) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return id == answerDto.id &&
                Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contents);
    }


}
