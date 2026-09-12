package com.techcof.ITive.common.database.entity;

import lombok.Builder;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;
import org.seasar.doma.Table;
import org.seasar.doma.Version;

import java.time.LocalDateTime;

/**
 * ブックストアエンティティ
 */
@Builder(toBuilder = true)
@Entity(immutable = true)
@Table(name = "book_store")
public class BookStore {
    /** ブックストアID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_store_id")
    public final Long bookStoreId;

    /** ブックID */
    @Column(name = "book_id")
    public final Long bookId;

    /** AmazonURL */
    @Column(name = "amazon_url")
    public final String amazonUrl;

    /** RakutenURL */
    @Column(name = "rakuten_url")
    public final String rakutenUrl;

    /** KindleURL */
    @Column(name = "kindle_url")
    public final String kindleUrl;

    /** 作成日時 */
    @Column(name = "created_at")
    public final LocalDateTime createdAt;

    /** 更新日時 */
    @Column(name = "updated_at")
    public final LocalDateTime updatedAt;

    /** 削除日時 */
    @Column(name = "deleted_at")
    public final LocalDateTime deletedAt;

    /** 作成者ID */
    @Column(name = "created_id")
    public final String createdId;

    /** バージョン */
    @Version
    @Column(name = "ver")
    public final Integer ver;
}
