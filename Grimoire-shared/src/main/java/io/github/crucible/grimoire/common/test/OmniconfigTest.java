package io.github.crucible.grimoire.common.test;

import java.util.Arrays;

import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class OmniconfigTest {

    public static int exampleInt = 23;
    public static double exampleDouble = 2.0;
    public static boolean exampleBoolean = false;
    public static String exampleString = "LOLOLOLOL";
    public static String[] exampleStringArray = { "LOL", "KEK", "MAN" };

    public static final OmniconfigTest INSTANCE = new OmniconfigTest();

    public OmniconfigTest() {
        Omniconfig.Builder wrapper = Omniconfig.builder("omnitest", true, "1.0");

        wrapper.versioningPolicy(VersioningPolicy.AGGRESSIVE);
        wrapper.terminateNonInvokedKeys(true);

        wrapper.loadFile();
        wrapper.category("Generic Config", "Just some generic stuff");

        wrapper.getInteger("exampleInt", exampleInt).comment("lol").minMax(100).sync()
        .uponLoad((value) -> {exampleInt = value.getValue(); System.out.println("Updated int: " + exampleInt);})
        .build();

        wrapper.getDouble("exampleDouble", exampleDouble).comment("example double or smth").min(-1).max(120000)
        .uponLoad((value) -> exampleDouble = value.getValue())
        .build();

        wrapper.getBoolean("exampleBoolean", exampleBoolean).comment("aaaaaand example boolean thing").sync()
        .uponLoad((value) -> exampleBoolean = value.getValue())
        .build();

        wrapper.getString("exampleString", exampleString).comment("string kekw").sync()
        .validValues("LOLOLOLOL", "KEKEKEKEKEKE", "OMEGALUL")
        .uponLoad((value) -> exampleString = value.getValue())
        .build();

        wrapper.getStringArray("exampleStringArray", exampleStringArray).comment("some string array").sync()
        .uponLoad((value) -> {exampleStringArray = value.getArrayValue(); System.out.println("Array now: " + Arrays.asList(exampleStringArray));})
        .build();

        wrapper.resetCategory();
        wrapper.setReloadable();

        wrapper.build();
    }

}