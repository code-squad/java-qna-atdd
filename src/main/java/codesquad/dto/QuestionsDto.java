package codesquad.dto;

import codesquad.domain.Question;

import java.util.List;

public class QuestionsDto {
    private List<QuestionDto> contents;

    public QuestionsDto(List<QuestionDto> contents) {
        this.contents = contents;
    }

    public int getSize() {
        return contents.size();
    }

}
