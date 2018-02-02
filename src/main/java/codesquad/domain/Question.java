package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Question extends AbstractEntity {
	private static final Logger log = LoggerFactory.getLogger(Question.class);

	@Size(min = 3, max = 100)
	@Column(length = 100, nullable = false)
	private String title;

	@Size(min = 3)
	@Lob
	private String contents;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@Embedded
	private Answers answers = new Answers();

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

	public void addAnswer(Answer answer) {
		answer.setQuestion(this);
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
		if (isDeleted()) {
			return null;
		}

		return new QuestionDto(getId(), this.title, this.contents);
	}

	public void update(User loginUser, QuestionDto updatedQuestion) {
		log.debug("loginUser: {}, id: {}, target: {}", loginUser, updatedQuestion);
		if (!isOwner(loginUser)) {
			throw new UnAuthorizedException();
		}

		this.title = updatedQuestion.getTitle();
		this.contents = updatedQuestion.getContents();
	}

	public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
		if (!canDelete(loginUser)) {
			throw new CannotDeleteException("삭제할 수 없습니다.");
		}

		this.deleted = true;

		List<DeleteHistory> deleteHistories = answers.delete(loginUser);
		deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));
		return deleteHistories;
	}


	public boolean canDelete(User loginUser) {
		return isOwner(loginUser) && answers.canDelete(loginUser);
	}

	@Override
	public String toString() {
		return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
	}
}
