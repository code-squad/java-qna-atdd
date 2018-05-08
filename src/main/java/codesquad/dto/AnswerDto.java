package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.User;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {
    private long id;

    @Size(min = 5)
    private String contents;

    private UserDto writer;

    public AnswerDto() {
    }

    public AnswerDto(String contents) {
        this(0, contents, null);
    }

    public AnswerDto(long id, String contents, User writer) {
        this.id = id;
        this.contents = contents;
        if (writer != null)
            this.writer = writer.toUserDto();
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

    public UserDto getWriter() {
        return writer;
    }

    public void setWriter(UserDto writer) {
        this.writer = writer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return id == answerDto.id &&
                Objects.equals(contents, answerDto.contents) &&
                Objects.equals(writer, answerDto.writer);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, contents, writer);
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                ", writer=" + writer +
                '}';
    }
}
