package com.techcof.ITive.common.database.dao;

import com.techcof.ITive.common.database.entity.BookStore;
import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.Result;

/**
 * ブックストアDAO
 */
@ConfigAutowireable
@Dao
public interface BookStoreDao {

    /**
     * 引数のブックIDを条件に取得する
     *
     * @param bookId ブックID
     * @return ブックストアエンティティ
     */
    @Select
    BookStore selectByBookId(Long bookId);

    /**
     * ブックストアを登録する
     *
     * @param entity ブックストアエンティティ
     * @return 登録結果
     */
    @Insert
    Result<BookStore> insert(BookStore entity);

    /**
     * ブックストアを更新する
     *
     * @param entity ブックストアエンティティ
     * @return 更新結果
     */
    @Update
    Result<BookStore> update(BookStore entity);

    /**
     * ブックストアを削除する
     *
     * @param entity ブックストアエンティティ
     * @return 削除結果
     */
    @Delete
    Result<BookStore> delete(BookStore entity);
}
