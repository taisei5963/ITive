package com.techcof.ITive.admin.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techcof.ITive.admin.config.CategoryUploadConfig;
import com.techcof.ITive.admin.service.CategoryService;
import com.techcof.ITive.admin.service.UploadFileService;
import com.techcof.ITive.common.constant.CsvDataType;
import com.techcof.ITive.common.constant.SiteType;
import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.constant.UploadType;
import com.techcof.ITive.common.csv.CategoryCsv;
import com.techcof.ITive.common.database.repository.CategoryRepository;
import com.techcof.ITive.common.dto.Account;
import com.techcof.ITive.common.dto.CsvDto;
import com.techcof.ITive.common.dto.TempFileDto;
import com.techcof.ITive.common.job.UploadCsvJob;
import com.techcof.ITive.common.model.CategoryModel;
import com.techcof.ITive.common.service.MessageService;
import com.techcof.ITive.common.service.UploadCsvService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * カテゴリCSVアップロードジョブ
 */
@AllArgsConstructor
@Component
public class UploadCategoryCsvJob implements UploadCsvJob<CategoryCsv> {
    /** カテゴリリポジトリ */
    private CategoryRepository categoryRepository;
    /** カテゴリサービス */
    private CategoryService categoryService;
    /** カテゴリアップロード設定 */
    private CategoryUploadConfig categoryUploadConfig;
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
                UploadFileService.getTmpFilePath(categoryUploadConfig.getTempFileName(),
                        account.id);
        UploadFileService.saveTmpCsvFile(file, tmpFile);
        return new TempFileDto(tmpFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execImport(CsvDto<CategoryCsv> csvDto, TempFileDto tempFileDto, SseEmitter emitter,
                           Account account) {
        categoryService.saveCsv(csvDto, emitter, account.code);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> extraValidation(List<CategoryCsv> csvList, List<Path> imgFiles,
                                        SseEmitter emitter, Account account) {
        List<String> errors = new ArrayList<>();

        Set<String> checkCodes = new HashSet<>();

        Iterator<CategoryCsv> ite = csvList.iterator();
        int count = 0;
        while (ite.hasNext()) {
            CategoryCsv csv = ite.next();
            count++;
            boolean hasError = false;
            CsvDataType dataType = CsvDataType.getEnum(csv.getCsvDataType());

            CategoryModel categoryModel =
                    categoryRepository.selectByCode(csv.getCategoryCode()).orElse(null);
            if (dataType == CsvDataType.ADD) {
                if (Objects.nonNull(categoryModel)) {
                    errors.add(messageService.getMessage("csv.error.category.exists",
                            csv.getRowNum().toString(), csv.getCategoryCode()));
                    hasError = true;
                }
            } else {
                if (Objects.isNull(categoryModel)) {
                    errors.add(messageService.getMessage("csv.error.category.notExists",
                            csv.getRowNum().toString(), csv.getCategoryCode()));
                    hasError = true;
                }
            }
            if (checkCodes.contains(csv.getCategoryCode())) {
                errors.add(messageService.getMessage("csv.error.category.duplicate",
                        csv.getRowNum().toString(), csv.getCategoryCode()));
                hasError = true;
            } else {
                checkCodes.add(csv.getCategoryCode());
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
    public Class<CategoryCsv> getCsvClass() {
        return CategoryCsv.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncode() {
        return categoryUploadConfig.getEncode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadType getUploadType() {
        return UploadType.CATEGORY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUploadProcessName() {
        return UploadType.CATEGORY.getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SiteType getSiteType() {
        return SiteType.ADMIN;
    }
}
