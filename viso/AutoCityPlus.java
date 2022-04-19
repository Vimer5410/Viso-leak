package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.Vec3;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_3486;
import net.minecraft.class_2846.class_2847;

public class AutoCityPlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> autoSwap;
   public final Setting<AutoCityPlus.swapMode> autoSwapSetting;
   private final Setting<Boolean> swapBack;
   private final Setting<Boolean> support;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> instant;
   private final Setting<Integer> instantDelay;
   private final Setting<Boolean> test;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderProgress;
   private final Setting<Double> progressScale;
   private final Setting<SettingColor> progressColor;
   FindItemResult ironPick;
   FindItemResult diamondPick;
   FindItemResult netheritePick;
   private boolean antiSpam;
   private class_1657 target;
   private class_2338 blockPosTarget;
   private int breakTimer;
   private boolean shouldCount;
   private int instantTimer;
   private int swapSlot;

   public AutoCityPlus() {
      super(Categories.NewCombat, "Defender-Breaker", "...");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue(6.0D).min(0.0D).sliderMax(7.0D).build());
      this.autoSwap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-swap")).description("auto swap to pick axe")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("swap-mode")).defaultValue(AutoCityPlus.swapMode.normal);
      Setting var10003 = this.autoSwap;
      Objects.requireNonNull(var10003);
      this.autoSwapSetting = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swap-back")).description("swap to previous slot")).defaultValue(false);
      var10003 = this.autoSwap;
      Objects.requireNonNull(var10003);
      this.swapBack = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      this.support = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("support")).description("If there is no block below a city block it will place one before mining.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates you towards the city block.")).defaultValue(false)).build());
      this.instant = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("instant")).description("instant re break")).defaultValue(false)).build());
      var10001 = this.sgGeneral;
      IntSetting.Builder var2 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("delay to re break blocks")).defaultValue(1)).min(0).sliderMax(5);
      var10003 = this.instant;
      Objects.requireNonNull(var10003);
      this.instantDelay = var10001.add(((IntSetting.Builder)var2.visible(var10003::get)).build());
      this.test = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("test")).description("test")).defaultValue(true)).build());
      this.swing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("client-side swing")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("render city block")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color for positions to be placed.")).defaultValue(new SettingColor(142, 6, 255, 41))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color for positions to be placed.")).defaultValue(new SettingColor(153, 0, 255, 255))).build());
      this.renderProgress = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-progress")).description("render mine progress")).defaultValue(true)).build());
      this.progressScale = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("progress-scale")).description("scale of text")).defaultValue(1.5D).min(0.0D).sliderMax(3.0D).build());
      this.progressColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("progress-color")).description("the color of text rendered")).defaultValue(new SettingColor(255, 255, 255))).build());
   }

   public void onActivate() {
      this.swapSlot = this.mc.field_1724.method_31548().field_7545;
      this.instantTimer = 0;
      this.breakTimer = 0;
      this.antiSpam = false;
      this.blockPosTarget = null;
      this.target = null;
      this.shouldCount = false;
   }

   @EventHandler
   public void onDeactivate() {
      if (this.blockPosTarget != null) {
         this.mc.field_1724.field_3944.method_2883(new class_2846(class_2847.field_12971, this.blockPosTarget, class_2350.field_11036));
      }

      if ((Boolean)this.swapBack.get()) {
         this.mc.field_1724.method_31548().field_7545 = this.swapSlot;
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.instantTimer;
      if (this.shouldCount) {
         ++this.breakTimer;
      }

      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         this.target = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), SortPriority.LowestDistance);
      }

      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         this.target = null;
         this.antiSpam = false;
         this.blockPosTarget = null;
         this.breakTimer = 0;
         ChatUtils.error("No target founded! Disabling...");
         this.toggle();
      } else {
         this.blockPosTarget = this.blockPosTarget == null ? EntityUtils.getCityBlock(this.target) : this.blockPosTarget;
         this.ironPick = InvUtils.find((itemStack) -> {
            return itemStack.method_7909() == class_1802.field_8403;
         });
         this.diamondPick = InvUtils.find((itemStack) -> {
            return itemStack.method_7909() == class_1802.field_8377;
         });
         this.netheritePick = InvUtils.find((itemStack) -> {
            return itemStack.method_7909() == class_1802.field_22024;
         });
         if (this.delay() == 0) {
            ChatUtils.error("No pick in hotbar! Disabling...");
            this.toggle();
         } else if (this.blockPosTarget == null) {
            ChatUtils.error("No city block found! Disabling...");
            this.toggle();
         } else {
            if (PlayerUtils.distanceTo(this.blockPosTarget) > (double)this.mc.field_1761.method_2904()) {
               ChatUtils.error("Block to mine too far! Disabling...");
            }

            if ((Boolean)this.test.get()) {
               if (this.mc.field_1724.method_5777(class_3486.field_15517) && !class_1890.method_8200(this.mc.field_1724)) {
                  return;
               }

               if (!this.mc.field_1724.method_24828()) {
                  return;
               }
            }

            if (!this.antiSpam) {
               ChatUtils.info("breaking " + this.target.method_7334().getName() + " defender");
               if ((Boolean)this.support.get()) {
                  BlockUtils.place(this.blockPosTarget.method_10087(1), InvUtils.findInHotbar(class_1802.field_8281), (Boolean)this.rotate.get(), 0, true);
               }

               if (this.autoSwapSetting.get() == AutoCityPlus.swapMode.normal) {
                  this.pickSwap();
               }

               if ((Boolean)this.rotate.get()) {
                  Rotations.rotate(Rotations.getYaw(this.blockPosTarget), Rotations.getPitch(this.blockPosTarget));
                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, this.blockPosTarget, class_2350.field_11036));
               } else {
                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, this.blockPosTarget, class_2350.field_11036));
               }

               if ((Boolean)this.swing.get()) {
                  this.mc.field_1724.method_6104(class_1268.field_5808);
               }

               this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, this.blockPosTarget, class_2350.field_11036));
               this.shouldCount = true;
               this.antiSpam = true;
            }

            if (this.breakTimer >= this.delay()) {
               if (!(Boolean)this.instant.get()) {
                  this.pickSwap();
               } else if (this.instantTimer >= (Integer)this.instantDelay.get()) {
                  this.pickSwap();
                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, this.blockPosTarget, class_2350.field_11036));
                  this.instantTimer = 0;
                  return;
               }

               if (this.breakTimer >= this.delay() + 3) {
                  this.toggle();
               }
            }

         }
      }
   }

   private int delay() {
      int slot;
      class_1799 pick;
      int eff;
      if (this.netheritePick.isHotbar()) {
         slot = this.netheritePick.getSlot();
         pick = this.mc.field_1724.method_31548().method_5438(slot);
         eff = class_1890.method_8225(class_1893.field_9131, pick);
         switch(eff) {
         case 0:
            return 170;
         case 1:
            return 140;
         case 2:
            return 110;
         case 3:
            return 80;
         case 4:
            return 60;
         case 5:
            return 45;
         }
      }

      if (this.diamondPick.isHotbar()) {
         slot = this.diamondPick.getSlot();
         pick = this.mc.field_1724.method_31548().method_5438(slot);
         eff = class_1890.method_8225(class_1893.field_9131, pick);
         switch(eff) {
         case 0:
            return 190;
         case 1:
            return 151;
         case 2:
            return 118;
         case 3:
            return 85;
         case 4:
            return 61;
         case 5:
            return 46;
         }
      }

      if (this.ironPick.isHotbar()) {
         slot = this.ironPick.getSlot();
         pick = this.mc.field_1724.method_31548().method_5438(slot);
         eff = class_1890.method_8225(class_1893.field_9131, pick);
         switch(eff) {
         case 0:
            return 836;
         case 1:
            return 627;
         case 2:
            return 457;
         case 3:
            return 315;
         case 4:
            return 220;
         case 5:
            return 160;
         }
      }

      return 0;
   }

   private void pickSwap() {
      if ((Boolean)this.autoSwap.get()) {
         int slot;
         if (this.netheritePick.isHotbar()) {
            slot = this.netheritePick.getSlot();
            this.mc.field_1724.method_31548().field_7545 = slot;
         } else if (this.diamondPick.isHotbar()) {
            slot = this.diamondPick.getSlot();
            this.mc.field_1724.method_31548().field_7545 = slot;
         } else if (this.ironPick.isHotbar()) {
            slot = this.ironPick.getSlot();
            this.mc.field_1724.method_31548().field_7545 = slot;
         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.blockPosTarget != null && (Boolean)this.render.get() && this.target != null && !this.mc.field_1724.method_31549().field_7477) {
         event.renderer.box((class_2338)this.blockPosTarget, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if (this.blockPosTarget != null && (Boolean)this.renderProgress.get() && this.target != null && !this.mc.field_1724.method_31549().field_7477) {
         Vec3 pos = new Vec3((double)this.blockPosTarget.method_10263() + 0.5D, (double)this.blockPosTarget.method_10264() + 0.5D, (double)this.blockPosTarget.method_10260() + 0.5D);
         if (NametagUtils.to2D(pos, (Double)this.progressScale.get())) {
            NametagUtils.begin(pos);
            TextRenderer.get().begin(1.0D, false, true);
            String progress = Math.round((float)(100 * this.breakTimer)) / this.delay() + "%";
            if (Math.round((float)(100 * this.breakTimer)) / this.delay() >= 100) {
               progress = "☛ ☑ ☚";
            }

            TextRenderer.get().render(progress, -TextRenderer.get().getWidth(progress) / 2.0D, 0.0D, (Color)this.progressColor.get());
            TextRenderer.get().end();
            NametagUtils.end();
         }

      }
   }

   public static enum swapMode {
      normal,
      smart;

      // $FF: synthetic method
      private static AutoCityPlus.swapMode[] $values() {
         return new AutoCityPlus.swapMode[]{normal, smart};
      }
   }
}
