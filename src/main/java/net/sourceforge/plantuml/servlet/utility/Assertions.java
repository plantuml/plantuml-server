package net.sourceforge.plantuml.servlet.utility;

import java.util.Optional;

public final class Assertions {
    private Assertions() {
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static void assertTrimmedIfPresent(Optional<String> optional, String name) throws AssertionError {
        if (optional.isPresent()) {
            String value = optional.get();
            if (!value.equals(value.trim())) {
                String message = String.format("%s >%s< should have been trimmed", name, value);
                throw new AssertionError(message);
            }
        }
    }
}
