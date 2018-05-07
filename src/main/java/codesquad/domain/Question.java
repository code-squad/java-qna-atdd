package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.Size;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    @Embedded
    private Answers answers;

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

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
        return answers.getAnswersDtoes();
    }

    public List<DeleteHistory> deleteQuestion(User loginUser) throws UnAuthenticationException {

        List<DeleteHistory> histories = new ArrayList<>();

        isQuestionOwner(loginUser);

        if(answers != null) {
            histories.addAll(answers.deleteAnswers(loginUser));
        }

        deleted = true;
        histories.add(new DeleteHistory(ContentType.QUESTION,this.getId(),loginUser, LocalDateTime.now()));
        return histories;
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

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Transactional
    public void addAnswer(Answer answer) {

        if(answers == null) {
            answers = new Answers();
        }
        answer.toQuestion(this);
        answers.addAnswer(answer);
    }

    public boolean isQuestionOwner(User loginUser) {
        if (!writer.matchUser(loginUser)) {
            throw new UnAuthorizedException("다른 사람의 글은 삭제할 수 없다.");
        }
        return true;
    }
}
