package com.apetta.detext.detextapi.controller;

import com.apetta.detext.detextapi.entity.TranslationObject;
import com.apetta.detext.detextapi.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detext/translations")
public class TranslationController {

    private TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping
    public ResponseEntity<List<TranslationObject>> getAllRecords() {
        translationService.getAllRecords();
        List<TranslationObject> response = new ArrayList<>();
        while(!translationService.isCompleted()) response = translationService.getAllRecordsList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/params")
    public ResponseEntity<Map<String, Object>> getStatsByParam(@RequestParam Map<String, String> params) {
        translationService.getStatsByParams(params);
        while(!translationService.isCompleted()) Thread.onSpinWait();
        Map<String, Object> response = new HashMap<>();
        response.put("Parameter key", translationService.getChildKey());
        response.put("Parameter value", translationService.getChildValue());
        response.put("Result's size", translationService.getAllRecordsList().size());
        response.put("Data", translationService.getAllRecordsList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/most-searched-word-per-country")
    public ResponseEntity<Map<String, String>> getTheMostSearchedWordPerCountry(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(translationService.getTheMostSearchedWordPerCountry());
    }

    @GetMapping("/{word}")
    public ResponseEntity<Map<String, Object>> getCountOfSearchedWord(@PathVariable String word) {
        translationService.getCountOfSearchedWord(word);
        Long count;
        while(!translationService.isCompleted()) Thread.onSpinWait();
        count = translationService.getCountWordAppearance();
        Map<String, Object> response = new HashMap<>();
        response.put("Word", word);
        response.put("Frequency", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/distinct")
    public ResponseEntity<Map<String, Integer>> getDistinctWords() {
        return ResponseEntity.ok(translationService.getDistinctWords());
    }

    @GetMapping("/most-searched")
    public ResponseEntity<Map<String, Integer>> getTheMostSearchedWord() {
        return ResponseEntity.ok(translationService.getTheMostSearchedWord());
    }
}
