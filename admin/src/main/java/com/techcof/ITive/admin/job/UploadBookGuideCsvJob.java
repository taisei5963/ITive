package com.techcof.ITive.admin.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcof.ITive.admin.config.BookGuideUploadConfig;
import com.techcof.ITive.admin.service.BookGuideService;
import com.techcof.ITive.admin.service.UploadFileService;
import com.techcof.ITive.common.constant.CsvDataType;
import com.techcof.ITive.common.constant.SiteType;
import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.constant.UploadType;
import com.techcof.ITive.common.csv.BookGuideCsv;
import com.techcof.ITive.common.database.repository.BookGuideRepository;
import com.techcof.ITive.common.database.repository.BookRepository;
import com.techcof.ITive.common.dto.Account;
import com.techcof.ITive.common.dto.CsvDto;
import com.techcof.ITive.common.dto.TempFileDto;
import com.techcof.ITive.common.job.UploadCsvJob;
import com.techcof.ITive.common.model.BookGuideModel;
import com.techcof.ITive.common.model.BookModel;
import com.techcof.ITive.common.service.MessageService;
import com.techcof.ITive.common.service.UploadCsvService;
import com.techcof.ITive.common.util.Strings;
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
 * ブックガイドCSVアップロードジョブ
 */
@AllArgsConstructor
@Component
public class UploadBookGuideCsvJob implements UploadCsvJob<BookGuideCsv> {
    /** ブックガイドリポジトリ */
    private BookGuideRepository bookGuideRepository;
    /** ブックリポジトリ */
    private BookRepository bookRepository;
    /** ブックガイドサービス */
    private BookGuideService bookGuideService;
    /** ブックアップロード設定 */
    private BookGuideUploadConfig bookGuideUploadConfig;
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
                UploadFileService.getTmpFilePath(bookGuideUploadConfig.getTempFileName(),
                        account.id);
        UploadFileService.saveTmpCsvFile(file, tmpFile);
        return new TempFileDto(tmpFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execImport(CsvDto<BookGuideCsv> csvDto, TempFileDto tempFileDto, SseEmitter emitter,
                           Account account) {
        bookGuideService.saveCsv(csvDto, emitter, account.code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> extraValidation(List<BookGuideCsv> csvList, SseEmitter emitter,
                                        Account account) {
        List<String> errors = new ArrayList<>();
        Pattern nextRecommendedPattern = Pattern.compile(SystemRegex.NEXT_RECOMMENDED_REGEX);

        Iterator<BookGuideCsv> ite = csvList.iterator();
        int count = 0;
        while (ite.hasNext()) {
            BookGuideCsv csv = ite.next();
            count++;
            boolean hasError = false;
            CsvDataType dataType = CsvDataType.getEnum(csv.getCsvDataType());
            BookModel bookModel =
                    bookRepository.selectByJanCode(csv.getJanCode()).orElse(null);
            BookGuideModel bookGuideModel = null;
            if (Objects.isNull(bookModel)) {
                errors.add(messageService.getMessage("csv.error.book.notExists",
                        csv.getRowNum().toString(), csv.getJanCode()));
                hasError = true;
            } else {
                csv.setBookId(bookModel.getBookId());
                bookGuideModel =
                        bookGuideRepository.selectByBookId(bookModel.getBookId()).orElse(null);
            }

            if (dataType == CsvDataType.ADD) {
                if (Objects.nonNull(bookGuideModel)) {
                    errors.add(messageService.getMessage("csv.error.bookGuide.exists",
                            csv.getRowNum().toString(), csv.getJanCode()));
                    hasError = true;
                }
            } else if (dataType == CsvDataType.UPDATE) {
                if (Objects.isNull(bookGuideModel)) {
                    errors.add(messageService.getMessage("csv.error.bookGuide.notExists",
                            csv.getRowNum().toString(), csv.getJanCode()));
                    hasError = true;
                }
            }

            if (!Strings.isEmpty(csv.getNextRecommended())) {
                String[] nextRecommends = csv.getNextRecommended().split("[\\n,・]+");
                boolean isFormatValid = true;
                for (String recommended : nextRecommends) {
                    String target = recommended.trim();
                    if (Strings.isEmpty(target)) {
                        continue;
                    }
                    if (!nextRecommendedPattern.matcher(target).matches()) {
                        isFormatValid = false;
                        break;
                    }
                }
                if (!isFormatValid) {
                    errors.add(messageService.getMessage("csv.error.bookGuide.nextRecommended.pattern",
                            csv.getRowNum().toString()));
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
    public Class<BookGuideCsv> getCsvClass() {
        return BookGuideCsv.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncode() {
        return bookGuideUploadConfig.getEncode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadType getUploadType() {
        return UploadType.BOOK_GUIDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadProcessName() {
        return UploadType.BOOK_GUIDE.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiteType getSiteType() {
        return SiteType.ADMIN;
    }
}
