/*
 Navicat Premium Data Transfer

 Source Server         : docker-mysql01
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : 192.168.3.151:3306
 Source Schema         : mypages

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 22/05/2021 21:05:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `config_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项键',
  `config_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项的值',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述说明',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (3, 'cache_control_max_age', '86400', '24*60*60 一天', b'0', '2021-05-15 16:24:24', '2021-05-15 16:59:24');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限路径',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限名',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限描述',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_url`(`url`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '/following/add', '添加关注', '添加关注用户', b'0', '2021-05-08 09:57:34', '2021-05-08 10:05:11');
INSERT INTO `sys_permission` VALUES (2, '/following/removeRelation', '移除关注', '移除关注用户', b'0', '2021-05-08 09:58:07', '2021-05-08 10:05:13');
INSERT INTO `sys_permission` VALUES (3, '/following/syncOne', '同步用户信息', '同步关注用户信息', b'0', '2021-05-08 09:58:43', '2021-05-08 10:05:01');
INSERT INTO `sys_permission` VALUES (4, '/following/syncBatch', '批量同步用户信息', '批量同步关注用户信息', b'0', '2021-05-08 10:01:16', '2021-05-08 10:05:04');
INSERT INTO `sys_permission` VALUES (5, '/remark/add', '添加标签', '添加标签', b'0', '2021-05-08 10:02:38', '2021-05-08 10:02:38');
INSERT INTO `sys_permission` VALUES (6, '/remark/removeBelongs', '移除所有标签', '移除用户所有标签', b'0', '2021-05-08 10:03:08', '2021-05-08 10:03:49');
INSERT INTO `sys_permission` VALUES (7, '/remark/removeOne', '移除单个标签', '移除用户单个标签', b'0', '2021-05-08 10:03:35', '2021-05-08 10:03:51');
INSERT INTO `sys_permission` VALUES (8, '/type/add', '添加类型', '添加类型', b'0', '2021-05-08 10:05:47', '2021-05-08 10:05:47');
INSERT INTO `sys_permission` VALUES (9, '/type/remove', '移除类型', '移除类型', b'0', '2021-05-08 10:06:09', '2021-05-08 10:06:09');
INSERT INTO `sys_permission` VALUES (10, '/type/list', '类型列表', '获取类型列表', b'0', '2021-05-08 10:06:34', '2021-05-08 10:06:34');
INSERT INTO `sys_permission` VALUES (11, '/opinion/add', '添加观点', '添加观点', b'0', '2021-05-08 10:06:58', '2021-05-08 10:06:58');
INSERT INTO `sys_permission` VALUES (12, '/opinion/remove', '移除观点', '移除观点', b'0', '2021-05-08 10:07:16', '2021-05-08 10:07:16');
INSERT INTO `sys_permission` VALUES (13, '/platform/add', '添加平台', '添加平台', b'0', '2021-05-08 10:07:38', '2021-05-08 10:07:38');
INSERT INTO `sys_permission` VALUES (14, '/platform/remove', '移除平台', '移除平台', b'0', '2021-05-08 10:07:54', '2021-05-08 10:07:54');
INSERT INTO `sys_permission` VALUES (15, '/platform/list', '平台列表', '获取平台列表', b'0', '2021-05-08 10:08:15', '2021-05-08 10:21:31');
INSERT INTO `sys_permission` VALUES (16, '/platform-relation/add', '添加用户平台关联', '添加用户平台关联', b'0', '2021-05-08 10:09:02', '2021-05-08 10:09:44');
INSERT INTO `sys_permission` VALUES (17, '/platform-relation/remove', '移除用户平台关联', '移除用户平台关联', b'0', '2021-05-08 10:09:34', '2021-05-08 10:09:34');
INSERT INTO `sys_permission` VALUES (18, '/platform/baseList', '平台基础列表', '平台基础列表', b'0', '2021-05-08 15:01:00', '2021-05-08 15:01:24');
INSERT INTO `sys_permission` VALUES (19, '/home/platformList', '平台首页数据', '平台首页数据', b'0', '2021-05-11 19:55:06', '2021-05-11 20:07:48');
INSERT INTO `sys_permission` VALUES (20, '/following-relation/add', '新增或修改关注用户关联', '新增或修改关注用户关联', b'0', '2021-05-12 19:43:29', '2021-05-12 19:43:29');
INSERT INTO `sys_permission` VALUES (21, '/sys/delRedisCache', '按KEY删除redis缓存', '按KEY删除redis缓存', b'0', '2021-05-15 13:30:13', '2021-05-15 13:30:13');
INSERT INTO `sys_permission` VALUES (22, '/sys/properties', '全局参数列表', '全局参数列表', b'0', '2021-05-15 13:30:33', '2021-05-15 13:30:33');
INSERT INTO `sys_permission` VALUES (23, '/user/detail', '获取用户详细信息', '获取用户详细信息', b'0', '2021-05-17 12:38:45', '2021-05-17 12:38:45');
INSERT INTO `sys_permission` VALUES (24, '/following/list', '获取关注用户列表', '获取关注用户列表', b'0', '2021-05-18 08:14:57', '2021-05-18 08:14:57');
INSERT INTO `sys_permission` VALUES (25, '/opinion/list', '获取观点列表', '获取观点列表', b'0', '2021-05-18 08:15:19', '2021-05-18 08:15:19');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色代码，英文，用于程序业务处理',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名，用于页面显示',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色描述，用于补充提示说明',
  `count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用于统计对应该角色的用户数量，目前暂未使用',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '角色状态，0-正常；1-禁用，默认0',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '系统管理员', '技术负责人或主管领导', 0, 0, 5, b'0', '2021-05-08 09:32:39', '2021-05-08 09:36:24');
INSERT INTO `sys_role` VALUES (2, 'user', '平台用户', '普通平台用户', 0, 0, 5, b'0', '2021-05-08 10:10:57', '2021-05-08 10:10:57');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联角色id',
  `permission_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联权限id',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (2, 1, 2, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (3, 1, 3, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (4, 1, 4, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (5, 1, 5, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (6, 1, 6, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (7, 1, 7, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (8, 1, 8, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (9, 1, 9, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (10, 1, 10, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (11, 1, 11, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (12, 1, 12, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (13, 1, 13, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (14, 1, 14, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (15, 1, 15, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (16, 1, 16, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (17, 1, 17, b'0', '2021-05-08 10:15:56', '2021-05-08 10:15:56');
INSERT INTO `sys_role_permission` VALUES (32, 2, 1, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (33, 2, 2, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (34, 2, 3, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (35, 2, 4, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (36, 2, 5, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (37, 2, 6, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (38, 2, 7, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (39, 2, 8, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (40, 2, 9, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (41, 2, 10, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (42, 2, 11, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (43, 2, 12, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (44, 2, 15, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (45, 2, 16, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (46, 2, 17, b'0', '2021-05-08 10:21:23', '2021-05-08 10:21:23');
INSERT INTO `sys_role_permission` VALUES (47, 1, 18, b'0', '2021-05-11 19:55:33', '2021-05-11 19:55:33');
INSERT INTO `sys_role_permission` VALUES (48, 1, 19, b'0', '2021-05-11 19:55:44', '2021-05-11 19:55:44');
INSERT INTO `sys_role_permission` VALUES (49, 2, 18, b'0', '2021-05-11 19:55:51', '2021-05-11 19:55:51');
INSERT INTO `sys_role_permission` VALUES (50, 2, 19, b'0', '2021-05-11 19:55:57', '2021-05-11 19:55:57');
INSERT INTO `sys_role_permission` VALUES (51, 1, 20, b'0', '2021-05-12 19:43:54', '2021-05-12 19:43:54');
INSERT INTO `sys_role_permission` VALUES (52, 2, 20, b'0', '2021-05-12 19:44:00', '2021-05-12 19:44:00');
INSERT INTO `sys_role_permission` VALUES (53, 1, 21, b'0', '2021-05-15 13:30:57', '2021-05-15 13:30:57');
INSERT INTO `sys_role_permission` VALUES (54, 1, 22, b'0', '2021-05-15 13:31:03', '2021-05-15 13:31:03');
INSERT INTO `sys_role_permission` VALUES (55, 1, 23, b'0', '2021-05-17 12:39:00', '2021-05-17 12:39:00');
INSERT INTO `sys_role_permission` VALUES (56, 2, 23, b'0', '2021-05-18 02:41:42', '2021-05-18 02:41:42');
INSERT INTO `sys_role_permission` VALUES (57, 1, 24, b'0', '2021-05-18 08:15:53', '2021-05-18 08:15:53');
INSERT INTO `sys_role_permission` VALUES (58, 1, 25, b'0', '2021-05-18 08:16:00', '2021-05-18 08:16:00');
INSERT INTO `sys_role_permission` VALUES (59, 2, 24, b'0', '2021-05-18 08:16:06', '2021-05-18 08:16:06');
INSERT INTO `sys_role_permission` VALUES (60, 2, 25, b'0', '2021-05-18 08:16:18', '2021-05-18 08:16:18');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联角色id',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, b'0', '2021-05-08 10:12:07', '2021-05-08 10:12:07');
INSERT INTO `sys_user_role` VALUES (2, 2, 2, b'0', '2021-05-08 10:12:16', '2021-05-08 10:12:16');

-- ----------------------------
-- Table structure for user_base
-- ----------------------------
DROP TABLE IF EXISTS `user_base`;
CREATE TABLE `user_base`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `profile_photo` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '形象照片（头像）',
  `gender` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '性别，1-男；0-女，默认1',
  `lock_time` datetime NULL DEFAULT NULL COMMENT '锁定时间，null-未锁定；当前时间之前-锁定；当前时间之后-待锁定',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_name`(`user_name`) USING BTREE,
  UNIQUE INDEX `uk_mobile`(`mobile`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_base
-- ----------------------------
INSERT INTO `user_base` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '/images/user-profile-photo/img-33307974ebe0ea92e27c48df28268546.jpg', 1, NULL, b'0', '2021-04-23 06:23:16', '2021-05-17 01:51:28');
INSERT INTO `user_base` VALUES (2, 'test', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, 1, NULL, b'0', '2021-04-23 06:23:16', '2021-05-07 07:28:50');
INSERT INTO `user_base` VALUES (3, 'guest', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, NULL, 1, NULL, b'0', '2021-04-23 06:23:16', '2021-05-07 07:28:53');

-- ----------------------------
-- Table structure for user_check_update
-- ----------------------------
DROP TABLE IF EXISTS `user_check_update`;
CREATE TABLE `user_check_update`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id（冗余，方便关联查询）',
  `following_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户关注表的id',
  `new_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '新动态数量',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_userId_followingId`(`user_id`, `following_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '检查关注用户更新表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_check_update
-- ----------------------------

-- ----------------------------
-- Table structure for user_following
-- ----------------------------
DROP TABLE IF EXISTS `user_following`;
CREATE TABLE `user_following`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `platform_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联平台id',
  `user_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联用户来源平台的id或标识',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `main_page` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主页地址',
  `profile_photo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '形象照片（头像）',
  `signature` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个性签名',
  `is_user` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否为用户，1-是用户；0-不是，默认1',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_platformId_userKey`(`platform_id`, `user_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 103 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关注表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_following
-- ----------------------------
INSERT INTO `user_following` VALUES (1, 2, 'v-technology', '知识区', 'https://www.bilibili.com/v/technology', '/images/user-profile-photo/02a0c09a6317e16c4077df10ab3780c2906f8f24.png', '点亮好奇心，在知识海洋里乘风破浪~', b'0', b'0', '2021-01-02 10:39:40', '2021-01-02 10:39:40');
INSERT INTO `user_following` VALUES (2, 2, 'v-life', '生活区', 'https://www.bilibili.com/v/life', '/images/user-profile-photo/c1e19150b5d1e413958d45e0e62f012e3ee200af.png', '衣食住行、柴米油盐', b'0', b'0', '2021-01-02 10:42:26', '2021-01-02 10:42:26');
INSERT INTO `user_following` VALUES (3, 2, '520155988', '所长林超', 'https://space.bilibili.com/520155988/video', '/images/user-profile-photo/bc1a14a6c811b02ef30e9e69a7eb343a677287db.jpg', '薛定谔的眨眼.看科技&商业原理 公主号其他同名~合作联系：suozhang03', b'1', b'0', '2021-01-02 10:46:37', '2021-01-02 10:46:37');
INSERT INTO `user_following` VALUES (4, 2, '496085430', '无趣的二姐', 'https://space.bilibili.com/496085430/video', '/images/user-profile-photo/94a960d95f08c141b97c8cd217166221999dae35.jpg', '分享我看到的世界，文案发在公众号: 无趣的二姐', b'1', b'0', '2021-01-02 10:48:07', '2021-01-02 10:48:07');
INSERT INTO `user_following` VALUES (5, 2, '250111460', '蜡笔和小勋', 'https://space.bilibili.com/250111460/video', '/images/user-profile-photo/8a8812e0a9bb3adda90044ef48830584e1efe7a2.jpg', '讲点有益的，拍点有趣的 I 微博@蜡笔和小勋 I 邮箱：labidakashe@163.com(蜡笔打卡社)', b'1', b'0', '2021-01-02 11:19:57', '2021-01-02 11:19:57');
INSERT INTO `user_following` VALUES (6, 2, '386364189', '雪颖Shae', 'https://space.bilibili.com/386364189/video', '/images/user-profile-photo/ae1381713c2e5de1262c558eba55d486d9901e37.jpg', '真我心理主理人，一个你心灵成长路上的陪伴者。', b'1', b'0', '2021-01-02 11:20:50', '2021-01-02 11:20:50');
INSERT INTO `user_following` VALUES (7, 2, '37663924', '硬核的半佛仙人', 'https://space.bilibili.com/37663924/video', '/images/user-profile-photo/aaf33dced1941af0946f37c62f4b48fcaba9c9a2.jpg', '喜欢小仙女', b'1', b'0', '2021-01-02 11:23:45', '2021-01-02 11:23:45');
INSERT INTO `user_following` VALUES (8, 2, '303740257', '牛顿顿顿', 'https://space.bilibili.com/303740257/video', '/images/user-profile-photo/ccec7bf50aa4e269a5122c945ec5a93c67b5ff4c.jpg', '诗人，斗士，被称作：东半球第二正经の男人', b'1', b'0', '2021-01-02 11:34:24', '2021-01-02 11:34:24');
INSERT INTO `user_following` VALUES (9, 2, '512574759', '公孙田浩', 'https://space.bilibili.com/512574759/video', '/images/user-profile-photo/54e991d0369df2dcb864c798bb9c637128c0a73d.jpg', '用数据和事实呈现另一个互联网世界', b'1', b'0', '2021-01-02 11:35:25', '2021-01-02 11:35:25');
INSERT INTO `user_following` VALUES (10, 2, '387460933', '营养师顾中一', 'https://space.bilibili.com/387460933/video', '/images/user-profile-photo/a9a045485d2aa2e52f5a3333eccdd55b9e816bb6.jpg', '清华大学公共卫生硕士，北京营养师协会理事，科学传播副高职称，入选福布斯中国50位意见领袖榜', b'1', b'0', '2021-01-02 11:36:39', '2021-01-02 11:36:39');
INSERT INTO `user_following` VALUES (11, 2, '19577966', '李子柒', 'https://space.bilibili.com/19577966/video', '/images/user-profile-photo/82d27965dae3b2fe9e52780c6309c7b37ad4cbf2.jpg', '李家有女，人称子柒。 新浪微博：李子柒 邮箱：liziqistyle@163.com', b'1', b'0', '2021-01-02 11:37:54', '2021-01-02 11:37:54');
INSERT INTO `user_following` VALUES (12, 2, '298317405', '我是马小坏', 'https://space.bilibili.com/298317405/video', '/images/user-profile-photo/8f9ef20753087e89137ea8ef55b097fd44eea66f.jpg', '分享美食，更是分享对生活的一种态度，更多精彩欢迎关注头条号：我是马小坏，微信公众号：我是马小坏，微博:马小坏频道', b'1', b'0', '2021-01-02 11:38:46', '2021-05-20 07:59:25');
INSERT INTO `user_following` VALUES (13, 2, '66391032', '帅soserious', 'https://space.bilibili.com/66391032/video', '/images/user-profile-photo/40a663bb18e9064a97901b96aaf7d84d8056e98b.jpg', '#健身路上有我陪伴❤️ 微博:帅soserious', b'1', b'0', '2021-01-02 11:40:11', '2021-01-02 11:40:11');
INSERT INTO `user_following` VALUES (14, 2, '156858999', '曼巴yelomamba', 'https://space.bilibili.com/156858999/video', '/images/user-profile-photo/08ba8a3b8ed2b14e8d320f725d8f6468b3b482b4.jpg', '健身先健心 健身路上一起进步！ 微博同名/邮箱：daijianxiang16@163.com', b'1', b'0', '2021-01-02 11:40:43', '2021-01-02 11:40:43');
INSERT INTO `user_following` VALUES (15, 2, '37889997', '冰寒哥', 'https://space.bilibili.com/37889997/video', '/images/user-profile-photo/9901d2301367671e8a201068e1f1d7221ad9d339.jpg', '同济大学皮肤学在读博士，《听肌肤的话》系列作者，《药妆品》主译。无干货，不冰寒。', b'1', b'0', '2021-01-02 11:42:25', '2021-01-02 11:42:25');
INSERT INTO `user_following` VALUES (16, 2, '456404164', '皮科医生魏小博', 'https://space.bilibili.com/456404164/video', '/images/user-profile-photo/ac989cda390065228126fe0e44db74906ca0741d.jpg', '北京某三甲医院皮肤科医生 北京大学皮肤性病学博士\n尽量不要私信问诊，实在回复不过来，大家有问题还是及时就医诊治，以免延误病情', b'1', b'0', '2021-01-02 11:43:40', '2021-01-02 11:43:40');
INSERT INTO `user_following` VALUES (17, 2, '77266891', '深读视频', 'https://space.bilibili.com/77266891/video', '/images/user-profile-photo/ca5812ac9eee536c268ac3d8ab4dd54dfc62d3ee.jpg', '关注公众号“深读视频”~！健康、科普、动漫，撸给你看！', b'1', b'0', '2021-01-02 11:45:29', '2021-01-02 11:45:29');
INSERT INTO `user_following` VALUES (18, 2, '402576555', '画渣花小烙', 'https://space.bilibili.com/402576555/video', '/images/user-profile-photo/8ef6037e6024c01357edad8fdcf0a1deb346a894.jpg', '努力画画，认真科普，偶尔不务正业的瞎扯，有时候太忙私信会延迟回复嗷～～！微博：@画渣花小烙    油管：画渣花小烙', b'1', b'0', '2021-01-02 11:47:06', '2021-05-20 06:51:57');
INSERT INTO `user_following` VALUES (19, 2, '456691117', 'Freya飞呀', 'https://space.bilibili.com/456691117/video', '/images/user-profile-photo/1be0d49467a78352f6098d811065e0353878968e.jpg', 'wb同名｜不看私信  \n偷偷收藏温暖｜慢慢提升幸福值｜勇敢用生活治愈自己\n', b'1', b'0', '2021-01-02 11:47:50', '2021-01-02 11:47:50');
INSERT INTO `user_following` VALUES (20, 2, '28794030', '起床大萌萌', 'https://space.bilibili.com/28794030/video', '/images/user-profile-photo/235aea61e952d200ed3bbfcddd342d9eba6a5634.jpg', '起床迎接美好的生活吧 ✨vx：pmwu01', b'1', b'0', '2021-01-02 11:48:28', '2021-01-02 11:48:28');
INSERT INTO `user_following` VALUES (21, 2, '4548018', '丧妹有点跳', 'https://space.bilibili.com/4548018/video', '/images/user-profile-photo/5be61949369dd844cc459eab808da151d8c363d2.gif', '⋆͛微博丧妹有点跳⋆͛商务Sombie96⋆͛粉丝Stiao96⋆͛粉丝群303255550通知群61651173', b'1', b'0', '2021-01-02 11:49:07', '2021-05-20 06:23:51');
INSERT INTO `user_following` VALUES (22, 2, '20259914', '稚晖君', 'https://space.bilibili.com/20259914/video', '/images/user-profile-photo/cb9ef82714507e6bda707dac216da94c97d70037.jpg', 'AI算法工程师/野生钢铁侠/Arduino版主/脑洞载体', b'1', b'0', '2021-01-02 11:50:36', '2021-01-02 11:50:36');
INSERT INTO `user_following` VALUES (23, 2, '430777205', '达尔闻', 'https://space.bilibili.com/430777205/video', '/images/user-profile-photo/ab81b1454dbf711fb28d76da053a676f288a89e4.jpg', '逆天小姐姐只讲技术，不撩汉！WeChat: 达尔闻说', b'1', b'0', '2021-01-02 11:51:25', '2021-01-02 11:51:25');
INSERT INTO `user_following` VALUES (24, 2, '12590', 'epcdiy', 'https://space.bilibili.com/12590/video', '/images/user-profile-photo/f288604112016e93ca224c4c2c58980a6cd6ba25.png', '商务合作加VX：Im_double_cloud ，只接数码/互联网产品合作，谢绝拼多多、培训机构相关推广！粉丝总群：1021463979', b'1', b'0', '2021-01-02 11:52:02', '2021-01-02 11:52:02');
INSERT INTO `user_following` VALUES (25, 3, '5a66a94524eb450993a83d59a2854a45', '微博热搜榜', 'https://s.weibo.com/top/summary?cate=realtimehot', '/images/user-profile-photo/1f883b5711ad41e4a0504f5d4e6beaa40dvsxvbt.png', '随时随地发现（新鲜事）新孩子', b'0', b'0', '2021-01-02 12:05:12', '2021-01-02 12:05:12');
INSERT INTO `user_following` VALUES (26, 3, '2803301701', '人民日报', 'https://m.weibo.cn/u/2803301701', '/images/user-profile-photo/a716fd45ly8gdijd1zmonj20sa0saaby.jpg', '《人民日报》法人微博 人民日报法人微博。参与、沟通、记录时代。', b'1', b'0', '2021-01-02 12:06:41', '2021-01-02 12:06:41');
INSERT INTO `user_following` VALUES (27, 3, '2286908003', '人民网', 'https://m.weibo.cn/u/2286908003', '/images/user-profile-photo/002uLDeXly8glmohn698dj60j60j6q3b02.jpg', '人民网法人微博 报道全球 传播中国', b'1', b'0', '2021-01-02 12:07:23', '2021-01-02 12:07:23');
INSERT INTO `user_following` VALUES (28, 3, '1974576991', '环球时报', 'https://m.weibo.cn/u/1974576991', '/images/user-profile-photo/0029D7FZly8glmnmyyeicj605k05k74702.jpg', '《环球时报》微博 报道多元世界    解读复杂中国', b'1', b'0', '2021-01-02 12:07:58', '2021-01-02 12:07:58');
INSERT INTO `user_following` VALUES (29, 3, '2656274875', '央视新闻', 'https://m.weibo.cn/u/2656274875', '/images/user-profile-photo/002TLsr9ly8gnsu8mh9dkj60u00u0q4c02.jpg', '中央电视台新闻中心官方微博 “央视新闻”微博是中央电视台新闻中心官方微博，是央视重大新闻、突发事件、重点报道的首发平台。', b'1', b'0', '2021-01-02 12:08:38', '2021-01-02 12:08:38');
INSERT INTO `user_following` VALUES (30, 3, '1784473157', '中国新闻网', 'https://m.weibo.cn/u/1784473157', '/images/user-profile-photo/6a5ce645ly8gdij7dw130j20u00u00uc.jpg', '中国新闻网法人微博 这里提供你不知道、想知道、不能不知道的新闻。', b'1', b'0', '2021-01-02 12:09:17', '2021-01-02 12:09:17');
INSERT INTO `user_following` VALUES (31, 3, '1774057271', '生命时报', 'https://m.weibo.cn/u/1774057271', '/images/user-profile-photo/69bdf737ly8gdi762asg1j20u00u00uc.jpg', '《生命时报》报社官方微博 人民日报主管，环球时报主办，一家具有国际视野的大众健康媒体。更多精彩关注微信“LT0385”。', b'1', b'0', '2021-01-02 12:10:01', '2021-01-02 12:10:01');
INSERT INTO `user_following` VALUES (32, 3, '5044281310', '澎湃新闻', 'https://m.weibo.cn/u/5044281310', '/images/user-profile-photo/005vnhZYly8ftjmwo0bx4j308c08cq32.jpg', '澎湃新闻，专注时政与思想的媒体开放平台 有内涵的时政新媒体', b'1', b'0', '2021-01-02 12:11:17', '2021-01-02 12:11:17');
INSERT INTO `user_following` VALUES (33, 3, '1496814565', '封面新闻', 'https://m.weibo.cn/u/1496814565', '/images/user-profile-photo/593793e5ly8gdi6sa5seej20gq0f50sm.jpg', '封面新闻华西都市报官方微博 欢迎关注封面新闻', b'1', b'0', '2021-01-02 12:13:50', '2021-01-02 12:13:50');
INSERT INTO `user_following` VALUES (34, 3, '1684012053', '财经杂志', 'https://m.weibo.cn/u/1684012053', '/images/user-profile-photo/645ffc15ly8gib1q0vevgj20u00u2403.jpg', '《财经》杂志官方微博 独立 独家 独到', b'1', b'0', '2021-01-02 12:14:56', '2021-01-02 12:14:56');
INSERT INTO `user_following` VALUES (35, 3, '1642634100', '新浪科技', 'https://m.weibo.cn/u/1642634100', '/images/user-profile-photo/61e89b74ly1gdiq06vjw9j20c60c4aac.jpg', '新浪科技官方微博 新浪科技是中国最有影响力的TMT产业资讯及数码产品服务平台。让我们带你观察世界变化，看清行业趋势！', b'1', b'0', '2021-01-02 12:15:35', '2021-01-02 12:15:35');
INSERT INTO `user_following` VALUES (36, 3, '2970452952', '李子柒', 'https://m.weibo.cn/u/2970452952', '/images/user-profile-photo/b10d83d8jw8f53xpxjlhaj20ku0kut9k.jpg', '李子柒品牌创始人 邮箱：liziqistyle@163.com', b'1', b'0', '2021-01-03 09:27:55', '2021-01-03 09:27:55');
INSERT INTO `user_following` VALUES (37, 3, '6089150236', '马小坏频道', 'https://m.weibo.cn/u/6089150236', '/images/user-profile-photo/006E5s8Aly1fnnii4uc6jj30m80m8gtc.jpg', '知名美食博主 美食视频自媒体 工作微：kjxnweixin', b'1', b'0', '2021-01-03 09:54:37', '2021-01-03 09:54:37');
INSERT INTO `user_following` VALUES (38, 3, '5786902874', '夏厨陈二十', 'https://m.weibo.cn/u/5786902874', '/images/user-profile-photo/006jDfN8ly8g51wvgien7j30ru0ruabc.jpg', '大眼互娱签约博主 知名美食博主 美食视频自媒体 我是陈二十，一个爱“夏厨”的女孩…    合作手机微信：17744495635  邮箱：bigeye.market@bigeyegroup.com', b'1', b'0', '2021-01-03 09:56:48', '2021-01-03 09:56:48');
INSERT INTO `user_following` VALUES (39, 3, '3136788225', '好煮艺', 'https://m.weibo.cn/u/3136788225', '/images/user-profile-photo/baf79701ly8fjwwtmvwtij20ku0kuwf8.jpg', '美食作家 作品《美食健康谱》美食点评团成员 美食作家，专注美食分享。关注@好煮艺，学得好厨艺。微博合作推广请联系微 信：ldtg2018', b'1', b'0', '2021-01-03 09:57:45', '2021-01-03 09:57:45');
INSERT INTO `user_following` VALUES (40, 3, '6257740340', '夏妈厨房', 'https://m.weibo.cn/u/6257740340', '/images/user-profile-photo/006PuQ7Oly8gdi6fzmux7j30u00u0mzt.jpg', '知名美食博主 微博故事红人 美食视频自媒体 微博原创视频博主 商务合作：XM16009', b'1', b'0', '2021-01-03 09:58:29', '2021-01-03 09:58:29');
INSERT INTO `user_following` VALUES (41, 3, '2010999701', '营养师张淋琳', 'https://m.weibo.cn/u/2010999701', '/images/user-profile-photo/77dd6b95ly8fz7ftj5uv6j20u00u0dj5.jpg', '一级公共营养师 美容师 健康管理师 陕西省养生协会副会长 知名健康养生博主 健康视频自媒体 原陕西省养生协会副会长，国家一级营养师，健康管理师，中医美容师，CCTV1、CCTV7、BTV生活、深圳卫视、旅游卫视、河北卫视等多家电视台特邀嘉宾！', b'1', b'0', '2021-01-03 10:00:56', '2021-01-03 10:00:56');
INSERT INTO `user_following` VALUES (42, 3, '2949338000', '本叔就是本切鸣', 'https://m.weibo.cn/u/2949338000', '/images/user-profile-photo/afcb5390jw8f4a5rbzov0j21kw2dcb29.jpg', '知名摄影博主 微博原创视频博主 男摄一枚/摄影前后期讲师/微信 ccremix 约拍 合作 请备注', b'1', b'0', '2021-01-03 10:01:22', '2021-01-03 10:01:22');
INSERT INTO `user_following` VALUES (43, 3, '2630646082', 'photoshop资源库', 'https://m.weibo.cn/u/2630646082', '/images/user-profile-photo/9ccc7942gw1enacnterhdj2050050jrk.jpg', '知名创意博主 微博知名设计美学博主 超话主持人（资源君超话） 设计！理想，让库粉们都成为设计师！', b'1', b'0', '2021-01-03 10:02:20', '2021-01-03 10:02:20');
INSERT INTO `user_following` VALUES (44, 3, '6414205745', '回形针PaperClip', 'https://m.weibo.cn/u/6414205745', '/images/user-profile-photo/00705lVnly8fm007um5jdj31jk1jk420.jpg', '泛科普视频自媒体 你的当代生活说明书。商务合作：paperclip@foxmail.com', b'1', b'0', '2021-01-03 10:03:19', '2021-01-03 10:03:19');
INSERT INTO `user_following` VALUES (45, 3, '7284299679', 'Freya飞呀', 'https://m.weibo.cn/u/7284299679', '/images/user-profile-photo/007WYb6vly8gdjbp50o3tj30e80e8gmb.jpg', '微博VLOG博主 我用尽全力过着平凡的一生♥️', b'1', b'0', '2021-01-03 10:04:16', '2021-01-03 10:04:16');
INSERT INTO `user_following` VALUES (46, 3, '6054601231', '基金小达人', 'https://m.weibo.cn/u/6054601231', '/images/user-profile-photo/006BKumzly8ftg4ak2ylmj30ro0roq59.jpg', '财经博主 微博基金合作作者 微博原创视频博主 头条文章作者 分享基金投资技巧，解答基金投资问题。', b'1', b'0', '2021-01-03 10:05:08', '2021-01-03 10:05:08');
INSERT INTO `user_following` VALUES (47, 3, '6032474791', '圖盗', 'https://m.weibo.cn/u/6032474791', '/images/user-profile-photo/006AfEgvjw8f871afe26cj30yi0xb766.jpg', '颜值博主 看看街上的帅哥美女是如何搭配衣服的 ...', b'1', b'0', '2021-01-03 10:06:04', '2021-01-03 10:06:04');
INSERT INTO `user_following` VALUES (48, 3, 'fac95b1a3b404e28aace410f06c9da8a', '深圳', 'https://m.weibo.cn/p/cardlist?containerid=2306570043_114.08595_22.547&extparam=map__', '/images/user-profile-photo/65ab8a79484048b5817d3898d241c596vrx97y76.jfif', '深圳周边 打卡记录', b'0', b'0', '2021-01-03 10:09:19', '2021-01-03 10:09:19');
INSERT INTO `user_following` VALUES (49, 3, '8f40e38b366e46e19407498fc4eb1c64', '长沙', 'https://m.weibo.cn/p/cardlist?containerid=2306570043_112.98228_28.19409&extparam=map__', '/images/user-profile-photo/24433698f26e43ec977db2036ba98adf0cfqx1y6.jfif', '长沙周边 打卡记录', b'0', b'0', '2021-01-03 10:12:34', '2021-01-03 10:12:34');
INSERT INTO `user_following` VALUES (50, 3, '3807059740', 'HeyUke_', 'https://m.weibo.cn/u/3807059740', '/images/user-profile-photo/e2eb1f1cly8gg2j0bqcakj20u00u0jwg.jpg', ' ～', b'1', b'0', '2021-01-03 10:14:55', '2021-01-03 10:14:55');
INSERT INTO `user_following` VALUES (51, 3, '3176592573', '-JinGi', 'https://m.weibo.cn/u/3176592573', '/images/user-profile-photo/bd56f4bdly8gozvnmq9chj20e80e8aam.jpg', '蜜蜂的膝盖', b'1', b'0', '2021-01-03 10:17:20', '2021-01-03 10:17:20');
INSERT INTO `user_following` VALUES (52, 3, '3802350894', '小赵灵长大后', 'https://m.weibo.cn/u/3802350894', '/images/user-profile-photo/e2a3452ely8g3b0o16c01j20u00u0gnz.jpg', ' ', b'1', b'0', '2021-01-03 10:22:35', '2021-01-03 10:22:35');
INSERT INTO `user_following` VALUES (53, 2, '217108321', '陈暖央LunaSea', 'https://space.bilibili.com/217108321/dynamic', '/images/user-profile-photo/7eb2951d1ff33da1900b2423319cc899e434d82c.jpg', '一个爱健身的正经UP主，喜欢健身的关注我，不喜欢的我再想想办法~ 围脖：@陈暖央', b'1', b'0', '2021-04-07 16:45:41', '2021-05-20 08:04:37');
INSERT INTO `user_following` VALUES (54, 2, '29959830', 'Topbook', 'https://space.bilibili.com/29959830/video', '/images/user-profile-photo/1f0ff00ad152f286f1dc47af2aadc0abfe221921.jpg', '让工具回归工具，让你成为你。「合作微信：yanghuaua（非全拼）」', b'1', b'0', '2021-04-07 16:55:27', '2021-04-07 16:55:27');
INSERT INTO `user_following` VALUES (55, 2, '547173382', '杨真直', 'https://space.bilibili.com/547173382/video', '/images/user-profile-photo/8a0a9f138771c304319cda85e4506b46cc714612.jpg', '公众号:杨真直 找我！商务微信：yangzz-001', b'1', b'0', '2021-04-07 16:56:25', '2021-04-07 16:56:25');
INSERT INTO `user_following` VALUES (56, 3, '2045254855', '__雷雨_', 'https://m.weibo.cn/u/2045254855', '/images/user-profile-photo/79e81cc7ly8gky4ig7rwhj20e80e874q.jpg', '知名旅游博主 有一个taobao店 🤷‍♀️', b'1', b'0', '2021-04-07 16:58:55', '2021-04-07 16:58:55');
INSERT INTO `user_following` VALUES (57, 3, '6488142313', 'LEIYU衣服好好看', 'https://m.weibo.cn/u/6488142313', '/images/user-profile-photo/00755AcFly8gmf6hdn42wj30e80e8t92.jpg', ' 淘宝店铺名：LEIYU STUDIO', b'1', b'0', '2021-04-07 17:00:19', '2021-04-07 17:00:19');
INSERT INTO `user_following` VALUES (58, 3, '7038906058', '用户刘妍汐', 'https://m.weibo.cn/u/7038906058', '/images/user-profile-photo/007Gmx0Sly8gb76xljde2j30ij0ijwef.jpg', ' 𝗠𝗼𝗱𝗲𝗹▫️𝗡𝗮𝗶𝗹𝗬𝗼𝗸𝗲▫️𝗖𝗧𝗕𝗨设计系大三在读▫️重庆女孩🌇', b'1', b'0', '2021-04-07 17:06:25', '2021-04-07 17:06:25');
INSERT INTO `user_following` VALUES (59, 2, '36416153', '周酷仔', 'https://space.bilibili.com/36416153/video', '/images/user-profile-photo/5508d4ca1d0739bf12afd4d6e997301c5b234b71.jpg', '快乐传播机', b'1', b'0', '2021-04-07 17:07:57', '2021-04-07 17:07:57');
INSERT INTO `user_following` VALUES (60, 3, '6864574333', 'ZlzYJh', 'https://m.weibo.cn/u/6864574333', '/images/user-profile-photo/007uz3mRly8gi6f6ew8txj30e80e8glu.jpg', ' 🎊🎊🎊', b'1', b'0', '2021-04-07 17:40:48', '2021-04-07 17:40:48');
INSERT INTO `user_following` VALUES (61, 3, '2289940200', '毛然-', 'https://m.weibo.cn/u/2289940200', '/images/user-profile-photo/887db6e8ly8gfjpt338arj20e80e8q3n.jpg', '摄影博主 约拍请私信', b'1', b'0', '2021-04-07 17:42:16', '2021-04-07 17:42:16');
INSERT INTO `user_following` VALUES (62, 3, '2731696573', '丧妹有点跳', 'https://m.weibo.cn/u/2731696573', '/images/user-profile-photo/a2d261bdly8gn6iidp8esj20ru0rvjt5.jpg', '微博vlog博主 🙋2.5次元半蠢不萌的妹子一只。🏡万年宅女🎐惬意慵懒的up主🙌一个想当裁缝能唱歌的舞者🙇合作事宜请私信~', b'1', b'0', '2021-04-07 17:45:30', '2021-04-07 17:45:30');
INSERT INTO `user_following` VALUES (63, 3, '2882083237', '陈暖央', 'https://m.weibo.cn/u/2882083237', '/images/user-profile-photo/abc919a5ly8gf3n6ovy7rj20u00u0tba.jpg', '暴走的萝莉品牌创始人 知名运动博主 TMall搜索：【暴走的萝莉】，工作洽谈发邮箱：nuan@s-loli.com 【ins:chennuanyang】', b'1', b'0', '2021-04-07 17:55:32', '2021-04-07 17:55:32');
INSERT INTO `user_following` VALUES (64, 3, '3920631851', '张饱饱baby', 'https://m.weibo.cn/u/3920631851', '/images/user-profile-photo/004hkzrBly8gnxtmbnfodj60u00u0ack02.jpg', '健身撰稿人 一位爱美妆的专业健身辣妹，关注我一起来健身变美吧！商务合作加VX：zxhdy13', b'1', b'0', '2021-04-20 16:54:55', '2021-04-20 16:54:55');
INSERT INTO `user_following` VALUES (65, 3, '1958509675', '豆豆波波茶是九九的最爱呀QAQ', 'https://m.weibo.cn/u/1958509675', '/images/user-profile-photo/74bc7c6bly8gorpnyk8xhj20u00u0q44.jpg', ' 看到优秀的人总是不由自主的自卑撒！！！', b'1', b'0', '2021-04-20 17:08:55', '2021-04-20 17:08:55');
INSERT INTO `user_following` VALUES (69, 3, '6905385839', '_月亮睡了吗', 'https://m.weibo.cn/u/6905385839', '/images/user-profile-photo/007xkiiXly8gj4z6t4xctj30e80e8t9s.jpg', ' 快走', b'1', b'0', '2021-04-25 20:02:33', '2021-04-25 20:02:33');
INSERT INTO `user_following` VALUES (71, 3, 'a3603574344e4bbb9e036d54243e41b9', 'werwr', 'https://m.weibo.cn/u/6905385839', '/images/user-profile-photo/97931e9aff724f8e96cd4e2014da4167yftmxbny.jpg', NULL, b'0', b'0', '2021-04-25 20:27:08', '2021-04-25 20:27:08');
INSERT INTO `user_following` VALUES (72, 3, '5d3466294ee0440ba8bff188fa5ee77b', 'werwrwrwr', 'https://m.weibo.cn/u/6905385839', '/images/user-profile-photo/1d84377855c540dfaa5145287cba2024gxlpsi8v.png', NULL, b'0', b'0', '2021-04-25 20:32:51', '2021-04-25 20:32:51');
INSERT INTO `user_following` VALUES (74, 3, '4ce3365c8065422f86c368a7ffb88576', 'dfgertsdfs', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/453ab783b05e42a58236eee0bd611aec6bac9hiq.png', 'sdfgwet34fghrtyh', b'0', b'0', '2021-04-26 15:08:57', '2021-04-26 15:08:57');
INSERT INTO `user_following` VALUES (75, 3, 'f3595d6f2d7d4ed9ae71b53c3e5a6058', 'test004', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/88fee488fe5f4ab9b0371877ad5c5033g6vfekov.png', 'test sign 2345356', b'0', b'0', '2021-04-26 16:31:05', '2021-04-26 16:31:05');
INSERT INTO `user_following` VALUES (76, 3, '57243966596e44fe951f3b059cf3e654', 'test005', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/948e93f88a124743a8eaab8f9c095be6qvdrxaat.png', 'test sign 2345356', b'0', b'0', '2021-04-26 16:41:47', '2021-04-26 16:41:47');
INSERT INTO `user_following` VALUES (78, 3, 'e07692038679496bb7c27c0e52ba5ccb', 'test007', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/656e1a7919db47ac8dc4216ced8d77c2gitztxxj.jpg', 'test sign 2345356', b'0', b'0', '2021-04-26 17:49:16', '2021-04-26 17:49:16');
INSERT INTO `user_following` VALUES (79, 3, 'e91dc76221ed408588b58435bea78a52', 'test008', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/386bee48595a477fbb5a85b80874c560l5bpqzin.jpg', 'test sign 2345356', b'0', b'0', '2021-04-26 17:57:01', '2021-04-26 17:57:01');
INSERT INTO `user_following` VALUES (80, 3, 'edc7f80bde3d46b9bd11cfec88d8664b', 'test999', 'https://m.weibo.cn/u/5821094126', '/images/user-profile-photo/3653c83697c948f8be38adea2926c3076hdhvu2c.jpg', '不成功便成仁', b'0', b'0', '2021-04-26 18:02:21', '2021-04-26 18:02:21');
INSERT INTO `user_following` VALUES (81, 3, '5821094126', '喻佳钰今天瘦了吗', 'https://m.weibo.cn/u/5821094126', '/images/user-profile-photo/006lWIuWly8g6dwuhlrlqj30ro0rogn0.jpg', '一个美女', b'1', b'0', '2021-04-26 18:38:47', '2021-04-26 18:38:47');
INSERT INTO `user_following` VALUES (82, 3, '5047558772', '小玉今天也要开心', 'https://m.weibo.cn/u/5047558772', '/images/user-profile-photo/005vB2Cgly8gouwceiqaej30ku0kugnl.jpg', '只是想睡个好觉', b'1', b'0', '2021-04-26 18:45:34', '2021-04-26 18:45:34');
INSERT INTO `user_following` VALUES (83, 3, '2847135953', 'FortyLions', 'https://m.weibo.cn/u/2847135953', '/images/user-profile-photo/a9b3d8d1jw8ewo8ufz2c0j20ku0kydg5.jpg', '时尚达人 微博vlog博主 工作联系📮: FortyLions40@outlook.com                    小📕/ Bilibili📺 : FortyLions40', b'1', b'0', '2021-04-26 19:05:04', '2021-04-26 19:05:04');
INSERT INTO `user_following` VALUES (84, 3, '5136300574', '夏夏夏呀xx', 'https://m.weibo.cn/u/5136300574', '/images/user-profile-photo/005BBopEly8gn8kxa1mprj30u00u0mz5.jpg', '很旧很旧的风在天上', b'1', b'0', '2021-04-26 19:51:34', '2021-04-26 19:51:34');
INSERT INTO `user_following` VALUES (85, 3, '884f8a787ea04b0bbab9a8dde4552be4', '5136300574', 'https://m.weibo.cn/u/5136300574', '/images/user-profile-photo/4252e2f68c224d05b606ff61d966a601iw9gu4dh.jpg', 'dfgert345', b'0', b'0', '2021-04-26 19:53:27', '2021-04-26 19:53:27');
INSERT INTO `user_following` VALUES (86, 3, '5469715609', 'Oriangle-', 'https://m.weibo.cn/u/5469715609', '/images/user-profile-photo/005YamTvly8gpcu6hvq8tj30e80e8my7.jpg', '是你的橘啊🍊', b'1', b'0', '2021-04-27 07:09:52', '2021-04-27 07:09:52');
INSERT INTO `user_following` VALUES (87, 3, '30d8674dc8c0451dbfd166288305d777', 'derfgsd000', 'https://m.weibo.cn/u/5821094126', '/images/user-profile-photo/bc975a87c23e468197285b631758ae604dryz1om.png', 'reterwd354df', b'0', b'0', '2021-04-29 12:44:19', '2021-05-04 17:29:07');
INSERT INTO `user_following` VALUES (88, 3, '79bc0419b1664b20a98a60f336504b0f', 'sdfwer1111', 'https://m.weibo.cn/u/3920631851', '/images/user-profile-photo/2f29593fd1aa485d81dd6de699331558pgijfvwy.png', 'fghert345ertert', b'0', b'0', '2021-04-29 15:20:44', '2021-04-29 15:20:44');
INSERT INTO `user_following` VALUES (89, 3, '652e6c073b844af48e1d6ff800c67368', 'ertefgdf2222', 'https://m.weibo.cn/u/3920631851', '/images/user-profile-photo/a4dbdb1dafe2499db01a5e5fb31a1472b1qjln48.jpeg', '测试下排序修改', b'0', b'0', '2021-04-29 15:25:51', '2021-05-04 13:03:27');
INSERT INTO `user_following` VALUES (91, 3, '1924a19462b549f4afd91ebc45cfc64f', 'werwer3333', 'https://m.weibo.cn/u/2882083237', '/images/user-profile-photo/dd9548cfc40045a3b2fa07fd62e9e4161aj0yr7f.jpeg', '动态更新生效啦，哈哈哈哈哈', b'0', b'0', '2021-04-29 18:36:59', '2021-04-29 18:36:59');
INSERT INTO `user_following` VALUES (92, 3, '68f1752fbfd44ca9938cb842d2d4fbf0', '测试1111', 'https://m.weibo.cn/u/2882083237', '/images/user-profile-photo/6b4b29c112da426ba1869cac6da860bfw8fxbr8g.jpg', '测试一下吧，写个签名又不会怀孕', b'0', b'0', '2021-04-30 03:16:58', '2021-04-30 03:16:58');
INSERT INTO `user_following` VALUES (93, 3, '1149780367', '--hhh我笑点很低', 'https://m.weibo.cn/u/1149780367', '/images/user-profile-photo/4488418fly8gpumi8uf0jj20uy0u0jt9.jpg', '人间清醒 ~', b'1', b'0', '2021-04-30 14:55:52', '2021-04-30 14:55:52');
INSERT INTO `user_following` VALUES (94, 3, '6501536720', '暴躁的朱小琪', 'https://m.weibo.cn/u/6501536720', '/images/user-profile-photo/0075ZMHuly8gngd2y2dkuj30e80e8js2.jpg', 'The love oneself is the beginning of a lifelong romance', b'1', b'0', '2021-05-03 12:54:53', '2021-05-03 12:54:53');
INSERT INTO `user_following` VALUES (95, 3, '3179687670', 'c-amory', 'https://m.weibo.cn/u/3179687670', '/images/user-profile-photo/bd862ef6ly8goqfnfd0yej20e80e8q3s.jpg', '有在好好生活', b'1', b'0', '2021-05-05 16:25:51', '2021-05-05 16:25:53');
INSERT INTO `user_following` VALUES (96, 3, '3161395620', '白切鸡不蘸酱', 'https://m.weibo.cn/u/3161395620', '/images/user-profile-photo/bc6f11a4ly8fqz8yaev6zj20ro0rogpy.jpg', '', b'1', b'0', '2021-05-05 16:26:50', '2021-05-05 16:26:51');
INSERT INTO `user_following` VALUES (97, 3, '2606717675', 'Violet_47', 'https://m.weibo.cn/u/2606717675', '/images/user-profile-photo/9b5f5aebly8gn78vs935fj20u00u00y9.jpg', 'لعل الزمان كان كله ربيعا🍃', b'1', b'0', '2021-05-05 16:27:33', '2021-05-05 16:27:36');
INSERT INTO `user_following` VALUES (99, 3, '5456695560', 'Feu_d_artifice', 'https://m.weibo.cn/u/5456695560', '/images/user-profile-photo/005XhJMIly8gq8ra6ltjmj30u00u040w.jpg', '心动是0.01秒', b'1', b'0', '2021-05-13 17:26:30', '2021-05-13 17:26:34');
INSERT INTO `user_following` VALUES (100, 3, '3540832610', '小猪吃草c', 'https://m.weibo.cn/u/3540832610', '/images/user-profile-photo/d30cd162ly8ge1qeoc55yj20u00u0417.jpg', '', b'1', b'0', '2021-05-14 07:49:00', '2021-05-14 07:49:01');
INSERT INTO `user_following` VALUES (101, 3, '1988946191', '干饭了老许', 'https://m.weibo.cn/u/1988946191', '/images/user-profile-photo/768ce90fly8g7f0805sb7j20u00u075y.jpg', '超话粉丝大咖（林书豪超话） 废话多吃饭也多', b'1', b'0', '2021-05-15 16:56:40', '2021-05-15 16:56:42');
INSERT INTO `user_following` VALUES (102, 2, '160c4dcb892d41e280b92b3a41f81e2e', '测试非用户1111', 'https://www.bilibili.com/v/life', '/images/user-profile-photo/5ab87646fede40f49638cb946b68392amginm9sw.jpg', '测试非用户是否共用，看看把', b'0', b'0', '2021-05-20 09:19:20', '2021-05-20 09:36:44');

-- ----------------------------
-- Table structure for user_following_relation
-- ----------------------------
DROP TABLE IF EXISTS `user_following_relation`;
CREATE TABLE `user_following_relation`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `following_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联关注用户id',
  `platform_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联平台id，冗余字段，用于提升查询效率',
  `type_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '关联关注类型表id，0-默认分类',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5。8-思想、学习；7-美食、营养；6、健身、锻炼；5-兴趣、生活；4~其他',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_where_order`(`user_id`, `platform_id`, `type_id`, `following_id`, `sort_no`) USING BTREE COMMENT '索引用于筛选和排序'
) ENGINE = InnoDB AUTO_INCREMENT = 111 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与关注用户关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_following_relation
-- ----------------------------
INSERT INTO `user_following_relation` VALUES (1, 1, 1, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (2, 1, 2, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (3, 1, 3, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (4, 1, 4, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (5, 1, 5, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (6, 1, 6, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (7, 1, 7, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (8, 1, 8, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (9, 1, 9, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (10, 1, 10, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (11, 1, 11, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (12, 1, 12, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (13, 1, 13, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (14, 1, 14, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (15, 1, 15, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (16, 1, 16, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (17, 1, 17, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (18, 1, 18, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (19, 1, 19, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (20, 1, 20, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (21, 1, 21, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (22, 1, 22, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (23, 1, 23, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (24, 1, 24, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (25, 1, 25, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (26, 1, 26, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (27, 1, 27, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (28, 1, 28, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (29, 1, 29, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (30, 1, 30, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (31, 1, 31, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (32, 1, 32, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (33, 1, 33, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (34, 1, 34, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (35, 1, 35, 3, 6, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (36, 1, 36, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (37, 1, 37, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (38, 1, 38, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (39, 1, 39, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (40, 1, 40, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (41, 1, 41, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (42, 1, 42, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (43, 1, 43, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (44, 1, 44, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (45, 1, 45, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (46, 1, 46, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (47, 1, 47, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (48, 1, 48, 3, 7, 1, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (49, 1, 49, 3, 7, 1, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (50, 1, 50, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (51, 1, 51, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (52, 1, 52, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (53, 1, 53, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (54, 1, 54, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (55, 1, 55, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (56, 1, 56, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (57, 1, 57, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (58, 1, 58, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (59, 1, 59, 2, 2, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:06:50');
INSERT INTO `user_following_relation` VALUES (60, 1, 60, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (61, 1, 61, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (62, 1, 62, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (63, 1, 63, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (64, 1, 64, 3, 7, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (65, 1, 65, 3, 9, 5, b'0', '2021-04-23 06:15:56', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (69, 1, 69, 3, 9, 5, b'1', '2021-04-25 20:02:33', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (71, 1, 71, 3, 9, 5, b'1', '2021-04-25 20:27:14', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (72, 1, 72, 3, 9, 5, b'1', '2021-04-25 20:32:51', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (74, 1, 74, 3, 9, 5, b'1', '2021-04-26 15:08:57', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (75, 1, 75, 3, 9, 5, b'1', '2021-04-26 16:31:05', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (76, 1, 76, 3, 9, 5, b'1', '2021-04-26 16:41:47', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (78, 1, 78, 3, 9, 5, b'1', '2021-04-26 17:49:16', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (79, 1, 79, 3, 9, 5, b'1', '2021-04-26 17:57:01', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (80, 1, 80, 3, 9, 5, b'1', '2021-04-26 18:02:21', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (81, 1, 81, 3, 9, 5, b'0', '2021-04-26 18:38:47', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (82, 1, 82, 3, 9, 5, b'0', '2021-04-26 18:45:34', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (83, 1, 83, 3, 9, 5, b'0', '2021-04-26 19:05:18', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (84, 1, 84, 3, 9, 5, b'1', '2021-04-26 19:51:34', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (85, 1, 85, 3, 9, 6, b'1', '2021-04-26 19:53:27', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (86, 1, 86, 3, 9, 5, b'0', '2021-04-27 07:09:52', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (87, 1, 84, 3, 9, 5, b'1', '2021-04-28 19:24:57', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (88, 1, 84, 3, 9, 5, b'1', '2021-04-28 19:30:19', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (89, 1, 84, 3, 9, 5, b'0', '2021-04-28 19:35:43', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (90, 1, 87, 3, 9, 5, b'1', '2021-04-29 12:44:19', '2021-05-19 16:09:57');
INSERT INTO `user_following_relation` VALUES (91, 1, 88, 3, 9, 5, b'1', '2021-04-29 15:20:44', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (92, 1, 64, 3, 7, 5, b'1', '2021-04-29 15:22:46', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (93, 1, 89, 3, 9, 6, b'1', '2021-04-29 15:25:51', '2021-05-19 15:18:14');
INSERT INTO `user_following_relation` VALUES (95, 1, 91, 3, 9, 5, b'1', '2021-04-29 18:36:59', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (96, 2, 92, 3, 9, 9, b'0', '2021-04-30 03:16:58', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (97, 1, 93, 3, 9, 4, b'1', '2021-04-30 14:55:52', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (98, 1, 94, 3, 9, 5, b'1', '2021-05-03 12:55:23', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (99, 1, 95, 3, 9, 5, b'0', '2021-05-05 16:25:51', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (100, 1, 96, 3, 9, 5, b'0', '2021-05-05 16:26:50', '2021-05-15 14:20:13');
INSERT INTO `user_following_relation` VALUES (101, 1, 97, 3, 9, 5, b'0', '2021-05-05 16:27:33', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (103, 1, 99, 3, 9, 5, b'0', '2021-05-13 17:26:30', '2021-05-15 14:20:15');
INSERT INTO `user_following_relation` VALUES (104, 1, 100, 3, 5, 5, b'0', '2021-05-14 07:49:00', '2021-05-15 14:17:23');
INSERT INTO `user_following_relation` VALUES (105, 1, 101, 3, 9, 5, b'0', '2021-05-15 16:56:40', '2021-05-15 16:56:40');
INSERT INTO `user_following_relation` VALUES (106, 2, 21, 2, 0, 10, b'0', '2021-05-20 06:23:50', '2021-05-20 08:08:12');
INSERT INTO `user_following_relation` VALUES (107, 2, 18, 2, 22, 5, b'1', '2021-05-20 06:30:38', '2021-05-20 06:58:56');
INSERT INTO `user_following_relation` VALUES (108, 2, 12, 2, 0, 5, b'0', '2021-05-20 07:59:24', '2021-05-20 08:08:12');
INSERT INTO `user_following_relation` VALUES (109, 2, 53, 2, 0, 5, b'0', '2021-05-20 08:04:36', '2021-05-20 08:04:36');
INSERT INTO `user_following_relation` VALUES (110, 2, 102, 2, 2, 5, b'0', '2021-05-20 09:19:50', '2021-05-20 09:19:50');

-- ----------------------------
-- Table structure for user_following_remark
-- ----------------------------
DROP TABLE IF EXISTS `user_following_remark`;
CREATE TABLE `user_following_remark`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `following_id` bigint(20) UNSIGNED NOT NULL COMMENT '注意是用户关系表的id',
  `label_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '备注/标签',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_where_order`(`user_id`, `following_id`, `sort_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 286 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关注备注（标签）表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_following_remark
-- ----------------------------
INSERT INTO `user_following_remark` VALUES (1, 1, 1, '独立思考，拒绝投喂', 5, b'0', '2021-01-02 10:39:41', '2021-01-02 10:39:41');
INSERT INTO `user_following_remark` VALUES (2, 1, 1, '适度安排，避免加压', 5, b'0', '2021-01-02 10:39:41', '2021-01-02 10:39:41');
INSERT INTO `user_following_remark` VALUES (3, 1, 1, '观点信号，自身现实', 5, b'0', '2021-01-02 10:39:41', '2021-01-02 10:39:41');
INSERT INTO `user_following_remark` VALUES (4, 1, 2, '别人的生活始终是别人的', 5, b'0', '2021-01-02 10:42:26', '2021-01-02 10:42:26');
INSERT INTO `user_following_remark` VALUES (5, 1, 2, '认清自身的实际情况，参考学习', 5, b'0', '2021-01-02 10:42:26', '2021-01-02 10:42:26');
INSERT INTO `user_following_remark` VALUES (6, 1, 3, '（格局太高，打工人可能用不到）', 1, b'0', '2021-01-02 10:46:38', '2021-01-02 10:46:38');
INSERT INTO `user_following_remark` VALUES (7, 1, 3, '大环境', 2, b'0', '2021-01-02 10:46:38', '2021-01-02 10:46:38');
INSERT INTO `user_following_remark` VALUES (8, 1, 3, '格局', 3, b'0', '2021-01-02 10:46:38', '2021-01-02 10:46:38');
INSERT INTO `user_following_remark` VALUES (9, 1, 3, '商业', 4, b'0', '2021-01-02 10:46:38', '2021-01-02 10:46:38');
INSERT INTO `user_following_remark` VALUES (10, 1, 3, '科技', 5, b'0', '2021-01-02 10:46:38', '2021-01-02 10:46:38');
INSERT INTO `user_following_remark` VALUES (11, 1, 4, '财经', 5, b'0', '2021-01-02 10:48:08', '2021-01-02 10:48:08');
INSERT INTO `user_following_remark` VALUES (12, 1, 4, '投资', 5, b'0', '2021-01-02 10:48:08', '2021-01-02 10:48:08');
INSERT INTO `user_following_remark` VALUES (13, 1, 4, '格局', 5, b'0', '2021-01-02 10:48:08', '2021-01-02 10:48:08');
INSERT INTO `user_following_remark` VALUES (14, 1, 4, '大环境', 5, b'0', '2021-01-02 10:48:08', '2021-01-02 10:48:08');
INSERT INTO `user_following_remark` VALUES (15, 1, 5, '知识', 5, b'0', '2021-01-02 11:19:57', '2021-01-02 11:19:57');
INSERT INTO `user_following_remark` VALUES (16, 1, 5, '生活', 5, b'0', '2021-01-02 11:19:57', '2021-01-02 11:19:57');
INSERT INTO `user_following_remark` VALUES (17, 1, 6, '心理', 5, b'0', '2021-01-02 11:20:50', '2021-01-02 11:20:50');
INSERT INTO `user_following_remark` VALUES (18, 1, 6, '生活', 5, b'0', '2021-01-02 11:20:50', '2021-01-02 11:20:50');
INSERT INTO `user_following_remark` VALUES (19, 1, 7, '（事实道理都懂，保持独立思考）', 1, b'0', '2021-01-02 11:23:45', '2021-01-02 11:23:45');
INSERT INTO `user_following_remark` VALUES (20, 1, 7, '经验分享', 2, b'0', '2021-01-02 11:23:45', '2021-01-02 11:23:45');
INSERT INTO `user_following_remark` VALUES (21, 1, 7, '思维理论', 3, b'0', '2021-01-02 11:23:45', '2021-01-02 11:23:45');
INSERT INTO `user_following_remark` VALUES (22, 1, 8, '（娱乐就行，思维影响不大）', 1, b'0', '2021-01-02 11:34:24', '2021-01-02 11:34:24');
INSERT INTO `user_following_remark` VALUES (23, 1, 8, '经验分享', 2, b'0', '2021-01-02 11:34:24', '2021-01-02 11:34:24');
INSERT INTO `user_following_remark` VALUES (24, 1, 8, '思维理论', 3, b'0', '2021-01-02 11:34:24', '2021-01-02 11:34:24');
INSERT INTO `user_following_remark` VALUES (25, 1, 9, '社会问题', 5, b'0', '2021-01-02 11:35:25', '2021-01-02 11:35:25');
INSERT INTO `user_following_remark` VALUES (26, 1, 9, '黑客领域', 5, b'0', '2021-01-02 11:35:25', '2021-01-02 11:35:25');
INSERT INTO `user_following_remark` VALUES (27, 1, 10, '饮食', 5, b'0', '2021-01-02 11:36:39', '2021-01-02 11:36:39');
INSERT INTO `user_following_remark` VALUES (28, 1, 10, '营养', 5, b'0', '2021-01-02 11:36:39', '2021-01-02 11:36:39');
INSERT INTO `user_following_remark` VALUES (29, 1, 10, '健康', 5, b'0', '2021-01-02 11:36:39', '2021-01-02 11:36:39');
INSERT INTO `user_following_remark` VALUES (30, 1, 11, '田园生活', 5, b'0', '2021-01-02 11:37:54', '2021-01-02 11:37:54');
INSERT INTO `user_following_remark` VALUES (31, 1, 11, '传统文化', 5, b'0', '2021-01-02 11:37:54', '2021-01-02 11:37:54');
INSERT INTO `user_following_remark` VALUES (32, 1, 12, '美食', 5, b'0', '2021-01-02 11:38:48', '2021-01-02 11:38:48');
INSERT INTO `user_following_remark` VALUES (33, 1, 12, '分享', 5, b'0', '2021-01-02 11:38:48', '2021-01-02 11:38:48');
INSERT INTO `user_following_remark` VALUES (34, 1, 13, '健身', 5, b'0', '2021-01-02 11:40:11', '2021-01-02 11:40:11');
INSERT INTO `user_following_remark` VALUES (35, 1, 13, '生活', 5, b'0', '2021-01-02 11:40:11', '2021-01-02 11:40:11');
INSERT INTO `user_following_remark` VALUES (36, 1, 14, '健身', 5, b'0', '2021-01-02 11:40:43', '2021-01-02 11:40:43');
INSERT INTO `user_following_remark` VALUES (37, 1, 14, '生活', 5, b'0', '2021-01-02 11:40:43', '2021-01-02 11:40:43');
INSERT INTO `user_following_remark` VALUES (38, 1, 15, '（看看就行了）', 1, b'0', '2021-01-02 11:42:26', '2021-01-02 11:42:26');
INSERT INTO `user_following_remark` VALUES (39, 1, 15, '健康', 2, b'0', '2021-01-02 11:42:26', '2021-01-02 11:42:26');
INSERT INTO `user_following_remark` VALUES (40, 1, 15, '护肤', 3, b'0', '2021-01-02 11:42:26', '2021-01-02 11:42:26');
INSERT INTO `user_following_remark` VALUES (41, 1, 16, '（推荐药品依然要谨慎买药用药）', 1, b'0', '2021-01-02 11:43:41', '2021-01-02 11:43:41');
INSERT INTO `user_following_remark` VALUES (42, 1, 16, '皮肤病学', 2, b'0', '2021-01-02 11:43:41', '2021-01-02 11:43:41');
INSERT INTO `user_following_remark` VALUES (43, 1, 17, '疾病', 5, b'0', '2021-01-02 11:45:29', '2021-01-02 11:45:29');
INSERT INTO `user_following_remark` VALUES (44, 1, 17, '健康', 5, b'0', '2021-01-02 11:45:29', '2021-01-02 11:45:29');
INSERT INTO `user_following_remark` VALUES (45, 1, 17, '科普', 5, b'0', '2021-01-02 11:45:29', '2021-01-02 11:45:29');
INSERT INTO `user_following_remark` VALUES (46, 1, 17, '注意不要自行带入', 5, b'0', '2021-01-02 11:45:29', '2021-01-02 11:45:29');
INSERT INTO `user_following_remark` VALUES (47, 1, 18, '绘画', 5, b'0', '2021-01-02 11:47:07', '2021-01-02 11:47:07');
INSERT INTO `user_following_remark` VALUES (48, 1, 18, '知识', 5, b'0', '2021-01-02 11:47:07', '2021-01-02 11:47:07');
INSERT INTO `user_following_remark` VALUES (49, 1, 18, '科普', 5, b'0', '2021-01-02 11:47:07', '2021-01-02 11:47:07');
INSERT INTO `user_following_remark` VALUES (50, 1, 19, '生活', 5, b'0', '2021-01-02 11:47:50', '2021-01-02 11:47:50');
INSERT INTO `user_following_remark` VALUES (51, 1, 19, '分享', 5, b'0', '2021-01-02 11:47:50', '2021-01-02 11:47:50');
INSERT INTO `user_following_remark` VALUES (52, 1, 20, '生活', 5, b'0', '2021-01-02 11:48:28', '2021-01-02 11:48:28');
INSERT INTO `user_following_remark` VALUES (53, 1, 20, '分享', 5, b'0', '2021-01-02 11:48:28', '2021-01-02 11:48:28');
INSERT INTO `user_following_remark` VALUES (54, 1, 21, 'ASMR', 1, b'0', '2021-01-02 11:49:07', '2021-01-02 11:49:07');
INSERT INTO `user_following_remark` VALUES (55, 1, 21, '生活', 2, b'0', '2021-01-02 11:49:07', '2021-01-02 11:49:07');
INSERT INTO `user_following_remark` VALUES (56, 1, 22, '（重质量，更新慢）', 1, b'0', '2021-01-02 11:50:37', '2021-01-02 11:50:37');
INSERT INTO `user_following_remark` VALUES (57, 1, 22, 'AI', 2, b'0', '2021-01-02 11:50:37', '2021-01-02 11:50:37');
INSERT INTO `user_following_remark` VALUES (58, 1, 22, '算法', 3, b'0', '2021-01-02 11:50:37', '2021-01-02 11:50:37');
INSERT INTO `user_following_remark` VALUES (59, 1, 23, '电子技术', 5, b'0', '2021-01-02 11:51:25', '2021-01-02 11:51:25');
INSERT INTO `user_following_remark` VALUES (60, 1, 24, '计算机技术', 5, b'0', '2021-01-02 11:52:02', '2021-01-02 11:52:02');
INSERT INTO `user_following_remark` VALUES (61, 1, 25, '娱乐明星', 3, b'0', '2021-01-02 12:05:12', '2021-01-02 12:05:12');
INSERT INTO `user_following_remark` VALUES (62, 1, 25, '资本斗秀场', 4, b'0', '2021-01-02 12:05:12', '2021-01-02 12:05:12');
INSERT INTO `user_following_remark` VALUES (63, 1, 25, '党政宣传干预', 1, b'0', '2021-01-02 12:05:12', '2021-01-02 12:05:12');
INSERT INTO `user_following_remark` VALUES (64, 1, 26, '党政', 5, b'0', '2021-01-02 12:06:41', '2021-01-02 12:06:41');
INSERT INTO `user_following_remark` VALUES (65, 1, 26, '官方', 5, b'0', '2021-01-02 12:06:41', '2021-01-02 12:06:41');
INSERT INTO `user_following_remark` VALUES (66, 1, 27, '党政', 5, b'0', '2021-01-02 12:07:23', '2021-01-02 12:07:23');
INSERT INTO `user_following_remark` VALUES (67, 1, 27, '官方', 5, b'0', '2021-01-02 12:07:23', '2021-01-02 12:07:23');
INSERT INTO `user_following_remark` VALUES (68, 1, 28, '党政', 5, b'0', '2021-01-02 12:07:58', '2021-01-02 12:07:58');
INSERT INTO `user_following_remark` VALUES (69, 1, 28, '官方', 5, b'0', '2021-01-02 12:07:58', '2021-01-02 12:07:58');
INSERT INTO `user_following_remark` VALUES (70, 1, 29, '党政', 5, b'0', '2021-01-02 12:08:38', '2021-01-02 12:08:38');
INSERT INTO `user_following_remark` VALUES (71, 1, 29, '官方', 5, b'0', '2021-01-02 12:08:38', '2021-01-02 12:08:38');
INSERT INTO `user_following_remark` VALUES (72, 1, 30, '党政', 5, b'0', '2021-01-02 12:09:17', '2021-01-02 12:09:17');
INSERT INTO `user_following_remark` VALUES (73, 1, 30, '官方', 5, b'0', '2021-01-02 12:09:17', '2021-01-02 12:09:17');
INSERT INTO `user_following_remark` VALUES (74, 1, 31, '生命', 5, b'0', '2021-01-02 12:10:01', '2021-01-02 12:10:01');
INSERT INTO `user_following_remark` VALUES (75, 1, 31, '健康', 5, b'0', '2021-01-02 12:10:01', '2021-01-02 12:10:01');
INSERT INTO `user_following_remark` VALUES (76, 1, 32, '时事', 5, b'0', '2021-01-02 12:11:17', '2021-01-02 12:11:17');
INSERT INTO `user_following_remark` VALUES (77, 1, 32, '要闻', 5, b'0', '2021-01-02 12:11:17', '2021-01-02 12:11:17');
INSERT INTO `user_following_remark` VALUES (78, 1, 32, '党政', 5, b'0', '2021-01-02 12:11:17', '2021-01-02 12:11:17');
INSERT INTO `user_following_remark` VALUES (79, 1, 32, '鸡毛', 5, b'0', '2021-01-02 12:11:17', '2021-01-02 12:11:17');
INSERT INTO `user_following_remark` VALUES (80, 1, 33, '时事', 5, b'0', '2021-01-02 12:13:50', '2021-01-02 12:13:50');
INSERT INTO `user_following_remark` VALUES (81, 1, 33, '要闻', 5, b'0', '2021-01-02 12:13:50', '2021-01-02 12:13:50');
INSERT INTO `user_following_remark` VALUES (82, 1, 33, '党政', 5, b'0', '2021-01-02 12:13:50', '2021-01-02 12:13:50');
INSERT INTO `user_following_remark` VALUES (83, 1, 33, '鸡毛', 5, b'0', '2021-01-02 12:13:50', '2021-01-02 12:13:50');
INSERT INTO `user_following_remark` VALUES (84, 1, 34, '宏观经济', 5, b'0', '2021-01-02 12:14:57', '2021-01-02 12:14:57');
INSERT INTO `user_following_remark` VALUES (85, 1, 35, '科技大杂烩', 5, b'0', '2021-01-02 12:15:35', '2021-01-02 12:15:35');
INSERT INTO `user_following_remark` VALUES (86, 1, 36, '田园生活', 5, b'0', '2021-01-03 09:28:55', '2021-01-03 09:28:55');
INSERT INTO `user_following_remark` VALUES (87, 1, 36, '传统文化', 5, b'0', '2021-01-03 09:28:55', '2021-01-03 09:28:55');
INSERT INTO `user_following_remark` VALUES (88, 1, 37, '美食', 5, b'0', '2021-01-03 09:54:37', '2021-01-03 09:54:37');
INSERT INTO `user_following_remark` VALUES (89, 1, 37, '分享', 5, b'0', '2021-01-03 09:54:37', '2021-01-03 09:54:37');
INSERT INTO `user_following_remark` VALUES (90, 1, 38, '美食', 5, b'0', '2021-01-03 09:56:48', '2021-01-03 09:56:48');
INSERT INTO `user_following_remark` VALUES (91, 1, 38, '分享', 5, b'0', '2021-01-03 09:56:48', '2021-01-03 09:56:48');
INSERT INTO `user_following_remark` VALUES (92, 1, 39, '美食', 5, b'0', '2021-01-03 09:57:45', '2021-01-03 09:57:45');
INSERT INTO `user_following_remark` VALUES (93, 1, 39, '分享', 5, b'0', '2021-01-03 09:57:45', '2021-01-03 09:57:45');
INSERT INTO `user_following_remark` VALUES (94, 1, 40, '美食', 5, b'0', '2021-01-03 09:58:30', '2021-01-03 09:58:30');
INSERT INTO `user_following_remark` VALUES (95, 1, 40, '分享', 5, b'0', '2021-01-03 09:58:30', '2021-01-03 09:58:30');
INSERT INTO `user_following_remark` VALUES (96, 1, 41, '饮食', 5, b'0', '2021-01-03 10:00:58', '2021-01-03 10:00:58');
INSERT INTO `user_following_remark` VALUES (97, 1, 41, '营养', 5, b'0', '2021-01-03 10:00:58', '2021-01-03 10:00:58');
INSERT INTO `user_following_remark` VALUES (98, 1, 41, '健康', 5, b'0', '2021-01-03 10:00:58', '2021-01-03 10:00:58');
INSERT INTO `user_following_remark` VALUES (99, 1, 42, '摄影', 5, b'0', '2021-01-03 10:01:22', '2021-01-03 10:01:22');
INSERT INTO `user_following_remark` VALUES (100, 1, 42, '教程', 5, b'0', '2021-01-03 10:01:22', '2021-01-03 10:01:22');
INSERT INTO `user_following_remark` VALUES (101, 1, 43, '资源', 5, b'0', '2021-01-03 10:02:21', '2021-01-03 10:02:21');
INSERT INTO `user_following_remark` VALUES (102, 1, 43, '教程', 5, b'0', '2021-01-03 10:02:21', '2021-01-03 10:02:21');
INSERT INTO `user_following_remark` VALUES (103, 1, 44, '知识', 5, b'0', '2021-01-03 10:03:19', '2021-01-03 10:03:19');
INSERT INTO `user_following_remark` VALUES (104, 1, 44, '科普', 5, b'0', '2021-01-03 10:03:19', '2021-01-03 10:03:19');
INSERT INTO `user_following_remark` VALUES (105, 1, 45, '生活', 5, b'0', '2021-01-03 10:04:16', '2021-01-03 10:04:16');
INSERT INTO `user_following_remark` VALUES (106, 1, 45, '分享', 5, b'0', '2021-01-03 10:04:16', '2021-01-03 10:04:16');
INSERT INTO `user_following_remark` VALUES (107, 1, 46, '理财', 5, b'0', '2021-01-03 10:05:08', '2021-01-03 10:05:08');
INSERT INTO `user_following_remark` VALUES (108, 1, 46, '基金', 5, b'0', '2021-01-03 10:05:08', '2021-01-03 10:05:08');
INSERT INTO `user_following_remark` VALUES (109, 1, 47, '街拍', 5, b'0', '2021-01-03 10:06:04', '2021-01-03 10:06:04');
INSERT INTO `user_following_remark` VALUES (110, 1, 47, '时尚', 5, b'0', '2021-01-03 10:06:04', '2021-01-03 10:06:04');
INSERT INTO `user_following_remark` VALUES (111, 1, 47, '审美', 5, b'0', '2021-01-03 10:06:04', '2021-01-03 10:06:04');
INSERT INTO `user_following_remark` VALUES (112, 1, 47, '欲望', 5, b'0', '2021-01-03 10:06:04', '2021-01-03 10:06:04');
INSERT INTO `user_following_remark` VALUES (113, 1, 48, '生活周边', 2, b'0', '2021-01-03 10:09:19', '2021-01-03 10:09:19');
INSERT INTO `user_following_remark` VALUES (114, 1, 48, '（别人的生活）', 2, b'1', '2021-01-03 10:09:19', '2021-01-03 10:09:19');
INSERT INTO `user_following_remark` VALUES (115, 1, 49, '（别人的生活）', 1, b'0', '2021-01-03 10:12:34', '2021-01-03 10:12:34');
INSERT INTO `user_following_remark` VALUES (116, 1, 49, '生活周边', 2, b'0', '2021-01-03 10:12:34', '2021-01-03 10:12:34');
INSERT INTO `user_following_remark` VALUES (117, 1, 50, 'Dancer', 5, b'0', '2021-01-03 10:15:02', '2021-01-03 10:15:02');
INSERT INTO `user_following_remark` VALUES (118, 1, 51, 'UI', 1, b'0', '2021-01-03 10:17:24', '2021-01-03 10:17:24');
INSERT INTO `user_following_remark` VALUES (119, 1, 52, 'HW\'PD', 5, b'0', '2021-01-03 10:22:35', '2021-01-03 10:22:35');
INSERT INTO `user_following_remark` VALUES (120, 1, 53, '健身', 5, b'0', '2021-04-07 16:54:45', '2021-04-07 16:54:45');
INSERT INTO `user_following_remark` VALUES (121, 1, 53, '生活', 5, b'0', '2021-04-07 16:54:45', '2021-04-07 16:54:45');
INSERT INTO `user_following_remark` VALUES (122, 1, 54, 'IT', 5, b'0', '2021-04-07 16:56:04', '2021-04-07 16:56:04');
INSERT INTO `user_following_remark` VALUES (123, 1, 54, '技术', 5, b'0', '2021-04-07 16:56:04', '2021-04-07 16:56:04');
INSERT INTO `user_following_remark` VALUES (124, 1, 54, '生活', 5, b'0', '2021-04-07 16:56:04', '2021-04-07 16:56:04');
INSERT INTO `user_following_remark` VALUES (125, 1, 55, '（理性借鉴，结合自身条件背景）', 1, b'0', '2021-04-07 16:57:13', '2021-04-07 16:57:13');
INSERT INTO `user_following_remark` VALUES (126, 1, 55, '感悟', 2, b'0', '2021-04-07 16:57:13', '2021-04-07 16:57:13');
INSERT INTO `user_following_remark` VALUES (127, 1, 55, '经验', 3, b'0', '2021-04-07 16:57:13', '2021-04-07 16:57:13');
INSERT INTO `user_following_remark` VALUES (128, 1, 55, '生活', 4, b'0', '2021-04-07 16:57:13', '2021-04-07 16:57:13');
INSERT INTO `user_following_remark` VALUES (129, 1, 56, '时尚', 5, b'0', '2021-04-07 17:00:02', '2021-04-07 17:00:02');
INSERT INTO `user_following_remark` VALUES (130, 1, 56, '穿搭', 5, b'0', '2021-04-07 17:00:02', '2021-04-07 17:00:02');
INSERT INTO `user_following_remark` VALUES (131, 1, 56, '生活', 5, b'0', '2021-04-07 17:00:02', '2021-04-07 17:00:02');
INSERT INTO `user_following_remark` VALUES (132, 1, 57, '时尚', 5, b'0', '2021-04-07 17:00:36', '2021-04-07 17:00:36');
INSERT INTO `user_following_remark` VALUES (133, 1, 57, '穿搭', 5, b'0', '2021-04-07 17:00:36', '2021-04-07 17:00:36');
INSERT INTO `user_following_remark` VALUES (134, 1, 57, '生活', 5, b'0', '2021-04-07 17:00:36', '2021-04-07 17:00:36');
INSERT INTO `user_following_remark` VALUES (135, 1, 58, '时尚', 5, b'0', '2021-04-07 17:06:34', '2021-04-07 17:06:34');
INSERT INTO `user_following_remark` VALUES (136, 1, 58, '穿搭', 5, b'0', '2021-04-07 17:06:34', '2021-04-07 17:06:34');
INSERT INTO `user_following_remark` VALUES (137, 1, 58, '生活', 5, b'0', '2021-04-07 17:06:34', '2021-04-07 17:06:34');
INSERT INTO `user_following_remark` VALUES (138, 1, 59, '时尚', 5, b'0', '2021-04-07 17:08:09', '2021-04-07 17:08:09');
INSERT INTO `user_following_remark` VALUES (139, 1, 59, '穿搭', 5, b'0', '2021-04-07 17:08:09', '2021-04-07 17:08:09');
INSERT INTO `user_following_remark` VALUES (140, 1, 59, '生活', 5, b'0', '2021-04-07 17:08:09', '2021-04-07 17:08:09');
INSERT INTO `user_following_remark` VALUES (141, 1, 60, '青春', 5, b'0', '2021-04-07 17:41:53', '2021-04-07 17:41:53');
INSERT INTO `user_following_remark` VALUES (142, 1, 60, '正能量', 5, b'0', '2021-04-07 17:41:53', '2021-04-07 17:41:53');
INSERT INTO `user_following_remark` VALUES (143, 1, 60, '生活', 5, b'0', '2021-04-07 17:41:53', '2021-04-07 17:41:53');
INSERT INTO `user_following_remark` VALUES (144, 1, 61, '青春', 5, b'0', '2021-04-07 17:42:21', '2021-04-07 17:42:21');
INSERT INTO `user_following_remark` VALUES (145, 1, 61, '正能量', 5, b'0', '2021-04-07 17:42:21', '2021-04-07 17:42:21');
INSERT INTO `user_following_remark` VALUES (146, 1, 61, '生活', 5, b'0', '2021-04-07 17:42:21', '2021-04-07 17:42:21');
INSERT INTO `user_following_remark` VALUES (147, 1, 62, 'ASMR', 1, b'0', '2021-04-07 17:45:30', '2021-04-07 17:45:30');
INSERT INTO `user_following_remark` VALUES (148, 1, 62, '分享', 2, b'0', '2021-04-07 17:45:30', '2021-04-07 17:45:30');
INSERT INTO `user_following_remark` VALUES (149, 1, 62, '生活', 3, b'0', '2021-04-07 17:45:30', '2021-04-07 17:45:30');
INSERT INTO `user_following_remark` VALUES (150, 1, 63, '身材', 1, b'0', '2021-04-07 17:55:32', '2021-04-07 17:55:32');
INSERT INTO `user_following_remark` VALUES (151, 1, 63, '健身', 2, b'0', '2021-04-07 17:55:32', '2021-04-07 17:55:32');
INSERT INTO `user_following_remark` VALUES (152, 1, 63, '时尚', 3, b'0', '2021-04-07 17:55:32', '2021-04-07 17:55:32');
INSERT INTO `user_following_remark` VALUES (153, 1, 63, '生活', 4, b'0', '2021-04-20 17:09:37', '2021-04-20 17:09:37');
INSERT INTO `user_following_remark` VALUES (154, 1, 64, '健身', 5, b'1', '2021-04-20 17:09:40', '2021-04-20 17:09:40');
INSERT INTO `user_following_remark` VALUES (155, 1, 64, '身材', 5, b'1', '2021-04-20 17:13:02', '2021-04-20 17:13:02');
INSERT INTO `user_following_remark` VALUES (156, 1, 64, '时尚', 5, b'1', '2021-04-20 17:14:49', '2021-04-20 17:14:49');
INSERT INTO `user_following_remark` VALUES (157, 1, 64, '生活', 5, b'1', '2021-04-20 17:14:51', '2021-04-20 17:14:51');
INSERT INTO `user_following_remark` VALUES (158, 1, 65, '凌佳慧', 5, b'0', '2021-04-20 17:14:54', '2021-04-20 17:14:54');
INSERT INTO `user_following_remark` VALUES (159, 1, 65, '青春', 5, b'0', '2021-04-20 17:25:45', '2021-04-20 17:25:45');
INSERT INTO `user_following_remark` VALUES (160, 1, 65, '单纯', 5, b'0', '2021-04-20 17:25:46', '2021-04-20 17:25:46');
INSERT INTO `user_following_remark` VALUES (161, 1, 65, '可爱', 5, b'0', '2021-04-20 17:25:49', '2021-04-20 17:25:49');
INSERT INTO `user_following_remark` VALUES (168, 1, 69, 't1', 3, b'0', '2021-04-25 20:02:42', '2021-04-25 20:02:42');
INSERT INTO `user_following_remark` VALUES (169, 1, 69, 't2', 2, b'0', '2021-04-25 20:02:42', '2021-04-25 20:02:42');
INSERT INTO `user_following_remark` VALUES (170, 1, 69, 't3', 1, b'0', '2021-04-25 20:02:42', '2021-04-25 20:02:42');
INSERT INTO `user_following_remark` VALUES (174, 1, 71, 't1', 3, b'0', '2021-04-25 20:27:18', '2021-04-25 20:27:18');
INSERT INTO `user_following_remark` VALUES (175, 1, 71, 't2', 2, b'0', '2021-04-25 20:27:18', '2021-04-25 20:27:18');
INSERT INTO `user_following_remark` VALUES (176, 1, 71, 't3', 1, b'0', '2021-04-25 20:27:18', '2021-04-25 20:27:18');
INSERT INTO `user_following_remark` VALUES (177, 1, 72, 't1', 3, b'0', '2021-04-25 20:32:52', '2021-04-25 20:32:52');
INSERT INTO `user_following_remark` VALUES (178, 1, 72, 't2', 2, b'0', '2021-04-25 20:32:52', '2021-04-25 20:32:52');
INSERT INTO `user_following_remark` VALUES (179, 1, 72, 't3', 1, b'0', '2021-04-25 20:32:52', '2021-04-25 20:32:52');
INSERT INTO `user_following_remark` VALUES (183, 1, 74, 't1', 3, b'0', '2021-04-26 15:08:58', '2021-04-26 15:08:58');
INSERT INTO `user_following_remark` VALUES (184, 1, 74, 't2', 2, b'0', '2021-04-26 15:08:58', '2021-04-26 15:08:58');
INSERT INTO `user_following_remark` VALUES (185, 1, 74, 't3', 1, b'0', '2021-04-26 15:08:58', '2021-04-26 15:08:58');
INSERT INTO `user_following_remark` VALUES (186, 1, 80, 'tag1', 3, b'0', '2021-04-26 18:02:21', '2021-04-26 18:02:21');
INSERT INTO `user_following_remark` VALUES (187, 1, 80, 'tag2', 2, b'0', '2021-04-26 18:02:21', '2021-04-26 18:02:21');
INSERT INTO `user_following_remark` VALUES (188, 1, 80, 'tag3', 1, b'0', '2021-04-26 18:02:21', '2021-04-26 18:02:21');
INSERT INTO `user_following_remark` VALUES (189, 1, 84, 'tag1', 3, b'1', '2021-04-26 19:51:34', '2021-04-26 19:51:34');
INSERT INTO `user_following_remark` VALUES (190, 1, 84, 'tag2', 4, b'1', '2021-04-26 19:51:34', '2021-04-26 19:51:34');
INSERT INTO `user_following_remark` VALUES (191, 1, 84, 'tag4', 2, b'1', '2021-04-26 19:51:34', '2021-04-26 19:51:34');
INSERT INTO `user_following_remark` VALUES (192, 1, 84, 'tag3', 3, b'1', '2021-04-26 19:51:34', '2021-04-26 19:51:34');
INSERT INTO `user_following_remark` VALUES (193, 1, 85, 'tag1', 2, b'0', '2021-04-26 19:53:28', '2021-04-26 19:53:28');
INSERT INTO `user_following_remark` VALUES (194, 1, 85, 'tag2', 1, b'0', '2021-04-26 19:53:28', '2021-04-26 19:53:28');
INSERT INTO `user_following_remark` VALUES (195, 1, 86, 'test1', 2, b'0', '2021-04-27 07:09:52', '2021-04-27 07:09:52');
INSERT INTO `user_following_remark` VALUES (196, 1, 86, 'test2', 1, b'0', '2021-04-27 07:09:52', '2021-04-27 07:09:52');
INSERT INTO `user_following_remark` VALUES (197, 1, 84, 'tag6', 1, b'1', '2021-04-28 16:07:45', '2021-04-28 16:07:45');
INSERT INTO `user_following_remark` VALUES (198, 1, 84, 'tag2', 3, b'1', '2021-04-28 17:06:01', '2021-04-28 17:06:01');
INSERT INTO `user_following_remark` VALUES (199, 1, 84, 'tag3', 2, b'1', '2021-04-28 17:06:01', '2021-04-28 17:06:01');
INSERT INTO `user_following_remark` VALUES (200, 1, 84, 'tag4', 1, b'1', '2021-04-28 17:06:01', '2021-05-11 20:10:22');
INSERT INTO `user_following_remark` VALUES (201, 1, 84, 'tag5', 1, b'1', '2021-04-28 17:06:01', '2021-04-28 17:06:01');
INSERT INTO `user_following_remark` VALUES (202, 1, 84, 'tag5', 2, b'1', '2021-04-28 17:09:29', '2021-04-28 17:09:29');
INSERT INTO `user_following_remark` VALUES (203, 1, 84, 'tag3', 2, b'1', '2021-04-28 17:09:29', '2021-04-28 17:09:29');
INSERT INTO `user_following_remark` VALUES (204, 1, 84, 'tag2', 2, b'1', '2021-04-28 17:44:19', '2021-04-28 17:44:19');
INSERT INTO `user_following_remark` VALUES (205, 1, 84, 'tag3', 2, b'1', '2021-04-28 17:44:19', '2021-04-28 17:44:19');
INSERT INTO `user_following_remark` VALUES (206, 1, 84, 'tag1', 4, b'1', '2021-04-28 17:47:09', '2021-04-28 17:47:09');
INSERT INTO `user_following_remark` VALUES (207, 1, 84, 'tag2', 1, b'1', '2021-04-28 17:47:09', '2021-04-28 17:47:09');
INSERT INTO `user_following_remark` VALUES (208, 1, 84, 'tag2', 3, b'1', '2021-04-28 18:06:35', '2021-04-28 18:06:35');
INSERT INTO `user_following_remark` VALUES (209, 1, 84, 'tag3', 2, b'1', '2021-04-28 18:06:35', '2021-04-28 18:06:35');
INSERT INTO `user_following_remark` VALUES (210, 1, 83, 'tag1', 3, b'0', '2021-04-28 18:57:16', '2021-04-28 18:57:16');
INSERT INTO `user_following_remark` VALUES (211, 1, 83, 'tag2', 2, b'0', '2021-04-28 18:57:16', '2021-04-28 18:57:16');
INSERT INTO `user_following_remark` VALUES (212, 1, 83, 'tag3', 1, b'0', '2021-04-28 18:57:16', '2021-04-28 18:57:16');
INSERT INTO `user_following_remark` VALUES (213, 1, 84, 'tag1', 4, b'1', '2021-04-28 19:25:14', '2021-04-28 19:25:14');
INSERT INTO `user_following_remark` VALUES (214, 1, 84, 'tag2', 3, b'1', '2021-04-28 19:25:31', '2021-04-28 19:25:31');
INSERT INTO `user_following_remark` VALUES (215, 1, 84, 'tag3', 2, b'1', '2021-04-28 19:25:49', '2021-04-28 19:25:49');
INSERT INTO `user_following_remark` VALUES (216, 1, 84, 'tag1', 4, b'1', '2021-04-28 19:30:19', '2021-04-28 19:30:19');
INSERT INTO `user_following_remark` VALUES (217, 1, 84, 'tag2', 3, b'1', '2021-04-28 19:31:18', '2021-04-28 19:31:18');
INSERT INTO `user_following_remark` VALUES (218, 1, 84, 'tag3', 1, b'0', '2021-04-28 19:31:29', '2021-05-11 20:10:22');
INSERT INTO `user_following_remark` VALUES (219, 1, 84, 'tag1', 3, b'0', '2021-04-28 19:35:43', '2021-05-11 20:10:22');
INSERT INTO `user_following_remark` VALUES (220, 1, 84, 'tag2', 2, b'0', '2021-04-28 19:35:50', '2021-05-11 20:10:22');
INSERT INTO `user_following_remark` VALUES (221, 1, 84, 'tag3', 2, b'1', '2021-04-28 19:35:54', '2021-04-28 19:35:54');
INSERT INTO `user_following_remark` VALUES (222, 1, 82, 'tag1', 3, b'0', '2021-04-28 19:45:38', '2021-04-28 19:45:38');
INSERT INTO `user_following_remark` VALUES (223, 1, 82, 'tag2', 2, b'0', '2021-04-28 19:45:38', '2021-04-28 19:45:38');
INSERT INTO `user_following_remark` VALUES (224, 1, 82, 'tag3', 1, b'0', '2021-04-28 19:45:38', '2021-04-28 19:45:38');
INSERT INTO `user_following_remark` VALUES (225, 1, 82, 'tag5', 1, b'1', '2021-04-28 19:47:17', '2021-04-28 19:47:17');
INSERT INTO `user_following_remark` VALUES (226, 1, 81, '美女？', 3, b'0', '2021-04-28 19:58:11', '2021-04-28 19:58:11');
INSERT INTO `user_following_remark` VALUES (227, 1, 81, '算吧', 2, b'0', '2021-04-28 19:58:11', '2021-04-28 19:58:11');
INSERT INTO `user_following_remark` VALUES (228, 1, 81, '哈哈', 1, b'1', '2021-04-29 05:17:24', '2021-04-29 05:17:24');
INSERT INTO `user_following_remark` VALUES (229, 1, 87, '自称', 2, b'0', '2021-04-29 12:44:19', '2021-05-04 17:29:08');
INSERT INTO `user_following_remark` VALUES (230, 1, 87, '是', 2, b'1', '2021-04-29 12:44:19', '2021-04-29 12:44:19');
INSERT INTO `user_following_remark` VALUES (231, 1, 87, '美女2', 1, b'0', '2021-04-29 12:44:19', '2021-05-04 17:29:08');
INSERT INTO `user_following_remark` VALUES (232, 1, 87, 'test', 1, b'1', '2021-04-29 14:58:31', '2021-04-29 14:58:31');
INSERT INTO `user_following_remark` VALUES (233, 1, 87, '哈哈', 2, b'1', '2021-04-29 15:18:32', '2021-05-04 14:27:42');
INSERT INTO `user_following_remark` VALUES (234, 1, 88, 'ce', 3, b'0', '2021-04-29 15:20:44', '2021-04-29 15:20:44');
INSERT INTO `user_following_remark` VALUES (235, 1, 88, 'cs3', 2, b'0', '2021-04-29 15:20:44', '2021-04-29 15:20:44');
INSERT INTO `user_following_remark` VALUES (236, 1, 64, '健身', 3, b'0', '2021-04-29 15:22:46', '2021-04-29 15:22:46');
INSERT INTO `user_following_remark` VALUES (237, 1, 64, '身材', 2, b'0', '2021-04-29 15:22:46', '2021-04-29 15:22:46');
INSERT INTO `user_following_remark` VALUES (238, 1, 89, 'tag1', 3, b'0', '2021-04-29 15:25:51', '2021-04-29 15:25:51');
INSERT INTO `user_following_remark` VALUES (239, 1, 89, 'tag2', 2, b'0', '2021-04-29 15:25:51', '2021-04-29 15:25:51');
INSERT INTO `user_following_remark` VALUES (241, 1, 91, 'fdsfd', 3, b'0', '2021-04-29 18:36:59', '2021-04-29 18:36:59');
INSERT INTO `user_following_remark` VALUES (242, 1, 91, 'wersf', 2, b'0', '2021-04-29 18:36:59', '2021-04-29 18:36:59');
INSERT INTO `user_following_remark` VALUES (243, 1, 91, 'dfgert', 1, b'1', '2021-04-29 18:39:48', '2021-04-29 18:39:48');
INSERT INTO `user_following_remark` VALUES (244, 1, 91, '34ert', 1, b'0', '2021-04-29 18:40:28', '2021-04-29 18:40:28');
INSERT INTO `user_following_remark` VALUES (245, 1, 92, 'tag12', 3, b'0', '2021-04-30 03:16:58', '2021-04-30 03:16:58');
INSERT INTO `user_following_remark` VALUES (246, 1, 92, 'tag2', 2, b'0', '2021-04-30 03:16:58', '2021-04-30 03:16:58');
INSERT INTO `user_following_remark` VALUES (247, 1, 92, 'tag3', 1, b'0', '2021-04-30 03:18:51', '2021-04-30 03:18:51');
INSERT INTO `user_following_remark` VALUES (248, 1, 64, '生活', 1, b'0', '2021-04-30 08:52:36', '2021-04-30 08:52:36');
INSERT INTO `user_following_remark` VALUES (249, 1, 88, 'erter', 1, b'0', '2021-04-30 09:16:01', '2021-04-30 09:16:01');
INSERT INTO `user_following_remark` VALUES (250, 1, 89, 'tag3', 1, b'0', '2021-04-30 09:17:38', '2021-04-30 09:17:38');
INSERT INTO `user_following_remark` VALUES (251, 1, 88, 'g45dr', 1, b'1', '2021-04-30 09:20:06', '2021-04-30 09:20:06');
INSERT INTO `user_following_remark` VALUES (252, 1, 91, '嗯嗯', 1, b'1', '2021-04-30 11:49:50', '2021-04-30 11:49:50');
INSERT INTO `user_following_remark` VALUES (253, 1, 87, '更新', 1, b'1', '2021-04-30 11:59:08', '2021-05-04 14:27:42');
INSERT INTO `user_following_remark` VALUES (254, 1, 89, 'new', 1, b'1', '2021-04-30 12:01:18', '2021-04-30 12:01:18');
INSERT INTO `user_following_remark` VALUES (255, 1, 48, '（别人的生活）', 1, b'1', '2021-04-30 12:45:54', '2021-04-30 12:45:54');
INSERT INTO `user_following_remark` VALUES (256, 1, 48, '（别人的生活）', 1, b'0', '2021-04-30 12:48:42', '2021-04-30 12:48:42');
INSERT INTO `user_following_remark` VALUES (257, 1, 49, '（别人的生活）', 1, b'1', '2021-04-30 12:49:58', '2021-04-30 12:49:58');
INSERT INTO `user_following_remark` VALUES (258, 1, 93, '测试', 3, b'0', '2021-04-30 14:55:52', '2021-04-30 14:55:52');
INSERT INTO `user_following_remark` VALUES (259, 1, 93, '标签', 2, b'0', '2021-04-30 14:55:52', '2021-04-30 14:55:52');
INSERT INTO `user_following_remark` VALUES (260, 1, 93, '有多低', 1, b'0', '2021-04-30 15:05:08', '2021-04-30 15:05:08');
INSERT INTO `user_following_remark` VALUES (261, 1, 25, '时事热搜', 2, b'0', '2021-05-02 02:04:36', '2021-05-02 02:04:36');
INSERT INTO `user_following_remark` VALUES (262, 1, 94, '测试', 2, b'0', '2021-05-04 07:49:37', '2021-05-04 07:49:37');
INSERT INTO `user_following_remark` VALUES (263, 1, 94, '看看', 1, b'0', '2021-05-04 07:49:37', '2021-05-04 07:49:37');
INSERT INTO `user_following_remark` VALUES (264, 1, 87, '更新', 1, b'1', '2021-05-04 17:27:45', '2021-05-04 17:29:08');
INSERT INTO `user_following_remark` VALUES (267, 1, 99, 'tag1', 4, b'0', '2021-05-13 17:26:31', '2021-05-13 17:52:23');
INSERT INTO `user_following_remark` VALUES (268, 1, 99, 'tag2', 3, b'0', '2021-05-13 17:26:31', '2021-05-13 17:52:23');
INSERT INTO `user_following_remark` VALUES (269, 1, 99, 'tag3', 2, b'0', '2021-05-13 17:52:23', '2021-05-13 17:52:23');
INSERT INTO `user_following_remark` VALUES (270, 1, 99, 'tag4', 1, b'0', '2021-05-13 17:52:23', '2021-05-13 17:52:23');
INSERT INTO `user_following_remark` VALUES (271, 1, 96, 'tag1', 3, b'0', '2021-05-13 19:02:20', '2021-05-13 19:02:20');
INSERT INTO `user_following_remark` VALUES (272, 1, 96, 'tag2', 2, b'0', '2021-05-13 19:02:20', '2021-05-13 19:02:20');
INSERT INTO `user_following_remark` VALUES (273, 1, 96, 'tag3', 1, b'0', '2021-05-13 19:02:20', '2021-05-13 19:02:20');
INSERT INTO `user_following_remark` VALUES (274, 1, 100, 'test', 1, b'0', '2021-05-14 07:49:00', '2021-05-14 07:49:00');
INSERT INTO `user_following_remark` VALUES (275, 1, 101, '干饭', 1, b'0', '2021-05-15 16:56:40', '2021-05-15 16:56:40');
INSERT INTO `user_following_remark` VALUES (276, 2, 21, '测试', 3, b'0', '2021-05-20 06:23:50', '2021-05-20 06:23:50');
INSERT INTO `user_following_remark` VALUES (277, 2, 21, '标签', 2, b'0', '2021-05-20 06:23:50', '2021-05-20 06:23:50');
INSERT INTO `user_following_remark` VALUES (278, 2, 21, '3333', 1, b'0', '2021-05-20 06:23:50', '2021-05-20 06:23:50');
INSERT INTO `user_following_remark` VALUES (279, 2, 18, 'sdfsf', 3, b'0', '2021-05-20 06:30:38', '2021-05-20 06:48:50');
INSERT INTO `user_following_remark` VALUES (280, 2, 18, 'werwr', 2, b'0', '2021-05-20 06:30:38', '2021-05-20 06:48:50');
INSERT INTO `user_following_remark` VALUES (281, 2, 18, 'upfsa', 1, b'0', '2021-05-20 06:48:50', '2021-05-20 06:48:50');
INSERT INTO `user_following_remark` VALUES (282, 2, 12, 'sdfsf', 2, b'0', '2021-05-20 07:59:24', '2021-05-20 07:59:24');
INSERT INTO `user_following_remark` VALUES (283, 2, 12, 'wersdf', 1, b'0', '2021-05-20 07:59:24', '2021-05-20 07:59:24');
INSERT INTO `user_following_remark` VALUES (284, 2, 53, 'sdfsf', 2, b'0', '2021-05-20 08:04:36', '2021-05-20 08:04:36');
INSERT INTO `user_following_remark` VALUES (285, 2, 53, 'werwer', 1, b'0', '2021-05-20 08:04:36', '2021-05-20 08:04:36');

-- ----------------------------
-- Table structure for user_following_type
-- ----------------------------
DROP TABLE IF EXISTS `user_following_type`;
CREATE TABLE `user_following_type`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `platform_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联平台id',
  `type_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关注者所属的类型',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_where_order`(`user_id`, `platform_id`, `sort_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户关注分类表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_following_type
-- ----------------------------
INSERT INTO `user_following_type` VALUES (1, 1, 2, '媒体平台', 9, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:08');
INSERT INTO `user_following_type` VALUES (2, 1, 2, '参考资源', 8, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:10');
INSERT INTO `user_following_type` VALUES (3, 1, 2, '生活圈子', 7, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:12');
INSERT INTO `user_following_type` VALUES (4, 1, 2, '陌生圈子', 6, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:14');
INSERT INTO `user_following_type` VALUES (5, 1, 2, '明星名人', 5, b'0', '2021-05-15 08:58:27', '2021-05-15 08:58:27');
INSERT INTO `user_following_type` VALUES (6, 1, 3, '媒体平台', 9, b'0', '2021-05-15 08:58:27', '2021-05-15 14:23:46');
INSERT INTO `user_following_type` VALUES (7, 1, 3, '参考资源', 8, b'0', '2021-05-15 08:58:27', '2021-05-15 14:23:48');
INSERT INTO `user_following_type` VALUES (8, 1, 3, '生活圈子', 7, b'0', '2021-05-15 08:58:27', '2021-05-15 14:23:51');
INSERT INTO `user_following_type` VALUES (9, 1, 3, '陌生圈子', 6, b'0', '2021-05-15 08:58:27', '2021-05-15 14:23:54');
INSERT INTO `user_following_type` VALUES (10, 1, 3, '明星名人', 5, b'0', '2021-05-15 08:58:27', '2021-05-15 08:58:27');
INSERT INTO `user_following_type` VALUES (11, 1, 4, '媒体平台', 9, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:20');
INSERT INTO `user_following_type` VALUES (12, 1, 4, '参考资源', 8, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:22');
INSERT INTO `user_following_type` VALUES (13, 1, 4, '生活圈子', 7, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:23');
INSERT INTO `user_following_type` VALUES (14, 1, 4, '陌生圈子', 6, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:26');
INSERT INTO `user_following_type` VALUES (15, 1, 4, '明星名人', 5, b'0', '2021-05-15 08:58:27', '2021-05-15 08:58:27');
INSERT INTO `user_following_type` VALUES (16, 1, 5, '媒体平台', 9, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:28');
INSERT INTO `user_following_type` VALUES (17, 1, 5, '参考资源', 8, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:30');
INSERT INTO `user_following_type` VALUES (18, 1, 5, '生活圈子', 7, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:33');
INSERT INTO `user_following_type` VALUES (19, 1, 5, '陌生圈子', 6, b'0', '2021-05-15 08:58:27', '2021-05-15 14:24:35');
INSERT INTO `user_following_type` VALUES (20, 1, 5, '明星名人', 5, b'0', '2021-05-15 08:58:27', '2021-05-15 08:58:27');
INSERT INTO `user_following_type` VALUES (21, 1, 3, '测试类型0000', 5, b'0', '2021-05-15 15:33:42', '2021-05-15 15:33:42');
INSERT INTO `user_following_type` VALUES (22, 2, 2, '默认分类', 5, b'1', '2021-05-20 06:12:20', '2021-05-20 08:08:12');
INSERT INTO `user_following_type` VALUES (23, 2, 2, '测试类型2222', 10, b'1', '2021-05-20 06:20:38', '2021-05-20 07:45:58');
INSERT INTO `user_following_type` VALUES (24, 2, 2, '测试类型', 5, b'0', '2021-05-20 07:48:44', '2021-05-20 07:48:44');

-- ----------------------------
-- Table structure for user_opinion
-- ----------------------------
DROP TABLE IF EXISTS `user_opinion`;
CREATE TABLE `user_opinion`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `platform_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联平台id',
  `opinion_type` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '观点对应类型，注意，这里 0 表示是平台的观点，默认类型不支持观点；其他-某一类型，默认0',
  `opinion_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '观点看法内容',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_where_order`(`user_id`, `platform_id`, `opinion_type`, `sort_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户观点看法表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_opinion
-- ----------------------------
INSERT INTO `user_opinion` VALUES (1, 1, 1, 0, '>其中对各个平台的观点看法仅仅是个人观点，可能有些片面、偏激，或是认知错误，不理会便是了，做好自己的事已不易。', 10, b'0', '2021-04-17 04:03:49', '2021-05-15 02:08:36');
INSERT INTO `user_opinion` VALUES (2, 1, 1, 0, '时间和健康是你最宝贵的财富，工作、生活要做的就是将它们变现。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (3, 1, 1, 0, '**你看到的不一定是真的**，有可能是别人刻意想让你看到的；你想看到的大多数在萌芽阶段就被扼杀和谐了，或者被劣质内容冲刷掉了。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (4, 1, 1, 0, '少看一些跟自己不搭界的东西，偌大的世界，互联网拉近了人类的距离，但人的圈子和阶层背景却非常现实和残酷。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (5, 1, 1, 0, '不要废话连篇，言简意赅，长篇大论自己都看不下去，想法总结再多依然可能控制不住自己的行为，不努力拼搏，哪来的经济能力成家立业。', 7, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (6, 1, 2, 0, '众多up主在各自专长的领域发光发热，百家齐放、争奇斗艳。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (7, 1, 2, 0, 'b站目前土壤环境很不错，犹如蓬勃生机的热带雨林。自媒体恰饭前景不错，各行业都有较为优秀的人做up主，优质内容也相对较多，内容丰富反而更要控制逛b站的时间，否则，你的业余时间就被b站收割了，“时间就是金钱”，这句话是真的。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (8, 1, 2, 0, '鱼龙混杂，内容水平参差不齐，好为人师的“野路子”、“半吊子”很多，当然大神们曾经也是小白，不懂别装懂装逼，虚心学习成长，这样的小up会更有魅力和发展空间。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (9, 1, 2, 0, '优质的一手资料教程基本都收费，你能免费看到的几乎都是一两年甚至更久之前的“老掉牙”的资料了，免费内容要么质量不高，要么加入广告营销，有的甚至**精心设计各种套路**（话术、剧本暗流涌动）让你购买付费内容。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (10, 1, 2, 0, '警惕“广告叫卖式”的up，“林子大了什么鸟都有”，一部分店铺拍一些自己工作场景视频，内容展示“我很厉害，很牛逼，我的产品质量高服务品质有保障，有什么需求来找我就行了！”，说白了就是利用b站平台做生意的，b站之后可能发展商家入驻业务（B2C），还有内容搜索业务，“你感兴趣的都在b站”，会不会再出现一个“百度一下，你就SD”呢？', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (11, 1, 2, 0, '每个人工作生活的时间精力都有限，事分轻重缓急，==即使内容很优质且看着都有用，但并不一定是你当下最迫切需要的==。完成主线目标任务，中间每间隔一两个小时，可以适当休息十分钟左右，放松大脑，调整压抑情绪，但辅线就尽量不要是技术学习内容了，压力过大达不到放松效果，反而还影响主线任务。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (12, 1, 2, 0, '经常看到一些大UP主都说自己某某大学毕业，或者逆袭名校上岸，名校光环有效吗？有！但是否能走得长远，还得是活到老学到老，社会、人生是一辈子的大学。不盲目、不自卑、不好高骛远，结合自身实际条件，踏实走好每一步，高楼大厦固然宏伟壮阔，但空中楼阁容易崩塌！', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (13, 1, 3, 0, '知道为什么现在的年轻人大都喜欢玩游戏吗？看看这几年评分高的电影、电视剧有几部啊？资本娱乐一个圈，矮子里面挑高个，硬凑生给，资本投喂（💩），为了圈快钱，捧造流量，造假热度...完全对不上观众的口味，不看吧，娱乐消遣的内容全都一个样，看吧，真的是==浪费生命==。上头政策管制约束只是一个框架，框架内部同样可以精彩纷呈，波澜壮阔。也不排除有各家对手职业买黑，毕竟市场蛋糕就这么大，竞争十分激烈。但观众用户都不是傻子，我想要的你没有，还一个劲地生给硬塞，投喂垃圾内容，脱离用户需求，还想吸引、留住用户？', 1, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (14, 1, 3, 0, '明星、网红、广告商、企业机构营销宣传的重点场所。2020年06月中旬热搜整顿后，加入了政府平台干预。以前只有渣浪和资本投喂娱乐八卦内容，现在又多了一个党媒官媒投喂政治民生内容。鸡毛蒜皮、屁大点事都能显示“沸”。资本买热搜、撤热搜手段越来越熟练，不想让你看到的，一眨眼的功夫就能把“爆”、“沸”的热度撤得无影无踪。', 7, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (15, 1, 3, 0, '“成也明星、败也明星”，明星评论全是控评，发布内容不是广告宣传就是自拍照片，因为有各家粉丝和各种营销号天天盯着，明星言论举止越来越谨慎，明星粉丝的距离拉得越来越远！只要年长一点的粉丝都逐渐清醒过来，脱离虚幻追星娱乐圈，不再愿意被资本当作韭菜割了。最后导致粉丝群体低龄化，“小-中-高-大”学生群体占大多数。步入社会开始工作的年轻人都或多或少的遭受了社会上残忍的“毒打”，沦为“社畜”，自己的生活都过不好，谁还管你什么明星私生活？！', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (16, 1, 3, 0, '关于”娱乐至死“，**至死的不是娱乐，而是人自身**。生活娱乐到处都是，酒吧、KTV、会所、电影院、游乐场、游戏、直播等等，哪个不是娱乐？青少年、年轻人追星荒废学业事业，不能把所有问题都归咎于娱乐明星，玩游戏一样也耽误学业。拿刀杀人的不是刀，而是人。把刀具管制了，就不会出现事故了？问题的重点在于人本身，从关注人自身的问题分析和行动，才能从本质上有效解决问题。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (17, 1, 3, 0, '“娱乐至死”、“追星荒废学业”、“玩游戏耽误学业”，等系列问题，从人身上寻找根本原因，是什么原因导致他们不停地沉迷娱乐、追星、玩游戏？家庭不和睦、家人关爱缺失导致孩子心理和性格上出现缺爱、孤僻的问题；父母亲戚长辈有没有做好榜样，家长自己各种行为不检点，自己做不到的事情，却把所有的期望要求一股脑强压在孩子的身上，不去聆听孩子的心声，缺乏有效沟通，不尊重孩子的想法。我是父母，你是孩子，你就得听话。缺乏关爱、陪伴、聆听、交流、尊重，还强制要求孩子做这做那，结果必然导致矛盾越来越恶化！孩子得不到父母的关爱和陪伴，就想要从其他地方寻找，虚幻的网络世界里，无数缺乏家长指引的孩子沉迷其中，数月数年，难以自拔。直到经历重大沉痛打击，才开始唤醒他们的内心，逐渐走出虚拟世界，追寻现实生活中的美好。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (18, 1, 3, 0, '为什么头部的名人明星经常被黑？==肉被他们吃了，汤也被他们喝了，骨头剩菜被他们宠物吃了，让其他人吃什么啊==？！少占用些资源吧，把`一九/二八法则`降到`三七/四六/五五法则`，你手上的资源已经堆不下了，让几个给后辈年轻人不行吗？非要让绝大部分人眼睁睁地看着你”吃好喝好“、”盆满钵满“？', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (19, 1, 3, 9, '微博、抖音、豆瓣等公众平台发视频、照片、美食、游玩的活跃人群，绝大部分都是还在上学（高中/大学）的年轻人。他们心智、思想还一直停留在学校这个封闭的环境里，还没真正体会到社会上的人情冷暖，校园里的单纯美好，毕业步入社会却成了最致命的弱点，轻易相信他人，容易上当受骗；坚信努力肯定会有回报，却没想到被上司公司压榨劳动力；渴望憧憬美好烂漫爱情，却被现实生活一次次打击。', 5, b'0', '2021-04-17 04:03:49', '2021-05-15 14:35:15');
INSERT INTO `user_opinion` VALUES (20, 1, 3, 9, '学业、工作、生活稳定，家人、朋友常聚，一切都按正常人生轨道进行的人群，他们根本不会在虚拟的社交平台花费时间精力，以弥补现实的缺失。**现实中越缺什么，在社交平台就越渴望得到什么**。缺爱、缺朋友，导致内心空虚孤独，现实生活得不到，肯定会到虚拟的世界寻求可能的机会或心灵的慰藉。', 5, b'0', '2021-04-17 04:03:49', '2021-05-15 14:35:16');
INSERT INTO `user_opinion` VALUES (21, 1, 3, 10, '局宣、广告、自拍。', 5, b'0', '2021-04-17 04:03:49', '2021-05-15 14:35:19');
INSERT INTO `user_opinion` VALUES (22, 1, 3, 10, '更博缓慢，半个月才更新。', 5, b'0', '2021-04-17 04:03:49', '2021-05-15 14:35:21');
INSERT INTO `user_opinion` VALUES (23, 1, 3, 10, '没啥互动，粉丝只是`韭菜`和`工具人`？', 5, b'0', '2021-04-17 04:03:49', '2021-05-15 14:35:23');
INSERT INTO `user_opinion` VALUES (24, 1, 4, 0, '有些兴趣小组还是很不错的，相互交流学习；个别“垃圾场”的污染扩散程度也需要保持清醒头脑和独立思考，避免被“洗脑”带节奏。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (25, 1, 4, 0, '小组内的成员是因为相同的兴趣爱好聚在一起，“物以类聚，人以群分”，跟贴吧一样，用户量不是很大，用户群里也偏年轻。跟某音5+亿用户相比，db的百十万，就像是一包盐里的一小粒。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (26, 1, 4, 0, '兴趣小组、贴吧、论坛，基本上都是相似的一类人，就好比你走进了一个行为习惯“疯癫”的村落，你以为自己是正常的，他们也以为自己是正常的，如果你选择留下，肯定避免不了主动或者被动疯癫化（即使是假装）。要么你就离开，别对着他们指手画脚，在他们眼中你才是疯癫的异类。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (27, 1, 4, 0, '可能出现各种NC、YY、显微镜ju人、营销、引战、带节奏。优质有看点、知识、情感分享内容被大量劣质内容、虚假营销内容淹没。想看的看不到，不想看的到处都是，像暴风式洗脑一般，待久了很大可能就被同化了。一个正常人在一群不正常的人里面，正常人反而是不正常的。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (28, 1, 5, 0, '**矮个子男生没人要吗？身高在择偶标准中有多重要**？有看到很多对男生 160+ 的情侣，每次看到都很有正能量。', 5, b'0', '2021-04-17 04:03:49', '2021-04-17 04:03:49');
INSERT INTO `user_opinion` VALUES (29, 1, 3, 10, '测试观点，**我是加粗字体**，==我有高亮你有码？==', 5, b'1', '2021-05-06 06:52:37', '2021-05-15 14:32:51');
INSERT INTO `user_opinion` VALUES (30, 1, 3, 10, '测试观点，特殊字符，验证一下**sql注入**。\'select 1=1', 5, b'1', '2021-05-06 09:02:54', '2021-05-15 14:32:54');
INSERT INTO `user_opinion` VALUES (31, 2, 1, 0, 'sdfsfsdf', 5, b'1', '2021-05-12 05:42:51', '2021-05-12 05:43:11');
INSERT INTO `user_opinion` VALUES (32, 1, 1, 0, '测试新增观点23333', 5, b'1', '2021-05-13 10:51:23', '2021-05-15 14:32:22');
INSERT INTO `user_opinion` VALUES (33, 1, 1, 0, '测试新增平台观点2222', 5, b'1', '2021-05-13 10:52:03', '2021-05-13 11:05:42');
INSERT INTO `user_opinion` VALUES (34, 1, 1, 0, 'this.userOpinion.', 5, b'1', '2021-05-13 14:05:43', '2021-05-15 14:32:24');
INSERT INTO `user_opinion` VALUES (35, 1, 1, 0, '新增一个观点又不会怀孕啊。。。', 5, b'1', '2021-05-13 14:08:11', '2021-05-15 14:32:55');
INSERT INTO `user_opinion` VALUES (36, 1, 1, 0, '后加入天涯是个', 5, b'1', '2021-05-13 14:10:03', '2021-05-15 14:32:26');
INSERT INTO `user_opinion` VALUES (37, 1, 3, 10, '新增一条观点又不会怀孕。。。', 5, b'0', '2021-05-15 15:58:08', '2021-05-15 15:58:08');
INSERT INTO `user_opinion` VALUES (38, 1, 1, 0, '添加一条测试观点，==看看==', 5, b'1', '2021-05-17 10:06:09', '2021-05-17 10:06:30');
INSERT INTO `user_opinion` VALUES (39, 2, 1, 0, '测试观点内容', 5, b'1', '2021-05-20 06:09:23', '2021-05-20 06:10:36');
INSERT INTO `user_opinion` VALUES (40, 2, 1, 0, '再来一个观点，又不会怎样，23333，drop delete ==LOOK LOOK==', 5, b'0', '2021-05-20 06:10:22', '2021-05-20 06:10:22');
INSERT INTO `user_opinion` VALUES (41, 2, 2, 0, 'b站也都是==恰饭==啊', 5, b'0', '2021-05-20 06:11:54', '2021-05-21 15:42:50');
INSERT INTO `user_opinion` VALUES (42, 2, 2, 23, '测试新增类型观点1111', 5, b'0', '2021-05-20 06:38:53', '2021-05-20 06:45:01');

-- ----------------------------
-- Table structure for user_platform
-- ----------------------------
DROP TABLE IF EXISTS `user_platform`;
CREATE TABLE `user_platform`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '平台名称',
  `name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '平台英文名称',
  `main_page` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台主页',
  `platform_logo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台logo',
  `platform_long_logo` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台长logo',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '社交媒体平台表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_platform
-- ----------------------------
INSERT INTO `user_platform` VALUES (1, '我的', 'mypages', '/', '/images/platform-logo/mypages-logo.png', '/images/platform-logo/mypages-long-logo.png', b'0', '2021-01-02 08:49:04', '2021-05-15 02:25:07');
INSERT INTO `user_platform` VALUES (2, 'B站', 'bilibili', 'https://www.bilibili.com', '/images/platform-logo/bilibili-logo.png', '/images/platform-logo/bilibili-long-logo.png', b'0', '2021-01-02 08:49:04', '2021-01-02 08:49:04');
INSERT INTO `user_platform` VALUES (3, '微博', 'weibo', 'https://weibo.com', '/images/platform-logo/weibo-logo.png', '/images/platform-logo/weibo-long-logo.png', b'0', '2021-01-02 08:49:04', '2021-01-02 08:49:04');
INSERT INTO `user_platform` VALUES (4, '豆瓣', 'douban', 'https://www.douban.com', '/images/platform-logo/douban-logo.png', '/images/platform-logo/douban-long-logo-2.png', b'0', '2021-01-02 08:49:04', '2021-01-02 08:49:04');
INSERT INTO `user_platform` VALUES (5, '知乎', 'zhihu', 'https://www.zhihu.com', '/images/platform-logo/zhihu-logo.png', '/images/platform-logo/zhihu-long-logo.png', b'0', '2021-01-02 08:49:04', '2021-01-02 08:49:04');

-- ----------------------------
-- Table structure for user_platform_relation
-- ----------------------------
DROP TABLE IF EXISTS `user_platform_relation`;
CREATE TABLE `user_platform_relation`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '表id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联用户id',
  `platform_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联平台id',
  `sort_no` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT '优先级由低到高：1-10，默认5',
  `is_deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '本条数据是否已删除，1-是；0-否，默认0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_where_order`(`user_id`, `sort_no`) USING BTREE,
  INDEX `idx_userId_platformId`(`user_id`, `platform_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与平台关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_platform_relation
-- ----------------------------
INSERT INTO `user_platform_relation` VALUES (1, 1, 1, 9, b'0', '2021-04-23 06:17:48', '2021-05-12 05:34:29');
INSERT INTO `user_platform_relation` VALUES (2, 1, 2, 8, b'0', '2021-04-23 06:17:48', '2021-05-12 05:34:32');
INSERT INTO `user_platform_relation` VALUES (3, 1, 3, 7, b'0', '2021-04-23 06:17:48', '2021-05-12 05:34:34');
INSERT INTO `user_platform_relation` VALUES (4, 1, 4, 6, b'0', '2021-04-23 06:17:48', '2021-05-12 05:34:37');
INSERT INTO `user_platform_relation` VALUES (5, 1, 5, 5, b'0', '2021-04-23 06:17:48', '2021-05-04 17:28:54');
INSERT INTO `user_platform_relation` VALUES (6, 2, 1, 7, b'0', '2021-05-12 05:38:52', '2021-05-12 05:38:52');
INSERT INTO `user_platform_relation` VALUES (7, 2, 2, 6, b'0', '2021-05-12 05:39:02', '2021-05-12 05:39:12');
INSERT INTO `user_platform_relation` VALUES (8, 2, 3, 5, b'0', '2021-05-12 05:39:09', '2021-05-12 05:39:09');

-- ----------------------------
-- Function structure for f_test_empty
-- ----------------------------
DROP FUNCTION IF EXISTS `f_test_empty`;
delimiter ;;
CREATE FUNCTION `f_test_empty`()
 RETURNS int(11)
  COMMENT '测试无参函数'
begin
	declare c int default 0;
	select count(*) into c from user where user_name like 'test%';
	return c;
end
;;
delimiter ;

-- ----------------------------
-- Function structure for f_test_params
-- ----------------------------
DROP FUNCTION IF EXISTS `f_test_params`;
delimiter ;;
CREATE FUNCTION `f_test_params`(name_str varchar(20), mobile_str varchar(20))
 RETURNS int(11)
  COMMENT '测试有参函数'
begin
	declare c int default 0;
	select count(*) into c from user where user_name like name_str and mobile like mobile_str;
	return c;
end
;;
delimiter ;

-- ----------------------------
-- Procedure structure for p_test_empty
-- ----------------------------
DROP PROCEDURE IF EXISTS `p_test_empty`;
delimiter ;;
CREATE PROCEDURE `p_test_empty`()
  COMMENT '测试无参类型存储过程'
begin
/*
自定义变量，只能接着 begin 定义，其他地方定义无效。
特别注意：存储过程的变量名不能与数据库表字段一样！否则表字段数据更新不了。
*/
declare no_str varchar(10) default '';
declare i int default 0;

