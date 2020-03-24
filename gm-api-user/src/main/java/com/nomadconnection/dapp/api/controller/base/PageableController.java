package com.nomadconnection.dapp.api.controller.base;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public interface PageableController {

	int MAX_SIZE = 100;

	default Pageable revise(Pageable pageable, String... fields) {
		if (pageable != null) {
			int page = pageable.getPageNumber();
			int size = Math.min(pageable.getPageSize(), MAX_SIZE);
			if (fields.length > 0) {
				List<Sort.Order> orders = pageable.getSort().stream().filter(
						order -> Arrays.stream(fields).noneMatch(
								field -> field.equalsIgnoreCase(order.getProperty())
						)
				).collect(Collectors.toList());
				return PageRequest.of(page, size, Sort.by(orders));
			}
			return PageRequest.of(page, size);
		}
		return null;
	}
}
