package com.nomadconnection.dapp.core.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.MySQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import com.querydsl.sql.types.DateTimeType;
import com.querydsl.sql.types.LocalDateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuerydslConfiguration {

	private final DataSource dataSource;

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory queryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	public SQLTemplates mySqlTemplates() {
		return new MySQLTemplates();
	}

	@Bean
	public SQLQueryFactory sqlQueryFactory(DataSource dataSource, SQLTemplates sqlTemplates) {
		com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(sqlTemplates);
		configuration.setExceptionTranslator(new SpringExceptionTranslator());
		configuration.register(new DateTimeType());
		configuration.register(new LocalDateType());

		return new SQLQueryFactory(configuration, dataSource);
	}

}
