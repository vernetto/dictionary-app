package com.dictionary.app.service.impl;

import com.dictionary.app.model.Dictionary;
import com.dictionary.app.model.DictionaryEntry;
import com.dictionary.app.service.DictionaryService;
import com.dictionary.app.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Value("${dictionary.files.path}")
    private String dictionaryFilesPath;

    private final FileUtils fileUtils;

    @Autowired
    public DictionaryServiceImpl(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Override
    public List<String> getAllDictionaryFiles() {
        try {
            File directory = new File(dictionaryFilesPath);
            if (!directory.exists()) {
                directory.mkdirs();
                return new ArrayList<>();
            }
            
            try (Stream<Path> paths = Files.walk(Paths.get(dictionaryFilesPath))) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(name -> name.startsWith("dictionary_") && name.endsWith(".json"))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading dictionary files", e);
        }
    }

    @Override
    public Dictionary loadDictionary(String fileName) {
        try {
            String filePath = Paths.get(dictionaryFilesPath, fileName).toString();
            return fileUtils.readDictionaryFromFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error loading dictionary: " + fileName, e);
        }
    }

    @Override
    public void saveDictionary(String fileName, Dictionary dictionary) {
        try {
            String filePath = Paths.get(dictionaryFilesPath, fileName).toString();
            
            // Create backup before saving
            if (fileUtils.fileExists(filePath)) {
                fileUtils.createBackup(filePath);
            }
            
            fileUtils.writeDictionaryToFile(dictionary, filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error saving dictionary: " + fileName, e);
        }
    }

    @Override
    public void addEntry(String fileName, DictionaryEntry entry) {
        Dictionary dictionary = loadDictionary(fileName);
        dictionary.getDictionaryEntries().add(entry);
        saveDictionary(fileName, dictionary);
    }

    @Override
    public void updateEntry(String fileName, int index, DictionaryEntry entry) {
        Dictionary dictionary = loadDictionary(fileName);
        if (index >= 0 && index < dictionary.getDictionaryEntries().size()) {
            dictionary.getDictionaryEntries().set(index, entry);
            saveDictionary(fileName, dictionary);
        } else {
            throw new RuntimeException("Invalid entry index: " + index);
        }
    }

    @Override
    public void deleteEntries(String fileName, List<Integer> indices) {
        Dictionary dictionary = loadDictionary(fileName);
        
        // Sort indices in descending order to avoid index shifting during removal
        indices.sort((a, b) -> b - a);
        
        for (int index : indices) {
            if (index >= 0 && index < dictionary.getDictionaryEntries().size()) {
                dictionary.getDictionaryEntries().remove(index);
            } else {
                throw new RuntimeException("Invalid entry index: " + index);
            }
        }
        
        saveDictionary(fileName, dictionary);
    }
}
