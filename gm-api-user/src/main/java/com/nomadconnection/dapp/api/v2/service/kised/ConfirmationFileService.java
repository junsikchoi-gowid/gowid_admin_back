package com.nomadconnection.dapp.api.v2.service.kised;

import com.nomadconnection.dapp.core.domain.kised.ConfirmationFile;
import com.nomadconnection.dapp.core.domain.repository.kised.ConfirmationFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmationFileService {

	private final ConfirmationFileRepository confirmationFileRepository;

	public ConfirmationFile save(ConfirmationFile confirmationFile){
		return confirmationFileRepository.save(confirmationFile);
	}

}
