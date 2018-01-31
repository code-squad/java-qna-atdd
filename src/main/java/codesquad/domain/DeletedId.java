package codesquad.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class DeletedId implements Serializable {
    @Column
    private Long contentId;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Builder
    public DeletedId(Long contentId, ContentType contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
    }
}
