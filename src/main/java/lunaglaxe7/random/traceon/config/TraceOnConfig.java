package lunaglaxe7.random.traceon.config;

import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.Properties;

public class TraceOnConfig {

    //To be configured
    public static boolean banMending = true;
    private static String banMendingKey = "BanMendingForTraceItems";
    public static boolean enableTraceItemLife = true;
    private static String enableTraceItemLifeKey = "TraceItemsHaveMaxExistingTime";
    public static int TRACE_BASE = 1000;

    public static int TRACE_ITEM_LIFE_BASE = 1200;
    private static String traceItemLifeBaseKey = "BasicExistingTimeForTraceItems";

    public static void initialize() throws IOException {
        String path = /*File.separator+*/"config"+File.separator+"traceon.properties";
        File configFile = new File(MinecraftClient.getInstance().runDirectory, path);
        if (!configFile.exists()){
            configFile.createNewFile();
        }
        Properties config = new Properties();
        FileReader input = new FileReader(configFile);
        FileWriter output = new FileWriter(configFile);

        config.load(input);

        banMending = Boolean.parseBoolean(config.getProperty(banMendingKey,"true"));
        config.setProperty(banMendingKey,Boolean.toString(banMending));

        enableTraceItemLife = Boolean.parseBoolean(config.getProperty(enableTraceItemLifeKey,"true"));
        config.setProperty(enableTraceItemLifeKey,Boolean.toString(enableTraceItemLife));

        if (enableTraceItemLife){
            TRACE_ITEM_LIFE_BASE = Integer.parseInt(config.getProperty(traceItemLifeBaseKey,"1200"));
            config.setProperty(traceItemLifeBaseKey,Integer.toString(TRACE_ITEM_LIFE_BASE));
        }else {
            if (config.containsKey(traceItemLifeBaseKey)) {
                config.remove(traceItemLifeBaseKey);
            }
        }

        config.store(output,"Configuration of mod TraceOn");

        input.close();
        output.flush();
        output.close();

    }
}
