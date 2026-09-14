package io.zaryx.content;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/** Monotonic hourly cadence; completing a round never resets the deadline. */
final class TriviaSchedule {
    static final long INTERVAL = TimeUnit.HOURS.toNanos(1);
    private final LongSupplier clock;
    private long nextQuestion;

    TriviaSchedule(LongSupplier clock) {
        this.clock = clock;
        nextQuestion = clock.getAsLong() + INTERVAL;
    }

    void tick(Runnable askQuestion) {
        long now = clock.getAsLong();
        if (now - nextQuestion < 0) return;
        askQuestion.run();
        // Skip missed rounds after a pause; never broadcast a burst of questions.
        nextQuestion += ((now - nextQuestion) / INTERVAL + 1) * INTERVAL;
    }
}
