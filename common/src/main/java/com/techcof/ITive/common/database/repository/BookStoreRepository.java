package com.techcof.ITive.common.database.repository;

import com.techcof.ITive.common.model.BookStoreModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ブックストアリポジトリ
 */
@Repository
public interface BookStoreRepository {

    /**
     * 引数のブックIDを条件に取得する
     *
     * @param bookId ブックID
     * @return
     */
    Optional<BookStoreModel> selectByBookId(Long bookId);

    /**
     * 引数のブックストアモデルで更新する
     *
     * @param model     ブックストアモデル
     * @param createdId 更新者ID
     * @return ブックストアモデル
     */
    BookStoreModel store(BookStoreModel model, String createdId);
}
