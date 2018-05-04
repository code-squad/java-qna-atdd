package codesquad.domain;

import codesquad.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Answers {
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@Where(clause = "deleted = false")
	@OrderBy("id ASC")
	private List<Answer> answers = new ArrayList<>();
	
	public Answers() {
	
	}
	
	public Answers(Collection answers) {
		this.answers = new ArrayList<Answer>((Collection<? extends Answer>) answers);
	}
	
	public void add(Answer answer) {
		answers.add(answer);
	}
	
	public List<DeleteHistory> deleteAll(User loginUser) throws CannotDeleteException {
		List<DeleteHistory> deleteHistories = new ArrayList();
		for (Answer answer : answers) {
			answer.delete(loginUser);
			deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer.getId(), loginUser));
		}
		return deleteHistories;
	}
	
	public boolean isEmpty() {
		return answers.isEmpty();
	}
	
	public boolean equalAllAnswerWriterWithQuestionWriter() {
		if(isEmpty()) {
			return true;
		}
		
		return answers.stream()
				.filter(answer -> !answer.isDeleted())
				.map(Answer::equalWriterWithQuestion)
				.reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
				.get();
	}
	
	public Answers getPageRequest(int pageNumber, int pageSize) {
		return new Answers(answers.stream()
				.skip(pageNumber * pageSize)
				.limit(pageSize).collect(Collectors.toList()));
	}
	
	public Stream<Answer> stream() {
		return answers.stream();
	}
	
	public boolean isAllDeleted() {
		return answers.stream()
				.map(Answer::isDeleted)
				.reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
				.get();
	}
}
