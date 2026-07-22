package taller.rest;

/**
 * DTO (Data Transfer Object) inmutable representado como Record en Java.
 * Encapsula la solicitud recibida en los endpoints REST del chat.
 * 
 * @param message El mensaje o pregunta enviada por el usuario hacia el modelo de IA.
 */
public record ChatRequest (String message){
}

