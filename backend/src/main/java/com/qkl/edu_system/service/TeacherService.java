package com.qkl.edu_system.service;

import com.qkl.edu_system.pojo.*;

import java.util.List;

public interface TeacherService {

    /* 个人信息 */
    User getProfile(Integer teacherId);
    void updateUsername(Integer teacherId, String username);
    void updatePassword(Integer teacherId, String password);
    void deleteAccount(Integer teacherId);

    /* 学生管理 */
    List<User> listStudents(Integer clazzId);
    void updateVerify(Integer studentId, Integer verify);
    void removeStudent(Integer clazzId,Integer studentId);
    List<ExamRecord> getStudentExamRecords(Integer studentId);

    /* 考试 */
    List<Exam> listExams(Integer clazzId);
    void createExam(Exam exam);
    void updateExamState(Integer examId, Integer state);

    /* 题库 */
    List<Question> listQuestions(Integer examId);
    void addQuestion(Question question);
    void updateQuestion(Question question);
    void deleteQuestion(Integer examId, Integer questionId);
    void updateQuestionState(Integer examId, Integer questionId, Integer state);


    void approveStudent(Integer studentId);

    void deleteExam(Integer examId);

    // 在 TeacherService 接口中添加
    List<Score> getExamScores(Integer examId);
    Score getScoreDetail(Integer testId);
}