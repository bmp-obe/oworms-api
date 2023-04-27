package com.oworms.word.controller;

import com.oworms.common.util.LogUtil;
import com.oworms.word.controller.api.WordAPI;
import com.oworms.word.dto.WordDTO;
import com.oworms.word.dto.WordFilter;
import com.oworms.word.dto.WordRequestDTO;
import com.oworms.word.service.WordService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/o/worms")
public class WordController implements WordAPI {

    private final WordService service;

    public WordController(final WordService service) {
        this.service = service;
    }

    @Override
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> create(final @Valid @RequestBody WordRequestDTO wordRequestDTO,
                                          @RequestParam("u") String u,
                                          @RequestParam("bna") String banana) {
        LogUtil.log("Creating new word");

        WordDTO created = service.create(wordRequestDTO, u, banana);

        return ResponseEntity.ok(created);
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WordDTO>> retrieveAll(
            @RequestParam(value = "numberOfWords") final int numberOfWords,
            @RequestParam(value = "word", required = false) final String word,
            @RequestParam(value = "pos", required = false) final List<String> pos,
            @RequestParam(value = "def", required = false) final String def,
            @RequestParam(value = "ori", required = false) final String origin,
            @RequestParam(value = "ex", required = false) final String example,
            @RequestParam(value = "tags", required = false) final List<String> tags,
            @RequestParam(value = "note", required = false) final String note,
            @RequestParam(value = "creator", required = false) final String creator,
            @RequestParam(value = "uuids", required = false) final List<String> uuids) {
        LogUtil.log("Retrieving all words");

        final WordFilter wordFilter = new WordFilter(numberOfWords, word, pos, def, origin, example, tags, note, creator, uuids);

        return ResponseEntity.ok(service.retrieveAll(wordFilter));
    }

    @Override
    @GetMapping(
            value = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> retrieve(@PathVariable("uuid") String uuid) {
        LogUtil.log("Retrieving word with uuid: " + uuid);

        return ResponseEntity.ok(service.retrieve(uuid));
    }

    @Override
    @GetMapping(
            value = "/random",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> retrieveRandom() {
        return ResponseEntity.ok(service.retrieveRandom());
    }

    @Override
    @PutMapping(
            value = "/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> update(@PathVariable("uuid") String uuid,
                                          @RequestBody WordRequestDTO wordRequestDTO,
                                          @RequestParam("u") String u,
                                          @RequestParam("bna") String banana) {
        LogUtil.log("Updating word with uuid: " + uuid);

        WordDTO updatedWordDTO = service.update(uuid, wordRequestDTO, u, banana);

        return ResponseEntity.ok().body(updatedWordDTO);
    }

}