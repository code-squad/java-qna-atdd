package codesquad.dto;

import codesquad.domain.Answer;

import javax.validation.constraints.Size;

public class AnswerDto {
    private long id;

    @Size(min = 3, max = 1000)
    private String contents;

    public AnswerDto() {
    }

    public AnswerDto(String contents) {
        this(0L, contents);
    }

    public AnswerDto(long id, String contents) {
        this.id = id;
        this.contents = contents;
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

    public Answer toAnswer() {
        return new Answer(contents);
    }
}
