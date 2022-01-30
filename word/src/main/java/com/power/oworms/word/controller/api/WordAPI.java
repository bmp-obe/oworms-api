package com.power.oworms.word.controller.api;

import com.power.oworms.common.error.OWormException;
import com.power.oworms.word.dto.StatisticsDTO;
import com.power.oworms.word.dto.WordDTO;
import com.power.oworms.word.dto.WordRequestDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WordAPI {

    @ApiOperation(
            code = 201,
            value = "Creates a word. Sends an email communication to relevant users upon successful creation",
            response = WordDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 201,
                    message = "Created the word successfully"
            ),
            @ApiResponse(
                    code = 400,
                    message = "Supplied values for creation are invalid"
            ),
            @ApiResponse(
                    code = 403,
                    message = "You do not have permission to do that"
            ),
            @ApiResponse(
                    code = 409,
                    message = "That word already exists"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Error while sending email communicating the creation of the new word"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while creating the word"
            )
    })
    ResponseEntity<WordDTO> create(WordRequestDTO wordDTO, String uname, String banana) throws OWormException;

    @ApiOperation(
            value = "Retrieves all words meeting the criteria provided by the query parameters. " +
                    "If no query parameters are specified, all words will be returned.",
            response = WordDTO.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Retrieved all words"
            ),
            @ApiResponse(
                    code = 404,
                    message = "No words found"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while retrieving all the words"
            )
    })
    ResponseEntity<List<WordDTO>> retrieveAll(
            String theWord,
            List<String> parts,
            String def,
            String origin,
            String example,
            List<String> tags,
            String note,
            String creator
    );

    @ApiOperation(
            value = "Retrieves a single word using the specified ID",
            response = WordDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Retrieved the word"
            ),
            @ApiResponse(
                    code = 404,
                    message = "That word does not exist"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while retrieving the word"
            )
    })
    ResponseEntity<WordDTO> retrieve(Long wordId);

    @ApiOperation(
            value = "Retrieves details for the word using the Oxford Dictionaries API in a JSON string format",
            response = String.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Retrieved details"
            ),
            @ApiResponse(
                    code = 404,
                    message = "That word does not exist"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while calling oxford api"
            )
    })
    ResponseEntity<String> oxfordRetrieve(String theWord, String uname, String banana);

    @ApiOperation(
            value = "Retrieves a random word",
            response = WordDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Retrieved the random word"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while retrieving the random word"
            )
    })
    ResponseEntity<WordDTO> retrieveRandom();

    @ApiOperation(
            code = 200,
            value = "Updates a word. Sends an email communication to relevant users upon successful update",
            response = WordDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 201,
                    message = "Updated the word successfully"
            ),
            @ApiResponse(
                    code = 400,
                    message = "Supplied values for update are invalid"
            ),
            @ApiResponse(
                    code = 403,
                    message = "You do not have permission to do that"
            ),
            @ApiResponse(
                    code = 409,
                    message = "That word already exists"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Error while sending email communicating the update of the word"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while updating the word"
            )
    })
    ResponseEntity<WordDTO> update(Long wordId, WordRequestDTO wordRequestDTO, String uname, String banana) throws OWormException;

    @ApiOperation(
            value = "Retrieves the statistics of the application",
            response = StatisticsDTO.class
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 200,
                    message = "Retrieved statistics"
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal server error while retrieving statistics"
            )
    })
    ResponseEntity<StatisticsDTO> getStatistics();
}