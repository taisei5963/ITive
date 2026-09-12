-- テーブルが存在する場合のみテーブル削除
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `action_role`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `book_chapter`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `maintenance`;
DROP TABLE IF EXISTS `privilege`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `reviewer`;
DROP TABLE IF EXISTS `sequence_manager`;
DROP TABLE IF EXISTS `SPRING_SESSION`;
DROP TABLE IF EXISTS `SPRING_SESSION_ATTRIBUTES`;
DROP TABLE IF EXISTS `upload_hist`;
DROP TABLE IF EXISTS `upload_hist_detail`;
SET FOREIGN_KEY_CHECKS = 1;

-- テーブル作成
CREATE TABLE `SPRING_SESSION` (
    `PRIMARY_ID` CHAR(36) NOT NULL,
    `SESSION_ID` CHAR(36) NOT NULL,
    `CREATION_TIME` BIGINT NOT NULL,
    `LAST_ACCESS_TIME` BIGINT NOT NULL,
    `MAX_INACTIVE_INTERVAL` INT NOT NULL,
    `EXPIRY_TIME` BIGINT NOT NULL,
    `PRINCIPAL_NAME` CHAR(100) DEFAULT NULL,
    PRIMARY KEY (`PRIMARY_ID`),
    UNIQUE KEY `SPRING_SESSION_UK_1` (`EXPIRY_TIME`),
    KEY `SPRING_SESSION_IDX_1` (`EXPIRY_TIME`),
    KEY `SPRING_SESSION_IDX_2` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='セッション';

CREATE TABLE `SPRING_SESSION_ATTRIBUTES` (
    `SESSION_PRIMARY_ID` CHAR(36) NOT NULL,
    `ATTRIBUTE_NAME` VARCHAR(200) NOT NULL,
    `ATTRIBUTE_BYTES` BLOB NOT NULL,
    PRIMARY KEY (`SESSION_PRIMARY_ID`, `ATTRIBUTE_NAME`),
    CONSTRAINT `SPRING_SESSION_FK_1` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `SPRING_SESSION` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='セッション属性';

CREATE TABLE `sequence_manager`(
    `sequence_manager_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'シーケンスID',
    `sequence` INT NOT NULL COMMENT 'シーケンス',
    `sequence_type` VARCHAR(32) NOT NULL COMMENT 'シーケンスタイプ',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    PRIMARY KEY (`sequence_manager_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='シーケンスマネージャー';

CREATE TABLE `upload_hist` (
    `upload_hist_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'アップロード履歴ID',
    `upload_type` VARCHAR(32) NOT NULL COMMENT 'アップロード種別',
    `upload_status` VARCHAR(32) DEFAULT 'EXECUTE' COMMENT 'アップロードステータス',
    `file_path` VARCHAR(255) DEFAULT NULL COMMENT 'ファイルパス',
    `file_size` INT DEFAULT NULL COMMENT 'ファイルサイズ',
    `row_count` INT DEFAULT NULL COMMENT '対象件数',
    `import_count` INT DEFAULT NULL COMMENT '取込対象件数',
    `site_type` VARCHAR(16) DEFAULT NULL COMMENT 'サイト種別',
    `created_at` DATETIME DEFAULT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`upload_hist_id`),
    KEY `upload_hist_idx_1` (`upload_type`),
    KEY `upload_hist_idx_2` (`created_id`,`site_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='アップロード履歴';

CREATE TABLE `upload_hist_detail` (
    `upload_hist_detail_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'アップロード履歴詳細ID',
    `upload_hist_id` BIGINT NOT NULL COMMENT 'アップロード履歴ID',
    `log_level` VARCHAR(16) DEFAULT NULL COMMENT 'ログレベル',
    `content` TEXT COMMENT '内容',
    `created_at` DATETIME DEFAULT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    PRIMARY KEY (`upload_hist_detail_id`),
    KEY `UPLOAD_HIST_DETAIL_IDX_1` (`upload_hist_id`),
    CONSTRAINT `UPLOAD_HIST_DETAIL_FK_1` FOREIGN KEY (`upload_hist_id`) REFERENCES `upload_hist` (`upload_hist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='アップロード履歴詳細';

CREATE TABLE `privilege` (
    `privilege_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '権限ID',
    `name` VARCHAR(255) NOT NULL COMMENT '権限名',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '説明',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`privilege_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='権限';

CREATE TABLE `admin` (
    `admin_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理者ID',
    `login_id` VARCHAR(30) DEFAULT NULL COMMENT 'ログインID',
    `password` TEXT COMMENT 'パスワード',
    `name` VARCHAR(255) NOT NULL COMMENT '管理者名',
    `privilege_id` BIGINT NOT NULL COMMENT '権限ID',
    `account_status` VARCHAR(15) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ステータス',
    `last_login_date` DATETIME DEFAULT NULL COMMENT '最終ログイン日時',
    `login_failure_count` INT NOT NULL DEFAULT '0' COMMENT 'ログイン失敗回数',
    `login_failure_date` DATETIME DEFAULT NULL COMMENT 'ログイン失敗日時',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`admin_id`),
    KEY `ADMIN_IDX_1` (`login_id`),
    KEY `ADMIN_IDX_2` (`privilege_id`),
    CONSTRAINT `ADMIN_FK_1` FOREIGN KEY (`privilege_id`) REFERENCES `privilege` (`privilege_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理者';

CREATE TABLE `reviewer` (
    `reviewer_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'レビュワーID',
    `login_id` VARCHAR(30) DEFAULT NULL COMMENT 'ログインID',
    `password` TEXT COMMENT 'パスワード',
    `name` VARCHAR(255) NOT NULL COMMENT 'レビュワー名',
    `account_status` VARCHAR(15) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ステータス',
    `last_login_date` DATETIME DEFAULT NULL COMMENT '最終ログイン日時',
    `login_failure_count` INT NOT NULL DEFAULT '0' COMMENT 'ログイン失敗回数',
    `login_failure_date` DATETIME DEFAULT NULL COMMENT 'ログイン失敗日時',
    `password_remind_date` DATETIME DEFAULT NULL COMMENT 'パスワードリマインド日時',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`reviewer_id`),
    KEY `ADMIN_IDX_1` (`login_id`),
    KEY `ADMIN_IDX_2` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='レビュワー';

CREATE TABLE `category` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'カテゴリID',
    `category_code` VARCHAR(16) NOT NULL COMMENT 'カテゴリコード',
    `category_name` VARCHAR(32) NOT NULL COMMENT 'カテゴリ名',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`category_id`),
    UNIQUE KEY `CATEGORY_UK_1` (`category_code`),
    KEY `CATEGORY_IDX_1` (`category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='カテゴリ';

CREATE TABLE `book` (
    `book_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ブックID',
    `jan_code` VARCHAR(16) NOT NULL COMMENT 'JANコード',
    `title` VARCHAR(100) NOT NULL COMMENT 'タイトル',
    `sub_title` VARCHAR(100) DEFAULT NULL COMMENT 'サブタイトル',
    `author1` VARCHAR(30) NOT NULL COMMENT '作者1',
    `author2` VARCHAR(30) DEFAULT NULL COMMENT '作者2',
    `translator` VARCHAR(30) DEFAULT NULL COMMENT '翻訳者',
    `publisher` VARCHAR(60) NOT NULL COMMENT '出版社',
    `pic_file_name` VARCHAR(128) DEFAULT NULL COMMENT 'ブック画像',
    `level` VARCHAR(15) NOT NULL DEFAULT 'NONE' COMMENT 'レベル',
    `category_id_1` BIGINT DEFAULT NULL COMMENT 'カテゴリID1',
    `category_id_2` BIGINT DEFAULT NULL COMMENT 'カテゴリID2',
    `category_id_3` BIGINT DEFAULT NULL COMMENT 'カテゴリID3',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`book_id`),
    UNIQUE KEY `BOOK_UK_1` (`jan_code`),
    KEY `BOOK_IDX_1` (`category_id_1`, `category_id_2`, `category_id_3`),
    CONSTRAINT `BOOK_FK_1` FOREIGN KEY (`category_id_1`) REFERENCES `category` (`category_id`) ON DELETE SET NULL,
    CONSTRAINT `BOOK_FK_2` FOREIGN KEY (`category_id_2`) REFERENCES `category` (`category_id`) ON DELETE SET NULL,
    CONSTRAINT `BOOK_FK_3` FOREIGN KEY (`category_id_3`) REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ブック';

CREATE TABLE `review` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'レビューID',
    `book_id` BIGINT NOT NULL COMMENT 'ブックID',
    `reviewer_id` BIGINT NOT NULL COMMENT 'レビュワーID',
    `rating` TINYINT UNSIGNED DEFAULT '0' COMMENT '評価',
    `comment` TEXT NOT NULL COMMENT 'コメント',
    `review_view_flag` VARCHAR(5) DEFAULT NULL COMMENT '表示フラグ',
    `review_date` DATE DEFAULT NULL COMMENT 'レビュー日',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`review_id`),
    KEY `REVIEW_IDX_1` (`book_id`),
    KEY `REVIEW_IDX_2` (`reviewer_id`),
    CONSTRAINT `REVIEW_FK_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT `REVIEW_FK_2` FOREIGN KEY (`reviewer_id`) REFERENCES `reviewer` (`reviewer_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='レビュー';

CREATE TABLE `action_role` (
    `action_role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'アクションロールID',
    `admin_id` BIGINT NOT NULL COMMENT '管理者ID',
    `path` VARCHAR(255) NOT NULL COMMENT 'アクションパス',
    `privilege_id` BIGINT NOT NULL COMMENT '権限ID',
    `available_flg` VARCHAR(8) NOT NULL DEFAULT 'OFF' COMMENT '利用可否フラグ',
    `note` TEXT COMMENT '備考',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`action_role_id`),
    KEY `ACTION_ROLE_IDX_1` (`admin_id`),
    CONSTRAINT `ACTION_ROLE_FK_1` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='アクションロール';

CREATE TABLE `maintenance` (
    `maintenance_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'メンテナンスID',
    `name` VARCHAR(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '名称',
    `type` VARCHAR(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'メンテナンスタイプ',
    `site_type` VARCHAR(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'サイト種別',
    `status` VARCHAR(8) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ステータス',
    `effective_from` DATETIME DEFAULT NULL COMMENT '適用開始日時',
    `effective_to` DATETIME DEFAULT NULL COMMENT '適用終了日',
    `message` TEXT COLLATE utf8mb4_general_ci COMMENT 'メッセージ',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`maintenance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='メンテナンス';

-- 2026.05.20 add
CREATE TABLE `book_chapter` (
    `book_chapter_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ブックチャプターID',
    `book_id` BIGINT NOT NULL COMMENT 'ブックID',
    `chapter` VARCHAR(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '章',
    `sort_order` INT NOT NULL DEFAULT '0' COMMENT 'ソート順',
    `title` VARCHAR(256) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'タイトル',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`book_chapter_id`),
    KEY `BOOK_CHAPTER_IDX_1` (`book_id`),
    CONSTRAINT `BOOK_CHAPTER_FK_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ブックチャプター';

-- 2026.05.27 add
CREATE TABLE `book_guide` (
    `book_guide_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ブックガイドID',
    `book_id` BIGINT NOT NULL COMMENT 'ブックID',
    `recommend_for` TEXT COLLATE utf8mb4_general_ci NOT NULL COMMENT 'おすすめ対象者',
    `recommended_timing` TEXT COLLATE utf8mb4_general_ci NOT NULL COMMENT 'おすすめタイミング',
    `recommended_points` TEXT COLLATE utf8mb4_general_ci NOT NULL COMMENT 'おすすめポイント',
    `next_recommended` TEXT COLLATE utf8mb4_general_ci NOT NULL COMMENT '次のおすすめ',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`book_guide_id`),
    KEY `BOOK_GUIDE_IDX_1` (`book_id`),
    CONSTRAINT `BOOK_GUIDE_FK_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ブックガイド';

-- 2026.09.12 add
CREATE TABLE `book_store` (
    `book_store_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ブックストアID',
    `book_id` BIGINT NOT NULL COMMENT 'ブックID',
    `amazon_url` TEXT DEFAULT NULL COMMENT 'AmazonURL',
    `rakuten_url` TEXT DEFAULT NULL COMMENT 'RakutenURL',
    `kindle_url` TEXT DEFAULT NULL COMMENT 'KindleURL',
    `created_at` DATETIME NOT NULL COMMENT '作成日時',
    `updated_at` DATETIME DEFAULT NULL COMMENT '更新日時',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '削除日時',
    `created_id` VARCHAR(30) DEFAULT NULL COMMENT '作成者ID',
    `ver` INT NOT NULL DEFAULT '0' COMMENT 'バージョン',
    PRIMARY KEY (`book_store_id`),
    KEY `BOOK_STORE_IDX_1` (`book_id`),
    CONSTRAINT `BOOK_STORE_FK_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='ブックストア';