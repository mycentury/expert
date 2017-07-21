package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "common_regular")
@Data
@EqualsAndHashCode(callSuper = false)
public class CommonRegularEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private Object name;// 名称
	private String expression;// 表达式
}
