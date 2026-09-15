package io.zaryx.testing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import io.zaryx.util.PasswordHashing;
import io.zaryx.net.login.LoginReturnCode;
import static org.junit.jupiter.api.Assertions.*;
class TestWorldTest {
 @TempDir Path root;
 @Test void accessRequiresProvisionedAccountAndPassword() throws Exception {
  assertEquals(LoginReturnCode.TEST_WORLD_RESTRICTED,TestWorld.checkLogin(root,"tester","password"));
  Files.writeString(root.resolve("approved-testers.txt"),"tester\n");
  assertEquals(LoginReturnCode.TEST_WORLD_RESTRICTED,TestWorld.checkLogin(root,"tester","password"));
  CreateTester.create(root,"tester",PasswordHashing.hash("password"),true);
  assertEquals(LoginReturnCode.SUCCESS,TestWorld.checkLogin(root,"Tester","password"));
  assertEquals(LoginReturnCode.INVALID_USERNAME_OR_PASSWORD,TestWorld.checkLogin(root,"tester","wrong"));
  assertThrows(FileAlreadyExistsException.class,()->CreateTester.create(root,"tester","unused",true));
  Files.writeString(root.resolve("approved-testers.txt"),"");
  assertEquals(LoginReturnCode.TEST_WORLD_RESTRICTED,TestWorld.checkLogin(root,"tester","password"));
 }
 @Test void isolationAndNetworkGuard() throws Exception {
  assertThrows(IllegalStateException.class,()->TestWorld.validateDirectory(root,root));
  Files.writeString(root.resolve(".zaryx-test-world"),"test");
  assertEquals(root.toRealPath(),TestWorld.validateDirectory(root,root));
  TestWorldNetworkGuard guard=new TestWorldNetworkGuard();
  assertThrows(SecurityException.class,()->guard.checkConnect("localhost",3306));
  assertThrows(SecurityException.class,()->guard.checkConnect("example.com",443));
  assertDoesNotThrow(()->guard.checkAccept("127.0.0.1",12345));
  assertFalse(TestWorld.validName("../tester"));
 }
}
