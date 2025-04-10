package com.dictionary.app.util;

import com.dictionary.app.model.Dictionary;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for file operations related to dictionary files
 */
@Component
public class FileUtils {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Read a dictionary from a JSON file
     * @param filePath Path to the dictionary file
     * @return Dictionary object
     * @throws IOException If file cannot be read
     */
    public Dictionary readDictionaryFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return objectMapper.readValue(file, Dictionary.class);
    }

    /**
     * Write a dictionary to a JSON file
     * @param dictionary Dictionary object to write
     * @param filePath Path to the dictionary file
     * @throws IOException If file cannot be written
     */
    public void writeDictionaryToFile(Dictionary dictionary, String filePath) throws IOException {
        File file = new File(filePath);
        
        // Ensure parent directory exists
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // Write dictionary to file with pretty printing
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, dictionary);
    }

    /**
     * Create a backup of a dictionary file
     * @param filePath Path to the dictionary file
     * @throws IOException If backup cannot be created
     */
    public void createBackup(String filePath) throws IOException {
        File originalFile = new File(filePath);
        if (!originalFile.exists()) {
            return; // No need to backup if file doesn't exist
        }
        
        String backupPath = filePath + ".bak";
        Files.copy(originalFile.toPath(), Paths.get(backupPath));
    }

    /**
     * Check if a file exists
     * @param filePath Path to the file
     * @return true if file exists, false otherwise
     */
    public boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
}
