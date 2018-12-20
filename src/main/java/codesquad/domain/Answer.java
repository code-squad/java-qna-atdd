package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.slf4j.Logger;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    private static final Logger log = getLogger(Answer.class);

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public static List<DeleteHistory> delete(List<Answer> answers, User writer) throws CannotDeleteException {
        List<DeleteHistory> deletions = new ArrayList<>();
        for (Answer answer : answers) {
            deletions.add(answer.delete(writer));
        }
        return deletions;
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        if(!isOwner(loginUser)) throw new CannotDeleteException("다른 유저의 답변을 삭제할 수 없습니다!");
        deleted = true;
        return new DeleteHistory(ContentType.ANSWER, getId(), loginUser);
    }

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public Answer setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Answer update(User loginUser, String updatedContents) {
        if(!isOwner(loginUser)) throw new UnAuthorizedException();
        this.contents = updatedContents;
        return this;
    }

    public boolean isSameContents(String targetContents) {
        return this.contents.equals(targetContents);
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + ", deleted=" + deleted + "]";
    }


}
