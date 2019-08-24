package com.bytecamp.dao;

import com.bytecamp.model.Order;
import com.bytecamp.model.OrderSearch;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper {
    long countByExample(OrderSearch example);

    int deleteByExample(OrderSearch example);

    int deleteByPrimaryKey(String id);

    int insert(Order record);

    int insertSelective(Order record);

    List<Order> selectByExample(OrderSearch example);

    Order selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderSearch example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderSearch example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    int upsert(Order record);

    int upsertSelective(Order record);

    @Update("truncate table orders")
    void truncate();
}