package io.zaryx.content;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class TriviaScheduleTest {
    @Test void asksOncePerHourRegardlessOfTickFrequency() {
        AtomicLong clock=new AtomicLong();AtomicInteger questions=new AtomicInteger();
        TriviaSchedule schedule=new TriviaSchedule(clock::get);
        for(int i=0;i<20000;i++)schedule.tick(questions::incrementAndGet);
        assertEquals(0,questions.get());
        clock.set(TriviaSchedule.INTERVAL-1);schedule.tick(questions::incrementAndGet);assertEquals(0,questions.get());
        clock.incrementAndGet();schedule.tick(questions::incrementAndGet);assertEquals(1,questions.get());
        for(int i=0;i<21;i++)schedule.tick(questions::incrementAndGet);assertEquals(1,questions.get());
        clock.set(TriviaSchedule.INTERVAL*2);schedule.tick(questions::incrementAndGet);assertEquals(2,questions.get());
    }
    @Test void delayedTickSkipsMissedRoundsWithoutLosingCadence() {
        AtomicLong clock=new AtomicLong();AtomicInteger questions=new AtomicInteger();TriviaSchedule schedule=new TriviaSchedule(clock::get);
        clock.set(TriviaSchedule.INTERVAL*3+100);schedule.tick(questions::incrementAndGet);schedule.tick(questions::incrementAndGet);assertEquals(1,questions.get());
        clock.set(TriviaSchedule.INTERVAL*4);schedule.tick(questions::incrementAndGet);assertEquals(2,questions.get());
    }
}
