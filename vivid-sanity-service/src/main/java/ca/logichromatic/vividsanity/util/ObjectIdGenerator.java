package ca.logichromatic.vividsanity.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class ObjectIdGenerator {
    private String characters = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private int MAX_LENGTH = 8;
    private Random randomGenerator = new Random();
    public String get() {
        StringBuilder objectIdBuilder = new StringBuilder(MAX_LENGTH);
        for(int i = 0; i < MAX_LENGTH; i++) {
            objectIdBuilder.append(characters.charAt(randomGenerator.nextInt(characters.length())));
        }
        return objectIdBuilder.toString();
    }
}
