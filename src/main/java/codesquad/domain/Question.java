package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Question(long id, @Size(min = 3, max = 100) String title, @Size(min = 3) String contents, User writer) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
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

    public void delete(User loginUser) throws CannotDeleteException {
        if(!isOwner(loginUser)) throw new CannotDeleteException("다른 유저의 글을 삭제할 수 없습니다!");
        this.deleted = true;
        // todo 답변 삭제 추가구현 필요
    }

    public boolean isDeleted() {
        return deleted;
    }


    public Question update(User loginUser, Question updatedQuestion) {
        if(!isOwner(loginUser)) throw new UnAuthorizedException();
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
        return this;
    }

    public boolean equalsTitleAndContents(Question target) {
        if(Objects.isNull(target)) {
            return false;
        }
        return title.equals(target.title) && contents.equals(target.contents);
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
