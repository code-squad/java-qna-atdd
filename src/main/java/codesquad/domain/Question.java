package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))

    private User writer;

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
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

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public Question update(User loginUser, QuestionDto questionDto) {
        if(!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.title = questionDto.getTitle();
        this.contents = questionDto.getContents();
        return this;
    }

    public Question delete(User loginUser) {
        if(!isOwner(loginUser) || !answers.isAllSameOwner(loginUser)) {
            throw new CannotDeleteException("자신의 글만 삭제할 수 있습니다");
        }
        this.deleted = true;
        answers.delete(loginUser);
        return this;
    }

    public List<DeleteHistory> toDeleteHistory() {
        return Stream.concat(
                Stream.of(new DeleteHistory(ContentType.QUESTION, id, writer, LocalDateTime.now())),
                answers.getAll()
                        .stream()
                        .map(answer -> answer.toDeleteHistory(writer))
        ).collect(Collectors.toList());
    }

    public Answers getAnswers() {
        return answers;
    }
}
