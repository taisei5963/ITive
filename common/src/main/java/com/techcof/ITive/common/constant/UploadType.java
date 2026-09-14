package com.techcof.ITive.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * アップロード種別
 */
@AllArgsConstructor
@Getter
public enum UploadType {
    BOOK("01", "ブックCSVアップロード", false, false),
    BOOK_CHAPTER("02", "ブックチャプターCSVアップロード", false, false),
    CATEGORY("03", "カテゴリCSVアップロード", false, false),
    BOOK_GUIDE("04", "ブックガイドCSVアップロード", false, false),
    BOOK_STORE("05", "ブックストアCSVアップロード", false, false);

    /** 値 */
    private final String value;
    /** 説明 */
    private final String description;
    /** レビュワーサイトで使用する場合は true */
    private final boolean useReviewerSite;
    /** 一般サイトで使用する場合は true */
    private final boolean useGeneralSite;

    /**
     * 引数の値の UploadType 列挙型を返却する
     *
     * @param value 値
     * @return UploadType 列挙型
     */
    public static UploadType getEnum(String value) {
        for (UploadType e : values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
