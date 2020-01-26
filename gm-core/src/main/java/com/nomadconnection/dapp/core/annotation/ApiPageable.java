package com.nomadconnection.dapp.core.annotation;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = "page", paramType = "query", dataType = "int", defaultValue = "0", value = "" +
				"Results page you want to retrieve (0..N)"
		),
		@ApiImplicitParam(name = "size", paramType = "query", dataType = "int", defaultValue = "20", value =
				"Number of records per page."
		),
		@ApiImplicitParam(name = "sort", paramType = "query", dataType = "string", allowMultiple = true, value = "" +
				"Sorting criteria in the format: property(,asc|desc).\n" +
				"Default sort order is ascending.\n" +
				"Multiple sort criteria are supported."
		)
})
@SuppressWarnings("unused")
public @interface ApiPageable {

}
