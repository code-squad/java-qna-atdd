package codesquad.dto;

import codesquad.domain.Answer;
import codesquad.domain.Question;

import javax.validation.constraints.Size;
import java.util.Objects;

public class AnswerDto {
	private long id;

	@Size(min = 3)
	private String contents;

	public AnswerDto() {
	}

	public AnswerDto(String contents) {
		this(0, contents);
	}

	public AnswerDto(long id, String contents) {
		this.id = id;
		this.contents = contents;
	}

	public long getId() {
		return id;
	}

	public AnswerDto setId(long id) {
		this.id = id;
		return this;
	}

	public String getContents() {
		return contents;
	}

	public AnswerDto setContents(String contents) {
		this.contents = contents;
		return this;
	}

	public Answer toAnswer() {
		return new Answer(this.id, this.contents);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnswerDto answerDto = (AnswerDto) o;
		return Objects.equals(contents, answerDto.contents);
	}

	@Override
	public int hashCode() {
		return Objects.hash(contents);
	}

	@Override
	public String toString() {
		return "AnswerDto{" +
				"id=" + id +
				", contents='" + contents + '\'' +
				'}';
	}
}
