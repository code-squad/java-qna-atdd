package codesquad.dto;

public class AnswerDto {

    private long id;

    private String contents;

    private boolean deleted;

    public AnswerDto() {
    }

    public AnswerDto(String contents) {
        this(contents, false);
    }
    public AnswerDto(String contents, boolean deleted) {
        this.contents = contents;
        this.deleted = deleted;
    }

    public long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
