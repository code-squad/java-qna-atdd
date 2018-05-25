package codesquad.dto;

import javax.validation.constraints.Size;

import codesquad.domain.Question;

public class QuestionDto {
    private long id;

    @Size(min = 3, max = 100)
    private String title;

    @Size(min = 3)
    private String contents;

    public QuestionDto() {}

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

    public QuestionDto setId(long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public QuestionDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public QuestionDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

	public Question toQuestion() {
		return new Question(this.title, this.contents);
	}

	@Override
	public String toString() {
		return "QuestionDto [id=" + id + ", title=" + title + ", contents=" + contents + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contents == null) ? 0 : contents.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuestionDto other = (QuestionDto) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	
	
}
