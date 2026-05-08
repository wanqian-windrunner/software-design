package com.qkl.edu_system.controller;

import com.qkl.edu_system.mapper.UserMapper;
import com.qkl.edu_system.pojo.Result;
import com.qkl.edu_system.pojo.SubmitQuestionRequest;
import com.qkl.edu_system.pojo.User;
import com.qkl.edu_system.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 检查考试相关权限（需要审核通过 + 在班级中）
     */
    private User checkExamPermission(Integer studentId) {
        User user = userMapper.selectById(studentId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getVerify() == null || user.getVerify() != 1) {
            throw new RuntimeException("未通过审核，无法操作");
        }
        if (user.getClazzId() == null || user.getClazzId() == 0) {
            throw new RuntimeException("您已被移出班级，请重新登录");
        }
        return user;
    }

    /**
     * 检查基本状态（只检查用户是否存在，不检查审核状态）
     */
    private User checkUserExists(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    /* 个人信息 */
    @GetMapping("/profile")
    public Result profile(@RequestParam Integer userId) {
        try {
            checkUserExists(userId);
            return Result.success(studentService.getProfile(userId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 修改用户名 */
    @PutMapping("/update-username")
    public Result updateUsername(@RequestParam Integer userId,
                                 @RequestParam String username) {
        try {
            checkUserExists(userId);
            studentService.updateUsername(userId, username);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 修改密码 */
    @PutMapping("/update-password")
    public Result updatePassword(@RequestParam Integer userId,
                                 @RequestParam String password) {
        try {
            checkUserExists(userId);
            studentService.updatePassword(userId, password);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 更换班级（不检查审核状态，允许未审核的学生申请换班） */
    @PutMapping("/change-clazz")
    public Result changeClazz(@RequestParam Integer userId,
                              @RequestParam Integer clazzId) {
        try {
            checkUserExists(userId);
            studentService.changeClazz(userId, clazzId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 删除账号 */
    @DeleteMapping("/delete")
    public Result deleteAccount(@RequestParam Integer userId) {
        try {
            checkUserExists(userId);
            studentService.deleteAccount(userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 考试列表 - 需要审核通过 */
    @GetMapping("/exams")
    public Result getExams(@RequestParam Integer clazzId,
                           @RequestParam Integer studentId) {
        try {
            User user = checkExamPermission(studentId);

            if (!user.getClazzId().equals(clazzId)) {
                return Result.error("班级不匹配，请重新登录");
            }

            return Result.success(studentService.getExamsWithStatus(clazzId, studentId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取考试列表失败");
        }
    }

    /* 考试结果 - 需要审核通过 */
    @GetMapping("/exam/result")
    public Result getExamResult(@RequestParam Integer examId,
                                @RequestParam Integer studentId) {
        try {
            checkExamPermission(studentId);
            return Result.success(studentService.getExamResult(examId, studentId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 已答题记录 - 需要审核通过 */
    @GetMapping("/exam/answers")
    public Result getAnswers(@RequestParam Integer examId,
                             @RequestParam Integer studentId) {
        try {
            checkExamPermission(studentId);
            return Result.success(studentService.getStudentAnswers(examId, studentId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 获取考试题目 - 需要审核通过 */
    @GetMapping("/exam/questions")
    public Result getExamQuestions(@RequestParam Integer examId,
                                   @RequestParam(required = false) Integer studentId) {
        // 如果传了 studentId 就检查权限
        if (studentId != null) {
            try {
                checkExamPermission(studentId);
            } catch (RuntimeException e) {
                return Result.error(e.getMessage());
            }
        }
        return Result.success(studentService.getExamQuestions(examId));
    }

    /* 提交答案 - 需要审核通过 */
    @PostMapping("/exam/submit-question")
    public Result submitQuestion(@RequestBody SubmitQuestionRequest request) {
        try {
            checkExamPermission(request.getStudentId());
            return Result.success(studentService.submitQuestion(request));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 开始考试 - 需要审核通过 */
    @PostMapping("/exam/start")
    public Result startExam(@RequestParam Integer examId,
                            @RequestParam Integer studentId) {
        try {
            checkExamPermission(studentId);
            studentService.startExam(examId, studentId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 完成考试 - 需要审核通过 */
    @PostMapping("/exam/finish")
    public Result finishExam(@RequestParam Integer examId,
                             @RequestParam Integer studentId) {
        try {
            checkExamPermission(studentId);
            return Result.success(studentService.finishExam(examId, studentId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /* 检查状态（供前端调用） */
    @GetMapping("/check-status")
    public Result checkStatus(@RequestParam Integer studentId) {
        try {
            checkExamPermission(studentId);
            return Result.success("正常");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}