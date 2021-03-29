package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResConCorpListRepository extends JpaRepository<ResConCorpList, Long> {
    List<ResConCorpList> findByBusinessTypeAndIdxCorp(String BusinessType, Long IdxCorp);

    Optional<ResConCorpList> findByConnectedIdAndCode(String connectedId, String Code);

    List<ResConCorpList> findByConnectedId(String connectedId);

    interface distinctData{
        String getBusinessType();
        String getOrganization();
    }
    List<distinctData> findDistinctByConnectedIdInAndStatusIn(List<String> connectedIdList,List<ConnectedMngStatus> statusList);

    List<ResConCorpList> findByConnectedIdInAndStatusInAndBusinessTypeAndOrganization(List<String> connectedIdList,List<ConnectedMngStatus> statusList, String BusinessType, String organization);

    ResConCorpList findTopByConnectedIdInAndStatusInAndBusinessTypeAndOrganizationOrderByCreatedAtDesc(List<String> connectedIdList,List<ConnectedMngStatus> statusList, String BusinessType, String organization);

    Optional<ResConCorpList> findByConnectedIdAndOrganizationAndStatusIn(String connectedId, String organization, List<ConnectedMngStatus> connectedMngStatusList);

    List<ResConCorpList> findByConnectedIdAndStatusIn(String connectedId, List<ConnectedMngStatus> connectedMngStatusList);

    Optional<ResConCorpList> findByIdx(Long idx);

}