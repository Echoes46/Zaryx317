package io.zaryx.content.holiday;
import org.junit.jupiter.api.Test;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;
class HolidayProgressTest {
 @Test void fullRoundCannotSkipStepsOrClaimTwice(){
  HolidayProgress p=new HolidayProgress();p.useEdition(2026);
  assertFalse(p.complete());assertFalse(p.deliver(0));assertFalse(p.mix(0));
  assertTrue(p.start(4));assertFalse(p.start(0));assertTrue(p.gather(0));assertFalse(p.gather(0));
  assertTrue(p.gather(1));assertTrue(p.gather(2));assertEquals(2,p.stage);
  assertFalse(p.mix((p.nextIngredient()+1)%3));assertEquals(0,p.puzzle);
  for(int i=0;i<3;i++)assertTrue(p.mix(p.nextIngredient()));assertEquals(3,p.stage);
  for(int i=0;i<3;i++){assertTrue(p.deliver(i));assertFalse(p.deliver(i));}
  assertTrue(p.complete());assertFalse(p.complete());assertEquals(1,p.runs);assertEquals(5,p.tokens);
  assertTrue(p.start(2));assertEquals(0,p.gathered);assertEquals(5,p.tokens);
 }
 @Test void saveAndRestartPreserveEachStageAndRewards(){
  HolidayProgress p=new HolidayProgress();p.useEdition(2026);p.start(5);p.gather(2);
  HolidayProgress restored=HolidayProgress.decode(p.encode());restored.useEdition(2026);
  assertEquals(p.encode(),restored.encode());assertEquals(4,restored.gathered);
  restored.useEdition(2027);assertEquals(0,restored.stage);assertEquals(0,restored.gathered);
  assertThrows(IllegalArgumentException.class,()->HolidayProgress.decode("2026,4,0,0,0,0,0,0"));
 }
 @Test void flagsAreIndependentAndDisabledByDefault(){
  Properties props=new Properties();HolidaySettings s=new HolidaySettings(props);
  assertFalse(s.enabled(Holiday.HALLOWEEN));assertFalse(s.enabled(Holiday.CHRISTMAS));
  props.setProperty("halloween.enabled","true");assertTrue(s.enabled(Holiday.HALLOWEEN));assertFalse(s.enabled(Holiday.CHRISTMAS));
  props.setProperty("christmas.enabled","true");assertTrue(s.enabled(Holiday.CHRISTMAS));
  props.setProperty("halloween.enabled","false");assertFalse(s.enabled(Holiday.HALLOWEEN));assertTrue(s.enabled(Holiday.CHRISTMAS));
  props.setProperty("halloween.enabled","tru");assertThrows(IllegalArgumentException.class,()->s.enabled(Holiday.HALLOWEEN));
 }
}
