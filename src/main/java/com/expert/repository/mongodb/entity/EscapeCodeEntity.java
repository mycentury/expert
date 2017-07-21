package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "escape_code")
@Data
@EqualsAndHashCode(callSuper = false)
public class EscapeCodeEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String forJava;
	private String forHtml;
	private String forJs;
}
