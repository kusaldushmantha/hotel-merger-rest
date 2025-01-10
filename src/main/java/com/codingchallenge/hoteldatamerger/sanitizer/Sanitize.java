package com.codingchallenge.hoteldatamerger.sanitizer;

import java.util.ArrayList;
import java.util.List;

// helper class to sanitize inputs
public class Sanitize {

    public static List<String> sanitizeStringList(List<String> str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        for (String s : str) {
            String stripped = s.strip().replaceAll("\\s+", "");
            if (!stripped.isBlank()) {
                result.add(stripped);
            }
        }
        return result;
    }
}
