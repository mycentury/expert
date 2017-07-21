/**
 * 
 */
package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @desc 用户
 * @author yanwenge
 * @date 2016年12月10日
 * @class UserEntity
 */
@Document(collection = "user")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * userId
	 */
	@Id
	private String id;
	/**
	 * 用户类型
	 */
	private String usertype;// V-visitor,G-general,S-system
	/**
	 * 用户头像
	 */
	private String photo = "/img/logo.jpg";
	/**
	 * 用户昵称
	 */
	private String name = "新手试驾";
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 注册日期
	 */
	private String registerDate;
	/**
	 * 经验
	 */
	private Integer score = 0;// 发帖+10，评论+5，被顶+1
	/**
	 * 等级
	 */
	private String level = "0";
	/**
	 * 登陆状态：1-已登录，0-未登录
	 */
	private String status = "0";
}
