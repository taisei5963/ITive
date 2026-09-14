package com.techcof.ITive.admin.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcof.ITive.admin.config.BookStoreUploadConfig;
import com.techcof.ITive.admin.service.BookStoreService;
import com.techcof.ITive.admin.service.UploadFileService;
import com.techcof.ITive.common.constant.CsvDataType;
import com.techcof.ITive.common.constant.SiteType;
import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.constant.UploadType;
import com.techcof.ITive.common.csv.BookStoreCsv;
import com.techcof.ITive.common.database.repository.BookRepository;
import com.techcof.ITive.common.database.repository.BookStoreRepository;
import com.techcof.ITive.common.dto.Account;
import com.techcof.ITive.common.dto.CsvDto;
import com.techcof.ITive.common.dto.TempFileDto;
import com.techcof.ITive.common.job.UploadCsvJob;
import com.techcof.ITive.common.model.BookModel;
import com.techcof.ITive.common.model.BookStoreModel;
import com.techcof.ITive.common.service.MessageService;
import com.techcof.ITive.common.service.UploadCsvService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ブックストアCSVアップロードジョブ
 */
@AllArgsConstructor
@Component
public class UploadBookStoreCsvJob implements UploadCsvJob<BookStoreCsv> {
    /** ブックストアリポジトリ */
    private BookStoreRepository bookStoreRepository;
    /** ブックリポジトリ */
    private BookRepository bookRepository;
    /** ブックストアサービス */
    private BookStoreService bookStoreService;
    /** ブックストアアップロード設定 */
    private BookStoreUploadConfig bookStoreUploadConfig;
    /** メッセージサービス */
    private MessageService messageService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String validateUploadFile(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            return messageService.getMessage("errors.upload.notFound");
        }
        Pattern pattern = Pattern.compile(SystemRegex.FILE_EXTENSION, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(file.getOriginalFilename());
        if (!matcher.matches()) {
            return messageService.getMessage("errors.extension.type", "csv");
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TempFileDto saveTempFile(MultipartFile file, Account account) {
        Path tmpFile =
                UploadFileService.getTmpFilePath(bookStoreUploadConfig.getTempFileName(),
                        account.id);
        UploadFileService.saveTmpCsvFile(file, tmpFile);
        return new TempFileDto(tmpFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execImport(CsvDto<BookStoreCsv> csvDto, TempFileDto tempFileDto, SseEmitter emitter,
                           Account account) {
        bookStoreService.saveCsv(csvDto, emitter, account.code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> extraValidation(List<BookStoreCsv> csvList, SseEmitter emitter,
                                        Account account) {
        List<String> errors = new ArrayList<>();
        Iterator<BookStoreCsv> ite = csvList.iterator();
        int count = 0;
        while (ite.hasNext()) {
            BookStoreCsv csv = ite.next();
            count++;
            boolean hasError = false;
            CsvDataType dataType = CsvDataType.getEnum(csv.getCsvDataType());
            BookModel bookModel = bookRepository.selectByJanCode(csv.getJanCode()).orElse(null);
            BookStoreModel bookStoreModel = null;

            if (Objects.isNull(bookModel)) {
                errors.add(messageService.getMessage("csv.error.book.notExists",
                        csv.getRowNum().toString(), csv.getJanCode()));
                hasError = true;
            } else {
                csv.setBookId(bookModel.getBookId());
                bookStoreModel =
                        bookStoreRepository.selectByBookId(bookModel.getBookId()).orElse(null);
            }

            if (dataType == CsvDataType.ADD) {
                if (Objects.nonNull(bookStoreModel)) {
                    errors.add(messageService.getMessage("csv.error.bookStore.exists",
                            csv.getRowNum().toString(), csv.getJanCode()));
                    hasError = true;
                }
            } else if (dataType == CsvDataType.UPDATE) {
                if (Objects.isNull(bookStoreModel)) {
                    errors.add(messageService.getMessage("csv.error.bookStore.notExists",
                            csv.getRowNum().toString(), csv.getJanCode()));
                    hasError = true;
                }
            }

            if (hasError) {
                ite.remove();
            }

            if (count % 1000 == 0) {
                UploadCsvService.sendEmitterProgressResponse(emitter,
                        "extraValidation loop. count: " + count);
            }
        }

        if (!errors.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> errorPayload = new HashMap<>();
                errorPayload.put("result", "ERROR");
                errorPayload.put("message", "CSV validation errors occurred.");
                errorPayload.put("errorMessages", errors);
                UploadCsvService.sendEmitterProgressResponse(emitter,
                        mapper.writeValueAsString(errorPayload));
            } catch (Exception e) {
                System.err.println("Error sending validation errors via SSE: " + e.getMessage());
            }
        }
        return errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BookStoreCsv> getCsvClass() {
        return BookStoreCsv.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncode() {
        return bookStoreUploadConfig.getEncode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadType getUploadType() {
        return UploadType.BOOK_STORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadProcessName() {
        return UploadType.BOOK_STORE.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiteType getSiteType() {
        return SiteType.ADMIN;
    }
}
