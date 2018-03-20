package codesquad.domain;

import java.time.LocalDateTime;
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
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
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

	public List<DeleteHistory> delete(User loginUser) {
		List<DeleteHistory> deleteHistories = new ArrayList<>();
		
		if(this.isDeleted())
			throw new IllegalStateException("이미 삭제되어있는 질문입니다.");
		if (!this.isOwner(loginUser))
			throw new IllegalStateException("자신의 질문만 수정/삭제 가능합니다.");
		
		for (Answer answer : answers) {
			deleteHistories.add(answer.delete(writer));
		}
		deleteHistories.add(this.deleteQuestion());

		return deleteHistories;
	}

	private DeleteHistory deleteQuestion() {
		deleted = true;
		return new DeleteHistory(ContentType.QUESTION, getId(), writer, LocalDateTime.now());
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

	public void update(User loginUser, QuestionDto updatequestion) {
		if (!this.isOwner(loginUser))
			throw new IllegalStateException("자신의 질문만 수정/삭제 가능합니다.");

		this.title = updatequestion.getTitle();
		this.contents = updatequestion.getContents();

	}

	@Override
	public String toString() {
		return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
	}

}
