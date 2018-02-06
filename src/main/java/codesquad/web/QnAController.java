package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnAService;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;

@Controller
@RequestMapping("/questions")
public class QnAController {

    @Resource(name = "qnaService")
    private QnAService qnAService;

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnAService.findById(id));
        return "/qna/show";
    }

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping("/")
    public String create(
            @LoginUser User loginUser,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            Model model) {
        model.addAttribute("question",
                qnAService.create(loginUser, new QuestionDto(title, contents)));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String update(
            @PathVariable long id,
            @LoginUser User loginUser,
            Model model) {
        if (!qnAService.findById(id).isOwner(loginUser)) throw new UnAuthorizedException();
        model.addAttribute("question", qnAService.findById(id));
        return "/qna/updateForm";
    }

    @PostMapping("/{id}/update")
    public String update(
            @LoginUser User loginUser,
            @PathVariable long id,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents,
            Model model) {
        model.addAttribute("question",
                qnAService.update(loginUser, id, new Question(title, contents)));
        return "/qna/show";
    }

    @DeleteMapping("/{id}")
    public String delete(
            @LoginUser User loginUser,
            @PathVariable long id,
            Model model) throws CannotDeleteException {
        qnAService.deleteQuestion(loginUser, id);
        model.addAttribute("questions", Arrays.asList(Iterables.toArray(qnAService.findAll(), Question.class)));
        return "home";
    }
}