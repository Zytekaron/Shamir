package com.zytekaron.shamir;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class ShamirTests {
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        Shamir shamir = new Shamir(StandardCharsets.UTF_16, 15, 12);
        
        String secret = "This is a secret";
        List<String> shares = shamir.split(secret);
        
        shares.forEach(System.out::println);
    
        // 15/15
        assert shamir.join(shares).equals(secret);
        shares.remove(random.nextInt(shares.size()));
        
        // 14/15
        assert shamir.join(shares).equals(secret);
        shares.remove(random.nextInt(shares.size()));
        
        // 13/15
        assert shamir.join(shares).equals(secret);
        shares.remove(random.nextInt(shares.size()));
        
        // 12/15
        assert shamir.join(shares).equals(secret);
        shares.remove(random.nextInt(shares.size()));
        
        // 11/15 !!!
        assert !shamir.join(shares).equals(secret);
    
        System.out.println("Success");
    }
}