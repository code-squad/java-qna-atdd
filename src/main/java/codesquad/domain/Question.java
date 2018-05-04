package codesquad.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.transaction.Transactional;
import javax.validation.constraints.Size;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
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

    public List<AnswerDto> getAnswersDtoes() {
        List<AnswerDto> answerDtoList = new ArrayList<AnswerDto>();
        for(Answer answer : answers) {
            answerDtoList.add(answer.toAnswerDto());
        }
        return answerDtoList;
    }

    public boolean isOwner(User loginUser) {
        return writer.matchUser(loginUser);
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

    public void update(User loginUser, Question target) {
        if (!writer.matchUser(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.contents = target.contents;
        this.title = target.title;
    }

    @Transactional
    public void addAnswer(User loginUser, Answer answer) {
        if (!writer.matchUser(loginUser)) {
            throw new UnAuthorizedException();
        }
        answer.toQuestion(this);
        answers.add(answer);
    }
}
