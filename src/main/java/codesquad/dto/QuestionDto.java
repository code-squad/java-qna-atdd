package codesquad.dto;

import codesquad.domain.Question;

import javax.validation.constraints.Size;

public class QuestionDto {
    private long id;

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    public QuestionDto() {

    }

    public QuestionDto(String title, String contents) {
        this(0, title, contents);
    }

    public QuestionDto(long id, String title, String contents) {
        this.id = id;
        this.title = title;
        this.contents = contents;
    }

    public Question toQuestion() {
        return new Question(this.title, this.contents);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionDto)) return false;

        QuestionDto that = (QuestionDto) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return contents != null ? contents.equals(that.contents) : that.contents == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        return result;
    }
}
