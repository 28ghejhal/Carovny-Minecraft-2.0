package net.minecraftforge.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;

public record ServerStatusPing(Map<ResourceLocation, ChannelData> channels, Map<String, String> mods, int fmlNetworkVer, boolean truncated) {
   private static final Codec<ByteBuf> BYTE_BUF_CODEC;
   public static final Codec<ServerStatusPing> CODEC;
   private static final int VERSION_FLAG_IGNORESERVERONLY = 1;

   public ServerStatusPing() {
      this(NetworkRegistry.buildChannelVersionsForListPing(), (Map)Util.make(new HashMap(), (map) -> ModList.get().forEachModContainer((modid, mc) -> map.put(modid, (String)mc.getCustomExtension(IExtensionPoint.DisplayTest.class).map(IExtensionPoint.DisplayTest::suppliedVersion).map(Supplier::get).orElse("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31")))), 3, false);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ServerStatusPing)) {
         return false;
      } else {
         ServerStatusPing that = (ServerStatusPing)o;
         return this.fmlNetworkVer == that.fmlNetworkVer && this.channels.equals(that.channels) && this.mods.equals(that.mods);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.channels, this.mods, this.fmlNetworkVer});
   }

   private HashMap<String, List<Map.Entry<ResourceLocation, ChannelData>>> getChannelsByMod() {
      HashMap<String, List<Map.Entry<ResourceLocation, ChannelData>>> ret = new HashMap();

      for(Map.Entry<ResourceLocation, ChannelData> channel : this.channels.entrySet()) {
         String namespace = ((ResourceLocation)channel.getKey()).getNamespace();
         List<Map.Entry<ResourceLocation, ChannelData>> list = (List)ret.get(namespace);
         if (list == null) {
            list = new ArrayList();
            ret.put(namespace, list);
         }

         list.add(channel);
      }

      return ret;
   }

   private List<Map.Entry<ResourceLocation, ChannelData>> getNonModChannels() {
      return this.channels.entrySet().stream().filter((c) -> !this.mods.containsKey(((ResourceLocation)c.getKey()).getNamespace())).toList();
   }

   public ByteBuf toBuf() {
      boolean reachedSizeLimit = false;
      FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
      buf.writeBoolean(false);
      buf.writeShort(this.mods.size());
      int writtenCount = 0;
      HashMap<String, List<Map.Entry<ResourceLocation, ChannelData>>> channelsByMod = this.getChannelsByMod();

      for(Map.Entry<String, String> modEntry : this.mods.entrySet()) {
         boolean isIgnoreServerOnly = ((String)modEntry.getValue()).equals("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31");
         List<Map.Entry<ResourceLocation, ChannelData>> channelsForMod = (List)channelsByMod.getOrDefault(modEntry.getKey(), List.of());
         int channelSizeAndVersionFlag = channelsForMod.size() << 1;
         if (isIgnoreServerOnly) {
            channelSizeAndVersionFlag |= 1;
         }

         buf.writeVarInt(channelSizeAndVersionFlag);
         buf.writeUtf((String)modEntry.getKey());
         if (!isIgnoreServerOnly) {
            buf.writeUtf((String)modEntry.getValue());
         }

         for(Map.Entry<ResourceLocation, ChannelData> entry : channelsForMod) {
            buf.writeUtf(((ResourceLocation)entry.getKey()).getPath());
            buf.writeUtf(((ChannelData)entry.getValue()).version());
            buf.writeBoolean(((ChannelData)entry.getValue()).required());
         }

         ++writtenCount;
         if (buf.readableBytes() >= 60000) {
            reachedSizeLimit = true;
            break;
         }
      }

      if (!reachedSizeLimit) {
         List<Map.Entry<ResourceLocation, ChannelData>> nonModChannels = this.getNonModChannels();
         buf.writeVarInt(nonModChannels.size());

         for(Map.Entry<ResourceLocation, ChannelData> entry : nonModChannels) {
            buf.writeResourceLocation((ResourceLocation)entry.getKey());
            buf.writeUtf(((ChannelData)entry.getValue()).version());
            buf.writeBoolean(((ChannelData)entry.getValue()).required());
         }
      } else {
         buf.setShort(1, writtenCount);
         buf.writeVarInt(0);
      }

      buf.setBoolean(0, reachedSizeLimit);
      return buf;
   }

   private static ServerStatusPing deserializeOptimized(int fmlNetworkVersion, ByteBuf bbuf) {
      FriendlyByteBuf buf = new FriendlyByteBuf(bbuf);

      boolean truncated;
      Map<ResourceLocation, ChannelData> channels;
      Map<String, String> mods;
      try {
         truncated = buf.readBoolean();
         int modsSize = buf.readUnsignedShort();
         mods = new HashMap();
         channels = new HashMap();

         for(int i = 0; i < modsSize; ++i) {
            int channelSizeAndVersionFlag = buf.readVarInt();
            int channelSize = channelSizeAndVersionFlag >>> 1;
            boolean isIgnoreServerOnly = (channelSizeAndVersionFlag & 1) != 0;
            String modId = buf.readUtf();
            String modVersion = isIgnoreServerOnly ? "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31" : buf.readUtf();

            for(int i1 = 0; i1 < channelSize; ++i1) {
               String channelName = buf.readUtf();
               String channelVersion = buf.readUtf();
               boolean requiredOnClient = buf.readBoolean();
               ResourceLocation id = new ResourceLocation(modId, channelName);
               channels.put(id, new ChannelData(id, channelVersion, requiredOnClient));
            }

            mods.put(modId, modVersion);
         }

         int nonModChannelCount = buf.readVarInt();

         for(int i = 0; i < nonModChannelCount; ++i) {
            ResourceLocation channelName = buf.readResourceLocation();
            String channelVersion = buf.readUtf();
            boolean requiredOnClient = buf.readBoolean();
            channels.put(channelName, new ChannelData(channelName, channelVersion, requiredOnClient));
         }
      } finally {
         buf.release();
      }

      return new ServerStatusPing(channels, mods, fmlNetworkVersion, truncated);
   }

   private static String encodeOptimized(ByteBuf buf) {
      int byteLength = buf.readableBytes();
      StringBuilder sb = new StringBuilder();
      sb.append((char)(byteLength & 32767));
      sb.append((char)(byteLength >>> 15 & 32767));
      int buffer = 0;

      int bitsInBuf;
      for(bitsInBuf = 0; buf.isReadable(); bitsInBuf += 8) {
         if (bitsInBuf >= 15) {
            char c = (char)(buffer & 32767);
            sb.append(c);
            buffer >>>= 15;
            bitsInBuf -= 15;
         }

         short b = buf.readUnsignedByte();
         buffer |= b << bitsInBuf;
      }

      buf.release();
      if (bitsInBuf > 0) {
         char c = (char)(buffer & 32767);
         sb.append(c);
      }

      return sb.toString();
   }

   private static ByteBuf decodeOptimized(String s) {
      int size0 = s.charAt(0);
      int size1 = s.charAt(1);
      int size = size0 | size1 << 15;
      ByteBuf buf = Unpooled.buffer(size);
      int stringIndex = 2;
      int buffer = 0;

      int bitsInBuf;
      for(bitsInBuf = 0; stringIndex < s.length(); ++stringIndex) {
         while(bitsInBuf >= 8) {
            buf.writeByte(buffer);
            buffer >>>= 8;
            bitsInBuf -= 8;
         }

         char c = s.charAt(stringIndex);
         buffer |= (c & 32767) << bitsInBuf;
         bitsInBuf += 15;
      }

      while(buf.readableBytes() < size) {
         buf.writeByte(buffer);
         buffer >>>= 8;
         bitsInBuf -= 8;
      }

      return buf;
   }

   public Map<ResourceLocation, ChannelData> getRemoteChannels() {
      return this.channels;
   }

   public Map<String, String> getRemoteModData() {
      return this.mods;
   }

   public int getFMLNetworkVersion() {
      return this.fmlNetworkVer;
   }

   public boolean isTruncated() {
      return this.truncated;
   }

   static {
      BYTE_BUF_CODEC = Codec.STRING.xmap(ServerStatusPing::decodeOptimized, ServerStatusPing::encodeOptimized);
      CODEC = RecordCodecBuilder.create((in) -> in.group(Codec.INT.fieldOf("fmlNetworkVersion").forGetter(ServerStatusPing::getFMLNetworkVersion), BYTE_BUF_CODEC.optionalFieldOf("d").forGetter((ping) -> Optional.of(ping.toBuf())), ServerStatusPing.ChannelData.CODEC.listOf().optionalFieldOf("channels").forGetter((ping) -> Optional.of(List.of())), ServerStatusPing.ModInfo.CODEC.listOf().optionalFieldOf("mods").forGetter((ping) -> Optional.of(List.of())), Codec.BOOL.optionalFieldOf("truncated").forGetter((ping) -> Optional.of(ping.isTruncated()))).apply(in, (fmlVer, buf, channels, mods, truncated) -> (ServerStatusPing)buf.map((byteBuf) -> deserializeOptimized(fmlVer, byteBuf)).orElseGet(() -> new ServerStatusPing((Map)((List)channels.orElseGet(List::of)).stream().collect(Collectors.toMap(ChannelData::res, Function.identity())), (Map)((List)mods.orElseGet(List::of)).stream().collect(Collectors.toMap(ModInfo::modId, ModInfo::modmarker)), fmlVer, (Boolean)truncated.orElse(false)))));
   }

   public static record ModInfo(String modId, String modmarker) {
      public static final Codec<ModInfo> CODEC = RecordCodecBuilder.create((in) -> in.group(Codec.STRING.fieldOf("modId").forGetter(ModInfo::modId), Codec.STRING.fieldOf("modmarker").forGetter(ModInfo::modmarker)).apply(in, ModInfo::new));
   }

   public static record ChannelData(ResourceLocation res, String version, boolean required) {
      public static final Codec<ChannelData> CODEC = RecordCodecBuilder.create((in) -> in.group(ResourceLocation.CODEC.fieldOf("res").forGetter(ChannelData::res), Codec.STRING.fieldOf("version").forGetter(ChannelData::version), Codec.BOOL.fieldOf("required").forGetter(ChannelData::required)).apply(in, ChannelData::new));
   }
}
