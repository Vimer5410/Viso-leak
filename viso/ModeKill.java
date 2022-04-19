package meteordevelopment.meteorclient.systems.modules.viso;

import baritone.api.BaritoneAPI;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1829;
import net.minecraft.class_1934;
import net.minecraft.class_2868;

public class ModeKill extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgTargeting;
   private final SettingGroup sgDelay;
   private final Setting<ModeKill.Weapon> weapon;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> onlyOnClick;
   private final Setting<Boolean> onlyWhenLook;
   private final Setting<Boolean> randomTeleport;
   private final Setting<ModeKill.RotationMode> rotation;
   private final Setting<Double> hitChance;
   private final Setting<Boolean> pauseOnCombat;
   private final Setting<Object2BooleanMap<class_1299<?>>> entities;
   private final Setting<Double> range;
   private final Setting<Double> wallsRange;
   private final Setting<SortPriority> priority;
   private final Setting<Integer> maxTargets;
   private final Setting<Boolean> babies;
   private final Setting<Boolean> nametagged;
   private final Setting<Boolean> smartDelay;
   private final Setting<Integer> hitDelay;
   private final Setting<Boolean> randomDelayEnabled;
   private final Setting<Integer> randomDelayMax;
   private final Setting<Integer> switchDelay;
   private final List<class_1297> targets;
   private int hitDelayTimer;
   private int randomDelayTimer;
   private int switchTimer;
   private boolean wasPathing;

   public ModeKill() {
      super(Categories.NewCombat, "Mode-Sword", "Kill Aura but its post-tick instead of pre-tick.");
      this.sgGeneral = this.settings.createGroup("General");
      this.sgTargeting = this.settings.createGroup("Targeting");
      this.sgDelay = this.settings.createGroup("Delay");
      this.weapon = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("weapon")).description("Only attacks an entity when a specified item is in your hand.")).defaultValue(ModeKill.Weapon.Both)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Switches to your selected weapon when attacking the target.")).defaultValue(false)).build());
      this.onlyOnClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-click")).description("Only attacks when hold left click.")).defaultValue(false)).build());
      this.onlyWhenLook = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-when-look")).description("Only attacks when you are looking at the entity.")).defaultValue(false)).build());
      this.randomTeleport = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random-teleport")).description("Randomly teleport around the target")).defaultValue(false)).visible(() -> {
         return !(Boolean)this.onlyWhenLook.get();
      })).build());
      this.rotation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotate")).description("Determines when you should rotate towards the target.")).defaultValue(ModeKill.RotationMode.Always)).build());
      this.hitChance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("hit-chance")).description("The probability of your hits landing.")).defaultValue(100.0D).min(0.0D).max(100.0D).sliderMax(100.0D).build());
      this.pauseOnCombat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-combat")).description("Freezes Baritone temporarily until you are finished attacking the entity.")).defaultValue(true)).build());
      this.entities = this.sgTargeting.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).defaultValue(new Object2BooleanOpenHashMap(0))).onlyAttackable().build());
      this.range = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to attack it.")).defaultValue(4.5D).min(0.0D).sliderMax(6.0D).build());
      this.wallsRange = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("The maximum range the entity can be attacked through walls.")).defaultValue(3.5D).min(0.0D).sliderMax(6.0D).build());
      this.priority = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.LowestHealth)).build());
      this.maxTargets = this.sgTargeting.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-targets")).description("How many entities to target at once.")).defaultValue(1)).min(1).sliderMin(1).sliderMax(5).build());
      this.babies = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("babies")).description("Whether or not to attack baby variants of the entity.")).defaultValue(true)).build());
      this.nametagged = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("nametagged")).description("Whether or not to attack mobs with a name tag.")).defaultValue(false)).build());
      this.smartDelay = this.sgDelay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-delay")).description("Uses the vanilla cooldown to attack entities.")).defaultValue(true)).build());
      this.hitDelay = this.sgDelay.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hit-delay")).description("How fast you hit the entity in ticks.")).defaultValue(0)).min(0).sliderMax(60).visible(() -> {
         return !(Boolean)this.smartDelay.get();
      })).build());
      this.randomDelayEnabled = this.sgDelay.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("random-delay-enabled")).description("Adds a random delay between hits to attempt to bypass anti-cheats.")).defaultValue(false)).visible(() -> {
         return !(Boolean)this.smartDelay.get();
      })).build());
      this.randomDelayMax = this.sgDelay.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("random-delay-max")).description("The maximum value for random delay.")).defaultValue(4)).min(0).sliderMax(20).visible(() -> {
         return (Boolean)this.randomDelayEnabled.get() && !(Boolean)this.smartDelay.get();
      })).build());
      this.switchDelay = this.sgDelay.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("How many ticks to wait before hitting an entity after switching hotbar slots.")).defaultValue(0)).min(0).sliderMax(10).build());
      this.targets = new ArrayList();
   }

   public void onDeactivate() {
      this.hitDelayTimer = 0;
      this.randomDelayTimer = 0;
      this.targets.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_5805() && PlayerUtils.getGameMode() != class_1934.field_9219) {
         TargetUtils.getList(this.targets, this::entityCheck, (SortPriority)this.priority.get(), (Integer)this.maxTargets.get());
         if (this.targets.isEmpty()) {
            if (this.wasPathing) {
               BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("resume");
               this.wasPathing = false;
            }

         } else {
            if ((Boolean)this.pauseOnCombat.get() && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing() && !this.wasPathing) {
               BaritoneAPI.getProvider().getPrimaryBaritone().getCommandManager().execute("pause");
               this.wasPathing = true;
            }

            class_1297 primary = (class_1297)this.targets.get(0);
            if (this.rotation.get() == ModeKill.RotationMode.Always) {
               this.rotate(primary, (Runnable)null);
            }

            if (!(Boolean)this.onlyOnClick.get() || this.mc.field_1690.field_1886.method_1434()) {
               if ((Boolean)this.onlyWhenLook.get()) {
                  primary = this.mc.field_1692;
                  if (primary == null) {
                     return;
                  }

                  if (!this.entityCheck(primary)) {
                     return;
                  }

                  this.targets.clear();
                  this.targets.add(primary);
               }

               if ((Boolean)this.autoSwitch.get()) {
                  FindItemResult weaponResult = InvUtils.findInHotbar((itemStack) -> {
                     class_1792 item = itemStack.method_7909();
                     boolean var10000;
                     switch((ModeKill.Weapon)this.weapon.get()) {
                     case Axe:
                        var10000 = item instanceof class_1743;
                        break;
                     case Sword:
                        var10000 = item instanceof class_1829;
                        break;
                     case Both:
                        var10000 = item instanceof class_1743 || item instanceof class_1829;
                        break;
                     default:
                        var10000 = true;
                     }

                     return var10000;
                  });
                  InvUtils.swap(weaponResult.getSlot(), false);
               }

               if (this.itemInHand()) {
                  if (this.delayCheck()) {
                     this.targets.forEach(this::attack);
                  }

                  if ((Boolean)this.randomTeleport.get() && !(Boolean)this.onlyWhenLook.get()) {
                     this.mc.field_1724.method_5814(primary.method_23317() + this.randomOffset(), primary.method_23318(), primary.method_23321() + this.randomOffset());
                  }

               }
            }
         }
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private double randomOffset() {
      return Math.random() * 4.0D - 2.0D;
   }

   private boolean entityCheck(class_1297 entity) {
      if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
         if ((!(entity instanceof class_1309) || !((class_1309)entity).method_29504()) && entity.method_5805()) {
            if (PlayerUtils.distanceTo(entity) > (Double)this.range.get()) {
               return false;
            } else if (!((Object2BooleanMap)this.entities.get()).getBoolean(entity.method_5864())) {
               return false;
            } else if (!(Boolean)this.nametagged.get() && entity.method_16914()) {
               return false;
            } else if (!PlayerUtils.canSeeEntity(entity) && PlayerUtils.distanceTo(entity) > (Double)this.wallsRange.get()) {
               return false;
            } else {
               if (entity instanceof class_1657) {
                  if (((class_1657)entity).method_7337()) {
                     return false;
                  }

                  if (!Friends.get().shouldAttack((class_1657)entity)) {
                     return false;
                  }
               }

               return !(entity instanceof class_1429) || (Boolean)this.babies.get() || !((class_1429)entity).method_6109();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean delayCheck() {
      if (this.switchTimer > 0) {
         --this.switchTimer;
         return false;
      } else if ((Boolean)this.smartDelay.get()) {
         return this.mc.field_1724.method_7261(0.5F) >= 1.0F;
      } else if (this.hitDelayTimer >= 0) {
         --this.hitDelayTimer;
         return false;
      } else {
         this.hitDelayTimer = (Integer)this.hitDelay.get();
         if ((Boolean)this.randomDelayEnabled.get()) {
            if (this.randomDelayTimer > 0) {
               --this.randomDelayTimer;
               return false;
            }

            this.randomDelayTimer = (int)Math.round(Math.random() * (double)(Integer)this.randomDelayMax.get());
         }

         return true;
      }
   }

   private void attack(class_1297 target) {
      if (!(Math.random() > (Double)this.hitChance.get() / 100.0D)) {
         if (this.rotation.get() == ModeKill.RotationMode.OnHit) {
            this.rotate(target, () -> {
               this.hitEntity(target);
            });
         } else {
            this.hitEntity(target);
         }

      }
   }

   private void hitEntity(class_1297 target) {
      this.mc.field_1761.method_2918(this.mc.field_1724, target);
      this.mc.field_1724.method_6104(class_1268.field_5808);
   }

   private void rotate(class_1297 target, Runnable callback) {
      Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body), callback);
   }

   private boolean itemInHand() {
      boolean var10000;
      switch((ModeKill.Weapon)this.weapon.get()) {
      case Axe:
         var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743;
         break;
      case Sword:
         var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
         break;
      case Both:
         var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743 || this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
         break;
      default:
         var10000 = true;
      }

      return var10000;
   }

   public class_1297 getTarget() {
      return !this.targets.isEmpty() ? (class_1297)this.targets.get(0) : null;
   }

   public String getInfoString() {
      if (!this.targets.isEmpty()) {
         EntityUtils.getName(this.getTarget());
      }

      return null;
   }

   public static enum Weapon {
      Sword,
      Axe,
      Both,
      Any;

      // $FF: synthetic method
      private static ModeKill.Weapon[] $values() {
         return new ModeKill.Weapon[]{Sword, Axe, Both, Any};
      }
   }

   public static enum RotationMode {
      Always,
      OnHit,
      None;

      // $FF: synthetic method
      private static ModeKill.RotationMode[] $values() {
         return new ModeKill.RotationMode[]{Always, OnHit, None};
      }
   }
}
