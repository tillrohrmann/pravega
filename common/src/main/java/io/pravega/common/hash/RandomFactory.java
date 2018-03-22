package io.pravega.common.hash;

import java.security.SecureRandom;
import java.util.Random;
import javax.annotation.concurrent.GuardedBy;
import lombok.Synchronized;

public class RandomFactory {

    @GuardedBy("$LOCK")
    private static final SecureRandom seedGenerator = new SecureRandom();
    
    @Synchronized
    public static Random create() {
        return new Random(seedGenerator.nextLong());
    }
    
    @Synchronized
    public static long getSeed() {
        return seedGenerator.nextLong();
    }
}
