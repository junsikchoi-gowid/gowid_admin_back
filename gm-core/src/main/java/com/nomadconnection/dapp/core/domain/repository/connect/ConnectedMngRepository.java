package com.nomadconnection.dapp.core.domain.repository.connect;

import com.nomadconnection.dapp.core.domain.common.ConnectedMng;
import com.nomadconnection.dapp.core.domain.common.ConnectedMngStatus;
import com.nomadconnection.dapp.core.domain.corp.Corp;
import com.nomadconnection.dapp.core.domain.repository.querydsl.ConnectedMngCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectedMngRepository extends JpaRepository<ConnectedMng, Long>, ConnectedMngCustomRepository {
	@Deprecated
	List<ConnectedMng> findByIdxUser(Long idxUser);

	List<ConnectedMng> findByIdxUserAndStatusInOrderByCreatedAtDesc(Long idxUser, List<ConnectedMngStatus> connectedMngStatusList);

	Optional<ConnectedMng> findByIdxAndIdxUser(Long idx, Long idxUser);

	@Query(value = "select c.* FROM ConnectedMng c where c.idxUser = :idxUser order by createdAt " ,nativeQuery = true)
	List<ConnectedMngDto> findIdxUser(@Param("idxUser")Long idxUser);

	@Query(value = "select ifnull(max(a),0) from (select if(endFlag is null , 0 , endFlag) a from ResBatch where idxCorp = :idxCorp order by idx desc limit 1 ) a " ,nativeQuery = true)
	Integer findRefresh(@Param("idxCorp")Long idxCorp);

	@Query(value = "select count(idx) from ResAccount where connectedId in ( select connectedId from ConnectedMng where idxUser = :idxUser)" ,nativeQuery = true)
	Integer findResAccountCount(@Param("idxUser")Long idxUser);

    ConnectedMng findByConnectedId(String connectedId);

    interface ConnectedMngDto {
		Long getIdx();
		String getConnectedId();
		Long getIdxUser();
		String getName();
		String getStartDate();
		String getEndDate();
		String getType();
		String getDesc1();
		String getDesc2();
	}

	@Transactional
	@Modifying
	@Query("delete from ConnectedMng  where idxuser = :idxUser")
	void deleteAllByUserIdx(@Param("idxUser") Long idxUser);

    List<ConnectedMng> findByCorp(Corp corp);


}