/* 变量赋值 */
set no_str = replace(time(now()), ':', '');

/* 批量sql */
while i < 5 do
insert into user(user_name, `password`) values(concat('test', no_str, i), '123456');
set i=i+1;
end while; -- ; 必须加上，否则报错

end
;;
delimiter ;

-- ----------------------------
-- Procedure structure for p_test_in
-- ----------------------------
DROP PROCEDURE IF EXISTS `p_test_in`;
delimiter ;;
CREATE PROCEDURE `p_test_in`(in no_str varchar(10))
  COMMENT '测试 in 参数类型存储过程'
begin
-- declare no_str varchar(10) default '';
declare i int default 0;

/* 变量赋值 */
-- set no_str = replace(time(now()), ':', '');

/* 批量sql */
while i < 5 do
insert into user(user_name, `password`) values(concat('test', no_str, i), '123456');
set i=i+1;
end while;

end
;;
delimiter ;

-- ----------------------------
-- Procedure structure for p_test_in2
-- ----------------------------
DROP PROCEDURE IF EXISTS `p_test_in2`;
delimiter ;;
CREATE PROCEDURE `p_test_in2`(in uname varchar(20), in upwd varchar(100))
  COMMENT '测试 in 多个参数类型存储过程'
begin
	declare rt tinyint default 0;
	select count(*) into rt from user where user_name = uname and `password` = upwd;
	
	select if(rt > 0, '成功', '失败');
	
