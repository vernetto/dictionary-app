package com.dictionary.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryEntry {
    private String from;
    private String to;
    private boolean known;
    private int freq;
}
