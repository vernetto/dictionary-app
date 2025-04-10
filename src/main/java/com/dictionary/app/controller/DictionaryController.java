package com.dictionary.app.controller;

import com.dictionary.app.model.Dictionary;
import com.dictionary.app.model.DictionaryEntry;
import com.dictionary.app.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @Autowired
    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dictionaryFiles", dictionaryService.getAllDictionaryFiles());
        return "index";
    }

    @GetMapping("/api/dictionaries")
    @ResponseBody
    public List<String> getAllDictionaryFiles() {
        return dictionaryService.getAllDictionaryFiles();
    }

    @GetMapping("/api/dictionaries/{fileName}")
    @ResponseBody
    public Dictionary getDictionary(@PathVariable String fileName) {
        return dictionaryService.loadDictionary(fileName);
    }

    @PostMapping("/api/dictionaries/{fileName}/entries")
    @ResponseBody
    public ResponseEntity<String> addEntry(@PathVariable String fileName, @RequestBody DictionaryEntry entry) {
        dictionaryService.addEntry(fileName, entry);
        return ResponseEntity.ok("Entry added successfully");
    }

    @PutMapping("/api/dictionaries/{fileName}/entries/{index}")
    @ResponseBody
    public ResponseEntity<String> updateEntry(
            @PathVariable String fileName,
            @PathVariable int index,
            @RequestBody DictionaryEntry entry) {
        dictionaryService.updateEntry(fileName, index, entry);
        return ResponseEntity.ok("Entry updated successfully");
    }

    @DeleteMapping("/api/dictionaries/{fileName}/entries")
    @ResponseBody
    public ResponseEntity<String> deleteEntries(
            @PathVariable String fileName,
            @RequestBody List<Integer> indices) {
        dictionaryService.deleteEntries(fileName, indices);
        return ResponseEntity.ok("Entries deleted successfully");
    }
}
