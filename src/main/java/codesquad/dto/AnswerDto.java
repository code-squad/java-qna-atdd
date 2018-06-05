package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;

import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

public class AnswerDto {

    @Size(min = 5)
    @Lob
    private String contents;

    public AnswerDto(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Answer toAnswer(User writer){
        return new Answer(writer, contents);
    }
}
