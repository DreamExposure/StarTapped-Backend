package org.dreamexposure.tap.core.enums.post;

public enum SafeSearchRating {
    UNKNOWN(0), VERY_UNLIKELY(0.2), UNLIKELY(0.4), POSSIBLE(0.6), LIKELY(0.8), VERY_LIKELY(1);

    private final double score;

    SafeSearchRating(double _score) {
        score = _score;
    }

    public double getScore() {
        return score;
    }

    public static SafeSearchRating fromString(String s) {
        switch (s.toUpperCase()) {
            case "UNKNOWN":
                return UNKNOWN;
            case "VERY_UNLIKELY":
                return VERY_UNLIKELY;
            case "UNLIKELY":
                return UNLIKELY;
            case "POSSIBLE":
                return POSSIBLE;
            case "LIKELY":
                return LIKELY;
            case "VERY_LIKELY":
                return VERY_LIKELY;
        }
        return UNKNOWN;
    }
}
