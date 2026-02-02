package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.Quiz;
import com.quizBuilder.project.Entity.QuizSubmission;
import com.quizBuilder.project.Entity.User;
import com.quizBuilder.project.Model.Student.LeaderBoardResponse;
import com.quizBuilder.project.Repository.QuizRepository;
import com.quizBuilder.project.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JWTService jwtService;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    @Transactional
    public List<LeaderBoardResponse> getLeaderBoard(String token, String quizCode) {
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);

        Quiz quiz = quizRepository.findByCode(quizCode).orElse(null);

        if(quiz.getEndTime().isAfter(LocalDateTime.now().plusMinutes(5))){
            throw new RuntimeException("You can see the leaderboard after the quiz");
        }

        List<QuizSubmission> submissionList = quiz.getQuizSubmissionList();
        System.out.println(submissionList);

        submissionList.sort(
                Comparator.comparing(QuizSubmission::getScore).reversed()
        );


         List<LeaderBoardResponse> response = new ArrayList<>();
         Long rank = 1L;
         for(var submission:submissionList){
             LeaderBoardResponse leaderBoardResponse = LeaderBoardResponse.builder()
                     .name(user.getName()).score(submission.getScore()).rank(rank).build();
             submission.setQuizRank(rank);
             rank++;
             response.add(leaderBoardResponse);
         }
        quiz.setQuizSubmissionList(submissionList);
         return response;
    }
}
