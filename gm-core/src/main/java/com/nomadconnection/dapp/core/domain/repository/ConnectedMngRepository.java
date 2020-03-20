package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.ConnectedMng;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface ConnectedMngRepository extends JpaRepository<ConnectedMng, Long>{

	@Transactional
	@Modifying
	@Query("delete from ConnectedMng c where c.connectedId = :connectedId")
	int deleteConnectedQuery(@Param("connectedId") String connectedId);

	List<ConnectedMng> findByIdxUser(Long idxUser);

	@Query(value = "select c.* FROM ConnectedMng c where c.idxUser = :idxUser order by createdAt " ,nativeQuery = true)
	List<CconnectedMngDto> findIdxUser(@Param("idxUser")Long idxUser);

	Optional<ConnectedMng> findByConnectedIdAndIdxUser(String connectedId, Long idxUser);

	public static interface CconnectedMngDto {
		Long getIdx();
		String getConnectedId();
		Long getIdxUser();
		String getName();
		String getStartDate();
		String getEndDate();
		String getDesc1();
		String getDesc2();
	}

}
