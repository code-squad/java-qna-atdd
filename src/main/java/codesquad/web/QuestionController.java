package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("")
    public String home(Model model) {
        List<Question> questions = qnaService.findAll(new PageRequest(0, 10));
        model.addAttribute("questions", questions);
        return "/home";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, QuestionDto question) {
        qnaService.create(loginUser, new Question(question.getTitle(), question.getContents()));
        return "redirect:/questions";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id);
        if (!question.isOwner(loginUser)) {
            return "/qna/updateForm_failed";
        }

        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto target) {
        qnaService.update(loginUser, id, new Question(target.getTitle(), target.getContents()));
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException cannotDeleteException) {
            cannotDeleteException.printStackTrace();
            return "/qna/updateForm_failed";
        }
        return "redirect:/questions";
    }

}
