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
@RequestMapping(value = "/questions")
public class QuestionController {

    @Resource(name = "qnaService")
    private QnAService qnAService;

    @PostMapping("")
    public String create(
            @LoginUser User loginUser,
            QuestionDto questionDto,
            Model model) {
        model.addAttribute("question",
                qnAService.create(loginUser, questionDto));
        return "/qna/show";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question", qnAService.findById(id));
        return "/qna/show";
    }

    @GetMapping(value = "/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @GetMapping(value = "/{id}/form")
    public String update(
            @PathVariable long id,
            @LoginUser User loginUser,
            Model model) {
        if (!qnAService.findById(id).isOwner(loginUser)) throw new UnAuthorizedException();
        model.addAttribute("question", qnAService.findById(id));
        return "/qna/updateForm";
    }

    @PostMapping(value = "/{id}/update")
    public String update(
            @LoginUser User loginUser,
            @PathVariable long id,
            QuestionDto questionDto,
            Model model) {
        model.addAttribute("question",
                qnAService.update(loginUser, id, questionDto));
        return "/qna/show";
    }

    @DeleteMapping(value = "/{id}")
    public String delete(
            @LoginUser User loginUser,
            @PathVariable long id,
            Model model) throws CannotDeleteException {
        qnAService.deleteQuestion(loginUser, id);
        model.addAttribute("questions", Arrays.asList(Iterables.toArray(qnAService.findAll(), Question.class)));
        return "home";
    }
}