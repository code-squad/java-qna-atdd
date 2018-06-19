package codesquad.web;

import codesquad.CannotDeleteException;
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
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@Controller
@RequestMapping("/qna")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String createForm() {
        return "/qna/form";
    }

    @PostMapping("")
    public String create(QuestionDto questionDto, @LoginUser User loginUser) {
        log.debug("QuestionDto : {}", questionDto);

        Question question = qnaService.addQuestion(loginUser, questionDto);

        return "redirect:" + question.generateUrl();
    }

    @GetMapping("/{id}")
    public String read(@PathVariable Long id, Model model) {
        Question foundQuestion = qnaService.findQuestionById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", foundQuestion);

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable Long id, Model model, @LoginUser User loginUser) {
        Question foundQuestion = qnaService.findByLoginUser(id, loginUser);
        model.addAttribute("question", foundQuestion);

        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @RequestBody QuestionDto updatedQuestion, @LoginUser User loginUser) {
        qnaService.update(loginUser, id, updatedQuestion);

        return String.format("redirect:/qna/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @LoginUser User loginUser) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);

        return "redirect:/";
    }
}
