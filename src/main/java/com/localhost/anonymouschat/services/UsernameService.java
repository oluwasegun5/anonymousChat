package com.localhost.anonymouschat.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;


@RequiredArgsConstructor
@Service
public class UsernameService {
    private static final String VERBS_FILE = "src/main/resources/verbs.txt";
    private static final String NOUNS_FILE = "src/main/resources/nouns.txt";
    private final Random random = new Random();

    public String generateUsername() {
        try{
            List<String> verbs = Files.readAllLines(Paths.get(VERBS_FILE));
            List<String> nouns = Files.readAllLines(Paths.get(NOUNS_FILE));

            String verb = verbs.get(random.nextInt(verbs.size()));
            String noun = nouns.get(random.nextInt(nouns.size()));
            int number = 100 + random.nextInt(900);

            return capitalize(verb) + capitalize(noun) + number;
        } catch (IOException e) {
            throw new RuntimeException("Error reading word lists", e);
        }
    }

    private String capitalize(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
