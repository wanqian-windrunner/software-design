package com.qkl.edu_system.mapper;

import com.qkl.edu_system.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("""
        INSERT INTO user(username, password, identity, clazz_id)
        VALUES(#{username}, #{password}, #{identity}, #{clazzId})
    """)
    void insertUser(User user);

    @Select("""
        SELECT * FROM user WHERE username = #{username}
    """)
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Integer id);

    @Update("UPDATE user SET username = #{username} WHERE id = #{userId}")
    void updateUsername(@Param("userId") Integer userId,
                        @Param("username") String username);

    @Update("UPDATE user SET password = #{password} WHERE id = #{userId}")
    void updatePassword(@Param("userId") Integer userId,
                        @Param("password") String password);

    @Update("""
        UPDATE user
        SET clazz_id = #{clazzId}, verify = 0
        WHERE id = #{userId}
    """)
    void updateClazz(@Param("userId") Integer userId,
                     @Param("clazzId") Integer clazzId);

    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteById(Integer id);



    @Select("SELECT * FROM user WHERE clazz_id = #{clazzId}")
    List<User> selectByClazz(Integer clazzId);

    @Update("UPDATE user SET verify = #{verify} WHERE id = #{studentId}")
    void updateVerify(@Param("studentId") Integer studentId,
                      @Param("verify") Integer verify);

    @Select("""
    SELECT id, username, clazz_id, verify
    FROM user
    WHERE clazz_id = #{clazzId}
    AND identity = 0
""")
    List<User> selectStudentsByClazz(@Param("clazzId") Integer clazzId);

    @Update("UPDATE user SET username = #{username}, password = #{password}, " +
            "identity = #{identity}, clazz_id = #{clazzId}, verify = #{verify} " +
            "WHERE id = #{id}")
    void updateById(User user);

    @Select("SELECT DISTINCT clazz_id FROM user WHERE clazz_id IS NOT NULL AND clazz_id > 0")
    List<Integer> selectDistinctClazzIds();

    @Update("UPDATE user SET clazz_id = #{clazzId}, verify = 0  WHERE id = #{userId}")
    void updateClazzId(Integer userId, Integer clazzId);
    
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
}