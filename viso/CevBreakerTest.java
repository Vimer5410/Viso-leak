package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Iterator;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.BlockUtilsWorld;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_3965;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2846.class_2847;

public class CevBreakerTest extends Module {
   private final SettingGroup sgPlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgMisc;
   private final SettingGroup sgRender;
   private final Setting<Double> distance;
   private final Setting<Integer> delay;
   private final Setting<Boolean> instant;
   private final Setting<Integer> instantDelay;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> ak47;
   private final Setting<Boolean> chat;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   public class_1657 target;
   boolean offhand;
   boolean started;
   boolean placed;
   class_2338 targetPos;
   int ticks;
   int instaTicks;

   public CevBreakerTest() {
      super(Categories.NewCombat, "Cev-Breaker", "Place crystals on obsidian above target and break/attack obsidian/crystals.");
      this.sgPlace = this.settings.createGroup("Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgMisc = this.settings.createGroup("Misc");
      this.sgRender = this.settings.createGroup("Render");
      this.distance = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Range where player will targeting.")).defaultValue(7.0D).min(0.0D).sliderMax(6.0D).max(6.0D).build());
      this.delay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The amount of delay in ticks before placing.")).defaultValue(1)).min(0).sliderMax(20).build());
      this.instant = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instant")).description("Allow insta break for Cev.")).defaultValue(false)).build());
      this.instantDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The amount of delay in ticks before mining with insta break.")).defaultValue(1)).min(0).sliderMax(20).visible(() -> {
         return (Boolean)this.instant.get();
      })).build());
      this.rotate = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotate to block that placing.")).defaultValue(true)).build());
      this.ak47 = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("remove-crystals")).description("...")).defaultValue(true)).build());
      this.chat = this.sgMisc.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("...")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("...")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).visible(() -> {
         return (Boolean)this.render.get();
      })).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(142, 6, 255, 41))).visible(() -> {
         return (Boolean)this.render.get();
      })).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(153, 0, 255, 255))).visible(() -> {
         return (Boolean)this.render.get();
      })).build());
      this.offhand = false;
      this.started = false;
      this.placed = false;
      this.ticks = 0;
      this.instaTicks = 0;
   }

   public void onActivate() {
      this.started = false;
      this.target = null;
      this.ticks = 0;
   }

   public void onDeactivate() {
      this.started = false;
      this.target = null;
      this.ticks = 0;
   }

   @EventHandler
   public void onTick(TickEvent.Pre e) {
      this.target = this.getTarget();
      if (this.target != null) {
         this.targetPos = this.target.method_24515().method_10069(0, 2, 0);
         class_243 vec = new class_243((double)this.targetPos.method_10263(), (double)this.targetPos.method_10264(), (double)this.targetPos.method_10260());
         if (!(this.mc.field_1687.method_8320(this.targetPos.method_10084()).method_26204() instanceof class_2189)) {
            return;
         }

         if (this.ticks == (Integer)this.delay.get()) {
            if (InvUtils.findInHotbar(class_1802.field_8281).getSlot() == -1) {
               return;
            }

            InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8281).getSlot(), false);
            if (this.mc.field_1687.method_8320(this.targetPos).method_26204() instanceof class_2189) {
               if ((Boolean)this.chat.get()) {
                  this.info("Placing", new Object[0]);
               }

               if ((Boolean)this.rotate.get()) {
                  BlockUtilsWorld.rotateBl(this.targetPos);
               }

               this.mc.field_1761.method_2896(this.mc.field_1724, this.mc.field_1687, class_1268.field_5808, new class_3965(vec, class_2350.field_11036, this.targetPos, true));
            }
         }

         if (this.ticks == (Integer)this.delay.get()) {
            if (InvUtils.findInHotbar(class_1802.field_8301).getSlot() == -1 && this.mc.field_1724.method_6079().method_7909() != class_1802.field_8301) {
               return;
            }

            if (this.mc.field_1724.method_6079().method_7909() != class_1802.field_8301) {
               InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8301).getSlot(), false);
               this.offhand = false;
            } else {
               if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() != -1) {
                  InvUtils.swap(InvUtils.findInHotbar(class_1802.field_22024).getSlot(), false);
               } else if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() == -1 && InvUtils.findInHotbar(class_1802.field_8377).getSlot() != -1) {
                  InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8377).getSlot(), false);
               } else {
                  if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() != -1 || InvUtils.findInHotbar(class_1802.field_8377).getSlot() != -1 || InvUtils.findInHotbar(class_1802.field_8403).getSlot() == -1) {
                     return;
                  }

                  InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8403).getSlot(), false);
               }

               this.offhand = true;
            }

            if ((Boolean)this.rotate.get()) {
               BlockUtilsWorld.rotateBl(this.targetPos);
            }

            this.mc.field_1761.method_2896(this.mc.field_1724, this.mc.field_1687, this.offhand ? class_1268.field_5810 : class_1268.field_5808, new class_3965(vec, class_2350.field_11033, this.targetPos, true));
            this.placed = true;
         }

         if (this.ticks == (Integer)this.delay.get()) {
            if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() == -1 && InvUtils.findInHotbar(class_1802.field_8377).getSlot() == -1 && InvUtils.findInHotbar(class_1802.field_8403).getSlot() == -1) {
               return;
            }

            if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() != -1) {
               InvUtils.swap(InvUtils.findInHotbar(class_1802.field_22024).getSlot(), false);
            } else if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() == -1 && InvUtils.findInHotbar(class_1802.field_8377).getSlot() != -1) {
               InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8377).getSlot(), false);
            } else {
               if (InvUtils.findInHotbar(class_1802.field_22024).getSlot() != -1 || InvUtils.findInHotbar(class_1802.field_8377).getSlot() != -1 || InvUtils.findInHotbar(class_1802.field_8403).getSlot() == -1) {
                  return;
               }

               InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8403).getSlot(), false);
            }

            if (!(Boolean)this.instant.get()) {
               if ((Boolean)this.chat.get()) {
                  this.info("Breaking", new Object[0]);
               }

               if ((Boolean)this.rotate.get()) {
                  BlockUtilsWorld.rotateBl(this.targetPos);
               }

               this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, this.targetPos, class_2350.field_11036));
               this.mc.field_1724.method_6104(class_1268.field_5808);
               this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, this.targetPos, class_2350.field_11036));
            }
         }

         if (this.ticks >= (Integer)this.delay.get()) {
            if (this.mc.field_1687.method_8320(this.targetPos).method_26204() instanceof class_2189) {
               if (this.getCrystal(this.target) != null) {
                  if ((Boolean)this.rotate.get()) {
                     BlockUtilsWorld.rotateBl(this.getCrystal(this.target).method_24515());
                  }

                  this.mc.field_1761.method_2918(this.mc.field_1724, this.getCrystal(this.target));
                  if ((Boolean)this.ak47.get()) {
                     this.getCrystal(this.target).method_5650(class_5529.field_26998);
                  }
               }

               this.placed = false;
               this.ticks = -1;
            } else if ((Boolean)this.instant.get()) {
               if ((Boolean)this.chat.get()) {
                  this.info("Breaking", new Object[0]);
               }

               if (!this.started) {
                  if ((Boolean)this.rotate.get()) {
                     BlockUtilsWorld.rotateBl(this.targetPos);
                  }

                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, this.targetPos, class_2350.field_11036));
                  this.started = true;
               }

               if (this.instaTicks >= (Integer)this.instantDelay.get()) {
                  this.instaTicks = 0;
                  if (this.shouldMine(this.targetPos)) {
                     this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, this.targetPos, class_2350.field_11036));
                     this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
                  }
               } else {
                  ++this.instaTicks;
               }
            }
         }

         if (this.placed && this.getCrystal(this.target) == null) {
            int prevSlot = this.mc.field_1724.method_31548().field_7545;
            if (InvUtils.findInHotbar(class_1802.field_8301).getSlot() == -1) {
               return;
            }

            InvUtils.swap(InvUtils.findInHotbar(class_1802.field_8301).getSlot(), false);
            if ((Boolean)this.rotate.get()) {
               BlockUtilsWorld.rotateBl(this.targetPos);
            }

            this.mc.field_1761.method_2896(this.mc.field_1724, this.mc.field_1687, this.offhand ? class_1268.field_5810 : class_1268.field_5808, new class_3965(vec, class_2350.field_11033, this.targetPos, true));
            InvUtils.swap(prevSlot, false);
         }

         ++this.ticks;
      } else {
         this.targetPos = null;
         if ((Boolean)this.chat.get()) {
            this.info("", new Object[0]);
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent e) {
      if ((Boolean)this.render.get() && this.targetPos != null) {
         e.renderer.box((class_2338)this.targetPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }

   }

   private class_1511 getCrystal(class_1657 target) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      class_1297 e;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         e = (class_1297)var2.next();
      } while(!(e instanceof class_1511) || Math.ceil(e.method_23317()) != Math.ceil(target.method_23317()) || Math.ceil(e.method_23321()) != Math.ceil(target.method_23321()));

      return (class_1511)e;
   }

   private boolean shouldMine(class_2338 bp) {
      if (bp.method_10264() == -1) {
         return false;
      } else if (this.mc.field_1687.method_8320(bp).method_26214(this.mc.field_1687, bp) < 0.0F) {
         return false;
      } else if (this.mc.field_1687.method_8320(bp).method_26215()) {
         return false;
      } else {
         return this.mc.field_1724.method_6047().method_7909() == class_1802.field_8377 || this.mc.field_1724.method_6047().method_7909() == class_1802.field_22024;
      }
   }

   private class_1657 getTarget() {
      Iterator var1 = this.mc.field_1687.method_18112().iterator();

      class_1297 e;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         e = (class_1297)var1.next();
      } while(!((double)this.mc.field_1724.method_5739(e) <= (Double)this.distance.get()) || !(e instanceof class_1657) || e == this.mc.field_1724);

      return (class_1657)e;
   }
}
