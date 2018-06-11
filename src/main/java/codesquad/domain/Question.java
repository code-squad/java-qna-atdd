package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static codesquad.domain.ContentType.QUESTION;

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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this(0L, title, contents);
    }

    public Question(long id, String title, String contents) {
        super(id);
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

    public QuestionDto update(User loginUser, QuestionDto updatedQuestionDto) {
        if (!isMatch(updatedQuestionDto)) {
            throw new EntityNotFoundException("not same question");
        }
        validateAuthorize(loginUser);
        title = updatedQuestionDto.getTitle();
        contents = updatedQuestionDto.getContents();
        return toQuestionDto();
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (isDeleted()) {
            throw new CannotDeleteException("not exist question");
        }
        validateAuthorize(loginUser);

        ArrayList<DeleteHistory> histories = new ArrayList<>();
        deleted = true;
        histories.add(DeleteHistory.convert(QUESTION, loginUser, this));
        deleteAnswers(loginUser, histories);
        return histories;
    }

    private void deleteAnswers(User loginUser, List<DeleteHistory> histories) throws CannotDeleteException {
        for (Answer answer : answers) {
            histories.add(answer.delete(loginUser));
        }
    }

    private boolean isMatch(QuestionDto questionDto) {
        return getId() == questionDto.getId();
    }

    private void validateAuthorize(User loginUser) {
        if (!writer.equals(loginUser)) {
            throw new UnAuthorizedException("not match writer");
        }
    }
}
