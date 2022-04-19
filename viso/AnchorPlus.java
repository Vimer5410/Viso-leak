package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Step;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.utils.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_3532;
import net.minecraft.class_2338.class_2339;

public class AnchorPlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgToggle;
   private final Setting<Integer> maxHeight;
   private final Setting<Integer> minPitch;
   private final Setting<Boolean> cancelMove;
   private final Setting<Boolean> pull;
   private final Setting<Double> pullSpeed;
   private final Setting<Boolean> webs;
   private final Setting<Boolean> whileForward;
   private final Setting<Boolean> whileJumping;
   private final Setting<Integer> pullDelay;
   private final Setting<Boolean> onGround;
   private final Setting<Boolean> turnOffStep;
   private final Setting<Boolean> turnOffStrafe;
   private final Setting<Boolean> turnOffSpeed;
   private final class_2339 blockPos;
   private boolean wasInHole;
   private boolean foundHole;
   private int holeX;
   private int holeZ;
   private boolean cancelJump;
   public boolean controlMovement;
   public double deltaX;
   public double deltaZ;
   private Timer inAirTime;
   boolean didJump;
   boolean pausing;

   public AnchorPlus() {
      super(Categories.NewCombat, "Anchor-V2", "Helps you get into holes by stopping your movement completely over a hole.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgToggle = this.settings.createGroup("Toggles");
      this.maxHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-height")).description("The maximum height Anchor will work at.")).defaultValue(10)).min(0).max(255).sliderMax(20).build());
      this.minPitch = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-pitch")).description("The minimum pitch at which anchor will work.")).defaultValue(-90)).min(-90).max(90).sliderMin(-90).sliderMax(90).build());
      this.cancelMove = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("cancel-jump-in-hole")).description("Prevents you from jumping when Anchor is active and Min Pitch is met.")).defaultValue(false)).build());
      this.pull = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pull")).description("The pull strength of Anchor.")).defaultValue(false)).build());
      this.pullSpeed = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("pull-speed")).description("How fast to pull towards the hole in blocks per second.")).defaultValue(0.3D).min(0.0D).sliderMax(5.0D).build());
      this.webs = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pull into webs")).description("Will also pull into webs.")).defaultValue(false)).build());
      this.whileForward = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("while-forward")).description("Should anchor+ be active forward key is held.")).defaultValue(true)).build());
      this.whileJumping = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("while-jumping")).description("Should anchor be active while jump key held.")).defaultValue(true)).build());
      this.pullDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Pull-Delay")).description("Amount of ticks anchor+ should wait before pulling you after you jump.")).defaultValue(14)).min(1).sliderMax(60).visible(() -> {
         return !(Boolean)this.whileJumping.get();
      })).build());
      this.onGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pull-On-Ground")).description("If the pull delay should be reset when u land on the ground.")).defaultValue(true)).visible(() -> {
         return !(Boolean)this.whileJumping.get();
      })).build());
      this.turnOffStep = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Turn-off-Step")).description("Turns off Step on activation.")).defaultValue(false)).build());
      this.turnOffStrafe = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Turn-off-strafe+")).description("Turns off strafe+ on activation.")).defaultValue(false)).build());
      this.turnOffSpeed = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Turn-off-Speed")).description("Turns off Speed on activation.")).defaultValue(false)).build());
      this.blockPos = new class_2339();
      this.inAirTime = new Timer();
      this.didJump = false;
      this.pausing = false;
   }

   public void onActivate() {
      this.didJump = false;
      this.wasInHole = false;
      this.holeX = this.holeZ = 0;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      this.cancelJump = this.foundHole && (Boolean)this.cancelMove.get() && this.mc.field_1724.method_36455() >= (float)(Integer)this.minPitch.get();
      Modules modules = Modules.get();
      if ((Boolean)this.turnOffStep.get() && ((Step)modules.get(Step.class)).isActive()) {
         ((Step)modules.get(Step.class)).toggle();
      }

      if ((Boolean)this.turnOffStrafe.get() && ((StrafePlus)modules.get(StrafePlus.class)).isActive()) {
         ((StrafePlus)modules.get(StrafePlus.class)).toggle();
      }

      if ((Boolean)this.turnOffSpeed.get() && ((Speed)modules.get(Speed.class)).isActive()) {
         ((Speed)modules.get(Speed.class)).toggle();
      }

   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      if (!(Boolean)this.whileJumping.get()) {
         if (this.mc.field_1690.field_1903.method_1434()) {
            this.inAirTime.reset();
            this.didJump = true;
         }

         if (this.inAirTime.passedTicks((long)(Integer)this.pullDelay.get()) && this.didJump || (Boolean)this.onGround.get() && this.mc.field_1724.method_24828()) {
            this.didJump = false;
         }
      }

      if (!(Boolean)this.whileForward.get()) {
         if (this.mc.field_1690.field_1894.method_1434()) {
            this.pausing = true;
         } else {
            this.pausing = false;
         }
      } else {
         this.pausing = false;
      }

      if (!this.didJump && !this.pausing) {
         this.controlMovement = false;
         int x = class_3532.method_15357(this.mc.field_1724.method_23317());
         int y = class_3532.method_15357(this.mc.field_1724.method_23318());
         int z = class_3532.method_15357(this.mc.field_1724.method_23321());
         if (this.isHole(x, y, z)) {
            this.wasInHole = true;
            this.holeX = x;
            this.holeZ = z;
         } else if (!this.wasInHole || this.holeX != x || this.holeZ != z) {
            if (this.wasInHole) {
               this.wasInHole = false;
            }

            if (!(this.mc.field_1724.method_36455() < (float)(Integer)this.minPitch.get())) {
               this.foundHole = false;
               double holeX = 0.0D;
               double holeZ = 0.0D;

               for(int i = 0; i < (Integer)this.maxHeight.get(); ++i) {
                  --y;
                  if (y <= 0 || !this.isAir(x, y, z)) {
                     break;
                  }

                  if (this.isHole(x, y, z) && ((Boolean)this.webs.get() || !this.isWeb(x, y, z))) {
                     this.foundHole = true;
                     holeX = (double)x + 0.5D;
                     holeZ = (double)z + 0.5D;
                     break;
                  }
               }

               if (this.foundHole) {
                  this.controlMovement = true;
                  this.deltaX = Utils.clamp(holeX - this.mc.field_1724.method_23317(), -0.05D, 0.05D);
                  this.deltaZ = Utils.clamp(holeZ - this.mc.field_1724.method_23321(), -0.05D, 0.05D);
                  ((IVec3d)this.mc.field_1724.method_18798()).set(this.deltaX, this.mc.field_1724.method_18798().field_1351 - ((Boolean)this.pull.get() ? (Double)this.pullSpeed.get() : 0.0D), this.deltaZ);
               }

            }
         }
      }
   }

   private boolean isHole(int x, int y, int z) {
      return this.isHoleBlock(x, y - 1, z) && this.isHoleBlock(x + 1, y, z) && this.isHoleBlock(x - 1, y, z) && this.isHoleBlock(x, y, z + 1) && this.isHoleBlock(x, y, z - 1);
   }

   private boolean isHoleBlock(int x, int y, int z) {
      this.blockPos.method_10103(x, y, z);
      class_2248 block = this.mc.field_1687.method_8320(this.blockPos).method_26204();
      return block == class_2246.field_9987 || block == class_2246.field_10540 || block == class_2246.field_23152 || block == class_2246.field_22109 || block == class_2246.field_22423 || block == class_2246.field_10443 || block == class_2246.field_22108 || block == class_2246.field_10535 || block == class_2246.field_10414 || block == class_2246.field_10105;
   }

   private boolean isAir(int x, int y, int z) {
      this.blockPos.method_10103(x, y, z);
      return !((AbstractBlockAccessor)this.mc.field_1687.method_8320(this.blockPos).method_26204()).isCollidable();
   }

   private boolean isWeb(int x, int y, int z) {
      return this.isWebBlock(x, y, z);
   }

   private boolean isWebBlock(int x, int y, int z) {
      this.blockPos.method_10103(x, y, z);
      class_2248 block = this.mc.field_1687.method_8320(this.blockPos).method_26204();
      return block == class_2246.field_10343;
   }
}
