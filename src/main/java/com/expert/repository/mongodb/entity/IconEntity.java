package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "icon")
@Data
@EqualsAndHashCode(callSuper = false)
public class IconEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String url;
	private String originalUrl;
	private String publisher;
	private String publishDate;
	private Integer status = 0;
}
