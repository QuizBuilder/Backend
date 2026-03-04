package com.quizBuilder.project.Contoller;

import com.quizBuilder.project.Model.Student.*;
import com.quizBuilder.project.Model.Teacher.GenQuizInfoResponse;
import com.quizBuilder.project.Model.Teacher.GenQuizzesResponse;
import com.quizBuilder.project.Model.Teacher.QuizGenerationRequest;
import com.quizBuilder.project.Model.Teacher.QuizGenerationResponse;
import com.quizBuilder.project.Service.StudentService;
import com.quizBuilder.project.Service.TeacherService;
import com.quizBuilder.project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final UserService userService;
    @PostMapping("/teacher/generate_quiz")
    public ResponseEntity<QuizGenerationResponse> generateQuiz(@RequestHeader("Authorization") String token, @RequestBody QuizGenerationRequest request){
        try {

            QuizGenerationResponse data = teacherService.generateAIQuiz(token, request);

            return ResponseEntity.ok(data);
        }catch (Exception e){
                e.printStackTrace();
                throw e;

        }
    }

    @GetMapping("/teacher/quizzes")
    public ResponseEntity<List<GenQuizzesResponse>> getGeneratedQuizzes(@RequestHeader("Authorization") String token){
        List<GenQuizzesResponse> data = teacherService.getGeneratedQuizzes(token);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/teacher/quizzes/{quiz_code}")
    public ResponseEntity<GenQuizInfoResponse> getQuizInfo(@RequestHeader("Authorization") String token, @PathVariable("quiz_code") String quizCode){

        GenQuizInfoResponse data = teacherService.getQuizInfo(token, quizCode);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/student/quiz/getquiz/{quiz_code}")
    public ResponseEntity<QuizResponse> getQuiz(@RequestHeader("Authorization") String token, @PathVariable("quiz_code") String quizCode){
        QuizResponse data = studentService.getQuiz(token, quizCode);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/student/quiz/submit/{quiz_code}")
    public ResponseEntity<QuizEndResponse> submitQuiz(@RequestHeader("Authorization") String token, @RequestBody List<AnswerRequest> answerRequests, @PathVariable("quiz_code") String quizCode){
        QuizEndResponse data = studentService.submitQuiz(token, answerRequests, quizCode);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/quiz/{quiz_code}/leaderboard")
    public ResponseEntity<List<LeaderBoardResponse>> getLeaderBoard(@RequestHeader("Authorization") String token, @PathVariable("quiz_code") String quizCode){
        List<LeaderBoardResponse> data = userService.getLeaderBoard(token, quizCode);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/student/quizzes/history")
    public ResponseEntity<List<StudentQuizHistoryResponse>> getAttemptedQuizInfo(@RequestHeader("Authorization") String token){
        List<StudentQuizHistoryResponse> data = studentService.getAttemptedQuizInfo(token);
        return ResponseEntity.ok(data);
    }

}
