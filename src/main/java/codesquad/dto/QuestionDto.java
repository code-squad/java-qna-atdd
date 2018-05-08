package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuestionDto {
    private long id;

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    private UserDto writer;

    private List<AnswerDto> answers;

    public QuestionDto() {
    }

    public QuestionDto(long id, String title, String contents, User writer, List<Answer> answers) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        if (writer != null)
            this.writer = writer.toUserDto();

        if (answers != null) {
            this.answers = new ArrayList<>();
            this.answers = answers.stream().map(Answer::toAnswerDto).collect(Collectors.toList());
        }
    }

    public QuestionDto(String title, String contents) {
        this(0, title, contents, null, null);
    }

    public QuestionDto(long id, String title, String contents) {
        this(id, title, contents, null, null);
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

    public UserDto getWriter() {
        return writer;
    }

    public void setWriter(UserDto writer) {
        this.writer = writer;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    public Question toQuestion() {
        return new Question(this.title, this.contents);
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionDto that = (QuestionDto) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(contents, that.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, contents);
    }
}
