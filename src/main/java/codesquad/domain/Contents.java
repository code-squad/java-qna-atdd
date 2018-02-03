package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import support.domain.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;

@MappedSuperclass
public abstract class Contents extends AbstractEntity implements Auditable {

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_writer"))
    private User writer;

    private boolean deleted = false;

    public Contents() {

    }

    public Contents(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Contents(long id, User writer, String contents) {
        super(id);
        this.writer = writer;
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void update(User loginUser, String contents) {
        if(!isOwner(loginUser))
            throw new UnAuthorizedException();
        this.contents = contents;
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException{
        if(!isOwner(loginUser))
            throw new CannotDeleteException("Question must be deleted by owner.");
        deleted = true;
        return audit();
    }
}
