package com.bytecamp.dao;

import com.bytecamp.model.Product;
import com.bytecamp.model.ProductSearch;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper {
    long countByExample(ProductSearch example);

    int deleteByExample(ProductSearch example);

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    List<Product> selectByExample(ProductSearch example);

    Product selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Product record, @Param("example") ProductSearch example);

    int updateByExample(@Param("record") Product record, @Param("example") ProductSearch example);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int upsert(Product record);

    int upsertSelective(Product record);
}