package com.apetta.detext.detextapi.service;

import com.apetta.detext.detextapi.entity.TranslationObject;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TranslationStatsService {

    private DatabaseReference ref;
    private static List<TranslationObject> allRecordsList;
    private static List<String> distinctWordsList;
    private static volatile boolean completed;
    private static String childKey, childValue;

    private static long countWordAppearance = -10;

    public TranslationStatsService() {
        // As an admin, the app has access to read and write all data, regardless of Security Rules
        ref = FirebaseDatabase.getInstance()
                .getReference("stats/fromTranslation");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                List<TranslationStats> translationStatsList = new ArrayList<>();
//                for(DataSnapshot snap : dataSnapshot.getChildren()) {
//                    translationStatsList.add(snap.getValue(TranslationStats.class));
//                }
//                translationStatsList.forEach(translationStats -> System.out.println(translationStats.getSourceWord()));
////				Object document = dataSnapshot.getValue();
////				System.out.println(document);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }

    public void getAllRecords() {
        allRecordsList = new ArrayList<>();
        completed = false;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveData(dataSnapshot);
                if(dataSnapshot.getChildrenCount() == (long) allRecordsList.size()) completed = true;
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void getStatsByParams(Map<String, String> params) {
        allRecordsList = new ArrayList<>();
        completed = false;
        FindBy findBy = FindBy.valueOf(params.get("findBy").toUpperCase());
        String country = params.get("country");
        String locality = params.get("locality");
        String month = params.get("month").toUpperCase();
        String year = params.get("year");
        childKey = findBy.toString().toLowerCase();
        if(findBy.equals(FindBy.COUNTRY)) childValue = country;
        else if(findBy.equals(FindBy.LOCALITY)) childValue = locality;
        else if(findBy.equals(FindBy.YEAR)) childValue = year;
        else childValue = month;  // default is month
        /*
        * gia to sugkekrimeno query
        * https://firebase.google.com/docs/database/admin/retrieve-data
        */
        ref.orderByChild(childKey).equalTo(childValue)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        retrieveData(dataSnapshot);
                        if(dataSnapshot.getChildrenCount() == (long) allRecordsList.size()) completed = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    public Map<String, String> getTheMostSearchedWordPerCountry() {
        completed = false;
        allRecordsList = new ArrayList<>();
        getAllRecords();
        while (!completed) Thread.onSpinWait();
        List<String> distinctCountriesList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        allRecordsList.forEach(t -> tempList.add(t.getCountry()));
        distinctCountriesList = tempList.stream().distinct().collect(Collectors.toList());
        // for each country I have to get the most searched word
        Map<String, String> mostSearchedWordPerCountry = new HashMap<>();
        for(int i = 0; i < distinctCountriesList.size(); i++) {
            List<String> words = new ArrayList<>();
            for (TranslationObject translationObject : allRecordsList){
                if(translationObject.getCountry() != null)
                    if (translationObject.getCountry().equals(distinctCountriesList.get(i))) words.add(translationObject.getSourceWord());
            }
            mostSearchedWordPerCountry.put(distinctCountriesList.get(i), findMostFrequentWord(words));
        }
        return mostSearchedWordPerCountry;
    }

    private String findMostFrequentWord(List<String> words) {
        int maxFreq = 0;
        String mostFrequentWord = "";
        for (int i = 0; i< words.size(); i++) {
            int count = 0;
            for (int j = 0; j < words.size(); j++) if (words.get(i).equals(words.get(j))) count++;
            if(count > maxFreq) maxFreq = count; mostFrequentWord = words.get(i);
        }
        return mostFrequentWord;
    }


    public void getCountOfSearchedWord(String word) {
        completed = false;
        ref.orderByChild("sourceWord").equalTo(word.toLowerCase())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        countWordAppearance = dataSnapshot.getChildrenCount();
                        completed = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
    }

    private void retrieveData(DataSnapshot dataSnapshot) {
        for(DataSnapshot snap : dataSnapshot.getChildren()) {
            allRecordsList.add(snap.getValue(TranslationObject.class));
        }
    }

    /* Gets the frequency for distinct words */
    public Map<String, Integer> getDistinctWords() {
        completed = false;
        getAllRecords();
        while (!completed) Thread.onSpinWait();
        distinctWordsList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        Map<String, Integer> result = new HashMap<>();
        allRecordsList.forEach(t -> tempList.add(t.getSourceWord()));
        distinctWordsList = tempList.stream().distinct().collect(Collectors.toList());
        distinctWordsList.forEach(w -> result.put(w, Collections.frequency(tempList, w)));
        return result;
    }


    public List<TranslationObject> getAllRecordsList() { return allRecordsList; }

    public boolean isCompleted() { return completed; }

    public String getChildKey() { return childKey; }

    public String getChildValue() { return childValue; }

    public Long getCountWordAppearance() { return countWordAppearance; }

    public static List<String> getDistinctWordsList() { return distinctWordsList; }
}
