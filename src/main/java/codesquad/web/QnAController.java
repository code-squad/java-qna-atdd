package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/questions")
public class QnAController {
    private static final Logger log = LoggerFactory.getLogger(QnAController.class);

    @Autowired
    private QnaService qnaService;

    @GetMapping("/form")
    public String form() {
        return "/qna/form";
    }

    @PostMapping
    public String create(@LoginUser User user, QuestionDto questionDto) {
        Question question = new Question(questionDto.getTitle(),questionDto.getContents());

        qnaService.create(user, question);
        return "redirect:/questions";
    }

    @GetMapping
    public String list(@PageableDefault Pageable pageable, Model model) {
        List<Question> questions = qnaService.findAll(pageable);
        log.debug("questions size : {}", questions.size());
        model.addAttribute("contents", questions);
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable long id, Model model) {
        model.addAttribute("contents", qnaService.findById(id));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, QuestionDto target) {
        log.debug("question -- {}",target.getTitle());
        qnaService.update(loginUser,id,target);
        return "redirect:/questions";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser,@PathVariable long id) throws CannotDeleteException {
        log.debug("delete {}", id);
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }

}
