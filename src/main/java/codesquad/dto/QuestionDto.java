package codesquad.dto;

import codesquad.domain.Question;
import codesquad.domain.User;

import javax.validation.constraints.Size;

public class QuestionDto {
    private long id;
    private User writer;

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    public QuestionDto() {}

    public QuestionDto(long id, String title, String contents, User writer) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public QuestionDto(String title, String contents, User writer) {
        super();
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public Question toQuestion() {
        return new Question(this.id, this.title, this.contents, this.writer);
    }

    public long getId() {
        return id;
    }

    public QuestionDto setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QuestionDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public QuestionDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public QuestionDto setWriter(User writer) {
        this.writer = writer;
        return this;
    }
}
