package com.techcof.ITive.general.service;

import com.techcof.ITive.common.database.repository.BookStoreRepository;
import com.techcof.ITive.common.model.BookStoreModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * ブックストアサービス
 */
@AllArgsConstructor
@Service
public class BookStoreService {
    /** ブックストアリポジトリ */
    private BookStoreRepository bookStoreRepository;

    /**
     * 引数のブックIDのブックストア情報を取得する
     *
     * @param bookId ブックID
     * @return ブックストアモデル
     */
    public Optional<BookStoreModel> selectByBookId(Long bookId) {
        return bookStoreRepository.selectByBookId(bookId);
    }
}
