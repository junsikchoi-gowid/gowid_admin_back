package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaasCategoryRepository extends JpaRepository<SaasCategory, Long> {

    @Query(value = "SELECT \n" +
                    "    info.name name, info.imageName imageName\n" +
                    "FROM\n" +
                    "    SaasPaymentInfo payment\n" +
                    "        JOIN\n" +
                    "    SaasInfo info ON payment.idxSaasInfo = info.idx\n" +
                    "WHERE\n" +
                    "    payment.activeSubscription = 1\n" +
                    "        AND payment.idxUser = :idxUser \n" +
                    "        AND info.idxSaasCategory = :idxSaasCategory ", nativeQuery = true)
    List<SaasCategoryRepository.UseSaasByCategoryDto> getUseSaasByCategoryId(@Param("idxUser") Long idxUser,
                                                                             @Param("idxSaasCategory") Long idxSaasCategory);

    public static interface UseSaasByCategoryDto {
        String getName();
        String getImageName();
    }

}
