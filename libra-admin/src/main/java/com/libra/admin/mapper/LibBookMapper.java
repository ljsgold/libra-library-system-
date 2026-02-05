package com.libra.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libra.admin.entity.LibBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface LibBookMapper extends BaseMapper<LibBook> {
    
    @Update("UPDATE lib_book SET stock_count = stock_count + #{delta} " +
            "WHERE id = #{id} AND stock_count + #{delta} >= 0 AND is_deleted = 0")
    int updateStock(@Param("id") Long id, @Param("delta") int delta);

    @Select("SELECT COALESCE(SUM(total_count), 0) FROM lib_book WHERE is_deleted = 0")
    long selectSumTotalCount();

    @Select("SELECT COALESCE(SUM(stock_count), 0) FROM lib_book WHERE is_deleted = 0")
    long selectSumStockCount();

    @Select("SELECT DISTINCT category_id FROM lib_book WHERE is_deleted = 0 AND category_id IS NOT NULL ORDER BY category_id")
    List<Long> selectCategoryIds();
}
