package codesquad.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
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

	public Answer update(String contents) {
		this.contents = contents;
		return this;
	}
	
	public DeleteHistory delete(User loginUser) throws CannotDeleteException {
		if (!this.isOwner(loginUser)) {
			throw new CannotDeleteException("댓글 삭제 권한이 없습니다. (본인의 댓글만 삭제할 수 있습니다.");
		}
		
		this.deleted = true;
		return new DeleteHistory(ContentType.ANSWER, this.getId(), this.writer, LocalDateTime.now());
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
