package com.techcof.ITive.admin.controller;

import com.techcof.ITive.admin.dto.AdminDto;
import com.techcof.ITive.admin.job.UploadBookGuideCsvJob;
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
 * ブックガイドアップロードコントローラー
 */
@AllArgsConstructor
@Controller
@RequestMapping("/bookGuideUpload")
public class BookGuideUploadController {
    /** CSVアップロードサービス */
    private UploadCsvService uploadCsvService;
    /** ブックガイドCSVアップロードジョブ */
    private UploadBookGuideCsvJob uploadBookGuideCsvJob;
    /** 管理者DTO */
    private AdminDto adminDto;

    /**
     * ブックガイドCSVアップロード
     *
     * @return テンプレートパス
     */
    @RequestMapping({"", "/", "index"})
    public String index() {
        return "bookGuideUpload/index";
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
        uploadCsvService.upload(uploadBookGuideCsvJob, file, emitter, adminDto.getAccount());
        return emitter;
    }
}
