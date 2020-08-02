package com.zytekaron.shamir;

import com.codahale.shamir.Scheme;
import com.google.common.io.BaseEncoding;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Shamir {
    private final Charset charset;
    private final BaseEncoding encoding = BaseEncoding.base16();
    private final String splitCharacter = ";";
    private final Scheme scheme;
    private final int shares;
    private final int required;
    
    public Shamir(int shares, int required) {
        this(StandardCharsets.UTF_8, new SecureRandom(), shares, required);
    }
    
    public Shamir(Charset charset, int shares, int required) {
        this(charset, new SecureRandom(), shares, required);
    }
    
    public Shamir(SecureRandom random, int shares, int required) {
        this(StandardCharsets.UTF_8, random, shares, required);
    }
    
    public Shamir(Charset charset, SecureRandom random, int shares, int required) {
        this.shares = shares;
        this.required = required;
        this.charset = charset;
        this.scheme = new Scheme(random, shares, required);
    }
    
    public List<String> split(String secret) {
        // List of Hexadecimal string shares
        List<String> data = splitToList(secret);
        // Prepend the index to each share
        for (int i = 0; i < data.size(); i++) {
            String text = (i + 1) + splitCharacter + data.get(i);
            data.set(i, text);
        }
        // Give it to me!!!
        return data;
    }
    
    private List<String> splitToList(String secret) {
        // Convert the UTF-8 string into raw bytes
        byte[] bytes = secret.getBytes(charset);
        // Split the bytes into shares
        // Encode each share into a String using base16
        return scheme.split(bytes)
                .values().stream()
                .map(encoding::encode)
                .collect(Collectors.toList());
    }
    
    public String join(List<String> secrets) {
        // Prepare a map for the library
        Map<Integer, byte[]> parts = new HashMap<>();
        // Extract each share's index, and decode it
        for (String secret : secrets) {
            // Extract the index and part
            String[] pieces = secret.split(splitCharacter, 2);
            // Parse the index
            int index = Integer.parseInt(pieces[0]);
            // Decode the part back into base16
            byte[] part = encoding.decode(pieces[1]);
            // Insert the part into the map
            parts.put(index, part);
        }
        // Join all the parts together to recover the secret
        byte[] recovered = scheme.join(parts);
        // Convert the raw bytes into a UTF-8 string
        return new String(recovered, charset);
    }
    
    public int getShares() {
        return shares;
    }
    
    public int getRequired() {
        return required;
    }
}