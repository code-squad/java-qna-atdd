package codesquad.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DeleteHistory {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    private Long contentId;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_deletehistory_to_user"))
    private User deletedBy;

    private LocalDateTime createDate = LocalDateTime.now();

    public DeleteHistory() {
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this(contentType, contentId, deletedBy);
        this.createDate = createDate;
    }

    public static List<DeleteHistory> createDeleteHistories(Question question, Long id) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(question.createQuestionOfDeleteHistory(id));
        for(DeleteHistory deleteHistory : question.createAnswersOfDeleteHistories()) {
            deleteHistories.add(deleteHistory);
        }
        return deleteHistories;
    }

    @Override
    public String toString() {
        return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createDate=" + createDate + "]";
    }
}
