package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.User;

import javax.validation.constraints.Size;

public class AnswerDto {
    private long id;

    @Size(min = 3)
    private String content;

    public AnswerDto() {}

    public AnswerDto(String content) {
        this.content = content;
    }

    public AnswerDto(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Answer toAnswer() {
        return new Answer(content);
    }
}
