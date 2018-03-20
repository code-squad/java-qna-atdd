package codesquad.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.service.QnaService;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
	private static final Logger log = LoggerFactory.getLogger(Answer.class);

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

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }
    
    public boolean isYourQuestion(Question newQuestion) {
        return question.equals(newQuestion);
    }
    
    public DeleteHistory delete(User loginUser) {
    	if(this.isDeleted())
    		throw new IllegalStateException("이미 삭제되어있는 답변입니다.");
    	if(!this.isOwner(loginUser))
    		throw new IllegalStateException("질문자와 답변글의 글쓴이가 다릅니다.");
    	this.deleted = true;
    	return new DeleteHistory(ContentType.ANSWER, getId(), writer, LocalDateTime.now());
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }
}
