package io.zaryx.testing;

import io.zaryx.Configuration;
import io.zaryx.net.login.LoginReturnCode;
import io.zaryx.util.PasswordHashing;
import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

/** On-demand World 2. Always launched with a separate working directory. */
public final class TestWorld {
    public static final int PORT=43598;
    private static FileChannel lockChannel;
    private static FileLock lock;
    private TestWorld(){ }
    public static boolean enabled(){return Boolean.getBoolean("zaryx.testWorld");}
    public static Path validateDirectory(Path directory,Path expected) throws IOException {
        Path root=directory.toRealPath();
        if(!root.equals(expected.toRealPath())||!Files.isRegularFile(root.resolve(".zaryx-test-world")))
            throw new IllegalStateException("Test world must run from its prepared, isolated directory.");
        for(String folder:List.of("etc","save_files","logs","user_data","lib")) {
            Path path=root.resolve(folder);Files.createDirectories(path);
            if(!path.toRealPath().startsWith(root))throw new IllegalStateException("Test directory points outside its runtime: "+folder);
        }
        return root;
    }
    public static void initialise() throws IOException {
        if(!enabled())return;
        if(Runtime.version().feature()!=11)throw new IllegalStateException("Run the test world with JDK 11.");
        String expected=System.getProperty("zaryx.testWorld.root");
        if(expected==null)throw new IllegalStateException("Use Start Test Server.bat.");
        Path root=validateDirectory(Path.of("."),Path.of(expected));
        lockChannel=FileChannel.open(root.resolve("world.lock"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);
        lock=lockChannel.tryLock();
        if(lock==null)throw new IllegalStateException("The test world is already running.");
        Configuration.USER_FOLDER=root.resolve("user_data").toString();
        Configuration.DISABLE_DATABASES=true;
        Configuration.DISABLE_DISCORD_MESSAGING=true;
        Configuration.DISABLE_REGISTRATION=true;
        Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA=true;
        Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA=true;
        Configuration.DISABLE_CAPTCHA_EVERY_LOGIN=true;
        System.setSecurityManager(new TestWorldNetworkGuard());
    }
    public static String normalize(String name) {
        return name==null?"":name.replace('_',' ').trim().toLowerCase(Locale.ROOT);
    }
    public static boolean validName(String name){return name.matches("[a-z0-9 ]{1,12}")&&!name.isBlank();}
    public static LoginReturnCode checkLogin(Path root,String name,String password) {
        String login=normalize(name);
        if(!validName(login))return LoginReturnCode.TEST_WORLD_RESTRICTED;
        try {
            Path list=root.resolve("approved-testers.txt");
            if(!Files.isRegularFile(list)||Files.readAllLines(list).stream().map(line->line.split("#",2)[0]).map(TestWorld::normalize).noneMatch(login::equals))
                return LoginReturnCode.TEST_WORLD_RESTRICTED;
            Path save=root.resolve("save_files/public/character_saves").resolve(login+".txt");
            if(!Files.isRegularFile(save)||!save.toRealPath().startsWith(root.toRealPath()))return LoginReturnCode.TEST_WORLD_RESTRICTED;
            String hash=null;
            for(String line:Files.readAllLines(save))if(line.trim().startsWith("character-password =")){
                if(hash!=null)return LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
                hash=line.substring(line.indexOf('=')+1).trim();
            }
            return hash!=null&&password!=null&&PasswordHashing.check(password,hash)?LoginReturnCode.SUCCESS:LoginReturnCode.INVALID_USERNAME_OR_PASSWORD;
        }catch(IOException|IllegalArgumentException e){return LoginReturnCode.TEST_WORLD_RESTRICTED;}
    }
}
