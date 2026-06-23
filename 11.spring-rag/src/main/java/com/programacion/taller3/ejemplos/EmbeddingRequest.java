package com.programacion.taller3.ejemplos;

import com.drew.lang.annotations.Nullable;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.model.ModelRequest;

import java.util.List;

public class EmbeddingRequest implements ModelRequest<List<String>> {

    private final List<String> inputs;

    private final @Nullable EmbeddingOptions options;

    public EmbeddingRequest(List<String> inputs, @Nullable EmbeddingOptions options) {
        this.inputs = inputs;
        this.options = options;
    }

    @Override
    public List<String> getInstructions() { return this.inputs; }

    @Override
    public @Nullable EmbeddingOptions getOptions() { return this.options; }

}

