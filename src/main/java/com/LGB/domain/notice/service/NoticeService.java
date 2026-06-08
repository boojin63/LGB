package com.LGB.domain.notice.service;

import com.LGB.domain.notice.dto.CreateNoticeRequest;
import com.LGB.domain.notice.dto.NoticeDetailResponse;
import com.LGB.domain.notice.dto.NoticeListResponse;
import com.LGB.domain.notice.dto.NoticePageResponse;
import com.LGB.domain.notice.dto.UpdateNoticeRequest;
import com.LGB.domain.notice.entity.Notice;
import com.LGB.domain.notice.repository.NoticeRepository;
import com.LGB.domain.user.entity.User;
import com.LGB.domain.user.repository.UserRepository;
import com.LGB.global.exception.CustomException;
import com.LGB.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private static final int MAX_PAGE_SIZE = 50;

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeDetailResponse create(Long authorId, CreateNoticeRequest request) {
        User author = userRepository.findById(authorId)
                .filter(User::isActive)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Notice notice = new Notice(
                request.title(),
                request.content(),
                author,
                Boolean.TRUE.equals(request.pinned())
        );

        return NoticeDetailResponse.from(noticeRepository.save(notice));
    }

    public NoticePageResponse getNotices(int page, int size) {
        validatePageRequest(page, size);
        int limitedSize = Math.min(size, MAX_PAGE_SIZE);

        Page<NoticeListResponse> notices = noticeRepository
                .findAllByOrderByPinnedDescCreatedAtDesc(PageRequest.of(page, limitedSize))
                .map(NoticeListResponse::from);

        return NoticePageResponse.from(notices);
    }

    @Transactional
    public NoticeDetailResponse getNotice(Long noticeId) {
        findNotice(noticeId);
        noticeRepository.incrementViewCount(noticeId);

        return NoticeDetailResponse.from(findNotice(noticeId));
    }

    @Transactional
    public NoticeDetailResponse update(Long noticeId, UpdateNoticeRequest request) {
        validateUpdateRequest(request);

        Notice notice = findNotice(noticeId);
        notice.update(request.title(), request.content(), request.pinned());
        noticeRepository.flush();

        return NoticeDetailResponse.from(notice);
    }

    @Transactional
    public void delete(Long noticeId) {
        Notice notice = findNotice(noticeId);
        noticeRepository.delete(notice);
    }

    private Notice findNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private void validatePageRequest(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_REQUEST);
        }
    }

    private void validateUpdateRequest(UpdateNoticeRequest request) {
        if (request.title() == null && request.content() == null && request.pinned() == null) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_REQUEST);
        }
        if (request.title() != null && request.title().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_REQUEST);
        }
        if (request.content() != null && request.content().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_REQUEST);
        }
    }
}