end
;;
delimiter ;

-- ----------------------------
-- Procedure structure for p_test_inout
-- ----------------------------
DROP PROCEDURE IF EXISTS `p_test_inout`;
delimiter ;;
CREATE PROCEDURE `p_test_inout`(inout str varchar(200))
  COMMENT '测试 inout 参数类型存储过程'
begin
	declare i int default 0;

	/* 批量sql */
	-- set str = ''; -- out 类型参数不会接收传入参数值，默认为 null，null 与任何数值操作的结果都为 null，影响正常结果
	
	-- 循环插入数据
	while i < 5 do
	insert into user(user_name, `password`) values(concat('inout', str, i), '123456');
	set i=i+1;
	end while;
	
	-- 重置输入参数
	set str = '';
	-- 嵌套游标结果集查询
	begin
		declare uname varchar(20) default '';
		-- 定义游标相关变量
		declare done int default 0;
		declare cur1 cursor for select user_name from user order by id desc limit 5;
		declare continue handler for not found set done=1;
		
		-- 循环遍历游标中的结果集
		open cur1;  
		cur1_loop: loop
			fetch cur1 into uname;
			if done=1 then 
				leave cur1_loop;
			end if;
			
			set str = concat(str, uname, '|');
			
		end loop cur1_loop;
		close cur1;

		-- 去掉最后一个“|”
		-- select str;
		set str = substring(str, 1, length(str)-1); -- 注意！MySQL 的 substring 下标从 1 开始，取[start, end]
		
	end;

