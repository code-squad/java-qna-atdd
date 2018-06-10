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

        Question question = qnaService.create(loginUser, questionDto.toQuestion());

        return "redirect:" + question.generateUrl();
    }

    @GetMapping("/{id}")
    public String read(@PathVariable Long id, Model model) {
        qnaService.findById(id).ifPresent(question -> {
            model.addAttribute("question", question);
        });

        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable Long id, Model model, @LoginUser User loginUser) {
        Question foundQuestion = qnaService.findQuestion(id, loginUser);
        model.addAttribute("question", foundQuestion);

        return "/qna/form";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, QuestionDto question, @LoginUser User loginUser) {
        qnaService.update(loginUser, id, question.toQuestion());

        return String.format("redirect:/qna/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @LoginUser User loginUser) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            log.debug(e.getMessage());
        }

        return "redirect:/";
    }
}
