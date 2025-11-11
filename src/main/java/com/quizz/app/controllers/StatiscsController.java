package com.quizz.app.controllers;

import com.quizz.app.dto.StatisctsResopnseDTO;
import com.quizz.app.services.QuizzServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatiscsController {
    @Autowired
    QuizzServices quizzServices;

    @GetMapping("/all")
    public StatisctsResopnseDTO getStatistics() {
        StatisctsResopnseDTO statistics = quizzServices.getStatistics();
        return statistics;
    }

}
