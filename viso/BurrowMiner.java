package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.BPlusEntityUtils;
import meteordevelopment.meteorclient.utils.BPlusPlayerUtils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_2846.class_2847;

public class BurrowMiner extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> swing;
   private final Setting<Boolean> selfToggle;
   private final Setting<Boolean> chatInfo;
   private class_1657 target;
   private class_2338 blockPosTarget;
   private boolean sentMessage;

   public BurrowMiner() {
      super(Categories.NewCombat, "burrow-miner", "Automatically mines target's burrow.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The radius in which players get targeted.")).defaultValue(5.0D).min(0.0D).sliderMax(6.0D).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Auto switches to a pickaxe when AutoCity is enabled.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates you towards the city block.")).defaultValue(true)).build());
      this.swing = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Renders your swing client-side.")).defaultValue(false)).build());
      this.selfToggle = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("self-toggle")).description("Automatically toggles off after activation.")).defaultValue(true)).build());
      this.chatInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat-info")).description("Sends a message when it is trying to burrow mine someone.")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         class_1657 search = TargetUtils.getPlayerTarget((Double)this.targetRange.get(), SortPriority.LowestDistance);
         if (search != this.target) {
            this.sentMessage = false;
         }

         this.target = search;
      }

      if (TargetUtils.isBadTarget(this.target, (Double)this.targetRange.get())) {
         this.target = null;
         this.blockPosTarget = null;
         if ((Boolean)this.selfToggle.get()) {
            this.toggle();
         }

      } else {
         if (BPlusEntityUtils.isBurrowed(this.target)) {
            this.blockPosTarget = this.target.method_24515();
         } else if (this.blockPosTarget == null) {
            if ((Boolean)this.selfToggle.get()) {
               this.error("No burrow block found... disabling.", new Object[0]);
               this.toggle();
            }

            this.target = null;
            return;
         }

         if (BPlusPlayerUtils.distanceTo(this.blockPosTarget) > (double)this.mc.field_1761.method_2904() && (Boolean)this.selfToggle.get()) {
            this.error("Burrow block out of reach... disabling.", new Object[0]);
            this.toggle();
         } else {
            if (!this.sentMessage && (Boolean)this.chatInfo.get()) {
               this.info("Attempting to burrow mine %s.", new Object[]{this.target.method_5820()});
               this.sentMessage = true;
            }

            FindItemResult pickaxe = InvUtils.find((itemStack) -> {
               return itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024;
            });
            if (!pickaxe.isHotbar()) {
               if ((Boolean)this.selfToggle.get()) {
                  this.error("No pickaxe found... disabling.", new Object[0]);
                  this.toggle();
               }

            } else {
               if ((Boolean)this.autoSwitch.get()) {
                  InvUtils.swap(pickaxe.getSlot(), false);
               }

               if ((Boolean)this.rotate.get()) {
                  Rotations.rotate(Rotations.getYaw(this.blockPosTarget), Rotations.getPitch(this.blockPosTarget), () -> {
                     this.mine(this.blockPosTarget);
                  });
               } else {
                  this.mine(this.blockPosTarget);
               }

               if ((Boolean)this.selfToggle.get()) {
                  this.toggle();
               }

            }
         }
      }
   }

   private void mine(class_2338 blockPos) {
      this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
      if ((Boolean)this.swing.get()) {
         this.mc.field_1724.method_6104(class_1268.field_5808);
      } else {
         this.mc.field_1724.field_3944.method_2883(new class_2879(class_1268.field_5808));
      }

      this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }
}
