package codesquad.domain;

import codesquad.CannotManageException;
import codesquad.dto.QuestionDto;
import org.apache.commons.lang3.StringUtils;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static codesquad.domain.ContentType.QUESTION;

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

    @Column
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

    private Question updateTitle(String title) {
        if(StringUtils.isEmpty(title)) { throw new IllegalArgumentException(); }
        this.title = title;
        return this;
    }

    private Question updateContents(String contents) {
        if(StringUtils.isEmpty(contents)) { throw new IllegalArgumentException(); }
        this.contents = contents;
        return this;
    }

    public Question update(User loginUser, Question updatedQuestion) throws CannotManageException {
        checkCommon(loginUser, "수정은 글쓴이만 가능합니다.", "삭제된 글입니다.");

        updateTitle(updatedQuestion.getTitle());
        updateContents(updatedQuestion.getContents());
        return this;
    }

    public List<DeleteHistory> deleted(User loginUser) throws CannotManageException {
        checkCommon(loginUser, "삭제는 글쓴이만 가능합니다.", "이미 삭제된 글입니다.");
        hasOtherAnswerWriter();
        this.deleted = true;

        return createDeleteHistorys(loginUser);
    }

    private List<DeleteHistory> createDeleteHistorys(User loginUser) {
        List<DeletedId> deletedIds = new ArrayList<>(Arrays.asList(new DeletedId(getId(), QUESTION)));
        deletedIds.addAll(answers.convertDeletedId());
        return deletedIds.stream().map(deletedId -> new DeleteHistory(deletedId, loginUser)).collect(Collectors.toList());
    }

    private void hasOtherAnswerWriter() throws CannotManageException {
        if(answers.isHasOtherWriter(writer)) { throw new CannotManageException("다른 사용자가 답변을 달아 삭제할 수 없습니다."); }
    }

    private void checkCommon(User loginUser, String ownerMessage, String deleteMessage) throws CannotManageException {
        if (!this.isOwner(loginUser)) { throw new CannotManageException(ownerMessage); }
        else if (isDeleted()) { throw new CannotManageException(deleteMessage); }
    }

    public static Question convert(QuestionDto questionDto) {
        return new Question(questionDto.getTitle(), questionDto.getContents());
    }

}
