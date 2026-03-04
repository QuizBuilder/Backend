package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.*;
import com.quizBuilder.project.Entity.Enum.Role;
import com.quizBuilder.project.Exception.BadRequestException;
import com.quizBuilder.project.Exception.ForbiddenException;
import com.quizBuilder.project.Exception.ResourceNotFoundException;
import com.quizBuilder.project.Exception.UnauthorizedException;
import com.quizBuilder.project.Model.Student.*;
import com.quizBuilder.project.Repository.QuestionRepository;
import com.quizBuilder.project.Repository.QuizRepository;
import com.quizBuilder.project.Repository.QuizSubmissionRepository;
import com.quizBuilder.project.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JWTService jwtService;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuizResponse getQuiz(String token, String code){

        if(!jwtService.validateToken(token)){
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user."));

        if(user.getRole() != Role.STUDENT){
            throw new ForbiddenException("Access denied.");
        }

        Quiz quiz = quizRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz code entered is invalid"));

        if(quiz.getEndTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException("You cannot enter quiz after end-time");
        }

        if(quiz.getStartTime().isAfter(LocalDateTime.now())){
            throw new BadRequestException("Quiz is not yet started");
        }

        List<QuestionResponse> questionResponses = new ArrayList<>();

        for(Question question : quiz.getQuestionList()){
            QuestionResponse elem = QuestionResponse.builder()
                    .questionId(question.getId())
                    .optAId(question.getOptions().get(0).getId())
                    .optBId(question.getOptions().get(1).getId())
                    .optCId(question.getOptions().get(2).getId())
                    .optDId(question.getOptions().get(3).getId())
                    .questionText(question.getText())
                    .optAText(question.getOptions().get(0).getText())
                    .optBText(question.getOptions().get(1).getText())
                    .optCText(question.getOptions().get(2).getText())
                    .optDText(question.getOptions().get(3).getText())
                    .build();

            questionResponses.add(elem);
        }
        QuizResponse quizResp = QuizResponse.builder().quizQuestions(questionResponses).endTime(quiz.getEndTime()).build();
        return quizResp;
    }


    public Long calculateScore(List<AnswerRequest> answerRequests){

        Long score = 0L;

        for(var answerRequest : answerRequests){

            Long questionId = answerRequest.getQuestionId();
            Long optionId = answerRequest.getSelectedOptionId();

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            for(var option : question.getOptions()){
                if(option.getId().equals(optionId) && option.isCorrect()){
                    score++;
                }
            }
        }

        return score;
    }


    @Transactional
    public QuizEndResponse submitQuiz(String token, List<AnswerRequest> answerRequests, String quizCode){

        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user."));

        if (user.getRole() != Role.STUDENT) {
            throw new ForbiddenException("Access denied.");
        }

        Quiz quiz = quizRepository.findByCode(quizCode)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid quiz code."));

        boolean isAttempted = quizSubmissionRepository.findByQuizAndUser(quiz, user) != null;

        if (isAttempted) {
            throw new BadRequestException("You cannot submit the quiz again");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = quiz.getEndTime();

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setUser(user);
        quizSubmission.setQuiz(quiz);

        Long score;

        if (now.isAfter(deadline)) {
            score = (answerRequests == null || answerRequests.isEmpty())
                    ? 0L
                    : calculateScore(answerRequests);
        } else {
            score = calculateScore(answerRequests);
        }

        quizSubmission.setScore(score);
        quizSubmissionRepository.save(quizSubmission);

        List<User> userList = quiz.getUserList();
        if (!userList.contains(user)) {
            userList.add(user);
            quiz.setUserList(userList);
        }

        QuizEndResponse quizEndResponse = new QuizEndResponse();
        quizEndResponse.setScore(score);

        return quizEndResponse;
    }


    public List<StudentQuizHistoryResponse> getAttemptedQuizInfo(String token){

        if(!jwtService.validateToken(token)){
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user."));

        if(user.getRole() != Role.STUDENT){
            throw new ForbiddenException("Access denied.");
        }

        List<Quiz> quizList = user.getQuizzesAttempted();

        if(quizList.isEmpty()){
            throw new ResourceNotFoundException("No quizzes are attempted");
        }

        List<StudentQuizHistoryResponse> data = new ArrayList<>();

        for(var quiz : quizList){

            QuizSubmission quizSubmission =
                    quizSubmissionRepository.findByQuizAndUser(quiz, user);

            StudentQuizHistoryResponse response =
                    StudentQuizHistoryResponse.builder()
                            .quizCode(quiz.getCode())
                            .topic(quiz.getTopic())
                            .difficulty(quiz.getDifficulty())
                            .score(quizSubmission.getScore())
                            .rank(quizSubmission.getQuizRank())
                            .build();

            data.add(response);
        }

        return data;
    }
}

