package com.codingchallenge.hoteldatamerger.sanitizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// helper class to sanitize inputs
public class InputSanitizer {

    public static List<String> sanitizeStringList(List<String> str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> result = new HashSet<>();
        for (String s : str) {
            String stripped = s.strip().replaceAll("\\s+", "");
            if (!stripped.isBlank()) {
                result.add(stripped);
            }
        }
        return new ArrayList<>(result);
    }
}
