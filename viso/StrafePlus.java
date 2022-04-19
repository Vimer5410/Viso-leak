package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Objects;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1294;

public class StrafePlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgSpeed;
   private final SettingGroup sgFriction;
   private final SettingGroup sgPotions;
   private final SettingGroup sgAC;
   public final Setting<Double> groundTimer;
   public final Setting<Double> airTimer;
   private final Setting<Boolean> autoJump;
   private final Setting<StrafePlus.JumpWhen> jumpIf;
   private final Setting<Boolean> lowHop;
   private final Setting<Double> height;
   private final Setting<Boolean> sprintBool;
   private final Setting<Boolean> TPSSync;
   private final Setting<Boolean> sneak;
   private final Setting<Double> sneakspeedVal;
   private final Setting<Boolean> ground;
   private final Setting<Double> groundspeedVal;
   private final Setting<Boolean> air;
   private final Setting<Double> airspeedVal;
   private final Setting<Double> airFriction;
   private final Setting<Double> waterFriction;
   private final Setting<Double> lavaFriction;
   private final Setting<Boolean> useJump;
   private final Setting<Boolean> useSpeed;
   private final Setting<Boolean> useSlow;
   private final Setting<Boolean> hungerCheck;
   public final Setting<Boolean> onFallFlying;
   private float FinalSpeed;

   public StrafePlus() {
      super(Categories.NewCombat, "Strafe-V2", "Increase speed and control");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.sgSpeed = this.settings.createGroup("_Speed Value_");
      this.sgFriction = this.settings.createGroup("_Frictions_");
      this.sgPotions = this.settings.createGroup("_Potions_");
      this.sgAC = this.settings.createGroup("_AntiCheat_");
      this.groundTimer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Ground-Timer")).description("Ground timer override.")).defaultValue(1.0D).sliderMin(0.01D).sliderMax(10.0D).build());
      this.airTimer = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Air-Timer")).description("Air timer override.")).defaultValue(1.088D).sliderMin(0.01D).sliderMax(10.0D).build());
      this.autoJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Auto Jump")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Jump When")).defaultValue(StrafePlus.JumpWhen.Walking);
      Setting var10003 = this.autoJump;
      Objects.requireNonNull(var10003);
      this.jumpIf = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Low Hop")).defaultValue(false);
      var10003 = this.autoJump;
      Objects.requireNonNull(var10003);
      this.lowHop = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      this.height = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Low Hop Height")).defaultValue(0.37D).sliderMax(1.0D).visible(() -> {
         return (Boolean)this.autoJump.get() && (Boolean)this.lowHop.get();
      })).build());
      this.sprintBool = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Auto Sprint")).defaultValue(true)).build());
      this.TPSSync = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS Sync")).defaultValue(false)).build());
      this.sneak = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use sneak speed")).defaultValue(false)).build());
      var10001 = this.sgSpeed;
      DoubleSetting.Builder var2 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Sneak speed")).defaultValue(0.85D).sliderRange(0.0D, 2.0D).range(0.0D, 2.0D);
      var10003 = this.sneak;
      Objects.requireNonNull(var10003);
      this.sneakspeedVal = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      this.ground = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use ground speed")).defaultValue(true)).build());
      var10001 = this.sgSpeed;
      var2 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Ground speed")).defaultValue(1.15D).sliderRange(0.0D, 2.0D).range(0.0D, 2.0D);
      var10003 = this.ground;
      Objects.requireNonNull(var10003);
      this.groundspeedVal = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      this.air = this.sgSpeed.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Use air speed")).defaultValue(true)).build());
      var10001 = this.sgSpeed;
      var2 = ((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Air Speed")).defaultValue(0.95D).sliderRange(0.0D, 2.0D).range(0.0D, 2.0D);
      var10003 = this.air;
      Objects.requireNonNull(var10003);
      this.airspeedVal = var10001.add(((DoubleSetting.Builder)var2.visible(var10003::get)).build());
      this.airFriction = this.sgFriction.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Air Friction")).defaultValue(0.02199999988079071D).sliderRange(0.0D, 1.0D).range(0.0D, 1.0D).build());
      this.waterFriction = this.sgFriction.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Water Friction")).defaultValue(0.10999999940395355D).sliderRange(0.0D, 1.0D).range(0.0D, 1.0D).build());
      this.lavaFriction = this.sgFriction.add(((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Lava Friction")).defaultValue(0.4650000035762787D).sliderRange(0.0D, 1.0D).range(0.0D, 1.0D).build());
      this.useJump = this.sgPotions.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Apply Jump Boost Effect")).defaultValue(true)).build());
      this.useSpeed = this.sgPotions.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Apply Speed Effect")).defaultValue(true)).build());
      this.useSlow = this.sgPotions.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Apply Slowness Effect")).defaultValue(true)).build());
      this.hungerCheck = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Hunger check")).description("Pauses strafe when hunger reaches 3 or less drumsticks")).defaultValue(true)).build());
      this.onFallFlying = this.sgAC.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("on-fall-flying")).description("Uses strafe+ on fall flying.")).defaultValue(false)).build());
   }

   private boolean jump() {
      boolean var10000;
      switch((StrafePlus.JumpWhen)this.jumpIf.get()) {
      case Sprinting:
         var10000 = this.mc.field_1724.method_5624() && (this.mc.field_1724.field_6250 != 0.0F || this.mc.field_1724.field_6212 != 0.0F);
         break;
      case Walking:
         var10000 = this.mc.field_1724.field_6250 != 0.0F || this.mc.field_1724.field_6212 != 0.0F;
         break;
      default:
         var10000 = false;
      }

      return var10000;
   }

   public void onDeactivate() {
      ((Timer)Modules.get().get(Timer.class)).setOverride(1.0D);
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (Utils.canUpdate()) {
         if ((Boolean)this.autoJump.get()) {
            if (!this.mc.field_1724.method_24828() || this.mc.field_1724.method_5715() || !this.jump()) {
               return;
            }

            if ((Boolean)this.lowHop.get()) {
               ((IVec3d)this.mc.field_1724.method_18798()).setY((Double)this.height.get());
            } else {
               this.mc.field_1724.method_6043();
            }
         }

         if ((this.mc.field_1690.field_1894.method_1434() || this.mc.field_1690.field_1881.method_1434() || this.mc.field_1690.field_1913.method_1434() || this.mc.field_1690.field_1849.method_1434()) && this.mc.field_1724.method_24828() && (Boolean)this.autoJump.get()) {
            if (this.mc.field_1724.method_6088().containsValue(this.mc.field_1724.method_6112(class_1294.field_5913)) && (Boolean)this.useJump.get()) {
               this.mc.field_1724.method_18800(this.mc.field_1724.method_18798().field_1352, (double)((float)(this.mc.field_1724.method_6112(class_1294.field_5913).method_5578() + 1) * 0.1F) + (Double)this.height.get(), this.mc.field_1724.method_18798().field_1350);
            } else {
               this.mc.field_1724.method_18800(this.mc.field_1724.method_18798().field_1352, (Double)this.height.get(), this.mc.field_1724.method_18798().field_1350);
            }
         }

      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.mc.field_1724.field_3913.field_3905 != 0.0F | this.mc.field_1724.field_3913.field_3907 != 0.0F) {
         if ((Boolean)this.sprintBool.get()) {
            this.mc.field_1724.method_5728(true);
         }

         if ((Boolean)this.hungerCheck.get() && this.mc.field_1724.method_7344().method_7586() <= 6) {
            return;
         }

         if (!(Boolean)this.sneak.get() && this.mc.field_1724.method_5715()) {
            return;
         }

         if (!(Boolean)this.ground.get() && this.mc.field_1724.method_24828()) {
            return;
         }

         if (!(Boolean)this.air.get() && !this.mc.field_1724.method_24828()) {
            return;
         }

         if (!(Boolean)this.onFallFlying.get() && this.mc.field_1724.method_6128()) {
            return;
         }

         if (this.mc.field_1724.method_24828()) {
            ((Timer)Modules.get().get(Timer.class)).setOverride(PlayerUtils.isMoving() ? (Double)this.groundTimer.get() : 1.0D);
         } else {
            ((Timer)Modules.get().get(Timer.class)).setOverride(PlayerUtils.isMoving() ? (Double)this.airTimer.get() : 1.0D);
         }

         this.FinalSpeed *= this.frictionValues();
         double[] airV = this.getSpeedTransform(Math.max((Double)this.airspeedVal.get() / 2.5D * (double)this.FinalSpeed * (this.getDefaultSpeed() / 0.15321D), 0.15321D));
         double[] groundV = this.getSpeedTransform(this.getDefaultSpeed() * 1.8752039684093726D * (Double)this.groundspeedVal.get());
         double[] sneakV = this.getSpeedTransform(this.getDefaultSpeed() * 1.8752039684093726D * (Double)this.sneakspeedVal.get());
         if (!this.mc.field_1724.method_24828()) {
            this.mc.field_1724.method_18800(airV[0], this.mc.field_1724.method_18798().field_1351, airV[1]);
         } else {
            this.FinalSpeed = 1.0F;
            if (!this.mc.field_1724.method_5715()) {
               this.mc.field_1724.method_18800(groundV[0], this.mc.field_1724.method_18798().field_1351, groundV[1]);
            } else {
               this.mc.field_1724.method_18800(sneakV[0], this.mc.field_1724.method_18798().field_1351, sneakV[1]);
            }
         }
      }

   }

   public double[] getSpeedTransform(double speed) {
      float forward = this.mc.field_1724.field_3913.field_3905;
      float sideways = this.mc.field_1724.field_3913.field_3907;
      float yaw = this.mc.field_1724.field_5982 + (this.mc.field_1724.method_36454() - this.mc.field_1724.field_5982) * this.mc.method_1488();
      return getSpeedTransform(speed, forward, sideways, yaw);
   }

   public static double[] getSpeedTransform(double speed, float forwards, float sideways, float yawDegrees) {
      return getSpeedTransform(speed, forwards, sideways, Math.toRadians((double)yawDegrees));
   }

   public static double[] getSpeedTransform(double speed, float forwards, float sideways, double yaw) {
      if (forwards != 0.0F) {
         if (sideways > 0.0F) {
            yaw += forwards > 0.0F ? -0.7853981633974483D : 0.7853981633974483D;
         } else if (sideways < 0.0F) {
            yaw += forwards > 0.0F ? 0.7853981633974483D : -0.7853981633974483D;
         }

         sideways = 0.0F;
         if (forwards > 0.0F) {
            forwards = 1.0F;
         } else if (forwards < 0.0F) {
            forwards = -1.0F;
         }
      }

      ++yaw;
      return new double[]{(double)forwards * speed * Math.cos(yaw) + (double)sideways * speed * Math.sin(yaw), (double)forwards * speed * Math.sin(yaw) - (double)sideways * speed * Math.cos(yaw)};
   }

   private double getDefaultSpeed() {
      double baseSpeed = 0.15321D * (double)this.getTPSMatch();
      int amplifier;
      if (this.mc.field_1724.method_6059(class_1294.field_5904) && (Boolean)this.useSpeed.get()) {
         amplifier = this.mc.field_1724.method_6112(class_1294.field_5904).method_5578();
         baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
      }

      if (this.mc.field_1724.method_6059(class_1294.field_5909) && (Boolean)this.useSlow.get()) {
         amplifier = this.mc.field_1724.method_6112(class_1294.field_5909).method_5578();
         baseSpeed /= 1.0D + 0.2D * (double)(amplifier + 1);
      }

      return baseSpeed;
   }

   private float frictionValues() {
      float airF = 1.0F - ((Double)this.airFriction.get()).floatValue();
      float waterF = 1.0F - ((Double)this.waterFriction.get()).floatValue();
      float lavaF = 1.0F - ((Double)this.lavaFriction.get()).floatValue();
      if (this.mc.field_1724.method_5771()) {
         return lavaF;
      } else {
         return this.mc.field_1724.method_5869() ? waterF : airF;
      }
   }

   private float getTPSMatch() {
      return (Boolean)this.TPSSync.get() ? TickRate.INSTANCE.getTickRate() / 20.0F : 1.0F;
   }

   public static enum JumpWhen {
      Sprinting,
      Walking;

      // $FF: synthetic method
      private static StrafePlus.JumpWhen[] $values() {
         return new StrafePlus.JumpWhen[]{Sprinting, Walking};
      }
   }
}
