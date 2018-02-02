package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {
    private long id;

    @Size(min = 5)
    private String contents;

    public AnswerDto() {

    }

    public AnswerDto(String contents) {
        this.contents = contents;
    }

    public AnswerDto(long id, String contents) {
        this.id = id;
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Answer toAnswer() {
        return new Answer(id, contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDto)) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents);
    }

    @Override
    public String toString() {
        return "AnswerDto[" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ']';
    }
}