end
;;
delimiter ;

-- ----------------------------
-- Procedure structure for p_test_out
-- ----------------------------
DROP PROCEDURE IF EXISTS `p_test_out`;
delimiter ;;
CREATE PROCEDURE `p_test_out`(out str varchar(200))
  COMMENT '测试 out 参数类型存储过程'
begin
declare uname varchar(20) default '';
-- 定义游标相关变量，游标劝退？没见过也别怕，游标就相当于一个结果集合，查出多条结果，接着遍历集合里面的数据
declare done int default 0;
declare cur1 cursor for select user_name from user order by id desc limit 5; -- 查询结果集放入游标
declare continue handler for not found set done=1; -- 设置游标遍历结束条件，注意！这句话只能放在定义语句最后


/* 批量sql */
set str = ''; -- out 类型参数不会接收传入参数值，默认为 null，null 与任何数值操作的结果都为 null，影响正常结果

-- 循环遍历游标中的结果集
open cur1;  
cur1_loop: loop
	fetch cur1 into uname;
	if done=1 then 
		leave cur1_loop;
	end if;
	
	set str = concat(str, uname, '|');
	
end loop cur1_loop;
close cur1;

-- 去掉最后一个“|”
-- select str;
set str = substring(str, 1, length(str)-1); -- 注意！MySQL 的 substring 下标从 1 开始，取[start, end]

end
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
