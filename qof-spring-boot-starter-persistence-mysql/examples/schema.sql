-- QOF 文件元数据表
-- 引入 qof-spring-boot-starter-persistence-mysql 后使用

CREATE TABLE IF NOT EXISTS `sys_files` (
    `id`                   bigint       NOT NULL COMMENT '主键标识',
    `create_time`          datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `invalid`              bigint       NOT NULL DEFAULT '0' COMMENT '数据是否有效：0数据有效',
    `file_name`            varchar(128) NOT NULL COMMENT '文件名称',
    `file_path`            varchar(512) NOT NULL COMMENT '文件路径',
    `file_type`            varchar(64)           DEFAULT NULL COMMENT '文件类型(image/png、image/jpeg)',
    `file_label`           varchar(64)           DEFAULT NULL COMMENT '文件标签（证件照、报告等）',
    `file_size`            bigint                DEFAULT NULL COMMENT '文件大小(单位byte)',
    `file_storage_mode`    varchar(16)  NOT NULL COMMENT '存储模式(local/cos/oss/rustfs)',
    `file_storage_station` varchar(64)           DEFAULT NULL COMMENT '文件存储站',
    `public_access`        int                   DEFAULT 0 COMMENT '是否公开访问：1-公开，0-不公开',
    `create_id`            bigint                DEFAULT NULL COMMENT '创建者ID',
    PRIMARY KEY (`id`),
    KEY `idx_sys_files_invalid_ctime` (`invalid`, `create_time`),
    KEY `idx_sys_files_mode_station` (`file_storage_mode`, `file_storage_station`),
    KEY `idx_sys_files_name` (`file_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统-文件元数据表';
