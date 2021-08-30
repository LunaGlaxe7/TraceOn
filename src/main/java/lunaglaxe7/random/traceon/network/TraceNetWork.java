package lunaglaxe7.random.traceon.network;

import lunaglaxe7.random.traceon.Trace;
import lunaglaxe7.random.traceon.TraceOnMain;
import lunaglaxe7.random.traceon.screen.TraceInvScreenHandler;
import lunaglaxe7.random.traceon.util.TraceKnowledgeUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TraceNetWork {


    public static Identifier traceKeyChannel = new Identifier(TraceOnMain.MODID,"trace");
    public static Identifier traceOnChannel = new Identifier(TraceOnMain.MODID,"traceon");
    public static Identifier traceSyncChannel = new Identifier(TraceOnMain.MODID,"tracesync");
    public static Identifier traceNumChannel = new Identifier(TraceOnMain.MODID,"traceknowledgenum");
    public static int knowledgeSize;

    public static void registerReceiver(){
        ServerPlayNetworking.registerGlobalReceiver(traceKeyChannel, TraceNetWork::openTraceInv);
        ServerPlayNetworking.registerGlobalReceiver(traceOnChannel, TraceNetWork::serverTraceReflect);

    }



    @Environment(EnvType.CLIENT)
    public static void registerReceiverClient(){
        ClientPlayNetworking.registerGlobalReceiver(traceNumChannel,(client,handler,buf,sender)->{
            NbtCompound nbt = buf.readNbt();
            knowledgeSize = nbt.getByte("tracenum");
        });

    }

    private static void openTraceInv(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        NbtCompound nbt = new NbtCompound();
        nbt.putByte("tracenum",(byte)(TraceKnowledgeUtil.getTraceKnowledge(player).size()));
        ServerPlayNetworking.send(player,traceNumChannel, PacketByteBufs.create().writeNbt(nbt));
        player.openHandledScreen(new TraceInvScreenHandler.TraceInvScreenHandlerFactory());
    }

    private static void serverTraceReflect(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int index = buf.readInt();
        Trace.trace(player,index);
    }



}
