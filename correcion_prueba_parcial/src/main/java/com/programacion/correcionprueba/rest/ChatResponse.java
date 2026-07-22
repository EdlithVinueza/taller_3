package com.programacion.correcionprueba.rest;

import java.util.List;

public record ChatResponse(String answer, List<String> source) {}
