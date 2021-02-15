package com.nomadconnection.dapp.core.domain.repository.user;

import com.nomadconnection.dapp.core.domain.user.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventsRepository extends JpaRepository<Events, Long> {

	Optional<Events> findTopByEventNameAndIdxUser(String eventName, Long idx);
}
