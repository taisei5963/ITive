package com.techcof.ITive.common.csv;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvPattern;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.techcof.ITive.common.constant.CsvDataType;
import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.model.BookStoreModel;
import lombok.Getter;
import lombok.Setter;

/**
 * ブックストアCSV
 */
@CsvBean(header = true)
@Getter
@Setter
public class BookStoreCsv implements CsvRow {
    @CsvColumn(label = "データ区分", number = 1)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvPattern(regex = SystemRegex.ADD_UPDATE_REGEX, message = "{csv.datatype.addUpdate.pattern}", cases = BuildCase.Read)
    private String csvDataType;

    @CsvColumn(label = "JANコード", number = 2)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvPattern(regex = SystemRegex.JAN_CODE_REGEX, message = "{csv.janCode.pattern}", cases = BuildCase.Read)
    private String janCode;

    @CsvColumn(label = "AmazonURL", number = 3)
    @CsvPattern(regex = SystemRegex.STORE_URL_REGEX, message = "{csv.storeURL.pattern}", cases = BuildCase.Read)
    private String amazonURL;

    @CsvColumn(label = "RakutenURL", number = 4)
    @CsvPattern(regex = SystemRegex.STORE_URL_REGEX, message = "{csv.storeURL.pattern}", cases = BuildCase.Read)
    private String rakutenURL;

    @CsvColumn(label = "KindleURL", number = 5)
    @CsvPattern(regex = SystemRegex.STORE_URL_REGEX, message = "{csv.storeURL.pattern}", cases = BuildCase.Read)
    private String kindleURL;

    @CsvColumn(label = "登録日時", number = 6)
    private String createdAt;

    @CsvColumn(label = "更新日時", number = 7)
    private String updatedAt;

    @CsvColumn(label = "登録者ID", number = 8)
    private String createdId;

    /** 行数 */
    private Integer rowNum;

    /** ブックID（キャッシュ用） */
    private Long bookId;
    /** ブックストアモデル（キャッシュ用） */
    private BookStoreModel bookStoreModel;

    /**
     * ブックストアCSVをブックストアモデルに変換する
     *
     * @return ブックストアモデル
     */
    public BookStoreModel toModel() {
        CsvDataType dataType = CsvDataType.getEnum(csvDataType);
        if (dataType == CsvDataType.ADD) {
            return BookStoreModel.builder()
                    .bookId(getBookId())
                    .amazonUrl(getAmazonURL())
                    .rakutenUrl(getRakutenURL())
                    .kindleUrl(getKindleURL())
                    .build();
        } else if (dataType == CsvDataType.UPDATE) {
            return bookStoreModel.toBuilder()
                    .amazonUrl(getAmazonURL())
                    .rakutenUrl(getRakutenURL())
                    .kindleUrl(getKindleURL())
                    .build();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSortValue() {
        return janCode;
    }
}
