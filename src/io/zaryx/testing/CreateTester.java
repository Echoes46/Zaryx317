package io.zaryx.testing;

import io.zaryx.util.PasswordHashing;
import java.io.Console;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;

/** Local console provisioning only; passwords are never passed on the command line. */
public final class CreateTester {
    public static void main(String[] args) throws Exception {
        Console console=System.console();
        if(console==null)throw new IllegalStateException("Run Add Test Account.bat in an interactive terminal.");
        Path root=TestWorld.validateDirectory(Path.of("."),Path.of(System.getProperty("zaryx.testWorld.root")));
        try(FileChannel channel=FileChannel.open(root.resolve("world.lock"),StandardOpenOption.CREATE,StandardOpenOption.WRITE);FileLock lock=channel.tryLock()){
            if(lock==null)throw new IllegalStateException("Stop World 2 before adding an account.");
            String name=TestWorld.normalize(console.readLine("Test login name (1-12 letters/numbers/spaces): "));
            if(!TestWorld.validName(name))throw new IllegalArgumentException("Invalid login name");
            char[] password=console.readPassword("New TEST password (use a different password from Live): ");
            char[] confirm=console.readPassword("Confirm password: ");
            try {
                if(password==null||password.length<6||password.length>20||!Arrays.equals(password,confirm))throw new IllegalArgumentException("Use matching passwords of 6-20 characters.");
                boolean owner="yes".equalsIgnoreCase(console.readLine("Owner rights on TEST only? Type yes, otherwise press Enter: "));
                create(root,name,PasswordHashing.hash(new String(password)),owner);
                console.printf("Created and approved test account %s. Live accounts were not changed.%n",name);
            }finally{if(password!=null)Arrays.fill(password,'\0');if(confirm!=null)Arrays.fill(confirm,'\0');}
        }
    }
    static void create(Path root,String name,String hash,boolean owner) throws Exception {
        if(!TestWorld.validName(name))throw new IllegalArgumentException("Invalid login name");
        Path saves=root.resolve("save_files/public/character_saves");Files.createDirectories(saves);
        String data="[ACCOUNT]\ncharacter-username = "+name+"\ncharacter-password = "+hash+"\n\n[CHARACTER]\ncharacter-rights = "+(owner?3:0)+"\ncharacter-rights-secondary = "+(owner?3:0)+"\ncharacter-posx = 3093\ncharacter-posy = 3511\ncharacter-height = 0\n\n[EOF]\n";
        Files.writeString(saves.resolve(name+".txt"),data,StandardOpenOption.CREATE_NEW);
        Files.writeString(root.resolve("approved-testers.txt"),"\n"+name+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND);
    }
}
