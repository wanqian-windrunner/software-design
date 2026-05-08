package com.qkl.edu_system.controller;

import com.qkl.edu_system.pojo.Exam;
import com.qkl.edu_system.pojo.Question;
import com.qkl.edu_system.pojo.Result;
import com.qkl.edu_system.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /* 个人信息 */
    @GetMapping("/profile")
    public Result profile(@RequestParam Integer teacherId) {
        return Result.success(teacherService.getProfile(teacherId));
    }

    @PutMapping("/update-username")
    public Result updateUsername(@RequestParam Integer teacherId,
                                 @RequestParam String username) {
        teacherService.updateUsername(teacherId, username);
        return Result.success();
    }

    @PutMapping("/update-password")
    public Result updatePassword(@RequestParam Integer teacherId,
                                 @RequestParam String password) {
        teacherService.updatePassword(teacherId, password);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result deleteAccount(@RequestParam Integer teacherId) {
        teacherService.deleteAccount(teacherId);
        return Result.success();
    }

    /* 学生管理 */
    @GetMapping("/students")
    public Result studentList(@RequestParam Integer clazzId) {
        return Result.success(teacherService.listStudents(clazzId));
    }

    @PutMapping("/student/verify")
    public Result verifyStudent(@RequestParam Integer studentId) {
        teacherService.approveStudent(studentId);
        return Result.success();
    }

    @DeleteMapping("/student")
    public Result removeStudent(@RequestParam Integer clazzId,
                                @RequestParam Integer studentId) {
        teacherService.removeStudent(clazzId, studentId);
        return Result.success("学生已踢出");
    }

    @GetMapping("/student/exam-records")
    public Result studentExamRecords(@RequestParam Integer studentId) {
        return Result.success(teacherService.getStudentExamRecords(studentId));
    }

    /* 考试管理 */
    @GetMapping("/exams")
    public Result examList(@RequestParam Integer clazzId) {
        return Result.success(teacherService.listExams(clazzId));
    }

    @PostMapping("/exam")
    public Result createExam(@RequestBody Exam exam) {
        teacherService.createExam(exam);
        return Result.success();
    }

    @PutMapping("/exam/state")
    public Result updateExamState(@RequestParam Integer examId,
                                  @RequestParam Integer state) {
        teacherService.updateExamState(examId, state);
        return Result.success();
    }

    @DeleteMapping("/exam")
    public Result deleteExam(@RequestParam Integer examId) {
        teacherService.deleteExam(examId);
        return Result.success();
    }

    /* 题库管理 */
    @GetMapping("/questions")
    public Result questionList(@RequestParam Integer examId) {
        log.info("获取考试题目列表, examId: {}", examId);  // 添加日志
        return Result.success(teacherService.listQuestions(examId));
    }

    @PostMapping("/question")
    public Result addQuestion(@RequestBody Question question) {
        log.info("新增题目: {}", question);  // 添加日志
        teacherService.addQuestion(question);
        return Result.success();
    }

    @PutMapping("/question")
    public Result updateQuestion(@RequestBody Question question) {
        teacherService.updateQuestion(question);
        return Result.success();
    }

    @DeleteMapping("/question")
    public Result deleteQuestion(@RequestParam Integer examId,
                                 @RequestParam Integer questionId) {
        log.info("删除题目, examId: {}, questionId: {}", examId, questionId);  // 添加日志
        teacherService.deleteQuestion(examId, questionId);
        return Result.success();
    }

    @PutMapping("/question/state")
    public Result updateQuestionState(@RequestParam Integer examId,
                                      @RequestParam Integer questionId,
                                      @RequestParam Integer state) {
        teacherService.updateQuestionState(examId, questionId, state);
        return Result.success();
    }

    /* 考试成绩管理 */
    @GetMapping("/exam/scores")
    public Result getExamScores(@RequestParam Integer examId) {
        return Result.success(teacherService.getExamScores(examId));
    }

    @GetMapping("/exam/score/detail")
    public Result getScoreDetail(@RequestParam Integer testId) {
        return Result.success(teacherService.getScoreDetail(testId));
    }
}