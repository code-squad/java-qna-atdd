package codesquad.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

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
        this.writer = loginUser;
    }

    public Answer addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return answer;
    }

    /*
        질문과 답변 삭제 이력에 대한 정보를 DeleteHistory를 활용해 남긴다.
    */
    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = Collections.emptyList();

        if (!isOwner(loginUser)) {
            throw new CannotDeleteException("지울 수 있는 권한이 없습니다.");
        }

        deleteHistories = this.getAnswers().stream()
                .map(answer -> answer.delete(loginUser))
                .collect(Collectors.toList());

        this.deleted = true;

        return addDeleteHistory(loginUser, deleteHistories);
    }

    private List<DeleteHistory> addDeleteHistory(User loginUser, List<DeleteHistory> deleteHistories) {
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, this.getId(), loginUser));
        return deleteHistories;
    }

    public void update(User loginUser, Question question) throws UnAuthenticationException {
        if (!isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }
        this.title = question.title;
        this.contents = question.contents;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isEmptyAnswer() {
        return this.getAnswers().isEmpty();
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }


    public boolean isContentsEquals(Object o) {
        Question question = (Question) o;
        return Objects.equals(title, question.title) &&
                Objects.equals(contents, question.contents);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), title, contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public List<Answer> getAnswers() {
        return answers;
    }
}
