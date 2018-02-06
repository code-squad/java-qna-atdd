package codesquad.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Size;
import java.util.Objects;

@JsonIgnoreProperties
public class QuestionDto {
    private long id;

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    private boolean deleted;

    public QuestionDto() {
    }

    public QuestionDto(String title, String contents) {
        this(0, title, contents, false);
    }

    public QuestionDto(long id, String title, String contents, boolean deleted) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.deleted = deleted;
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

    public boolean isDeleted() {
        return deleted;
    }

    public QuestionDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionDto that = (QuestionDto) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, contents);
    }
}
