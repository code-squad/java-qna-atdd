package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/questions")
public class QuestionsController {

    private final QnaService qnaService;

    public QuestionsController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findQuestionByIdAndNotDeleted(id);
        question.checkAuthority(loginUser);
        model.addAttribute("question", question);

        return "/qna/updateForm";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        Question question = qnaService.findQuestionByIdAndNotDeleted(id);
        model.addAttribute("question", question);

        return "/qna/show";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser,
                         @Valid QuestionDto questionDto) {
        qnaService.create(loginUser, questionDto.toQuestion());

        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser,
                         @PathVariable long id) {
        qnaService.deleteQuestion(loginUser, id);

        return "redirect:/";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser,
                         @PathVariable long id,
                         @Valid QuestionDto questionDto) {
        Question question = qnaService.update(loginUser, id, questionDto.toQuestion());
        return "redirect:" + question.generateUrl();
    }
}
