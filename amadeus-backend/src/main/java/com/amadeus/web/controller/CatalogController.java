package com.amadeus.web.controller;

import com.amadeus.domain.service.CatalogService;
import com.amadeus.web.dto.CourseTreeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/tree")
    public ResponseEntity<CourseTreeDTO> getCourseTree() {
        return ResponseEntity.ok(catalogService.getCourseTree());
    }
}