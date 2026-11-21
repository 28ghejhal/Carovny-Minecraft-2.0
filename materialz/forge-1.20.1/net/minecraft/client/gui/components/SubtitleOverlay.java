package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubtitleOverlay implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlay(Minecraft p_94641_) {
      this.minecraft = p_94641_;
   }

   public void render(GuiGraphics p_282562_) {
      if (!this.isListening && (Boolean)this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !(Boolean)this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         Vec3 $$1 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3 $$2 = (new Vec3((double)0.0F, (double)0.0F, (double)-1.0F)).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180F));
         Vec3 $$3 = (new Vec3((double)0.0F, (double)1.0F, (double)0.0F)).xRot(-this.minecraft.player.getXRot() * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.getYRot() * ((float)Math.PI / 180F));
         Vec3 $$4 = $$2.cross($$3);
         int $$5 = 0;
         int $$6 = 0;
         double $$7 = (Double)this.minecraft.options.notificationDisplayTime().get();
         Iterator<Subtitle> $$8 = this.subtitles.iterator();

         while($$8.hasNext()) {
            Subtitle $$9 = (Subtitle)$$8.next();
            if ((double)$$9.getTime() + (double)3000.0F * $$7 <= (double)Util.getMillis()) {
               $$8.remove();
            } else {
               $$6 = Math.max($$6, this.minecraft.font.width((FormattedText)$$9.getText()));
            }
         }

         $$6 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(Subtitle $$10 : this.subtitles) {
            int $$11 = 255;
            Component $$12 = $$10.getText();
            Vec3 $$13 = $$10.getLocation().subtract($$1).normalize();
            double $$14 = -$$4.dot($$13);
            double $$15 = -$$2.dot($$13);
            boolean $$16 = $$15 > (double)0.5F;
            int $$17 = $$6 / 2;
            Objects.requireNonNull(this.minecraft.font);
            int $$18 = 9;
            int $$19 = $$18 / 2;
            float $$20 = 1.0F;
            int $$21 = this.minecraft.font.width((FormattedText)$$12);
            int $$22 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - $$10.getTime()) / (float)((double)3000.0F * $$7)));
            int $$23 = $$22 << 16 | $$22 << 8 | $$22;
            p_282562_.pose().pushPose();
            p_282562_.pose().translate((float)p_282562_.guiWidth() - (float)$$17 * 1.0F - 2.0F, (float)(p_282562_.guiHeight() - 35) - (float)($$5 * ($$18 + 1)) * 1.0F, 0.0F);
            p_282562_.pose().scale(1.0F, 1.0F, 1.0F);
            p_282562_.fill(-$$17 - 1, -$$19 - 1, $$17 + 1, $$19 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            int $$24 = $$23 + -16777216;
            if (!$$16) {
               if ($$14 > (double)0.0F) {
                  p_282562_.drawString(this.minecraft.font, ">", $$17 - this.minecraft.font.width(">"), -$$19, $$24);
               } else if ($$14 < (double)0.0F) {
                  p_282562_.drawString(this.minecraft.font, "<", -$$17, -$$19, $$24);
               }
            }

            p_282562_.drawString(this.minecraft.font, $$12, -$$21 / 2, -$$19, $$24);
            p_282562_.pose().popPose();
            ++$$5;
         }

      }
   }

   public void onPlaySound(SoundInstance p_94645_, WeighedSoundEvents p_94646_) {
      if (p_94646_.getSubtitle() != null) {
         Component $$2 = p_94646_.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            for(Subtitle $$3 : this.subtitles) {
               if ($$3.getText().equals($$2)) {
                  $$3.refresh(new Vec3(p_94645_.getX(), p_94645_.getY(), p_94645_.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new Subtitle($$2, new Vec3(p_94645_.getX(), p_94645_.getY(), p_94645_.getZ())));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Subtitle {
      private final Component text;
      private long time;
      private Vec3 location;

      public Subtitle(Component p_169072_, Vec3 p_169073_) {
         this.text = p_169072_;
         this.location = p_169073_;
         this.time = Util.getMillis();
      }

      public Component getText() {
         return this.text;
      }

      public long getTime() {
         return this.time;
      }

      public Vec3 getLocation() {
         return this.location;
      }

      public void refresh(Vec3 p_94657_) {
         this.location = p_94657_;
         this.time = Util.getMillis();
      }
   }
}
