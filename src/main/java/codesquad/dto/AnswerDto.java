package codesquad.dto;

import codesquad.domain.Answer;

public class AnswerDto {
	private long id;
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
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getContents() {
		return contents;
	}
	
	public void setContents(String contents) {
		this.contents = contents;
	}
	
	public Answer toAnswer() {
		return new Answer(this.contents);
	}
}
