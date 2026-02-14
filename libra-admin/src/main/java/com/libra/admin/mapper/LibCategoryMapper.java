package com.libra.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libra.admin.entity.LibCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LibCategoryMapper extends BaseMapper<LibCategory> {
    
    @Select("SELECT * FROM lib_category WHERE id = #{id} AND is_deleted = 0")
    LibCategory selectById(Long id);
    
    @Select("SELECT c.* FROM lib_category c " +
            "WHERE c.is_deleted = 0 AND c.parent_id = 0 AND c.tenant_id = 0 " +
            "ORDER BY c.sort_order, c.id")
    List<LibCategory> selectCategoriesWithBooks();
}
