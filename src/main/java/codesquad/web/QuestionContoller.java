package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/questions")
public class QuestionContoller {
    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public String getList(Model model, @PathVariable long id) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question.toQuestionDto());
        return "qna/show";
    }

    @GetMapping("/form")
    public String questionForm(@LoginUser User loginUser) throws UnAuthenticationException {
        if (Objects.isNull(loginUser)) {
            throw new UnAuthenticationException();
        }
        return "qna/form";
    }

    @PostMapping("")
    public String writeQuestion(@LoginUser User loginUser, QuestionDto questionDto) {
        qnaService.create(loginUser, new Question(questionDto.getTitle(), questionDto.getContents()));
        return "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        Question question = qnaService.findById(id);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", question);
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto questionDto) {
        qnaService.update(loginUser, id, new Question(questionDto.getTitle(), questionDto.getContents()));
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

}
