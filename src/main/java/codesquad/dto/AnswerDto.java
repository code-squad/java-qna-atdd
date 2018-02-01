package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.User;

import java.util.Objects;

public class AnswerDto {
    private long id;
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

    public Answer toAnswer(User user) {
        return new Answer(user, contents);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return id == answerDto.id &&
                Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contents);
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                '}';
    }
}
