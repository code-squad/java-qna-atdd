package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.dto.AnswerDto;
import support.domain.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
public class Answer extends AbstractEntity {
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

	public Answer(String contents) {
		this.contents = contents;
	}

	public Answer(Long id, String contents) {
		super(id);
		this.contents = contents;
	}

	public void writeBy(User loginUser) {
		this.writer = loginUser;
	}

	public Question getQuestion() {
		return question;
	}

	public String getContents() {
		return contents;
	}

	public Optional<AnswerDto> toAnswerDto() {
		if (this.deleted) {
			return Optional.empty();
		}

		return Optional.of(new AnswerDto(getId(), this.contents));
	}

	public void setQuestion(Question question) {
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

	public DeleteHistory delete(User loginUser) throws CannotDeleteException {
		if (!canDelete(loginUser)) {
			throw new CannotDeleteException("삭제할 수 없습니다.");
		}

		this.deleted = true;
		return new DeleteHistory(ContentType.ANSWER, getId(), loginUser, LocalDateTime.now());
	}

	public boolean canDelete(User loginUser) {
		return !isDeleted() && isOwner(loginUser);
	}

	@Override
	public String toString() {
		return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + ", deleted=" + deleted + "]";
	}

}
