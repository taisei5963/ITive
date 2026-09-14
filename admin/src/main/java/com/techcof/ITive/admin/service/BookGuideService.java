package com.techcof.ITive.admin.service;

import com.techcof.ITive.common.constant.SystemRegex;
import com.techcof.ITive.common.csv.BookGuideCsv;
import com.techcof.ITive.common.database.repository.BookGuideRepository;
import com.techcof.ITive.common.database.repository.BookRepository;
import com.techcof.ITive.common.dto.CsvDto;
import com.techcof.ITive.common.dto.NextRecommendedBookDto;
import com.techcof.ITive.common.exception.UploadException;
import com.techcof.ITive.common.model.BookGuideModel;
import com.techcof.ITive.common.model.BookModel;
import com.techcof.ITive.common.service.MessageService;
import com.techcof.ITive.common.service.UploadCsvService;
import com.techcof.ITive.common.util.Strings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ブックガイドサービス
 */
@AllArgsConstructor
@Service
public class BookGuideService {
    /** ブックガイドリポジトリ */
    private BookGuideRepository bookGuideRepository;
    /** ブックリポジトリ */
    private BookRepository bookRepository;
    /** メッセージサービス */
    private MessageService messageService;

    /**
     * ブックガイドCSVを登録する
     *
     * @param csvDto    CSV DTO
     * @param emitter   SSEエミッター
     * @param createdId 担当者ID
     */
    @Transactional
    public void saveCsv(CsvDto<BookGuideCsv> csvDto, SseEmitter emitter, String createdId) {
        for (BookGuideCsv csv : csvDto.getRows()) {
            try {
                bookGuideRepository.store(csv.toModel(), createdId);
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

    /**
     * 引数のブックIDのブックガイド情報を取得する
     *
     * @param bookId ブックID
     * @return ブックガイドモデル
     */
    public Optional<BookGuideModel> selectByBookId(Long bookId) {
        return bookGuideRepository.selectByBookId(bookId);
    }

    /**
     * 次に読むべき書籍情報のリストを返却する
     *
     * @param bookId ブックID
     * @return 次に読むべき書籍情報のリスト
     */
    public List<NextRecommendedBookDto> getNextRecommendedBooks(Long bookId) {
        List<NextRecommendedBookDto> resultList = new ArrayList<>();

        Optional<BookGuideModel> bookGuideOpt = bookGuideRepository.selectByBookId(bookId);
        if (bookGuideOpt.isEmpty()) {
            return Collections.emptyList();
        }

        String nextRecommended = bookGuideOpt.get().getNextRecommended();
        String[] nextRecommends = nextRecommended.split("[\\n,・]+");

        Pattern pattern = Pattern.compile(SystemRegex.NEXT_RECOMMENDED_REGEX);

        for (String recommended : nextRecommends) {
            String target = recommended.trim();
            if (Strings.isEmpty(target)) {
                continue;
            }

            NextRecommendedBookDto dto = new NextRecommendedBookDto();
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) {
                String title = matcher.group(1).trim();
                String publisher = matcher.group(2).trim();

                dto.setTitle(title);
                dto.setPublisher(publisher);

                Optional<BookModel> bookOpt =
                        bookRepository.selectByTitleAndPublisher(title, publisher);
                if (bookOpt.isPresent()) {
                    dto.setHasImg(true);
                    dto.setImgUrl(bookOpt.get().getPicFileUrl());
                } else {
                    dto.setHasImg(false);
                }
                resultList.add(dto);
            } else {
                // INFO: 本来は到達しない
            }
        }
        return resultList;
    }
}
