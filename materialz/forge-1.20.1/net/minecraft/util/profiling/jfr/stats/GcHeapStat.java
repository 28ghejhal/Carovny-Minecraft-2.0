package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, Timing timing) {
   public GcHeapStat(Instant timestamp, long heapUsed, Timing timing) {
      this.timestamp = timestamp;
      this.heapUsed = heapUsed;
      this.timing = timing;
   }

   public static GcHeapStat from(RecordedEvent p_185698_) {
      return new GcHeapStat(p_185698_.getStartTime(), p_185698_.getLong("heapUsed"), p_185698_.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.Timing.BEFORE_GC : GcHeapStat.Timing.AFTER_GC);
   }

   public static Summary summary(Duration p_185691_, List<GcHeapStat> p_185692_, Duration p_185693_, int p_185694_) {
      return new Summary(p_185691_, p_185693_, p_185694_, calculateAllocationRatePerSecond(p_185692_));
   }

   private static double calculateAllocationRatePerSecond(List<GcHeapStat> p_185696_) {
      long $$1 = 0L;
      Map<Timing, List<GcHeapStat>> $$2 = (Map)p_185696_.stream().collect(Collectors.groupingBy((p_185689_) -> p_185689_.timing));
      List<GcHeapStat> $$3 = (List)$$2.get(GcHeapStat.Timing.BEFORE_GC);
      List<GcHeapStat> $$4 = (List)$$2.get(GcHeapStat.Timing.AFTER_GC);

      for(int $$5 = 1; $$5 < $$3.size(); ++$$5) {
         GcHeapStat $$6 = (GcHeapStat)$$3.get($$5);
         GcHeapStat $$7 = (GcHeapStat)$$4.get($$5 - 1);
         $$1 += $$6.heapUsed - $$7.heapUsed;
      }

      Duration $$8 = Duration.between(((GcHeapStat)p_185696_.get(1)).timestamp, ((GcHeapStat)p_185696_.get(p_185696_.size() - 1)).timestamp);
      return (double)$$1 / (double)$$8.getSeconds();
   }

   public static record Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
      public Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
         this.duration = duration;
         this.gcTotalDuration = gcTotalDuration;
         this.totalGCs = totalGCs;
         this.allocationRateBytesPerSecond = allocationRateBytesPerSecond;
      }

      public float gcOverHead() {
         return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
      }
   }

   static enum Timing {
      BEFORE_GC,
      AFTER_GC;

      // $FF: synthetic method
      private static Timing[] $values() {
         return new Timing[]{BEFORE_GC, AFTER_GC};
      }
   }
}
