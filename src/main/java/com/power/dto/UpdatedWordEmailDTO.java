package com.power.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdatedWordEmailDTO {

    public static final String TEMPLATE = "oworm-update";
    public static final String ACTION = "A word was recently updated";
    private String title;
    private WordDTO oldWord;
    private WordDTO updatedWord;
    private String retrievalLink;

    public UpdatedWordEmailDTO(String title, WordDTO oldWord, WordDTO updatedWord, String retrievalLink) {
        this.title = title;
        this.oldWord = oldWord;
        this.updatedWord = updatedWord;
        this.retrievalLink = retrievalLink;
    }
}
