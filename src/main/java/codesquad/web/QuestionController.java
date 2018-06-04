package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Question question = qnaService.findById(id);
        model.addAttribute("question", question.toQuestionDto());
        return "qna/show";
    }

    @GetMapping("/form")
    public String showCreateForm(@LoginUser User loginUser) {
        return "qna/form";
    }

    @PostMapping("")
    public String createQuestion(@LoginUser User loginUser, QuestionDto target) {
        Question createdQuestion = qnaService.create(loginUser, target.toQuestion());
        return String.format("redirect:%s", createdQuestion.generateUrl());
    }

    @GetMapping("/{id}/form")
    public String showUpdateForm(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        Question question = qnaService.findById(loginUser, id);
        model.addAttribute("question", question.toQuestionDto());
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, QuestionDto target) {
        Question question = qnaService.update(loginUser, id, target);
        return String.format("redirect:%s", question.generateUrl());
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
