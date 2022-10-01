package com.apetta.detext.detextapi.controller;

import com.apetta.detext.detextapi.entity.TranslationObject;
import com.apetta.detext.detextapi.service.TranslationStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/detext/stats")
public class TranslationStatsController {

    private TranslationStatsService translationStatsService;

    public TranslationStatsController(TranslationStatsService translationStatsService) {
        this.translationStatsService = translationStatsService;
    }

    @GetMapping
    public ResponseEntity<List<TranslationObject>> getAllRecords() {
        translationStatsService.getAllRecords();
        List<TranslationObject> response = new ArrayList<>();
        while(!translationStatsService.isCompleted()) response = translationStatsService.getAllRecordsList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/params")
    public ResponseEntity<Map<String, Object>> getStatsByParam(@RequestParam Map<String, String> params) {
        translationStatsService.getStatsByParams(params);
        while(!translationStatsService.isCompleted()) Thread.onSpinWait();
        Map<String, Object> response = new HashMap<>();
        response.put("Parameter key", translationStatsService.getChildKey());
        response.put("Parameter value", translationStatsService.getChildValue());
        response.put("Result's size", translationStatsService.getAllRecordsList().size());
        response.put("Data", translationStatsService.getAllRecordsList());
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/most-searched-word")
//    public ResponseEntity<List<String>> getTheMostSearchedWord() {
//        // TODO
//        return ResponseEntity.ok(new ArrayList<>());
//    }

    @GetMapping("/most-searched-word-per-country")
    public ResponseEntity<Map<String, String>> getTheMostSearchedWordPerCountry(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok(translationStatsService.getTheMostSearchedWordPerCountry());
    }

    @GetMapping("/{word}")
    public ResponseEntity<Map<String, Object>> getCountOfSearchedWord(@PathVariable String word) {
        translationStatsService.getCountOfSearchedWord(word);
        Long count;
        while(!translationStatsService.isCompleted()) Thread.onSpinWait();
        count = translationStatsService.getCountWordAppearance();
        Map<String, Object> response = new HashMap<>();
        response.put("Word", word);
        response.put("Frequency", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/distinct")
    public ResponseEntity<Map<String, Integer>> getDistinctWords() {
        return ResponseEntity.ok(translationStatsService.getDistinctWords());
    }
}
