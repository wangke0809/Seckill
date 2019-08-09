package com.bytecamp.dao;

import com.bytecamp.model.Sessions;
import com.bytecamp.model.SessionsSearch;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SessionsMapper {
    long countByExample(SessionsSearch example);

    int deleteByExample(SessionsSearch example);

    int deleteByPrimaryKey(Integer id);

    int insert(Sessions record);

    int insertSelective(Sessions record);

    List<Sessions> selectByExample(SessionsSearch example);

    Sessions selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Sessions record, @Param("example") SessionsSearch example);

    int updateByExample(@Param("record") Sessions record, @Param("example") SessionsSearch example);

    int updateByPrimaryKeySelective(Sessions record);

    int updateByPrimaryKey(Sessions record);

    /**
     * 这是Mybatis Generator拓展插件生成的方法(请勿删除).
     * This method corresponds to the database table sessions
     *
     * @mbg.generated
     * @author hewei
     */
    int upsert(Sessions record);

    /**
     * 这是Mybatis Generator拓展插件生成的方法(请勿删除).
     * This method corresponds to the database table sessions
     *
     * @mbg.generated
     * @author hewei
     */
    int upsertSelective(Sessions record);
}