package com.techcof.ITive.admin.service;

import com.techcof.ITive.common.csv.BookStoreCsv;
import com.techcof.ITive.common.database.repository.BookRepository;
import com.techcof.ITive.common.database.repository.BookStoreRepository;
import com.techcof.ITive.common.dto.CsvDto;
import com.techcof.ITive.common.exception.UploadException;
import com.techcof.ITive.common.service.MessageService;
import com.techcof.ITive.common.service.UploadCsvService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * ブックストアサービス
 */
@AllArgsConstructor
@Service
public class BookStoreService {
    /** ブックストアリポジトリ */
    private BookStoreRepository bookStoreRepository;
    /** メッセージサービス */
    private MessageService messageService;

    /**
     * ブックストアCSVを登録する
     *
     * @param csvDto    CSV DTO
     * @param emitter   SSEエミッター
     * @param createdId 担当者ID
     */
    @Transactional
    public void saveCsv(CsvDto<BookStoreCsv> csvDto, SseEmitter emitter, String createdId) {
        for (BookStoreCsv csv : csvDto.getRows()) {
            try {
                bookStoreRepository.store(csv.toModel(), createdId);
                csvDto.importSuccess();
                UploadCsvService.sendEmitterProgressResponseAuto(emitter, csvDto.getRowCount(),
                        csvDto.getImportCount());
            } catch (Exception e) {
                csvDto.appendErrorMessage(messageService.getMessage("csv.error.system.exception",
                        csv.getRowNum().toString(), e.getMessage()));
                throw new UploadException(messageService.getMessage("csv.error.exception"), e);
            }
            UploadCsvService.sendEmitterProgressResponse(emitter, csvDto.getRowCount(),
                    csvDto.getImportCount());
        }
    }
}
