package com.foros.util.preview.token;

import com.foros.util.preview.PreviewContext;
import com.foros.util.preview.TokenDefinition;

import java.util.Random;

public class RandomTokenDefinition implements TokenDefinition  {
    public static final TokenDefinition INSTANCE = new RandomTokenDefinition();

    private final Random random = new Random();

    @Override
    public String evaluate(PreviewContext context) {
        return String.valueOf(random.nextInt(Integer.MAX_VALUE));
    }
}
