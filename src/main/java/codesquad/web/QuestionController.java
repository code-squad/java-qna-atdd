package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User user,
                         QuestionDto questionDTO) {
        qnaService.create(user, questionDTO.toQuestion());
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable(name = "id") long questionId,
                       Model model){
        Question question = qnaService.findById(questionId);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String modifyForm(
            @PathVariable(name = "id") long questionId,
            @LoginUser User user,
            Model model) {
        Question question = qnaService.findById(questionId);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PostMapping("/{id}")
    public String modify(
        @PathVariable(name = "id") long questionId,
        @LoginUser User user,
        QuestionDto questionDTO) {
        Question update = qnaService.update(user, questionId, questionDTO.toQuestion());
        return "redirect:"+update.generateUrl();
    }

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable(name = "id") long questionId,
            @LoginUser User user) throws CannotDeleteException{
        qnaService.deleteQuestion(user, questionId);
        return "redirect:/";
    }
}
