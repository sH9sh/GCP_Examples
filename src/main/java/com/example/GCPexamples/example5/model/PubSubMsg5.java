package com.example.GCPexamples.example5.model;

import lombok.*;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PubSubMsg5 {
    private String data;
    private Map<String, String> attributes;
    private String messageId;
    private String publishTime;
}
