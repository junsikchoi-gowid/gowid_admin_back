package com.nomadconnection.dapp.core.domain.repository.res;

import com.nomadconnection.dapp.core.domain.res.ResConCorpList;
import com.nomadconnection.dapp.core.domain.res.ResConCorpListStatus;
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
    List<distinctData> findDistinctByConnectedIdInAndStatusIn(List<String> connectedIdList,List<ResConCorpListStatus> statusList);

    List<ResConCorpList> findByConnectedIdInAndStatusInAndBusinessTypeAndOrganization(List<String> connectedIdList,List<ResConCorpListStatus> statusList, String BusinessType, String organization);

    ResConCorpList findTopByConnectedIdInAndStatusInAndBusinessTypeAndOrganizationOrderByCreatedAtDesc(List<String> connectedIdList,List<ResConCorpListStatus> statusList, String BusinessType, String organization);

    Optional<ResConCorpList> findByConnectedIdAndOrganizationAndStatusIn(String connectedId, String organization, List<ResConCorpListStatus> statusList);

    List<ResConCorpList> findByConnectedIdAndStatusIn(String connectedId, List<ResConCorpListStatus> statusList);

    Optional<ResConCorpList> findByIdx(Long idx);

}