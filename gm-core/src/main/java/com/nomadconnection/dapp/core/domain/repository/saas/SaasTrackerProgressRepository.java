package com.nomadconnection.dapp.core.domain.repository.saas;

import com.nomadconnection.dapp.core.domain.saas.SaasTrackerProgress;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaasTrackerProgressRepository extends JpaRepository<SaasTrackerProgress, Long> {

    Optional<SaasTrackerProgress> findByUser(User user);

    @Query(value = "select am.idxUser,\n" +
                    "       eu.corp_id     idxCorp,\n" +
                    "       c.resCompanyNm companyName,\n" +
                    "       u.name         userName,\n" +
                    "       u.email,\n" +
                    "       stp.processDate,\n" +
                    "       stp.status,\n" +
                    "       stp.step,\n" +
                    "       stp.createdAt,\n" +
                    "       stp.updatedAt\n" +
                    "from AuthoritiesMapping am\n" +
                    "         left join User u on am.idxUser = u.idx\n" +
                    "         left join Corp c on u.idxCorp = c.idx\n" +
                    "         left join expense.user eu on u.email = eu.email\n" +
                    "         left join SaasTrackerProgress stp on u.idx = stp.idxUser\n" +
                    "where am.idxAuthority = 9\n" +
                    "  and c.resCompanyNm is not null\n" +
                    "order by resCompanyNm", nativeQuery = true)
    List<SaasTrackerUserDto> getSaasTrackerUsers();
    public static interface SaasTrackerUserDto {
        Long getIdxUser();
        Long getIdxCorp();
        String getUserName();
        String getCompanyName();
        String getEmail();
        String getProcessDate();
        Integer getStatus();
        Integer getStep();
        String getCreatedAt();
        String getUpdatedAt();
    }
}
