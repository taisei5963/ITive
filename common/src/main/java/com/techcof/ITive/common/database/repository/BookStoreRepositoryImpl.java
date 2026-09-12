package com.techcof.ITive.common.database.repository;

import com.techcof.ITive.common.database.dao.BookStoreDao;
import com.techcof.ITive.common.database.entity.BookStore;
import com.techcof.ITive.common.model.BookStoreModel;
import lombok.AllArgsConstructor;
import org.seasar.doma.jdbc.Result;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * ブックストアリポジトリ
 */
@AllArgsConstructor
@Repository
public class BookStoreRepositoryImpl implements BookStoreRepository {
    /** ブックストアDAO */
    private BookStoreDao bookStoreDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BookStoreModel> selectByBookId(Long bookId) {
        if (Objects.isNull(bookId)) {
            return Optional.empty();
        }
        BookStore entity = bookStoreDao.selectByBookId(bookId);
        return Optional.ofNullable(convertModel(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookStoreModel store(BookStoreModel model, String createdId) {
        LocalDateTime now = LocalDateTime.now();
        Result<BookStore> result;
        if (Objects.isNull(model.getBookStoreId())) {
            BookStoreModel setModel =
                    model.toBuilder().createdAt(now).updatedAt(now).createdId(createdId).ver(1)
                            .build();
            result = bookStoreDao.insert(convertEntity(setModel));
        } else {
            BookStoreModel setModel = model.toBuilder().updatedAt(now).createdId(createdId).build();
            result = bookStoreDao.update(convertEntity(setModel));
        }
        return convertModel(result.getEntity());
    }

    /**
     * 引数のブックストアエンティティをブックストアモデルに変換する
     *
     * @param entity ブックストアエンティティ
     * @return ブックストアモデル
     */
    private static BookStoreModel convertModel(BookStore entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return BookStoreModel.builder()
                .bookStoreId(entity.bookStoreId)
                .bookId(entity.bookId)
                .amazonUrl(entity.amazonUrl)
                .rakutenUrl(entity.rakutenUrl)
                .kindleUrl(entity.kindleUrl)
                .createdAt(entity.createdAt)
                .updatedAt(entity.updatedAt)
                .createdId(entity.createdId)
                .ver(entity.ver)
                .build();
    }

    /**
     * 引数のブックストアモデルをブックストアエンティティに変換する
     *
     * @param model ブックストアモデル
     * @return ブックストアエンティティ
     */
    private static BookStore convertEntity(BookStoreModel model) {
        if (Objects.isNull(model)) {
            return null;
        }
        return BookStore.builder()
                .bookStoreId(model.getBookStoreId())
                .bookId(model.getBookId())
                .amazonUrl(model.getAmazonUrl())
                .rakutenUrl(model.getRakutenUrl())
                .kindleUrl(model.getKindleUrl())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .createdId(model.getCreatedId())
                .ver(model.getVer())
                .build();
    }
}
