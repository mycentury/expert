/**
 * 
 */
package com.expert.repository.mongodb.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Desc 菜单
 * @author wewenge.yan
 * @Date 2016年12月8日
 * @ClassName MenuEntity
 */
@Document(collection = "file")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;// 自生成
	private String orginalName;// 源名称（包含后缀）
	private String targetName;// 保存文件名（包含后缀）
	private String path;// 路径
	private String fingerprint;// 指纹
	private String sessionId;// 用于非登录
	private String userId;// 用于登录
	private Boolean isTemp = false;// 会话结束则删除
	private Boolean downloadSupport;// 是否支持下载
	private Integer downloadCount;// 下载次数
	private String uploadTime;// 上传时间
	private String status;// 状态（U-上传，D-删除，E-修改等）
}
