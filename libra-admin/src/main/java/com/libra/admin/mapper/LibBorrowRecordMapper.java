package com.libra.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.vo.AdminBorrowRecordVO;
import com.libra.admin.vo.BorrowTrendVO;
import com.libra.admin.vo.HotBookVO;
import com.libra.admin.vo.OverdueRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface LibBorrowRecordMapper extends BaseMapper<LibBorrowRecord> {

    @Select("SELECT DATE(borrow_time) as date, COUNT(*) as count " +
            "FROM lib_borrow_record " +
            "WHERE borrow_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY DATE(borrow_time) " +
            "ORDER BY date")
    List<Map<String, Object>> selectDailyBorrowTrend();

    @Select("SELECT DATE(borrow_time) as date, COUNT(*) as borrowCount " +
            "FROM lib_borrow_record " +
            "WHERE borrow_time >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(borrow_time) " +
            "ORDER BY date")
    List<BorrowTrendVO> selectBorrowTrend(@Param("days") int days);

    @Select("SELECT b.id AS bookId, b.title AS title, b.author AS author, COUNT(r.id) AS borrowCount " +
            "FROM lib_borrow_record r " +
            "JOIN lib_book b ON r.book_id = b.id " +
            "WHERE b.is_deleted = 0 " +
            "GROUP BY b.id, b.title, b.author " +
            "ORDER BY borrowCount DESC " +
            "LIMIT #{limit}")
    List<HotBookVO> selectHotBooks(@Param("limit") int limit);

    @Select("SELECT r.id AS recordId, COALESCE(u.nickname, u.username) AS userName, " +
            "b.title AS bookTitle, GREATEST(DATEDIFF(CURDATE(), r.due_time), 0) AS overdueDays " +
            "FROM lib_borrow_record r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN lib_book b ON r.book_id = b.id " +
            "WHERE r.status = 3 " +
            "ORDER BY r.due_time ASC " +
            "LIMIT #{limit}")
    List<OverdueRecordVO> selectOverdueRecords(@Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM lib_borrow_record WHERE DATE(borrow_time) = CURDATE()")
    long countBorrowToday();

    @Select("SELECT COUNT(*) FROM lib_borrow_record WHERE return_time IS NOT NULL AND DATE(return_time) = CURDATE()")
    long countReturnToday();

    @Select("SELECT r.id AS recordId, COALESCE(u.nickname, u.username) AS userName, " +
            "b.title AS bookTitle, r.borrow_time AS borrowTime, r.due_time AS dueTime, " +
            "r.return_time AS returnTime, r.status AS status, " +
            "GREATEST(DATEDIFF(CURDATE(), r.due_time), 0) AS overdueDays " +
            "FROM lib_borrow_record r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN lib_book b ON r.book_id = b.id " +
            "ORDER BY r.borrow_time DESC " +
            "LIMIT #{limit}")
    List<AdminBorrowRecordVO> selectBorrowRecords(@Param("limit") int limit);

    @Select("SELECT r.id AS recordId, COALESCE(u.nickname, u.username) AS userName, " +
            "b.title AS bookTitle, r.borrow_time AS borrowTime, r.due_time AS dueTime, " +
            "r.return_time AS returnTime, r.status AS status, " +
            "GREATEST(DATEDIFF(CURDATE(), r.due_time), 0) AS overdueDays " +
            "FROM lib_borrow_record r " +
            "LEFT JOIN sys_user u ON r.user_id = u.id " +
            "LEFT JOIN lib_book b ON r.book_id = b.id " +
            "WHERE r.status = #{status} " +
            "ORDER BY r.borrow_time DESC " +
            "LIMIT #{limit}")
    List<AdminBorrowRecordVO> selectBorrowRecordsByStatus(@Param("status") int status, @Param("limit") int limit);
}
