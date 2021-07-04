package com.power.controller;

import com.power.dto.WordDTO;
import com.power.service.FileService;
import com.power.service.WordService;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/o/worms")
public class WordController {

    private final WordService service;
    private final FileService fileService;

    public WordController(final WordService service, final FileService fileService) {
        this.service = service;
        this.fileService = fileService;
    }

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> create(final @Valid @RequestBody WordDTO wordDTO,
                                          final @RequestParam(value = "u", required = false) String u) {
        if (null == wordDTO || null == u) {
            // throw custom error.
            return ResponseEntity.badRequest().build();
        }

        WordDTO created = service.create(wordDTO, u);

        return ResponseEntity.ok(created);
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<WordDTO>> retrieveAll(@RequestParam(value = "w", required = false) String theWord,
                                                     @RequestParam(value = "def", required = false) String definition,
                                                     @RequestParam(value = "pos", required = false) List<String> partsOfSpeech,
                                                     @RequestParam(value = "creator", required = false) String creator,
                                                     @RequestParam(value = "learnt", required = false) String haveLearnt) {
        return ResponseEntity.ok(
                service.retrieveAll(theWord, definition, partsOfSpeech, creator, haveLearnt)
        );
    }

    @GetMapping(
            value = "/{word}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> retrieve(@PathVariable("word") String theWord) {
        return ResponseEntity.ok(service.retrieve(theWord));
    }

    @PutMapping(
            value = "/{word}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> update(@PathVariable("word") String theWord, @RequestBody WordDTO updatedWord) {
        WordDTO updatedWordDTO = service.update(theWord, updatedWord);

        return ResponseEntity.ok().body(updatedWordDTO);
    }

    @GetMapping(
            value = "/random",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WordDTO> retrieve() {
        return ResponseEntity.ok(service.retrieveRandom());
    }

    @PostMapping(
            value = "/read",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> readCSV(@RequestParam("excel_file") MultipartFile excelFile) {
        boolean success = fileService.writeWordsInSpreadsheetToDB(excelFile);

        // throw custom error.
        return success ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
