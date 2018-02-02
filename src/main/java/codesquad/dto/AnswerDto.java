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
}
