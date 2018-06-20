package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    @JsonProperty
    private String title;

    @Size(min = 3)
    @Lob
    @JsonProperty
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    @JsonProperty
    private User writer;

    @JsonIgnore
    @Embedded
    private Answers answers = new Answers();

    @JsonProperty
    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.matchUser(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/qna/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), title, contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public Question update(Question target, User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("question writer not equal");
        }

        title = target.title;
        contents = target.contents;

        return this;
    }

    public boolean checkAnswerExist() {
        return !answers.isEmpty();
    }

    public boolean checkAllAnswerWriterIsSameWithWriter() {
        return answers.checkAllWriterSameWith(writer);
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        checkLoginUser(loginUser);

        List<DeleteHistory> toReturnList = new ArrayList<>();
        if (checkAnswerExist()) {
            toReturnList = deleteAllAnswers(toReturnList);
        }

        deleted = true;
        toReturnList.add(new DeleteHistory(ContentType.QUESTION, getId(), writer));

        return toReturnList;
    }

    private void checkLoginUser(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
    }

    public List<DeleteHistory> deleteAllAnswers(List<DeleteHistory> toReturnList) throws CannotDeleteException {
        if (!checkAllAnswerWriterIsSameWithWriter()) {
            throw new CannotDeleteException("모든 답변자와 작성자가 같지 않음");
        }

        answers.delete(toReturnList, writer);

        return toReturnList;
    }
}
