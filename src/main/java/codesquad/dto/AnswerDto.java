package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.User;

import javax.validation.constraints.Size;

public class AnswerDto {

    private long id;

    @Size(min = 5)
    private String contents;

    public AnswerDto() {
    }

    public AnswerDto(long id, String contents) {
        this.id = id;
        this.contents = contents;
    }

    public AnswerDto(String contents) {
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Answer toAnswer(User writer) {
        return new Answer(writer, this.contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof AnswerDto)) { return false; }

        AnswerDto answerDto = (AnswerDto) o;

        return contents != null ? contents.equals(answerDto.contents) : answerDto.contents == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        return result;
    }
}
