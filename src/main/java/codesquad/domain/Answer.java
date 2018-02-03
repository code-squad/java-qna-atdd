package codesquad.domain;

import support.domain.UrlGeneratable;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Answer extends Contents implements UrlGeneratable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        super(writer, contents);
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id, writer, contents);
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + getWriter() + ", contents=" + getContents() + "]";
    }

    @Override
    public DeleteHistory audit() {
        return new DeleteHistory(ContentType.ANSWER, getId(), getWriter());
    }
}
