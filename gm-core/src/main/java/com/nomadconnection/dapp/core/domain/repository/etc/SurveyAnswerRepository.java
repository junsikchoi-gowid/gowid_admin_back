package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.domain.etc.SurveyAnswer;
import com.nomadconnection.dapp.core.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {

	Optional<List<SurveyAnswer>> findAllByUser(User user);
	Optional<List<SurveyAnswer>> findAllByUserAndTitle(User user, String title);
	Optional<SurveyAnswer> findAllByUserAndTitleAndAnswer(User user, String title, String answer);

}
