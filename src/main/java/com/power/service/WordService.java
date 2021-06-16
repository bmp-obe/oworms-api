package com.power.service;

import com.power.domain.PartOfSpeech;
import com.power.domain.Word;
import com.power.dto.WordDTO;
import com.power.mapper.WordMapper;
import com.power.repository.WordRepository;
import com.power.util.WordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class WordService {

    private final WordRepository repository;
    private final WordMapper mapper;
    private final FileService fileService;
    private final SecurityService ss;

    public WordService(final WordRepository repository,
                       final WordMapper mapper,
                       final FileService fileService,
                       final SecurityService ss) {
        this.repository = repository;
        this.mapper = mapper;
        this.fileService = fileService;
        this.ss = ss;
    }

    @Transactional
    public void create(final WordDTO wordDTO, String id) {
        wordDTO.trim();

        if (wordExists(wordDTO)) {
            throw new EntityExistsException("That word already exists.");
        }

        final Word word = mapper.map(wordDTO, id);

        repository.save(word);
    }

    private boolean wordExists(WordDTO wordDTO) {
        return repository.findByTheWordIgnoreCase(wordDTO.getTheWord()).isPresent();
    }

    public List<WordDTO> retrieveAll(String theWord, String definition, String pos, String creator, String haveLearnt) {
        final List<Word> words = repository.findAll();

        return filter(words, theWord, definition, pos, creator, haveLearnt);
    }

    public WordDTO retrieve(final String theWord) {
        final Word word = repository.findByTheWordIgnoreCase(theWord).orElseThrow(EntityNotFoundException::new);

        word.setTimesViewed(word.getTimesViewed() + 1);
        repository.save(word);

        return mapper.map(word);
    }

    public boolean readCSV(final MultipartFile excelFile) {
        return fileService.writeWordsInSpreadsheetToDB(excelFile);
    }

    private List<WordDTO> filter(List<Word> words,
                                 String theWord,
                                 String definition,
                                 String pos,
                                 String creator,
                                 String haveLearnt) {
        List<Word> filteredWords = words
                .parallelStream()
                .filter(word -> isAMatch(word.getTheWord(), theWord))
                .filter(word -> isAMatch(word.getDefinition(), definition))
                .filter(word -> isAMatch(word.getPartOfSpeech(), pos))
                .filter(word -> isTheCreator(word.getCreatedBy(), creator))
                .filter(word -> haveLearntMatch(word.getHaveLearnt(), haveLearnt))
                .collect(toList());

        return mapper.map(filteredWords);
    }

    private boolean isAMatch(String theWord, String filterWord) {
        if (WordUtil.isBlank(filterWord)) {
            return true;
        }

        return WordUtil.isEqual(theWord, filterWord);
    }

    private boolean isAMatch(PartOfSpeech partOfSpeech, String posFilter) {
        if (WordUtil.isBlank(posFilter)) {
            return true;
        }

        try {
            PartOfSpeech partOfSpeechParsed = PartOfSpeech.getPartOfSpeech(posFilter);

            return partOfSpeech.equals(partOfSpeechParsed);
        } catch (IllegalArgumentException e) {
            // the part of speech is not recognized. return true to ignore this filter.
            // (the user is dumb)
            return true;
        }
    }

    private boolean isTheCreator(String createdBy, String creatorFilter) {
        boolean isBP = ss.isBP(creatorFilter);
        boolean isKMW = ss.isBP(creatorFilter);

        if (!isBP && !isKMW) {
            // ignore filter. unknown user.
            return true;
        }

        if (isBP) {
            return ss.isBP(createdBy);
        }

        return ss.isKeegan(createdBy);
    }

    private boolean haveLearntMatch(Boolean haveLearnt, String hlFilter) {
        boolean invalidFilter = hlFilter == null || !WordUtil.isEqual(hlFilter, "y") || !WordUtil.isEqual(hlFilter, "n");

        if (invalidFilter) {
            return true;
        }

        if (hlFilter.equals("y")) {
            return haveLearnt;
        }

        return !haveLearnt;
    }
}