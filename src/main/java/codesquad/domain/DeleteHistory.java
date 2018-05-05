package codesquad.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

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

    private DeleteHistory() { }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createDate = createDate;
    }

    public DeleteHistory(Question deletedQuestion, User deletedBy) {
        this(ContentType.QUESTION, deletedQuestion.getId(), deletedBy, LocalDateTime.now());
    }

    public DeleteHistory(Answer deletedAnswer, User deletedBy) {
        this(ContentType.ANSWER, deletedAnswer.getId(), deletedBy, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "DeleteHistory [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", deletedBy="
                + deletedBy + ", createDate=" + createDate + "]";
    }
}
