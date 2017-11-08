package codesquad.domain;

import java.time.LocalDateTime;

public class DeleteHistory {
	private Long id;
	
	private ContentType contentType;
	
	private Long contentId;
	
	private User deletedBy;
	
	private LocalDateTime createDate = LocalDateTime.now();

	public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
		this.contentType = contentType;
		this.contentId = contentId;
		this.deletedBy = deletedBy;
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
				+ deletedBy + ", createDate=" + createDate + "]";
	}
}
