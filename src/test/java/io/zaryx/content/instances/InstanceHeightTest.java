package io.zaryx.content.instances;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class InstanceHeightTest {
 @Test void allocatingMoreWorldsNeverReleasesExistingReservations(){
  int before=InstanceHeight.getReservedCount();int a=InstanceHeight.getFreeAndReserve(),b=InstanceHeight.getFreeAndReserve(),c=InstanceHeight.getFreeAndReserve();
  try {assertTrue(a>0);assertNotEquals(a,b);assertNotEquals(a,c);assertNotEquals(b,c);assertEquals(before+3,InstanceHeight.getReservedCount());InstanceHeight.getFree();assertEquals(before+3,InstanceHeight.getReservedCount());}
  finally{InstanceHeight.free(a);InstanceHeight.free(b);InstanceHeight.free(c);}
 }
}
