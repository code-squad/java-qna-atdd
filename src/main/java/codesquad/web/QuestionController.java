package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger logger = getLogger(QuestionController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String createForm(@LoginUser User loginUser){
        return "qna/form";
    }

    @PostMapping
    public String create(@LoginUser User loginUser, Question question){
        qnaService.create(loginUser, question);
        logger.debug("question : {}", question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable long id, Model model){
        model.addAttribute("question", qnaService.findById(id).get());
        return "qna/show";
    }

    @GetMapping("/{id}/form")
    public String showModify(@LoginUser User loginUser, @PathVariable long id, Model model){
        Question question = qnaService.findById(id).get();
        if(!question.isOwner(loginUser))
            throw new UnAuthorizedException("Invalid user");
        model.addAttribute("question", question);
        return "qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question otherQuestion){
        otherQuestion.writeBy(loginUser);
        qnaService.update(loginUser, id, otherQuestion);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

}
