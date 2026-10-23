package com.techcof.ITive.common.csv;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvPattern;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.techcof.ITive.common.constant.CsvDataType;
import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.model.BookGuideModel;
import lombok.Getter;
import lombok.Setter;

/**
 * ブックガイドCSV
 */
@CsvBean(header = true)
@Getter
@Setter
public class BookGuideCsv implements CsvRow {
    @CsvColumn(label = "データ区分", number = 1)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvPattern(regex = SystemRegex.ADD_UPDATE_REGEX, message = "{csv.datatype.addUpdate.pattern}", cases = BuildCase.Read)
    private String csvDataType;

    @CsvColumn(label = "JANコード", number = 2)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvPattern(regex = SystemRegex.JAN_CODE_REGEX, message = "{csv.janCode.pattern}", cases = BuildCase.Read)
    private String janCode;

    @CsvColumn(label = "おすすめ対象者", number = 3)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvLengthMax(value = 1000, cases = BuildCase.Read)
    private String recommendFor;

    @CsvColumn(label = "おすすめタイミング", number = 4)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvLengthMax(value = 1000, cases = BuildCase.Read)
    private String recommendedTiming;

    @CsvColumn(label = "おすすめポイント", number = 5)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvLengthMax(value = 1000, cases = BuildCase.Read)
    private String recommendedPoint;

    @CsvColumn(label = "次のおすすめ", number = 6)
    @CsvRequire(considerBlank = true, cases = BuildCase.Read)
    @CsvLengthMax(value = 1000, cases = BuildCase.Read)
    private String nextRecommended;

    @CsvColumn(label = "登録日時", number = 7)
    private String createdAt;

    @CsvColumn(label = "更新日時", number = 8)
    private String updatedAt;

    @CsvColumn(label = "登録者ID", number = 9)
    private String createdId;

    /** 行数 */
    private Integer rowNum;

    /** ブックID（キャッシュ用） */
    private Long bookId;
    /** ブックガイドモデル（キャッシュ用） */
    private BookGuideModel bookGuideModel;

    /**
     * ブックガイドCSVをブックガイドモデルに変換する
     *
     * @return ブックガイドモデル
     */
    public BookGuideModel toModel() {
        CsvDataType dataType = CsvDataType.getEnum(csvDataType);
        if (dataType == CsvDataType.ADD) {
            return BookGuideModel.builder()
                    .bookId(getBookId())
                    .recommendFor(getRecommendFor())
                    .recommendedTiming(getRecommendedTiming())
                    .recommendedPoints(getRecommendedPoint())
                    .nextRecommended(getNextRecommended())
                    .build();
        } else if (dataType == CsvDataType.UPDATE) {
            return bookGuideModel.toBuilder()
                    .recommendFor(getRecommendFor())
                    .recommendedTiming(getRecommendedTiming())
                    .recommendedPoints(getRecommendedPoint())
                    .nextRecommended(getNextRecommended())
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
