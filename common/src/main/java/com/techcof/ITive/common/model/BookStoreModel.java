package com.techcof.ITive.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * ブックストアモデル
 */
@Builder(toBuilder = true)
@Getter
public class BookStoreModel {
    /** ブックストアID */
    private Long bookStoreId;

    /** ブックID */
    private Long bookId;

    /** AmazonURL */
    private String amazonUrl;

    /** RakutenURL */
    private String rakutenUrl;

    /** KindleURL */
    private String kindleUrl;

    /** 作成日時 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新日時 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 作成者ID */
    private String createdId;

    /** バージョン */
    private Integer ver;
}
