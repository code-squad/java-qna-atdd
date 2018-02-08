package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.User;

public class AnswerDto {
    private long id;
    private User writer;
    private String contents;

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public AnswerDto(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public AnswerDto() {

    }

    public Answer toAnswer() {
        return new Answer(this.writer,this.contents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswerDto)) return false;

        AnswerDto answerDto = (AnswerDto) o;

        if (id != answerDto.id) return false;
        if (writer != null ? !writer.equals(answerDto.writer) : answerDto.writer != null) return false;
        return contents != null ? contents.equals(answerDto.contents) : answerDto.contents == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (writer != null ? writer.hashCode() : 0);
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
        return result;
    }
}
