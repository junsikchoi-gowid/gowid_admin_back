package com.nomadconnection.dapp.core.domain.repository;

import com.nomadconnection.dapp.core.domain.Card;
import com.nomadconnection.dapp.core.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

	@Modifying
	@Query("UPDATE Card c SET c.disabled = true WHERE c.owner = :user")
	int disableCards(User user);
}
