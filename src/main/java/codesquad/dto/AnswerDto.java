package codesquad.dto;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {

    @Size(min = 3)
    private String contents;

    public AnswerDto() { }

    public AnswerDto(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }


    @Override
    public String toString() {
        return "AnswerDto{" +
                "contents='" + contents + '\'' +
                '}';
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

        return Objects.hash(contents);
    }
}
