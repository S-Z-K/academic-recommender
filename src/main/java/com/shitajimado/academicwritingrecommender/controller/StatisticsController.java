package com.shitajimado.academicwritingrecommender.controller;

import com.shitajimado.academicwritingrecommender.core.ExcelFile;
import com.shitajimado.academicwritingrecommender.core.Statistics;
import com.shitajimado.academicwritingrecommender.core.exceptions.CorpusNotFoundException;
import com.shitajimado.academicwritingrecommender.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Stream;

@Controller
public class StatisticsController {
    @Autowired private CorpusRepository corpusRepository;
    @Autowired private DocumentRepository documentRepository;
    @Autowired private TextRepository textRepository;

    //расчет статистик и вывод отчета на html-страницу
    @RequestMapping("/statistics/{corpusId}")
    public ModelAndView openStatistics(@PathVariable String corpusId) throws CorpusNotFoundException {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("statistics");
            List<String> docs = findDocuments(corpusId);
            //String[] documentsNames = docs.toArray(new String[docs.size()]);
            Statistics statistics = readStatistics(corpusId);
            if(statistics!=null)
                modelAndView.addObject("statistics", statistics);    
                modelAndView.addObject("documentsNames", docs);  
            return modelAndView;
    } 

    //расчет статистик
    public Statistics readStatistics(String corpusId) throws CorpusNotFoundException {
        return corpusRepository.findById(corpusId).map(
                Corpus::getDocuments
        ).map(
                documentIds -> findByIds(documentIds.stream(), documentRepository)
        ).map(
                documentStream -> documentStream.map(Document::getTextId)
        ).map(
                textIdStream -> findByIds(textIdStream, textRepository)
        ).map(Statistics::fromTextStream).orElseThrow(
                () -> new CorpusNotFoundException("Unable to find a corpus with given ID")
        );
    }

    //наименования текстов корпуса
    public List<String> findDocuments(String corpusId) throws CorpusNotFoundException {
        return corpusRepository.findById(corpusId).map(
                Corpus::getDocuments
        ).map(
                documentIds -> findByIds(documentIds.stream(), documentRepository)
        ).map(Statistics::getDocumentsNames).orElseThrow(
                () -> new CorpusNotFoundException("Unable to find a corpus with given ID")
        );
    }

    //имя корпуса для формирования имени файла Excel
    public String getCorporaName(String corpusId) throws CorpusNotFoundException {
        return corpusRepository.findById(corpusId).get().getName();
    }

    //загрузка файла Excel
    @GetMapping(value = "/download_Excel", consumes = "application/x-www-form-urlencoded")
    public void downloadExcel(String corpusId) throws CorpusNotFoundException {
            Statistics stats = readStatistics(corpusId);
            List<String> documentsNames = findDocuments(corpusId);
            String corporaName = getCorporaName(corpusId);
            ExcelFile.create(stats, documentsNames, corporaName);
    }

    private <T> Stream<T> findByIds(Stream<String> stream, MongoRepository<T, String> repository) {
        return stream.map(item -> repository.findById(item).get());
    }
}
