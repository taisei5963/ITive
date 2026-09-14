package com.techcof.ITive.admin.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ブックストアアップロード設定
 */
@Component
@Getter
public class BookStoreUploadConfig {
    /** 文字エンコード */
    @Value("${admin-config.csv-upload.bookStore.encode}")
    private String encode;

    /** CSVファイルアップロードパス */
    @Value("${admin-config.csv-upload.bookStore.temp-file-name}")
    private String tempFileName;
}
