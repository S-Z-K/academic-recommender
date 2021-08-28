package com.shitajimado.academicwritingrecommender.controller.api;

import com.shitajimado.academicwritingrecommender.entities.dtos.CorpusDto;
import com.shitajimado.academicwritingrecommender.entities.dtos.DocumentDto;
import com.shitajimado.academicwritingrecommender.core.exceptions.CorpusNotFoundException;
import com.shitajimado.academicwritingrecommender.core.exceptions.DocumentNotCreatedException;
import com.shitajimado.academicwritingrecommender.entities.*;
import com.shitajimado.academicwritingrecommender.services.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "api", consumes = "application/json", produces = "application/json")
public class DocumentApiController {
    @Autowired private CorpusService corpusService;

    @PostMapping("/create_document")
    public Corpus createDocument(@RequestBody DocumentDto documentDto) throws DocumentNotCreatedException {
        try {
        return corpusService.createAndAddDocument(documentDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentNotCreatedException("Failed to create documents");
        }
    }

    @PostMapping("/create_documents")
    public List<Corpus> createDocuments(@RequestBody List<DocumentDto> documentDtos) throws DocumentNotCreatedException {
        try {
            return corpusService.createAndAddDocuments(documentDtos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocumentNotCreatedException("Failed to create documents");
        }
    }

    @GetMapping(value = "/read_document", consumes = "application/x-www-form-urlencoded")
    public List<Document> readDocument(@RequestParam String corpusId) throws CorpusNotFoundException {
        return corpusService.readDocuments(corpusId);
    }

    @PostMapping("/update_document")
    public void updateDocument(Model model) {

    }

    @PostMapping("/delete_document")
    public void deleteDocument(@RequestBody Document document) throws CorpusNotFoundException {
        corpusService.removeDocument(document);
    }

    @GetMapping("/refresh")
    public void refresh() throws DocumentNotCreatedException {
        var corpora = corpusService.readCorpora();

        for (var corpus : corpora) {
            corpusService.deleteCorpus(corpus);
        }

        var corpus = corpusService.createCorpus(new CorpusDto("Default corpus"));

        var content = "Hi, I'm a native speaker. I will try to do my best";

        corpusService.createAndAddDocument(
                new DocumentDto(corpus.getId(), "BLACK SIEMENS", content, Document.getDate())
        );
    }
}

