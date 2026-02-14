package com.libra.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libra.admin.domain.borrow.BorrowStatus;
import com.libra.admin.entity.LibBook;
import com.libra.admin.entity.LibBorrowRecord;
import com.libra.admin.entity.LibInventory;
import com.libra.admin.entity.LibReservation;
import com.libra.admin.entity.SysUser;
import com.libra.admin.entity.LibCategory;
import com.libra.admin.mapper.LibBookMapper;
import com.libra.admin.mapper.LibBorrowRecordMapper;
import com.libra.admin.mapper.LibCategoryMapper;
import com.libra.admin.mapper.LibInventoryMapper;
import com.libra.admin.mapper.LibReservationMapper;
import com.libra.admin.mapper.SysUserMapper;
import com.libra.admin.vo.*;
import com.libra.common.core.constant.ResultCode;
import com.libra.common.exception.BusinessException;
import com.libra.framework.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserPortalService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Autowired
    private LibBookMapper bookMapper;

    @Autowired
    private LibBorrowRecordMapper borrowRecordMapper;

    @Autowired
    private LibInventoryMapper inventoryMapper;

    @Autowired
    private LibReservationMapper reservationMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private LibBorrowService borrowService;

    @Autowired
    private LibCategoryMapper categoryMapper;

    private Long requireUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未授权");
        }
        return userId;
    }

    private String categoryName(Long categoryId) {
        if (categoryId == null) return "未分类";
        LibCategory category = categoryMapper.selectById(categoryId);
        return category != null ? category.getCategoryName() : "未分类";
    }

    private BookItemVO toBookItem(LibBook book) {
        BookItemVO vo = new BookItemVO();
        vo.setId(book.getId());
        vo.setTitle(book.getTitle());
        vo.setAuthor(book.getAuthor());
        vo.setIsbn(book.getIsbn());
        vo.setCoverUrl(book.getCoverUrl());
        vo.setCategoryName(categoryName(book.getCategoryId()));
        int total = Math.toIntExact(inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, book.getId())));
        int available = Math.toIntExact(inventoryMapper.selectCount(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, book.getId())
                .eq(LibInventory::getStatus, 1)));
        vo.setTotalCount(total);
        vo.setAvailableCount(available);
        vo.setCanBorrow(available > 0 && Optional.ofNullable(book.getStatus()).orElse(1) == 1);
        return vo;
    }

    public List<BookItemVO> getNewBooks(int limit) {
        QueryWrapper<LibBook> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0).eq("status", 1).orderByDesc("create_time").last("limit " + limit);
        List<LibBook> books = bookMapper.selectList(wrapper);
        return books.stream().map(this::toBookItem).collect(Collectors.toList());
    }

    public List<BookItemVO> getPopularBooks(int limit) {
        QueryWrapper<LibBook> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0).eq("status", 1)
                .orderByDesc("(total_count - stock_count)")
                .orderByDesc("create_time")
                .last("limit " + limit);
        List<LibBook> books = bookMapper.selectList(wrapper);
        if (books.isEmpty()) {
            return getNewBooks(limit);
        }
        return books.stream().map(this::toBookItem).collect(Collectors.toList());
    }

    public List<CategoryItemVO> getCategories() {
        List<LibCategory> categories = categoryMapper.selectCategoriesWithBooks();
        return categories.stream()
                .map(c -> new CategoryItemVO(c.getId(), c.getCategoryName()))
                .collect(Collectors.toList());
    }

    public PageResult<BookItemVO> searchBooks(String keyword, Long categoryId, Boolean onlyAvailable, String sort, int page, int size) {
        Page<LibBook> mpPage = new Page<>(page, size);
        QueryWrapper<LibBook> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0).eq("status", 1);
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like("title", keyword)
                    .or().like("author", keyword)
                    .or().like("isbn", keyword));
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (onlyAvailable != null && onlyAvailable) {
            wrapper.gt("stock_count", 0);
        }
        if ("popular".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc("(total_count - stock_count)");
        } else if ("available".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc("stock_count");
        } else {
            wrapper.orderByDesc("create_time");
        }

        Page<LibBook> result = bookMapper.selectPage(mpPage, wrapper);
        List<BookItemVO> records = result.getRecords().stream().map(this::toBookItem).collect(Collectors.toList());
        return new PageResult<>(records, result.getTotal());
    }

    public BookDetailVO getBookDetail(Long id) {
        LibBook book = bookMapper.selectById(id);
        if (book == null || Optional.ofNullable(book.getIsDeleted()).orElse(0) == 1) {
            return null;
        }
        BookDetailVO vo = new BookDetailVO();
        BookItemVO base = toBookItem(book);
        vo.setId(base.getId());
        vo.setTitle(base.getTitle());
        vo.setAuthor(base.getAuthor());
        vo.setIsbn(base.getIsbn());
        vo.setCategoryName(base.getCategoryName());
        vo.setCoverUrl(base.getCoverUrl());
        vo.setCanBorrow(base.isCanBorrow());
        vo.setAvailableCount(base.getAvailableCount());
        vo.setTotalCount(base.getTotalCount());
        vo.setDescription(book.getSummary());
        vo.setPublisher(book.getPublisher());
        vo.setPublishDate(book.getPubDate() == null ? null : book.getPubDate().format(DATE_FORMAT));

        LibInventory inventory = inventoryMapper.selectOne(new LambdaQueryWrapper<LibInventory>()
                .eq(LibInventory::getBookId, id)
                .eq(LibInventory::getStatus, 1)
                .last("limit 1"));
        if (inventory != null && inventory.getLocation() != null) {
            vo.setLocationInfo(inventory.getLocation());
        }
        return vo;
    }

    public void borrowBook(Long bookId) {
        Long userId = requireUserId();
        com.libra.admin.dto.BorrowDTO dto = new com.libra.admin.dto.BorrowDTO();
        dto.setBookId(bookId);
        dto.setUserId(userId);
        borrowService.borrowBook(dto);
    }

    public void reserveBook(Long bookId) {
        Long userId = requireUserId();
        long existing = reservationMapper.selectCount(new LambdaQueryWrapper<LibReservation>()
                .eq(LibReservation::getUserId, userId)
                .eq(LibReservation::getBookId, bookId)
                .in(LibReservation::getStatus, 0, 1));
        if (existing > 0) {
            throw new BusinessException("已预约");
        }
        Long waitingCount = reservationMapper.selectCount(new LambdaQueryWrapper<LibReservation>()
                .eq(LibReservation::getBookId, bookId)
                .eq(LibReservation::getStatus, 0));
        int queueNo = (waitingCount == null ? 0 : waitingCount.intValue()) + 1;

        LibReservation reservation = new LibReservation();
        reservation.setTenantId(SecurityUtils.getTenantId());
        reservation.setUserId(userId);
        reservation.setBookId(bookId);
        reservation.setStatus(0);
        reservation.setQueueNo(queueNo);
        reservation.setExpectedDate(LocalDate.now().plusDays(7));
        reservation.setPickupDeadline(LocalDate.now().plusDays(10));
        reservation.setNotified(0);
        reservationMapper.insert(reservation);
    }

    public List<BorrowRecordVO> getCurrentBorrowList() {
        Long userId = requireUserId();
        List<LibBorrowRecord> records = borrowRecordMapper.selectList(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getUserId, userId)
                .in(LibBorrowRecord::getStatus, 1, 3)
                .orderByDesc(LibBorrowRecord::getBorrowTime));
        return buildBorrowVO(records, true);
    }

    public List<BorrowRecordVO> getHistoryBorrowList() {
        Long userId = requireUserId();
        List<LibBorrowRecord> records = borrowRecordMapper.selectList(new LambdaQueryWrapper<LibBorrowRecord>()
                .eq(LibBorrowRecord::getUserId, userId)
                .in(LibBorrowRecord::getStatus, 2, 4)
                .orderByDesc(LibBorrowRecord::getBorrowTime));
        return buildBorrowVO(records, false);
    }

    private List<BorrowRecordVO> buildBorrowVO(List<LibBorrowRecord> records, boolean current) {
        if (records.isEmpty()) return new ArrayList<>();
        Set<Long> bookIds = records.stream().map(LibBorrowRecord::getBookId).collect(Collectors.toSet());
        // Use in-query to fetch books by id and build a map for quick lookup
        List<LibBook> books = bookMapper.selectList(new LambdaQueryWrapper<LibBook>().in(LibBook::getId, bookIds));
        Map<Long, LibBook> bookMap = books.stream().collect(Collectors.toMap(LibBook::getId, b -> b));
        List<BorrowRecordVO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (LibBorrowRecord r : records) {
            LibBook book = bookMap.get(r.getBookId());
            BorrowRecordVO vo = new BorrowRecordVO();
            vo.setId(r.getId());
            vo.setBookTitle(book == null ? "" : book.getTitle());
            vo.setAuthor(book == null ? "" : book.getAuthor());
            vo.setBorrowDate(r.getBorrowTime() == null ? null : r.getBorrowTime().toLocalDate().format(DATE_FORMAT));
            vo.setDueDate(r.getDueTime() == null ? null : r.getDueTime().toLocalDate().format(DATE_FORMAT));
            vo.setReturnDate(r.getReturnTime() == null ? null : r.getReturnTime().toLocalDate().format(DATE_FORMAT));
            if (current) {
                BorrowStatus status = r.getBorrowStatus();
                vo.setStatus(status == BorrowStatus.OVERDUE ? "OVERDUE" : "BORROWING");
                vo.setRenewable(status == BorrowStatus.BORROWING && (r.getDueTime() == null || r.getDueTime().isAfter(now)));
            } else {
                BorrowStatus status = r.getBorrowStatus();
                vo.setStatus(status == BorrowStatus.COMPENSATED ? "OVERDUE" : "RETURNED");
                vo.setRenewable(false);
            }
            result.add(vo);
        }
        return result;
    }

    public void renewBorrow(Long recordId) {
        Long userId = requireUserId();
        LibBorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null || !Objects.equals(record.getUserId(), userId)) {
            throw new BusinessException(ResultCode.BORROW_RECORD_NOT_FOUND);
        }
        BorrowStatus status = record.getBorrowStatus();
        if (status != BorrowStatus.BORROWING) {
            throw new BusinessException("借阅记录不可续借");
        }
        record.setDueTime(record.getDueTime().plusDays(30));
        borrowRecordMapper.updateById(record);
    }

    public void returnBorrow(Long recordId) {
        Long userId = requireUserId();
        LibBorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null || !Objects.equals(record.getUserId(), userId)) {
            throw new BusinessException(ResultCode.BORROW_RECORD_NOT_FOUND);
        }
        if (!record.isBorrowingOrOverdue()) {
            throw new BusinessException("借阅记录不可归还");
        }
        borrowService.returnBook(record.getInventoryId());
    }

    public UserProfileVO getProfile() {
        Long userId = requireUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) return null;
        UserProfileVO vo = new UserProfileVO();
        String name = user.getNickname() != null ? user.getNickname() : user.getUsername();
        vo.setName(name);
        String username = user.getUsername();
        if (username != null && username.matches("\\d{6,}")) {
            vo.setStudentNo(username);
        } else {
            vo.setEmployeeNo(username);
        }
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        boolean isAdmin = username != null && username.toLowerCase().startsWith("admin");
        vo.setIsAdmin(isAdmin);
        return vo;
    }

    public List<BorrowRuleItemVO> getBorrowRules() {
        List<BorrowRuleItemVO> rules = new ArrayList<>();
        rules.add(new BorrowRuleItemVO("借阅期限", "每次可借 30 天"));
        rules.add(new BorrowRuleItemVO("续借规则", "每本图书可续借 1 次，每次 30 天"));
        rules.add(new BorrowRuleItemVO("逾期说明", "逾期请尽快归还，超期可能产生费用"));
        return rules;
    }

    public List<ReservationRecordVO> getReservationList() {
        Long userId = requireUserId();
        List<LibReservation> records = reservationMapper.selectList(new LambdaQueryWrapper<LibReservation>()
                .eq(LibReservation::getUserId, userId)
                .orderByDesc(LibReservation::getCreateTime));
        if (records.isEmpty()) return new ArrayList<>();
        Set<Long> bookIds = records.stream().map(LibReservation::getBookId).collect(Collectors.toSet());
        List<LibBook> books = bookMapper.selectList(new LambdaQueryWrapper<LibBook>().in(LibBook::getId, bookIds));
        Map<Long, LibBook> bookMap = books.stream()
                .collect(Collectors.toMap(LibBook::getId, b -> b));
        List<ReservationRecordVO> result = new ArrayList<>();
        for (LibReservation r : records) {
            LibBook book = bookMap.get(r.getBookId());
            ReservationRecordVO vo = new ReservationRecordVO();
            vo.setId(r.getId());
            vo.setBookTitle(book == null ? "" : book.getTitle());
            vo.setAuthor(book == null ? "" : book.getAuthor());
            vo.setQueueNo(r.getQueueNo());
            vo.setExpectedDate(r.getExpectedDate() == null ? null : r.getExpectedDate().format(DATE_FORMAT));
            vo.setPickupDeadline(r.getPickupDeadline() == null ? null : r.getPickupDeadline().format(DATE_FORMAT));
            vo.setNotified(r.getNotified() != null && r.getNotified() == 1);
            vo.setCreatedAt(r.getCreateTime() == null ? null : r.getCreateTime().toLocalDate().format(DATE_FORMAT));
            String status;
            if (r.getStatus() == null || r.getStatus() == 0) status = "WAITING";
            else if (r.getStatus() == 1) status = "NOTIFIED";
            else status = "CANCELLED";
            vo.setStatus(status);
            result.add(vo);
        }
        return result;
    }

    public void cancelReservation(Long id) {
        Long userId = requireUserId();
        LibReservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !Objects.equals(reservation.getUserId(), userId)) {
            throw new BusinessException("预约记录不存在");
        }
        reservation.setStatus(2);
        reservationMapper.updateById(reservation);
    }

    public void subscribeArrival(Long id) {
        Long userId = requireUserId();
        LibReservation reservation = reservationMapper.selectById(id);
        if (reservation == null || !Objects.equals(reservation.getUserId(), userId)) {
            throw new BusinessException("预约记录不存在");
        }
        reservation.setNotified(1);
        reservationMapper.updateById(reservation);
    }
}
