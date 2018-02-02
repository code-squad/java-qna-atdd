package codesquad.domain;

import codesquad.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers implements Serializable {
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@Where(clause = "deleted = false")
	@OrderBy("id ASC")
	private List<Answer> answers = new ArrayList<>();

	public Answers() {
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	public Answers add(Answer answer) {
		this.answers.add(answer);
		return this;
	}

	public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
		List<DeleteHistory> deleteHistories = new ArrayList<>();
		for (Answer answer : answers) {
			deleteHistories.add(answer.delete(loginUser));
		}

		return deleteHistories;
	}

	public boolean canDelete(User loginUser) {
		for (Answer answer : answers) {
			if (!answer.canDelete(loginUser)) {
				return false;
			}
		}

		return true;
	}

}
