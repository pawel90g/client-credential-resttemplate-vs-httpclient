package dev.garbacik.restserver.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces="application/json")
public class ValueController {

    @GetMapping(value = "values")
    public ResponseEntity<List<String>> get() {
        return ResponseEntity.ok(values());
    }

    private List<String> values() {
        List<String> values = new ArrayList<>();

        values.add("val 1");
        values.add("val 2");
        values.add("val 3");
        values.add("val 4");
        values.add("val 5");
        values.add("val 6");
        values.add("val 7");
        values.add("val 8");
        values.add("val 9");
        values.add("val 10");

        return values;
    }
}