package codesquad.dto;

import codesquad.domain.Answer;

import javax.validation.constraints.Size;

public class AnswerDto {
    private long id;

    @Size(min = 3)
    private String contents;

    public AnswerDto() {
    }

    public AnswerDto(String contents) {
        this(0, contents);
    }

    public AnswerDto(long id, String contents) {
        this.id = id;
        this.contents = contents;
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

    public Answer toAnswer() {
        return new Answer(this.contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnswerDto answerDto = (AnswerDto) o;

        return contents != null ? contents.equals(answerDto.contents) : answerDto.contents == null;

    }

    @Override
    public int hashCode() {
        return contents != null ? contents.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
                "id=" + id +
                ", contents='" + contents + '\'' +
                '}';
    }
}
