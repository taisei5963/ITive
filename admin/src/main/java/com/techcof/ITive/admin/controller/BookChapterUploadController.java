package com.techcof.ITive.admin.controller;

import com.techcof.ITive.admin.dto.AdminDto;
import com.techcof.ITive.admin.job.UploadBookChapterCsvJob;
import com.techcof.ITive.common.service.UploadCsvService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ブックチャプターアップロードコントローラー
 */
@AllArgsConstructor
@Controller
@RequestMapping("/bookChapterUpload")
public class BookChapterUploadController {
    /** CSVファイルアップロードサービス */
    private UploadCsvService uploadCsvService;
    /** ブックCSVアップロードジョブ */
    private UploadBookChapterCsvJob uploadBookChapterCsvJob;
    /** 管理者DTO */
    private AdminDto adminDto;

    /**
     * ブックチャプターCSVアップロード
     *
     * @return テンプレートパス
     */
    @RequestMapping({"", "/", "index"})
    public String index() {
        return "bookChapterUpload/index";
    }

    /**
     * CSVファイルをアップロードする<br>
     * SSEによる非同期処理
     *
     * @param file CSVファイル
     * @return SSEエミッター
     */
    @PostMapping("upload")
    @ResponseBody
    public SseEmitter upload(@RequestParam("csvFile") MultipartFile file) {
        long timeout = 30 * 60 * 1000;
        SseEmitter emitter = new SseEmitter(timeout);
        uploadCsvService.upload(uploadBookChapterCsvJob, file, emitter, adminDto.getAccount());
        return emitter;
    }
}
