package codesquad.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(String title, String contents, User writer) {
        this.title = title;
        this.contents = contents;
        this.writer = writer;
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
    
    public Answers getAnswers() {
        return answers;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
    
    public void update(User loginUser, Question updatedQuestion) throws CannotUpdateException {
        if (!isOwner(loginUser)) {
            throw new CannotUpdateException("자신이 작성한 글만 수정할 수 있습니다.");
        }
        
        this.title = updatedQuestion.getTitle();
        this.contents = updatedQuestion.getContents();
    }
    
    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!canDelete(loginUser)) {
            throw new CannotDeleteException("자신이 작성한 글이 아니거나 다른 사람이 작성한 답변이 달려있어 삭제할 수 없습니다.");
        }

        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleted = true;
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser));
        deleteHistories.addAll(answers.deleteAll(loginUser));

        return deleteHistories;
    }
    
    private boolean canDelete(User loginUser) {
        return isOwner(loginUser) &&
                (answers.isEmpty() || answers.equalAllAnswerWriterWithQuestionWriter());
    }
}
