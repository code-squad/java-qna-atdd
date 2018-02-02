package codesquad.dto;

import java.util.List;

public class QuestionsDto {
    private List<QuestionDto> contents;

    public QuestionsDto() {
    }

    public QuestionsDto(List<QuestionDto> contents) {
        this.contents = contents;
    }

    public int getSize() {
        return contents.size();
    }

    public List<QuestionDto> getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "QuestionsDto{" +
                "contents=" + contents +
                '}';
    }
}
