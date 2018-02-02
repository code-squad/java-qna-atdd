package codesquad.dto;

import codesquad.domain.Question;
import codesquad.domain.User;

import java.util.Objects;

public class AnswerDto {

    private long id;

    private User writer;

    private Question question;

    private String contents;

    private boolean deleted = false;

    public AnswerDto() {

    }

    public long getId() {
        return id;
    }

    public AnswerDto setId(long id) {
        this.id = id;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public AnswerDto setWriter(User writer) {
        this.writer = writer;
        return this;
    }

    public Question getQuestion() {
        return question;
    }

    public AnswerDto setQuestion(Question question) {
        this.question = question;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public AnswerDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public AnswerDto setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerDto answerDto = (AnswerDto) o;
        return Objects.equals(contents, answerDto.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(writer, question, contents, deleted);
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "writer=" + writer +
                ", question=" + question +
                ", contents='" + contents + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
