package codesquad.dto;

import javax.validation.constraints.Size;

public class QuestionDto {
	private long id;
	
	@Size(min = 3, max = 100)
	private String title;
	
	@Size(min = 3)
	private String contents;
	
	public QuestionDto(String title, String contents) {
		this(0, title, contents);
	}

	public QuestionDto(long id, String title, String contents) {
		this.id = id;
		this.title = title;
		this.contents = contents;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
}
