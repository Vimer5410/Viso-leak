package meteordevelopment.meteorclient.systems.modules.viso;

import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IBox;
import meteordevelopment.meteorclient.mixininterface.IRaycastContext;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.Vec3;
import meteordevelopment.meteorclient.utils.player.DamageUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1794;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1831;
import net.minecraft.class_1832;
import net.minecraft.class_1834;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class ModeCrystalV2 extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final SettingGroup sgFacePlace;
   private final SettingGroup sgBreak;
   private final SettingGroup sgignore;
   private final SettingGroup sgRender;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> predictMovement;
   private final Setting<Boolean> ignoreTerrain;
   private final Setting<Boolean> smartDel;
   private final Setting<Double> minDamage;
   private final Setting<Double> maxDamage;
   private final Setting<ModeCrystalV2.AutoSwitchMode> autoSwitch;
   private final Setting<Boolean> rotate;
   private final Setting<ModeCrystalV2.YawStepMode> yawStepMode;
   private final Setting<Double> yawSteps;
   private final Setting<Boolean> antiSuicide;
   private final Setting<Boolean> doPlace;
   private final Setting<Integer> placeDelay;
   private final Setting<Boolean> doPlacetwo;
   private final Setting<Double> placeRange;
   private final Setting<Double> placeWallsRange;
   private final Setting<Boolean> placement112;
   private final Setting<ModeCrystalV2.SupportMode> support;
   private final Setting<Integer> supportDelay;
   private final Setting<Boolean> facePlace;
   private final Setting<Double> facePlaceHealth;
   private final Setting<Double> facePlaceDurability;
   private final Setting<Boolean> facePlaceArmor;
   private final Setting<Keybind> forceFacePlace;
   private final Setting<Boolean> doBreak;
   private final Setting<Integer> breakDelay;
   private final Setting<Boolean> smartDelay;
   private final Setting<Integer> switchDelay;
   private final Setting<Double> breakRange;
   private final Setting<Double> breakWallsRange;
   private final Setting<Boolean> onlyBreakOwn;
   private final Setting<Integer> breakAttempts;
   private final Setting<Integer> ticksExisted;
   private final Setting<Integer> attackFrequency;
   private final Setting<Boolean> fastBreak;
   private final Setting<Boolean> antiWeakness;
   private final Setting<Boolean> smartDela;
   private final Setting<Boolean> antifriend;
   private final Setting<Boolean> antiown;
   private final Setting<Boolean> ignoritems;
   private final Setting<Boolean> renderSwing;
   private final Setting<Boolean> render;
   private final Setting<Boolean> renderBreak;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderDamageText;
   private final Setting<Double> damageTextScale;
   private final Setting<Integer> renderTime;
   private final Setting<Integer> renderBreakTime;
   private int breakTimer;
   private int placeTimer;
   private int switchTimer;
   private int ticksPassed;
   private final List<class_1657> targets;
   private final class_243 vec3d;
   private final class_243 playerEyePos;
   private final Vec3 vec3;
   private final class_2339 blockPos;
   private final class_238 box;
   private final class_243 vec3dRayTraceEnd;
   private class_3959 raycastContext;
   private final IntSet placedCrystals;
   private boolean placing;
   private int placingTimer;
   private final class_2339 placingCrystalBlockPos;
   private final IntSet removed;
   private final Int2IntMap attemptedBreaks;
   private final Int2IntMap waitingToExplode;
   private int attacks;
   private double serverYaw;
   private class_1657 bestTarget;
   private double bestTargetDamage;
   private int bestTargetTimer;
   private boolean didRotateThisTick;
   private boolean isLastRotationPos;
   private final class_243 lastRotationPos;
   private double lastYaw;
   private double lastPitch;
   private int lastRotationTimer;
   private int renderTimer;
   private int breakRenderTimer;
   private final class_2339 renderPos;
   private final class_2339 breakRenderPos;
   private double renderDamage;

   public ModeCrystalV2() {
      super(Categories.NewCombat, "Mode-Drystal-V2", "Automatically places and attacks crystals.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgPlace = this.settings.createGroup("Place");
      this.sgFacePlace = this.settings.createGroup("Face Place");
      this.sgBreak = this.settings.createGroup("Break");
      this.sgignore = this.settings.createGroup("ignore");
      this.sgRender = this.settings.createGroup("Render");
      this.targetRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("Range in which to target players.")).defaultValue(9.0D).min(0.0D).sliderMax(16.0D).build());
      this.predictMovement = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("predict-movement")).description("Predicts target movement.")).defaultValue(true)).build());
      this.ignoreTerrain = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-terrain")).description("Completely ignores terrain if it can be blown up by end crystals.")).defaultValue(true)).build());
      this.smartDel = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Anti-Fake")).description("")).defaultValue(true)).build());
      this.minDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-dmg")).description("Minimum damage the crystal needs to deal to your target.")).defaultValue(3.5D).min(0.0D).build());
      this.maxDamage = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("max-self-dmg")).description("Maximum damage crystals can deal to yourself.")).defaultValue(6.0D).range(0.0D, 36.0D).sliderMax(36.0D).build());
      this.autoSwitch = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("auto-switch")).description("Switches to crystals in your hotbar once a target is found.")).defaultValue(ModeCrystalV2.AutoSwitchMode.Silent)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates server-side towards the crystals being hit/placed.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      EnumSetting.Builder var10002 = (EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("yaw-steps-mode")).description("When to run the yaw steps check.")).defaultValue(ModeCrystalV2.YawStepMode.Break);
      Setting var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawStepMode = var10001.add(((EnumSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgGeneral;
      DoubleSetting.Builder var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("yaw-steps")).description("Maximum number of degrees its allowed to rotate in one tick.")).defaultValue(180.0D).range(1.0D, 180.0D);
      var10003 = this.rotate;
      Objects.requireNonNull(var10003);
      this.yawSteps = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.antiSuicide = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-suicide")).description("Will not place and break crystals if they will kill you.")).defaultValue(true)).build());
      this.doPlace = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("crystal-place")).description("If the CA should place crystals.")).defaultValue(true)).build());
      this.placeDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("per-tick")).description("The delay in ticks to wait to place a crystal after it's exploded.")).defaultValue(7)).min(0).sliderMax(20).build());
      this.doPlacetwo = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-delay")).description("If the CA should place crystals.")).defaultValue(true)).build());
      this.placeRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-range")).description("Range in which to place crystals.")).defaultValue(6.0D).min(0.0D).sliderMax(6.0D).build());
      this.placeWallsRange = this.sgPlace.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("place-walls-range")).description("Range in which to place crystals when behind blocks.")).defaultValue(6.0D).min(0.0D).sliderMax(6.0D).build());
      this.placement112 = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("1.12-placement")).description("Uses 1.12 crystal placement.")).defaultValue(false)).build());
      this.support = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("support")).description("Places a support block in air if no other position have been found.")).defaultValue(ModeCrystalV2.SupportMode.Disabled)).build());
      this.supportDelay = this.sgPlace.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("support-delay")).description("Delay in ticks after placing support block.")).defaultValue(1)).min(0).visible(() -> {
         return this.support.get() != ModeCrystalV2.SupportMode.Disabled;
      })).build());
      this.facePlace = this.sgFacePlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place")).description("Will face-place when target is below a certain health or armor durability threshold.")).defaultValue(true)).build());
      var10001 = this.sgFacePlace;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-health")).description("The health the target has to be at to start face placing.")).defaultValue(20.0D).min(1.0D).sliderMin(1.0D).sliderMax(36.0D);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceHealth = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("face-place-durability")).description("The durability threshold percentage to be able to face-place.")).defaultValue(2.0D).min(1.0D).sliderMin(1.0D).sliderMax(100.0D);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceDurability = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      var10001 = this.sgFacePlace;
      BoolSetting.Builder var2 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("face-place-missing-armor")).description("Automatically starts face placing when a target misses a piece of armor.")).defaultValue(false);
      var10003 = this.facePlace;
      Objects.requireNonNull(var10003);
      this.facePlaceArmor = var10001.add(((BoolSetting.Builder)var2.visible(var10003::get)).build());
      this.forceFacePlace = this.sgFacePlace.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("force-face-place")).description("Starts face place when this button is pressed.")).defaultValue(Keybind.none())).build());
      this.doBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("If the CA should break crystals.")).defaultValue(true)).build());
      this.breakDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("per-tick")).description("The delay in ticks to wait to break a crystal after it's placed.")).defaultValue(0)).min(0).sliderMax(20).build());
      this.smartDelay = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("smart-delay")).description("Only breaks crystals when the target can receive damage.")).defaultValue(true)).build());
      this.switchDelay = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("The delay in ticks to wait to break a crystal after switching hotbar slot.")).defaultValue(0)).min(0).build());
      this.breakRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-range")).description("Range in which to break crystals.")).defaultValue(6.0D).min(0.0D).sliderMax(6.0D).build());
      this.breakWallsRange = this.sgBreak.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("break-walls-range")).description("Range in which to break crystals when behind blocks.")).defaultValue(6.0D).min(0.0D).sliderMax(6.0D).build());
      this.onlyBreakOwn = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-own")).description("Only breaks own crystals.")).defaultValue(false)).build());
      this.breakAttempts = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-attempts")).description("How many times to hit a crystal before stopping to target it.")).defaultValue(1)).sliderMin(1).sliderMax(5).build());
      this.ticksExisted = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("ticks")).description("Amount of ticks a crystal needs to have lived for it to be attacked by CrystalAura.")).defaultValue(0)).min(0).build());
      this.attackFrequency = this.sgBreak.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("attack-frequency")).description("Maximum hits to do per second.")).defaultValue(30)).min(1).sliderRange(1, 30).build());
      this.fastBreak = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("fast-break")).description("Ignores break delay and tries to break the crystal as soon as it's spawned in the world.")).defaultValue(true)).build());
      this.antiWeakness = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-weakness")).description("Switches to tools with high enough damage to explode the crystal with weakness effect.")).defaultValue(true)).build());
      this.smartDela = this.sgBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Bypass-crystal")).description("")).defaultValue(true)).build());
      this.antifriend = this.sgignore.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-friend-pop")).description("Dont break crystal, when you can pop friend.")).defaultValue(false)).build());
      this.antiown = this.sgignore.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-own-pop")).description("Dont break crystal, when you can pop you.")).defaultValue(false)).build());
      this.ignoritems = this.sgignore.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-items")).description("Ignore the items and placing block into it.")).defaultValue(false)).build());
      this.renderSwing = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing")).description("Renders hand swinging client side.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders a block overlay over the block the crystals are being placed on.")).defaultValue(true)).build());
      this.renderBreak = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("break")).description("Renders a block overlay over the block the crystals are broken on.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the block overlay.")).defaultValue(new SettingColor(142, 6, 255, 41))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the block overlay.")).defaultValue(new SettingColor(153, 0, 255, 255))).build());
      this.renderDamageText = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("damage")).description("Renders crystal damage text in the block overlay.")).defaultValue(true)).build());
      var10001 = this.sgRender;
      var1 = ((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("damage-scale")).description("How big the damage text should be.")).defaultValue(1.25D).min(1.0D).sliderMax(4.0D);
      var10003 = this.renderDamageText;
      Objects.requireNonNull(var10003);
      this.damageTextScale = var10001.add(((DoubleSetting.Builder)var1.visible(var10003::get)).build());
      this.renderTime = this.sgRender.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("render-time")).description("How long to render for.")).defaultValue(10)).min(0).sliderMax(20).build());
      var10001 = this.sgRender;
      IntSetting.Builder var3 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("break-time")).description("How long to render breaking for.")).defaultValue(13)).min(0).sliderMax(20);
      var10003 = this.renderBreak;
      Objects.requireNonNull(var10003);
      this.renderBreakTime = var10001.add(((IntSetting.Builder)var3.visible(var10003::get)).build());
      this.targets = new ArrayList();
      this.vec3d = new class_243(0.0D, 0.0D, 0.0D);
      this.playerEyePos = new class_243(0.0D, 0.0D, 0.0D);
      this.vec3 = new Vec3();
      this.blockPos = new class_2339();
      this.box = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      this.vec3dRayTraceEnd = new class_243(0.0D, 0.0D, 0.0D);
      this.placedCrystals = new IntOpenHashSet();
      this.placingCrystalBlockPos = new class_2339();
      this.removed = new IntOpenHashSet();
      this.attemptedBreaks = new Int2IntOpenHashMap();
      this.waitingToExplode = new Int2IntOpenHashMap();
      this.lastRotationPos = new class_243(0.0D, 0.0D, 0.0D);
      this.renderPos = new class_2339();
      this.breakRenderPos = new class_2339();
   }

   public void onActivate() {
      this.breakTimer = 0;
      this.placeTimer = 0;
      this.ticksPassed = 0;
      this.raycastContext = new class_3959(new class_243(0.0D, 0.0D, 0.0D), new class_243(0.0D, 0.0D, 0.0D), class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      this.placing = false;
      this.placingTimer = 0;
      this.attacks = 0;
      this.serverYaw = (double)this.mc.field_1724.method_36454();
      this.bestTargetDamage = 0.0D;
      this.bestTargetTimer = 0;
      this.lastRotationTimer = this.getLastRotationStopDelay();
      this.renderTimer = 0;
      this.breakRenderTimer = 0;
   }

   public void onDeactivate() {
      this.targets.clear();
      this.placedCrystals.clear();
      this.attemptedBreaks.clear();
      this.waitingToExplode.clear();
      this.removed.clear();
      this.bestTarget = null;
   }

   private int getLastRotationStopDelay() {
      return Math.max(10, (Integer)this.placeDelay.get() / 2 + (Integer)this.breakDelay.get() / 2 + 10);
   }

   @EventHandler(
      priority = 100
   )
   private void onPreTick(TickEvent.Pre event) {
      this.didRotateThisTick = false;
      ++this.lastRotationTimer;
      if (this.placing) {
         if (this.placingTimer > 0) {
            --this.placingTimer;
         } else {
            this.placing = false;
         }
      }

      if (this.ticksPassed < 20) {
         ++this.ticksPassed;
      } else {
         this.ticksPassed = 0;
         this.attacks = 0;
      }

      if (this.bestTargetTimer > 0) {
         --this.bestTargetTimer;
      }

      this.bestTargetDamage = 0.0D;
      if (this.breakTimer > 0) {
         --this.breakTimer;
      }

      if (this.placeTimer > 0) {
         --this.placeTimer;
      }

      if (this.switchTimer > 0) {
         --this.switchTimer;
      }

      if (this.renderTimer > 0) {
         --this.renderTimer;
      }

      if (this.breakRenderTimer > 0) {
         --this.breakRenderTimer;
      }

      IntIterator it = this.waitingToExplode.keySet().iterator();

      while(it.hasNext()) {
         int id = it.nextInt();
         int ticks = this.waitingToExplode.get(id);
         if (ticks > 3) {
            it.remove();
            this.removed.remove(id);
         } else {
            this.waitingToExplode.put(id, ticks + 1);
         }
      }

      ((IVec3d)this.playerEyePos).set(this.mc.field_1724.method_19538().field_1352, this.mc.field_1724.method_19538().field_1351 + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_19538().field_1350);
      this.findTargets();
      if (this.targets.size() > 0) {
         if (!this.didRotateThisTick) {
            this.doBreak();
         }

         if (!this.didRotateThisTick) {
            this.doPlace();
         }
      }

   }

   @EventHandler(
      priority = -866
   )
   private void onPreTickLast(TickEvent.Pre event) {
      if ((Boolean)this.rotate.get() && this.lastRotationTimer < this.getLastRotationStopDelay() && !this.didRotateThisTick) {
         Rotations.rotate(this.isLastRotationPos ? Rotations.getYaw(this.lastRotationPos) : this.lastYaw, this.isLastRotationPos ? Rotations.getPitch(this.lastRotationPos) : this.lastPitch, -100, (Runnable)null);
      }

   }

   @EventHandler
   private void onEntityAdded(EntityAddedEvent event) {
      if (event.entity instanceof class_1511) {
         if (this.placing && event.entity.method_24515().equals(this.placingCrystalBlockPos)) {
            this.placing = false;
            this.placingTimer = 0;
            this.placedCrystals.add(event.entity.method_5628());
         }

         if ((Boolean)this.fastBreak.get() && !this.didRotateThisTick && this.attacks < (Integer)this.attackFrequency.get()) {
            double damage = this.getBreakDamage(event.entity, true);
            if (damage > (Double)this.minDamage.get()) {
               this.doBreak(event.entity);
            }
         }

      }
   }

   @EventHandler
   private void onEntityRemoved(EntityRemovedEvent event) {
      if (event.entity instanceof class_1511) {
         this.placedCrystals.remove(event.entity.method_5628());
         this.removed.remove(event.entity.method_5628());
         this.waitingToExplode.remove(event.entity.method_5628());
      }

   }

   private void setRotation(boolean isPos, class_243 pos, double yaw, double pitch) {
      this.didRotateThisTick = true;
      this.isLastRotationPos = isPos;
      if (isPos) {
         ((IVec3d)this.lastRotationPos).set(pos.field_1352, pos.field_1351, pos.field_1350);
      } else {
         this.lastYaw = yaw;
         this.lastPitch = pitch;
      }

      this.lastRotationTimer = 0;
   }

   private void doBreak() {
      if ((Boolean)this.doBreak.get() && this.breakTimer <= 0 && this.switchTimer <= 0 && this.attacks < (Integer)this.attackFrequency.get()) {
         double bestDamage = 0.0D;
         class_1297 crystal = null;
         Iterator var4 = this.mc.field_1687.method_18112().iterator();

         while(var4.hasNext()) {
            class_1297 entity = (class_1297)var4.next();
            double damage = this.getBreakDamage(entity, true);
            if (damage > bestDamage) {
               bestDamage = damage;
               crystal = entity;
            }
         }

         if (crystal != null) {
            this.doBreak(crystal);
         }

      }
   }

   private double getBreakDamage(class_1297 entity, boolean checkCrystalAge) {
      if (!(entity instanceof class_1511)) {
         return 0.0D;
      } else if ((Boolean)this.onlyBreakOwn.get() && !this.placedCrystals.contains(entity.method_5628())) {
         return 0.0D;
      } else if (this.removed.contains(entity.method_5628())) {
         return 0.0D;
      } else if (this.attemptedBreaks.get(entity.method_5628()) > (Integer)this.breakAttempts.get()) {
         return 0.0D;
      } else if (checkCrystalAge && entity.field_6012 < (Integer)this.ticksExisted.get()) {
         return 0.0D;
      } else if (this.isOutOfRange(entity.method_19538(), entity.method_24515(), false)) {
         return 0.0D;
      } else {
         this.blockPos.method_10101(entity.method_24515()).method_10100(0, -1, 0);
         double selfDamage = DamageUtils.crystalDamage(this.mc.field_1724, entity.method_19538(), (Boolean)this.predictMovement.get(), this.blockPos, (Boolean)this.ignoreTerrain.get());
         if (!(selfDamage > (Double)this.maxDamage.get()) && (!(Boolean)this.antiSuicide.get() || !(selfDamage >= (double)EntityUtils.getTotalHealth(this.mc.field_1724)))) {
            double damage = this.getDamageToTargets(entity.method_19538(), this.blockPos, true, false);
            boolean facePlaced = (Boolean)this.facePlace.get() && this.shouldFacePlace(entity.method_24515()) || ((Keybind)this.forceFacePlace.get()).isPressed();
            return !facePlaced && damage < (Double)this.minDamage.get() ? 0.0D : damage;
         } else {
            return 0.0D;
         }
      }
   }

   private void doBreak(class_1297 crystal) {
      if ((Boolean)this.antiWeakness.get()) {
         class_1293 weakness = this.mc.field_1724.method_6112(class_1294.field_5911);
         class_1293 strength = this.mc.field_1724.method_6112(class_1294.field_5910);
         if (weakness != null && (strength == null || strength.method_5578() <= weakness.method_5578()) && !this.isValidWeaknessItem(this.mc.field_1724.method_6047())) {
            if (!InvUtils.swap(InvUtils.findInHotbar(this::isValidWeaknessItem).slot(), false)) {
               return;
            }

            this.switchTimer = 1;
            return;
         }
      }

      boolean attacked = true;
      if ((Boolean)this.rotate.get()) {
         double yaw = Rotations.getYaw(crystal);
         double pitch = Rotations.getPitch(crystal, Target.Feet);
         if (this.doYawSteps(yaw, pitch)) {
            this.setRotation(true, crystal.method_19538(), 0.0D, 0.0D);
            Rotations.rotate(yaw, pitch, 50, () -> {
               this.attackCrystal(crystal);
            });
            this.breakTimer = (Integer)this.breakDelay.get();
         } else {
            attacked = false;
         }
      } else {
         this.attackCrystal(crystal);
         this.breakTimer = (Integer)this.breakDelay.get();
      }

      if (attacked) {
         this.removed.add(crystal.method_5628());
         this.attemptedBreaks.put(crystal.method_5628(), this.attemptedBreaks.get(crystal.method_5628()) + 1);
         this.waitingToExplode.put(crystal.method_5628(), 0);
         this.breakRenderPos.method_10101(crystal.method_24515().method_10074());
         this.breakRenderTimer = (Integer)this.renderBreakTime.get();
      }

   }

   private boolean isValidWeaknessItem(class_1799 itemStack) {
      if (itemStack.method_7909() instanceof class_1831 && !(itemStack.method_7909() instanceof class_1794)) {
         class_1832 material = ((class_1831)itemStack.method_7909()).method_8022();
         return material == class_1834.field_8930 || material == class_1834.field_22033;
      } else {
         return false;
      }
   }

   private void attackCrystal(class_1297 entity) {
      this.mc.field_1724.field_3944.method_2883(class_2824.method_34206(entity, this.mc.field_1724.method_5715()));
      class_1268 hand = InvUtils.findInHotbar(class_1802.field_8301).getHand();
      if (hand == null) {
         hand = class_1268.field_5808;
      }

      if ((Boolean)this.renderSwing.get()) {
         this.mc.field_1724.method_6104(hand);
      } else {
         this.mc.method_1562().method_2883(new class_2879(hand));
      }

      ++this.attacks;
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private void doPlace() {
      if ((Boolean)this.doPlace.get() && this.placeTimer <= 0) {
         if (InvUtils.findInHotbar(class_1802.field_8301).found()) {
            if (this.autoSwitch.get() != ModeCrystalV2.AutoSwitchMode.None || this.mc.field_1724.method_6079().method_7909() == class_1802.field_8301 || this.mc.field_1724.method_6047().method_7909() == class_1802.field_8301) {
               Iterator var1 = this.mc.field_1687.method_18112().iterator();

               class_1297 entity;
               do {
                  if (!var1.hasNext()) {
                     AtomicDouble bestDamage = new AtomicDouble(0.0D);
                     AtomicReference<class_2339> bestBlockPos = new AtomicReference(new class_2339());
                     AtomicBoolean isSupport = new AtomicBoolean(this.support.get() != ModeCrystalV2.SupportMode.Disabled);
                     BlockIterator.register((int)Math.ceil((Double)this.placeRange.get()), (int)Math.ceil((Double)this.placeRange.get()), (bp, blockState) -> {
                        boolean hasBlock = blockState.method_27852(class_2246.field_9987) || blockState.method_27852(class_2246.field_10540);
                        if (hasBlock || isSupport.get() && blockState.method_26207().method_15800()) {
                           this.blockPos.method_10103(bp.method_10263(), bp.method_10264() + 1, bp.method_10260());
                           if (this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                              if ((Boolean)this.placement112.get()) {
                                 this.blockPos.method_10100(0, 1, 0);
                                 if (!this.mc.field_1687.method_8320(this.blockPos).method_26215()) {
                                    return;
                                 }
                              }

                              ((IVec3d)this.vec3d).set((double)bp.method_10263() + 0.5D, (double)(bp.method_10264() + 1), (double)bp.method_10260() + 0.5D);
                              this.blockPos.method_10101(bp).method_10100(0, 1, 0);
                              if (!this.isOutOfRange(this.vec3d, this.blockPos, true)) {
                                 double selfDamage = DamageUtils.crystalDamage(this.mc.field_1724, this.vec3d, (Boolean)this.predictMovement.get(), bp, (Boolean)this.ignoreTerrain.get());
                                 if (!(selfDamage > (Double)this.maxDamage.get()) && (!(Boolean)this.antiSuicide.get() || !(selfDamage >= (double)EntityUtils.getTotalHealth(this.mc.field_1724)))) {
                                    double damage = this.getDamageToTargets(this.vec3d, bp, false, !hasBlock && this.support.get() == ModeCrystalV2.SupportMode.Fast);
                                    boolean facePlaced = (Boolean)this.facePlace.get() && this.shouldFacePlace(this.blockPos) || ((Keybind)this.forceFacePlace.get()).isPressed();
                                    if (facePlaced || !(damage < (Double)this.minDamage.get())) {
                                       double x = (double)bp.method_10263();
                                       double y = (double)(bp.method_10264() + 1);
                                       double z = (double)bp.method_10260();
                                       ((IBox)this.box).set(x, y, z, x + 1.0D, y + (double)((Boolean)this.placement112.get() ? 1 : 2), z + 1.0D);
                                       if (!this.intersectsWithEntities(this.box)) {
                                          if (damage > bestDamage.get() || isSupport.get() && hasBlock) {
                                             bestDamage.set(damage);
                                             ((class_2339)bestBlockPos.get()).method_10101(bp);
                                          }

                                          if (hasBlock) {
                                             isSupport.set(false);
                                          }

                                       }
                                    }
                                 }
                              }
                           }
                        }
                     });
                     BlockIterator.after(() -> {
                        if (bestDamage.get() != 0.0D) {
                           class_3965 result = this.getPlaceInfo((class_2338)bestBlockPos.get());
                           ((IVec3d)this.vec3d).set((double)result.method_17777().method_10263() + 0.5D + (double)result.method_17780().method_10163().method_10263() * 1.0D / 2.0D, (double)result.method_17777().method_10264() + 0.5D + (double)result.method_17780().method_10163().method_10264() * 1.0D / 2.0D, (double)result.method_17777().method_10260() + 0.5D + (double)result.method_17780().method_10163().method_10260() * 1.0D / 2.0D);
                           if ((Boolean)this.rotate.get()) {
                              double yaw = Rotations.getYaw(this.vec3d);
                              double pitch = Rotations.getPitch(this.vec3d);
                              if (this.yawStepMode.get() == ModeCrystalV2.YawStepMode.Break || this.doYawSteps(yaw, pitch)) {
                                 this.setRotation(true, this.vec3d, 0.0D, 0.0D);
                                 Rotations.rotate(yaw, pitch, 50, () -> {
                                    this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null);
                                 });
                                 this.placeTimer += (Integer)this.placeDelay.get();
                              }
                           } else {
                              this.placeCrystal(result, bestDamage.get(), isSupport.get() ? (class_2338)bestBlockPos.get() : null);
                              this.placeTimer += (Integer)this.placeDelay.get();
                           }

                        }
                     });
                     return;
                  }

                  entity = (class_1297)var1.next();
               } while(!(this.getBreakDamage(entity, false) > 0.0D));

            }
         }
      }
   }

   private class_3965 getPlaceInfo(class_2338 blockPos) {
      ((IVec3d)this.vec3d).set(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318() + (double)this.mc.field_1724.method_18381(this.mc.field_1724.method_18376()), this.mc.field_1724.method_23321());
      class_2350[] var2 = class_2350.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         class_2350 side = var2[var4];
         ((IVec3d)this.vec3dRayTraceEnd).set((double)blockPos.method_10263() + 0.5D + (double)side.method_10163().method_10263() * 0.5D, (double)blockPos.method_10264() + 0.5D + (double)side.method_10163().method_10264() * 0.5D, (double)blockPos.method_10260() + 0.5D + (double)side.method_10163().method_10260() * 0.5D);
         ((IRaycastContext)this.raycastContext).set(this.vec3d, this.vec3dRayTraceEnd, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
         class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
         if (result != null && result.method_17783() == class_240.field_1332 && result.method_17777().equals(blockPos)) {
            return result;
         }
      }

      class_2350 side = (double)blockPos.method_10264() > this.vec3d.field_1351 ? class_2350.field_11033 : class_2350.field_11036;
      return new class_3965(this.vec3d, side, blockPos, false);
   }

   private void placeCrystal(class_3965 result, double damage, class_2338 supportBlock) {
      class_1792 targetItem = supportBlock == null ? class_1802.field_8301 : class_1802.field_8281;
      FindItemResult item = InvUtils.findInHotbar(targetItem);
      if (item.found()) {
         int prevSlot = this.mc.field_1724.method_31548().field_7545;
         if (this.autoSwitch.get() != ModeCrystalV2.AutoSwitchMode.None && !item.isOffhand()) {
            InvUtils.swap(item.slot(), false);
         }

         class_1268 hand = item.getHand();
         if (hand != null) {
            if (supportBlock == null) {
               this.mc.field_1724.field_3944.method_2883(new class_2885(hand, result));
               if ((Boolean)this.renderSwing.get()) {
                  this.mc.field_1724.method_6104(hand);
               } else {
                  this.mc.method_1562().method_2883(new class_2879(hand));
               }

               this.placing = true;
               this.placingTimer = 4;
               this.placingCrystalBlockPos.method_10101(result.method_17777()).method_10100(0, 1, 0);
               this.renderTimer = (Integer)this.renderTime.get();
               this.renderPos.method_10101(result.method_17777());
               this.renderDamage = damage;
            } else {
               BlockUtils.place(supportBlock, item, false, 0, (Boolean)this.renderSwing.get(), true, false);
               this.placeTimer += (Integer)this.supportDelay.get();
               if ((Integer)this.supportDelay.get() == 0) {
                  this.placeCrystal(result, damage, (class_2338)null);
               }
            }

            if (this.autoSwitch.get() == ModeCrystalV2.AutoSwitchMode.Silent) {
               InvUtils.swap(prevSlot, false);
            }

         }
      }
   }

   @EventHandler
   private void onPacketSent(PacketEvent.Sent event) {
      if (event.packet instanceof class_2828) {
         this.serverYaw = (double)((class_2828)event.packet).method_12271((float)this.serverYaw);
      }

   }

   public boolean doYawSteps(double targetYaw, double targetPitch) {
      targetYaw = class_3532.method_15338(targetYaw) + 180.0D;
      double serverYaw = class_3532.method_15338(this.serverYaw) + 180.0D;
      if (distanceBetweenAngles(serverYaw, targetYaw) <= (Double)this.yawSteps.get()) {
         return true;
      } else {
         double delta = Math.abs(targetYaw - serverYaw);
         double yaw = this.serverYaw;
         if (serverYaw < targetYaw) {
            if (delta < 180.0D) {
               yaw += (Double)this.yawSteps.get();
            } else {
               yaw -= (Double)this.yawSteps.get();
            }
         } else if (delta < 180.0D) {
            yaw -= (Double)this.yawSteps.get();
         } else {
            yaw += (Double)this.yawSteps.get();
         }

         this.setRotation(false, (class_243)null, yaw, targetPitch);
         Rotations.rotate(yaw, targetPitch, -100, (Runnable)null);
         return false;
      }
   }

   private static double distanceBetweenAngles(double alpha, double beta) {
      double phi = Math.abs(beta - alpha) % 360.0D;
      return phi > 180.0D ? 360.0D - phi : phi;
   }

   private boolean shouldFacePlace(class_2338 crystal) {
      Iterator var2 = this.targets.iterator();

      label51:
      while(true) {
         class_1657 target;
         class_2338 pos;
         do {
            do {
               do {
                  if (!var2.hasNext()) {
                     return false;
                  }

                  target = (class_1657)var2.next();
                  pos = target.method_24515();
               } while(crystal.method_10264() != pos.method_10264() + 1);
            } while(Math.abs(pos.method_10263() - crystal.method_10263()) > 1);
         } while(Math.abs(pos.method_10260() - crystal.method_10260()) > 1);

         if ((double)EntityUtils.getTotalHealth(target) <= (Double)this.facePlaceHealth.get()) {
            return true;
         }

         Iterator var5 = target.method_5661().iterator();

         class_1799 itemStack;
         label49:
         do {
            do {
               if (!var5.hasNext()) {
                  continue label51;
               }

               itemStack = (class_1799)var5.next();
               if (itemStack != null && !itemStack.method_7960()) {
                  continue label49;
               }
            } while(!(Boolean)this.facePlaceArmor.get());

            return true;
         } while(!((double)(itemStack.method_7936() - itemStack.method_7919()) / (double)itemStack.method_7936() * 100.0D <= (Double)this.facePlaceDurability.get()));

         return true;
      }
   }

   private boolean isOutOfRange(class_243 vec3d, class_2338 blockPos, boolean place) {
      ((IRaycastContext)this.raycastContext).set(this.playerEyePos, vec3d, class_3960.field_17558, class_242.field_1348, this.mc.field_1724);
      class_3965 result = this.mc.field_1687.method_17742(this.raycastContext);
      boolean behindWall = result == null || !result.method_17777().equals(blockPos);
      double distance = this.mc.field_1724.method_19538().method_1022(vec3d);
      return distance > behindWall ? (Double)(place ? this.placeWallsRange : this.breakWallsRange).get() : (Double)(place ? this.placeRange : this.breakRange).get();
   }

   private class_1657 getNearestTarget() {
      class_1657 nearestTarget = null;
      double nearestDistance = Double.MAX_VALUE;
      Iterator var4 = this.targets.iterator();

      while(var4.hasNext()) {
         class_1657 target = (class_1657)var4.next();
         double distance = target.method_5858(this.mc.field_1724);
         if (distance < nearestDistance) {
            nearestTarget = target;
            nearestDistance = distance;
         }
      }

      return nearestTarget;
   }

   private double getDamageToTargets(class_243 vec3d, class_2338 obsidianPos, boolean breaking, boolean fast) {
      double damage = 0.0D;
      if (fast) {
         class_1657 target = this.getNearestTarget();
         if (!(Boolean)this.smartDelay.get() || !breaking || target.field_6235 <= 0) {
            damage = DamageUtils.crystalDamage(target, vec3d, (Boolean)this.predictMovement.get(), obsidianPos, (Boolean)this.ignoreTerrain.get());
         }
      } else {
         Iterator var11 = this.targets.iterator();

         while(true) {
            class_1657 target;
            do {
               if (!var11.hasNext()) {
                  return damage;
               }

               target = (class_1657)var11.next();
            } while((Boolean)this.smartDelay.get() && breaking && target.field_6235 > 0);

            double dmg = DamageUtils.crystalDamage(target, vec3d, (Boolean)this.predictMovement.get(), obsidianPos, (Boolean)this.ignoreTerrain.get());
            if (dmg > this.bestTargetDamage) {
               this.bestTarget = target;
               this.bestTargetDamage = dmg;
               this.bestTargetTimer = 10;
            }

            damage += dmg;
         }
      }

      return damage;
   }

   public String getInfoString() {
      return this.bestTarget != null && this.bestTargetTimer > 0 ? this.bestTarget.method_7334().getName() : null;
   }

   private void findTargets() {
      this.targets.clear();
      Iterator var1 = this.mc.field_1687.method_18456().iterator();

      class_1657 player;
      while(var1.hasNext()) {
         player = (class_1657)var1.next();
         if (!player.method_31549().field_7477 && player != this.mc.field_1724 && !player.method_29504() && player.method_5805() && Friends.get().shouldAttack(player) && (double)player.method_5739(this.mc.field_1724) <= (Double)this.targetRange.get()) {
            this.targets.add(player);
         }
      }

      var1 = FakePlayerManager.getPlayers().iterator();

      while(var1.hasNext()) {
         player = (class_1657)var1.next();
         if (!player.method_29504() && player.method_5805() && Friends.get().shouldAttack(player) && (double)player.method_5739(this.mc.field_1724) <= (Double)this.targetRange.get()) {
            this.targets.add(player);
         }
      }

   }

   private boolean intersectsWithEntities(class_238 box) {
      return EntityUtils.intersectsWithEntity(box, (entity) -> {
         return !entity.method_7325() && !this.removed.contains(entity.method_5628());
      });
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.renderTimer > 0 && (Boolean)this.render.get()) {
         event.renderer.box((class_2338)this.renderPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }

      if (this.breakRenderTimer > 0 && (Boolean)this.renderBreak.get() && !this.mc.field_1687.method_8320(this.breakRenderPos).method_26215()) {
         int preSideA = ((SettingColor)this.sideColor.get()).a;
         SettingColor var10000 = (SettingColor)this.sideColor.get();
         var10000.a -= 20;
         ((SettingColor)this.sideColor.get()).validate();
         int preLineA = ((SettingColor)this.lineColor.get()).a;
         var10000 = (SettingColor)this.lineColor.get();
         var10000.a -= 20;
         ((SettingColor)this.lineColor.get()).validate();
         event.renderer.box((class_2338)this.breakRenderPos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         ((SettingColor)this.sideColor.get()).a = preSideA;
         ((SettingColor)this.lineColor.get()).a = preLineA;
      }

   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if ((Boolean)this.render.get() && this.renderTimer > 0 && (Boolean)this.renderDamageText.get()) {
         this.vec3.set((double)this.renderPos.method_10263() + 0.5D, (double)this.renderPos.method_10264() + 0.5D, (double)this.renderPos.method_10260() + 0.5D);
         if (NametagUtils.to2D(this.vec3, (Double)this.damageTextScale.get())) {
            NametagUtils.begin(this.vec3);
            TextRenderer.get().begin(1.0D, false, true);
            String text = String.format("%.1f", this.renderDamage);
            double w = TextRenderer.get().getWidth(text) / 2.0D;
            TextRenderer.get().render(text, -w, 0.0D, (Color)this.lineColor.get(), true);
            TextRenderer.get().end();
            NametagUtils.end();
         }

      }
   }

   public static enum AutoSwitchMode {
      Normal,
      Silent,
      None;

      // $FF: synthetic method
      private static ModeCrystalV2.AutoSwitchMode[] $values() {
         return new ModeCrystalV2.AutoSwitchMode[]{Normal, Silent, None};
      }
   }

   public static enum YawStepMode {
      Break,
      All;

      // $FF: synthetic method
      private static ModeCrystalV2.YawStepMode[] $values() {
         return new ModeCrystalV2.YawStepMode[]{Break, All};
      }
   }

   public static enum SupportMode {
      Disabled,
      Accurate,
      Fast;

      // $FF: synthetic method
      private static ModeCrystalV2.SupportMode[] $values() {
         return new ModeCrystalV2.SupportMode[]{Disabled, Accurate, Fast};
      }
   }
}
