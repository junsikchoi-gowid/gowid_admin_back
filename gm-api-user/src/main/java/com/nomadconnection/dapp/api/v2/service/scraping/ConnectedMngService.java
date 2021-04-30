package com.nomadconnection.dapp.api.v2.service.scraping;

import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.repository.connect.ConnectedMngRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectedMngService {

	private final ConnectedMngRepository repoConnectedMng;

	public String getConnectedId(Long idxUser){
		List<ConnectedMng> connectedMng = repoConnectedMng.findByIdxUser(idxUser);
		if(connectedMng.size() < 1) {
			//TODO: Change Exception
			throw new RuntimeException("CONNECTED ID");
		}
		return connectedMng.get(0).connectedId();
	}


}
