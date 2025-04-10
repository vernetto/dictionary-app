package com.dictionary.app.service;

import com.dictionary.app.model.Dictionary;
import com.dictionary.app.model.DictionaryEntry;

import java.util.List;
import java.util.Map;

public interface DictionaryService {
    
    /**
     * Get a list of all available dictionary file names
     * @return List of dictionary file names
     */
    List<String> getAllDictionaryFiles();
    
    /**
     * Load a dictionary from a specific file
     * @param fileName The name of the dictionary file
     * @return The loaded dictionary
     */
    Dictionary loadDictionary(String fileName);
    
    /**
     * Save a dictionary to a specific file
     * @param fileName The name of the dictionary file
     * @param dictionary The dictionary to save
     */
    void saveDictionary(String fileName, Dictionary dictionary);
    
    /**
     * Add a new entry to a dictionary
     * @param fileName The name of the dictionary file
     * @param entry The entry to add
     */
    void addEntry(String fileName, DictionaryEntry entry);
    
    /**
     * Update an existing entry in a dictionary
     * @param fileName The name of the dictionary file
     * @param index The index of the entry to update
     * @param entry The updated entry
     */
    void updateEntry(String fileName, int index, DictionaryEntry entry);
    
    /**
     * Delete entries from a dictionary
     * @param fileName The name of the dictionary file
     * @param indices The indices of the entries to delete
     */
    void deleteEntries(String fileName, List<Integer> indices);
}
