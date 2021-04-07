package com.nomadconnection.dapp.core.domain.repository.etc;

import com.nomadconnection.dapp.core.domain.etc.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

	Optional<Survey> findAllByTitleAndAnswer(String title, String answer);

	Optional<List<Survey>> findAllByTitleAndActivatedOrderByAnswerOrderAsc(String title, boolean activated);

	@Override
	Survey save(Survey contents);

	@Query(value = "select sc.* FROM Survey sc where sc.activated = true group by sc.title order by sc.updatedAt desc" ,nativeQuery = true)
	Optional<Survey> findAllGroupByTitle();

	@Query(value = "select sc.* FROM Survey sc where sc.title = :title and sc.activated = true group by sc.title order by sc.updatedAt desc" ,nativeQuery = true)
	Optional<Survey> findAllByTitleAndActivatedGroupByTitle(@Param("title") String title);

}
