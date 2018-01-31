package codesquad.domain;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
@Entity
public class DeleteHistory {
    @EmbeddedId
    private DeletedId id;

    @CreatedDate
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_deletehistory_to_user"))
    private User deletedBy;

    @Builder
    public DeleteHistory(DeletedId id, User deletedBy) {
        this.id = id;
        this.deletedBy = deletedBy;
        this.createDate = LocalDateTime.now();
    }
}
