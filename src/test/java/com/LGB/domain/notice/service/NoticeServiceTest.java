package com.LGB.domain.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.LGB.domain.notice.dto.CreateNoticeRequest;
import com.LGB.domain.notice.dto.NoticeDetailResponse;
import com.LGB.domain.notice.dto.NoticePageResponse;
import com.LGB.domain.notice.dto.UpdateNoticeRequest;
import com.LGB.domain.notice.entity.Notice;
import com.LGB.domain.notice.repository.NoticeRepository;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import com.LGB.global.security.RoleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Test
    void createNoticeWithActiveAdminAuthor() {
        User admin = user(1L, "Admin", RoleType.ADMIN);
        CreateNoticeRequest request = new CreateNoticeRequest("Title", "Content", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(noticeRepository.save(any(Notice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoticeDetailResponse response = noticeService.create(1L, request);

        assertThat(response.title()).isEqualTo("Title");
        assertThat(response.content()).isEqualTo("Content");
        assertThat(response.pinned()).isFalse();
        assertThat(response.author().name()).isEqualTo("Admin");
    }

    @Test
    void createRejectsMissingAuthor() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> noticeService.create(
                99L,
                new CreateNoticeRequest("Title", "Content", false)
        ))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FOUND.getMessage());
    }

    @Test
    void getNoticesReturnsPinnedThenNewestOrderFromRepository() {
        User admin = user(1L, "Admin", RoleType.ADMIN);
        Notice pinned = notice(1L, "Pinned", admin, true, LocalDateTime.of(2026, 6, 1, 10, 0), 0);
        Notice newest = notice(2L, "Newest", admin, false, LocalDateTime.of(2026, 6, 2, 10, 0), 0);
        Pageable pageable = PageRequest.of(0, 10);
        when(noticeRepository.findAllByOrderByPinnedDescCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(pinned, newest), pageable, 2));

        NoticePageResponse response = noticeService.getNotices(0, 10);

        assertThat(response.content()).extracting("title").containsExactly("Pinned", "Newest");
        assertThat(response.page()).isZero();
        assertThat(response.totalElements()).isEqualTo(2);
    }

    @Test
    void getNoticeIncrementsViewCountAndReturnsLatestNotice() {
        User admin = user(1L, "Admin", RoleType.ADMIN);
        Notice before = notice(1L, "Title", admin, false, LocalDateTime.now(), 3);
        Notice after = notice(1L, "Title", admin, false, LocalDateTime.now(), 4);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(before), Optional.of(after));
        when(noticeRepository.incrementViewCount(1L)).thenReturn(1);

        NoticeDetailResponse response = noticeService.getNotice(1L);

        assertThat(response.viewCount()).isEqualTo(4);
        verify(noticeRepository).incrementViewCount(1L);
    }

    @Test
    void getNoticeRejectsMissingNotice() {
        when(noticeRepository.findById(99L)).thenReturn(Optional.empty());

        assertNoticeNotFound(() -> noticeService.getNotice(99L));
        verify(noticeRepository, never()).incrementViewCount(99L);
    }

    @Test
    void updateNoticeChangesOnlyProvidedFields() {
        User admin = user(1L, "Admin", RoleType.ADMIN);
        Notice notice = notice(1L, "Old", admin, false, LocalDateTime.now(), 0);
        ReflectionTestUtils.setField(notice, "content", "Original content");
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        NoticeDetailResponse response = noticeService.update(
                1L,
                new UpdateNoticeRequest("New", null, true)
        );

        assertThat(response.title()).isEqualTo("New");
        assertThat(response.content()).isEqualTo("Original content");
        assertThat(response.pinned()).isTrue();
    }

    @Test
    void updateRejectsEmptyRequest() {
        assertInvalidRequest(() -> noticeService.update(
                1L,
                new UpdateNoticeRequest(null, null, null)
        ));
        verify(noticeRepository, never()).findById(1L);
    }

    @Test
    void updateRejectsBlankTitle() {
        assertInvalidRequest(() -> noticeService.update(
                1L,
                new UpdateNoticeRequest(" ", null, null)
        ));
    }

    @Test
    void updateRejectsMissingNotice() {
        when(noticeRepository.findById(99L)).thenReturn(Optional.empty());

        assertNoticeNotFound(() -> noticeService.update(
                99L,
                new UpdateNoticeRequest("New", null, null)
        ));
    }

    @Test
    void deleteNotice() {
        Notice notice = notice(1L, "Title", user(1L, "Admin", RoleType.ADMIN), false, LocalDateTime.now(), 0);
        when(noticeRepository.findById(1L)).thenReturn(Optional.of(notice));

        noticeService.delete(1L);

        verify(noticeRepository).delete(notice);
    }

    @Test
    void deleteRejectsMissingNotice() {
        when(noticeRepository.findById(99L)).thenReturn(Optional.empty());

        assertNoticeNotFound(() -> noticeService.delete(99L));
        verify(noticeRepository, never()).delete(any(Notice.class));
    }

    @Test
    void getNoticesRejectsNegativePage() {
        assertInvalidRequest(() -> noticeService.getNotices(-1, 10));
    }

    @Test
    void getNoticesRejectsZeroSize() {
        assertInvalidRequest(() -> noticeService.getNotices(0, 0));
    }

    @Test
    void getNoticesLimitsSizeToFifty() {
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(noticeRepository.findAllByOrderByPinnedDescCreatedAtDesc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 50), 0));

        noticeService.getNotices(0, 100);

        verify(noticeRepository).findAllByOrderByPinnedDescCreatedAtDesc(pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(50);
    }

    private User user(Long id, String name, RoleType role) {
        User user = new User(name.toLowerCase() + "@lgb.local", "encoded", name, role);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Notice notice(
            Long id,
            String title,
            User author,
            boolean pinned,
            LocalDateTime createdAt,
            long viewCount
    ) {
        Notice notice = new Notice(title, "Content", author, pinned);
        ReflectionTestUtils.setField(notice, "id", id);
        ReflectionTestUtils.setField(notice, "viewCount", viewCount);
        ReflectionTestUtils.setField(notice, "createdAt", createdAt);
        ReflectionTestUtils.setField(notice, "updatedAt", createdAt);
        return notice;
    }

    private void assertNoticeNotFound(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOTICE_NOT_FOUND.getMessage());
    }

    private void assertInvalidRequest(Runnable action) {
        assertThatThrownBy(action::run)
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_NOTICE_REQUEST.getMessage());
    }
}
