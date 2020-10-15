package com.nomadconnection.dapp.core.domain.repository.notification;

import com.nomadconnection.dapp.core.domain.notification.SlackNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlackNotificationRepository extends JpaRepository<SlackNotification, Long> {
}