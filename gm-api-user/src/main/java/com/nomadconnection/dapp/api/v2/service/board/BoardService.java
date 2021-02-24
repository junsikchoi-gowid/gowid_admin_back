package com.nomadconnection.dapp.api.v2.service.board;

import com.nomadconnection.dapp.api.v2.dto.BoardDto;
import com.nomadconnection.dapp.core.domain.etc.NoticeBoard;
import com.nomadconnection.dapp.core.domain.repository.etc.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;

	@Transactional(readOnly = true)
	public Page<NoticeBoard> findAll(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveNotice(Long idx, BoardDto.Notice notice) {
		boardRepository.save(NoticeBoard.builder()
				.idx(idx)
				.contents(notice.getContents())
				.enable(notice.getEnable())
				.endDate(notice.getEndDate())
				.idxUser(notice.getIdxUser())
				.startDate(notice.getStartDate())
				.replay(notice.getReplay())
				.title(notice.getTitle())
				.build());
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteNotice(Long idx) {
		boardRepository.deleteById(idx);
	}
}
