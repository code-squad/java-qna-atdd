package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnAService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/answers")
public class AnswerController {

    @Resource(name = "qnaService")
    private QnAService qnAService;

    @PostMapping("")
    public String create(
            @LoginUser User loginUser,
            AnswerDto answerDto,
            Model model) {
        model.addAttribute("question",
                qnAService.create(loginUser, answerDto).getQuestion());
        return "/qna/show";
    }

    @GetMapping(value = "/{id}")
    public String show(@PathVariable long id, Model model) {
        model.addAttribute("question",
                qnAService.findByAnswerId(id).getQuestion());
        return "/qna/show";
    }

}
