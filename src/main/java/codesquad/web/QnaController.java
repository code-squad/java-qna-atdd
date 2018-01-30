package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.UserDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QnaController {

    private static final Logger log = LoggerFactory.getLogger(QnaController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @GetMapping("/{id}")
    public String showQuestion(Question question, @PathVariable long id, Model model) {
        model.addAttribute("question", qnaService.findById(id));
        return "/qna/show";
    }

    @PostMapping("")
    public String create(@LoginUser User loginUser, String title, String contents) {
        qnaService.create(loginUser, new Question(title, contents));
        return "redirect:/questions";
    }

    @GetMapping("/{id}/updateForm")
    public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
        if(qnaService.findById(id).isOwner(loginUser)){
            model.addAttribute("question", qnaService.findById(id));
            return "/qna/updateForm";
        }
        return "redirect:/questions";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, String title, String contents) {
        qnaService.update(loginUser, id, new Question(title, contents));
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) {
        try {
            qnaService.deleteQuestion(loginUser, id);
        } catch (CannotDeleteException e) {
            e.printStackTrace();
        }
        return "redirect:/questions";
    }
}
