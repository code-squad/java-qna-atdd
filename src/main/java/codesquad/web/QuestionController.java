package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by hoon on 2018. 1. 27..
 */
@Controller
@RequestMapping("questions")
public class QuestionController {

    private final QnaService qnaService;

    @Autowired
    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("form")
    public String questionForm() {
        return "qna/form";
    }

    @PostMapping
    public String createQuestion(@LoginUser User loginUser, QuestionDto questionDto) {
        qnaService.create(loginUser, questionDto.toQuestion());
        return "redirect:/";
    }

    @GetMapping("{idx}/form")
    public String updateForm(@LoginUser User loginUser, @PathVariable Long idx, Model model) {
        Question question = qnaService.findById(idx);
        if(!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", qnaService.findById(idx));
        return "qna/updateForm";
    }

    @GetMapping("{idx}")
    public String detail(@PathVariable Long idx, Model model) {
        if(idx == null) {
            return "redirect:/";
        }
        model.addAttribute("question", qnaService.findById(idx));
        return "qna/show";
    }

    @PutMapping("{idx}")
    public String doUpdate(@LoginUser User loginUser, @PathVariable Long idx, QuestionDto questionDto) {
        if(idx == null) {
            return "redirect:/";
        }
        qnaService.updateQuestion(idx, loginUser, questionDto);
        return "redirect:/questions/"+idx;
    }

    @DeleteMapping("{idx}")
    public String doDelete(@LoginUser User loginUser, @PathVariable Long idx) {
        if(idx == null) {
            return "redirect:/";
        }
        qnaService.deleteQuestion(loginUser, idx);
        return "redirect:/";
    }

    @PostMapping("{idx}/answers")
    public String createAnswer(@LoginUser User loginUser, @PathVariable Long idx, @RequestBody String comment) {
        if(idx == null || StringUtils.isEmpty(comment.trim())) {
            return "redirect:/";
        }
        qnaService.addAnswer(loginUser, idx, comment);
        return "redirect:/questions/"+idx;
    }

    @PutMapping("{idx}/answers/{answerIdx}")
    public String doAnswerUpdate(@LoginUser User loginUser, @PathVariable Long idx, @PathVariable Long answerIdx, @RequestBody AnswerDto answerDto) {
        if(idx == null || answerIdx == null || StringUtils.isEmpty(answerDto.getContents().trim())) {
            return "redirect:/";
        }
        qnaService.updateAnswer(loginUser, answerIdx, answerDto);
        return "redirect:/questions/"+idx;
    }

    @PostMapping("{idx}/answers/{answerIdx}")
    public String doAnswerDelete(@LoginUser User loginUser, @PathVariable Long idx, @PathVariable Long answerIdx) {
        if(idx == null || answerIdx == null) {
            return "redirect:/";
        }
        qnaService.deleteAnswer(loginUser, answerIdx);
        return "redirect:/questions/"+idx;
    }

}
