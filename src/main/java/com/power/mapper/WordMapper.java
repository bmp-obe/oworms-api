package com.power.mapper;

import com.power.domain.PartOfSpeech;
import com.power.domain.Word;
import com.power.dto.WordDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class WordMapper {

    public Word map(WordDTO wordDTO) {
        PartOfSpeech partOfSpeech = PartOfSpeech.getPartOfSpeech(wordDTO.getPartOfSpeech());

        return Word
                .builder()
                .theWord(wordDTO.getTheWord())
                .definition(wordDTO.getDefinition())
                .partOfSpeech(partOfSpeech)
                .pronunciation(wordDTO.getPronunciation())
                .origin(wordDTO.getOrigin())
                .exampleUsage(wordDTO.getExampleUsage())
                .note(wordDTO.getNote())
                .haveLearnt(wordDTO.isHaveLearnt())
                .creationDate(wordDTO.getCreationDate())
                .createdBy(wordDTO.getCreatedBy())
                .timesViewed(wordDTO.getTimesViewed())
                .build();
    }

    public WordDTO map(Word word) {
        return WordDTO
                .builder()
                .id(word.getId())
                .theWord(word.getTheWord())
                .definition(word.getDefinition())
                .partOfSpeech(word.getPartOfSpeech().getLabel())
                .pronunciation(word.getPronunciation())
                .origin(word.getOrigin())
                .exampleUsage(word.getExampleUsage())
                .note(word.getNote())
                .haveLearnt(word.isHaveLearnt())
                .creationDate(word.getCreationDate())
                .createdBy(word.getCreatedBy())
                .timesViewed(word.getTimesViewed())
                .build();
    }

    public List<WordDTO> map(List<Word> words) {
        return words
                .parallelStream()
                .map(this::map)
                .collect(toList());
    }
}
