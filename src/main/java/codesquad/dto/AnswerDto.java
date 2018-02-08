package codesquad.dto;

import javax.persistence.Lob;
import javax.validation.constraints.Size;

/**
 * Created by hoon on 2018. 2. 7..
 */
public class AnswerDto {
    @Size(min = 5)
    @Lob
    String contents;

    public AnswerDto() {
    }

    public AnswerDto(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
