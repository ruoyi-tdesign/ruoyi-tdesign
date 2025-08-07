import type { BaseEntity } from '@/api/model/resultModel';

/**
 * 文件记录查询对象
 */
export interface SysFileQuery extends BaseEntity {
  /** 文件名称 */
  filename?: string;
  /** 原始文件名 */
  originalFilename?: string;
  /** 基础存储路径 */
  basePath?: string;
  /** 存储路径 */
  path?: string;
  /** 文件扩展名 */
  ext?: string;
  /** MIME类型 */
  contentType?: string;
  /** 文件ACL */
  fileAcl?: string;
  /** 分类id */
  fileCategoryId?: number;
  /** 用户类型 */
  userType?: string;
  /** 是否锁定状态 */
  isLock?: number;
  /** 上传人 */
  createBy?: number;
  /** 上传人 */
  createByName?: string;
  /** 多个文件后缀 */
  suffixes?: string[];
  /** 文件最大字节长度 */
  maxSize?: number;
  /** 内容类型 */
  contentTypes?: string[];
}
/**
 * 文件记录业务对象
 */
export interface SysFileForm {
  /** 文件id */
  fileId?: number;
  /** 原始文件名 */
  originalFilename?: string;
  /** 文件用户元数据 */
  userMetadata?: string;
  /** 分类id */
  fileCategoryId?: number;
  /** 是否锁定状态 */
  isLock?: number;
  /** 上传人 */
  createBy?: number;
}
/**
 * 文件记录视图对象
 */
export interface SysFileVo {
  /** 文件id */
  fileId?: number;
  /** 文件访问地址 */
  url?: string;
  /** 预览地址 */
  previewUrl?: string;
  /** 文件下载地址 */
  downloadUrl?: string;
  /** 文件大小，单位字节 */
  size?: number;
  /** 文件名称 */
  filename?: string;
  /** 原始文件名 */
  originalFilename?: string;
  /** 基础存储路径 */
  basePath?: string;
  /** 存储路径 */
  path?: string;
  /** 文件扩展名 */
  ext?: string;
  /** MIME类型 */
  contentType?: string;
  /** 存储配置id */
  storageConfigId?: number;
  /** 缩略图访问路径 */
  thUrl?: string;
  /** 缩略图名称 */
  thFilename?: string;
  /** 缩略图大小，单位字节 */
  thSize?: number;
  /** 缩略图MIME类型 */
  thContentType?: string;
  /** 文件所属对象id */
  objectId?: string;
  /** 文件所属对象类型，例如用户头像，评价图片 */
  objectType?: string;
  /** 文件元数据 */
  metadata?: string;
  /** 文件用户元数据 */
  userMetadata?: string;
  /** 缩略图元数据 */
  thMetadata?: string;
  /** 缩略图用户元数据 */
  thUserMetadata?: string;
  /** 附加属性 */
  attr?: string;
  /** 文件ACL */
  fileAcl?: string;
  /** 缩略图文件ACL */
  thFileAcl?: string;
  /** 哈希信息 */
  hashInfo?: string;
  /** 上传ID，仅在手动分片上传时使用 */
  uploadId?: string;
  /** 上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成 */
  uploadStatus?: number;
  /** 分类id */
  fileCategoryId?: number;
  /** 用户类型 */
  userType?: string;
  /** 是否锁定状态 */
  isLock?: number;
  /** 更新时间 */
  updateTime?: any;
  /** 创建时间 */
  createTime?: any;
  /** 分类路径 */
  categoryPath?: string;
}

export interface SysFileActiveVo extends SysFileVo {
  active: boolean;
}

/**
 * 上传对象信息
 */
export interface SysFileUploadVo {
  /** 文件名 */
  fileName?: string;
  /** 对象存储主键 */
  fileId?: string;
  /** 预览地址 */
  previewUrl?: string;
  /** 下载地址 */
  downloadUrl?: string;
}
