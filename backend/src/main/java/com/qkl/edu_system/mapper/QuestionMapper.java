package com.qkl.edu_system.mapper;

import com.qkl.edu_system.pojo.Question;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface QuestionMapper {

    // 修改：只查询启用状态的题目
    @Select("SELECT * FROM question WHERE exam_id = #{examId} AND state = 1 ORDER BY question_id")
    List<Question> findByExamId(@Param("examId") Integer examId);

    // 老师端查看所有题目（包括禁用的）
    @Select("SELECT * FROM question WHERE exam_id = #{examId} ORDER BY question_id")
    List<Question> findAllByExamId(@Param("examId") Integer examId);

    @Insert("INSERT INTO question (exam_id, question_id, question, answer, state, create_time, update_time) " +
            "SELECT #{examId}, IFNULL(MAX(question_id), 0) + 1, #{question}, #{answer}, #{state}, NOW(), NOW() " +
            "FROM question WHERE exam_id = #{examId}")
    void insert(Question question);

    @Update("UPDATE question SET question = #{question}, answer = #{answer}, " +
            "state = #{state}, update_time = NOW() WHERE exam_id = #{examId} AND question_id = #{questionId}")
    void update(Question question);

    @Delete("DELETE FROM question WHERE exam_id = #{examId} AND question_id = #{questionId}")
    void deleteByExamIdAndQuestionId(@Param("examId") Integer examId,
                                     @Param("questionId") Integer questionId);

    @Delete("DELETE FROM question WHERE exam_id = #{examId}")
    void deleteByExamId(@Param("examId") Integer examId);

    @Update("UPDATE question SET state = #{state}, update_time = NOW() " +
            "WHERE exam_id = #{examId} AND question_id = #{questionId}")
    void updateState(@Param("examId") Integer examId,
                     @Param("questionId") Integer questionId,
                     @Param("state") Integer state);

    @Select("SELECT * FROM question WHERE exam_id = #{examId}")
    List<Question> selectByExamId(Integer examId);


    @Select("SELECT * FROM question WHERE exam_id = #{examId} AND question_id = #{questionId}")
    Question findByExamIdAndQuestionId(@Param("examId") Integer examId,
                                       @Param("questionId") Integer questionId);
}