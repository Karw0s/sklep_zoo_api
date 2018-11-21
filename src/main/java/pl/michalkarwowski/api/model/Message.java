package pl.michalkarwowski.api.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Message {
    private String id = UUID.randomUUID().toString();
    private String content;
    public Message(String content) {
        this.content = content;
    }


}
