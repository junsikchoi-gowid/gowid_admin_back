package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Consent;
import com.nomadconnection.dapp.core.domain.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

	@Transactional
	@Modifying
	@Query("delete from Faq f where f.id in :faqIds")
	void deleteFaqDeleteQuery(@Param("faqIds") List<Long> faqIds);
}