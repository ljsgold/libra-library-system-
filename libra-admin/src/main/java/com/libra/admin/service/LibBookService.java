package com.libra.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libra.admin.dto.BookAddDTO;
import com.libra.admin.entity.LibBook;
import com.libra.admin.entity.LibInventory;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibInventoryMapper;
import com.libra.common.core.annotation.Log;
import com.libra.common.exception.BusinessException;
import com.libra.common.utils.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibBookService {

    @Autowired
    private LibBookMapper bookMapper;

    @Autowired
    private LibInventoryMapper inventoryMapper;

    @Log(module = "图书管理", operation = "新增图书")
    @Transactional(rollbackFor = Exception.class)
    public void addBook(BookAddDTO dto) {
        // ISBN 唯一性校验
        long count = bookMapper.selectCount(new LambdaQueryWrapper<LibBook>()
                .eq(LibBook::getIsbn, dto.getIsbn()));
        if (count > 0) {
            throw new BusinessException("ISBN已存在");
        }

        LibBook book = BeanUtil.copyProperties(dto, LibBook.class);
        book.setTenantId(TenantContext.getTenantId());
        book.setTotalCount(dto.getCopyCount());
        book.setStockCount(dto.getCopyCount());
        book.setStatus(1); // 上架
        bookMapper.insert(book);

        // 生成馆藏副本
        List<LibInventory> inventories = new ArrayList<>();
        for (int i = 0; i < dto.getCopyCount(); i++) {
            LibInventory inventory = new LibInventory();
            inventory.setTenantId(TenantContext.getTenantId());
            inventory.setBookId(book.getId());
            inventory.setStatus(1); // 在馆
            inventories.add(inventory);
        }
        inventories.forEach(inventoryMapper::insert);
    }

    public Page<LibBook> listBooks(int pageNum, int pageSize, String title) {
        Page<LibBook> page = new Page<>(pageNum, pageSize);
        return bookMapper.selectPage(page, new LambdaQueryWrapper<LibBook>()
                .like(title != null, LibBook::getTitle, title)
                .orderByDesc(LibBook::getCreateTime));
    }
}
