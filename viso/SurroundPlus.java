package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Step;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.utils.BPlusWorldUtils;
import meteordevelopment.meteorclient.utils.POPA;
import meteordevelopment.meteorclient.utils.Timer;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2620;
import net.minecraft.class_742;

public class SurroundPlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgToggle;
   private final SettingGroup sgModules;
   private final SettingGroup sgRender;
   private final Setting<Boolean> doubleHeight;
   private final Setting<Keybind> doubleHeightKeybind;
   private final Setting<SurroundPlus.Primary> primary;
   private final Setting<Integer> delay;
   private final Setting<Boolean> snap;
   private final Setting<Integer> centerDelay;
   private final Setting<Boolean> placeOnCrystal;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> air;
   private final Setting<Boolean> allBlocks;
   private final Setting<Boolean> notifyBreak;
   public final Setting<Boolean> ignoreOpenable;
   private final Setting<Boolean> onlyGround;
   private final Setting<Boolean> disableOnYChange;
   private final Setting<Boolean> disableOnLeaveHole;
   private final Setting<Boolean> surroundUp;
   public final Setting<Boolean> pauseAntiClick;
   private final Setting<Boolean> toggleStep;
   private final Setting<Boolean> toggleSpeed;
   private final Setting<Boolean> toggleStrafe;
   private final Setting<Boolean> toggleBack;
   private final Setting<Boolean> selfProtector;
   private final Setting<Boolean> render;
   private final Setting<Boolean> alwaysRender;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> lineTwoColor;
   private final Setting<SettingColor> sideTwoColor;
   public static SurroundPlus INSTANCE;
   private class_2338 lastPos;
   private int ticks;
   private boolean hasCentered;
   private boolean shouldExtra;
   private Timer onGroundCenter;
   private class_2338 prevBreakPos;
   private static final Timer surroundInstanceDelay = new Timer();
   int timeToStart;
   boolean doSnap;
   Modules modules;
   class_1657 prevBreakingPlayer;

   public static void setSurroundWait(int timeToStart) {
      INSTANCE.timeToStart = timeToStart;
   }

   public static void toggleCenter(boolean doSnap) {
      INSTANCE.doSnap = doSnap;
   }

   public SurroundPlus() {
      super(Categories.NewCombat, "Mode-Defender", "Defender you in blocks to prevent you from taking lots of damage.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgToggle = this.settings.createGroup("Toggle");
      this.sgModules = this.settings.createGroup("Modules");
      this.sgRender = this.settings.createGroup("Render");
      this.doubleHeight = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("double")).description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")).defaultValue(false)).build());
      this.doubleHeightKeybind = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("double-keybind")).description("Turns on double height.")).defaultValue(Keybind.fromKey(-1))).build());
      this.primary = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("block")).description("Primary block to use.")).defaultValue(SurroundPlus.Primary.Obsidian)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Tick")).description("Delay in ticks between placing blocks.")).defaultValue(0)).sliderMin(0).sliderMax(10).build());
      this.snap = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Center")).description("Will align you at the center of your hole when you turn this on.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = (IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Center Delay")).description("Delay in ticks before you get centered.");
      Setting var10003 = this.snap;
      Objects.requireNonNull(var10003);
      this.centerDelay = var10001.add(((IntSetting.Builder)((IntSetting.Builder)var10002.visible(var10003::get)).defaultValue(0)).sliderMin(0).sliderMax(10).build());
      this.placeOnCrystal = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore entities")).description("Will try to place even if there is an entity in its way.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Rotate")).description("Whether to rotate or not.")).defaultValue(false)).build());
      this.air = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Air-Place")).description("Whether to place blocks midair or not.")).defaultValue(true)).build());
      this.allBlocks = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Any-blastproof")).description("Will allow any blast proof block to be used.")).defaultValue(true)).build());
      this.notifyBreak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify")).description("Notifies you about who is breaking your surround.")).defaultValue(true)).build());
      this.ignoreOpenable = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Ignore-openable-blocks")).description("Ignores openable blocks when placing surround.")).defaultValue(true)).build());
      this.onlyGround = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Only On Ground")).description("Will not attempt to place while you are not standing on ground.")).defaultValue(false)).build());
      this.disableOnYChange = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-y-change")).description("Automatically disables when your y level (step, jumping, atc).")).defaultValue(false)).build());
      this.disableOnLeaveHole = this.sgToggle.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-hole-leave")).description("Automatically disables when you leave your hole (normal)")).defaultValue(true)).build());
      var10001 = this.sgToggle;
      BoolSetting.Builder var1 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround-Up")).description("helps you surround up")).defaultValue(true);
      var10003 = this.disableOnLeaveHole;
      Objects.requireNonNull(var10003);
      this.surroundUp = var10001.add(((BoolSetting.Builder)var1.visible(var10003::get)).build());
      this.pauseAntiClick = this.sgModules.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Pause-Anti-Click")).description("Pauses anti click when surround is enabled.")).defaultValue(false)).build());
      this.toggleStep = this.sgModules.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-step")).description("Toggles off step when activating surround.")).defaultValue(false)).build());
      this.toggleSpeed = this.sgModules.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-speed")).description("Toggles off speed when activating surround.")).defaultValue(false)).build());
      this.toggleStrafe = this.sgModules.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-strafe")).description("Toggles off strafe+ when activating surround.")).defaultValue(false)).build());
      this.toggleBack = this.sgModules.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle-back")).description("Toggles on speed or surround when turning off surround.")).defaultValue(false)).build());
      this.selfProtector = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("breaks-crystal")).description("Automatically breaks crystal near ur surround.")).defaultValue(true)).build());
      this.render = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("...")).defaultValue(true)).build());
      this.alwaysRender = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("always")).description(" ")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 0, 0))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 0))).build());
      this.lineTwoColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("perfect-line")).description("The second side color.")).defaultValue(new SettingColor(255, 0, 245, 255))).build());
      this.sideTwoColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("perfect-side")).description("The second side color.")).defaultValue(new SettingColor(238, 0, 255, 50))).build());
      this.lastPos = new class_2338(0, -100, 0);
      this.ticks = 0;
      this.hasCentered = false;
      this.onGroundCenter = new Timer();
      this.timeToStart = 0;
      this.doSnap = true;
      this.modules = Modules.get();
      this.prevBreakingPlayer = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.onGroundCenter.passedTicks((long)(Integer)this.centerDelay.get()) && (Boolean)this.snap.get() && this.doSnap && !this.hasCentered && this.mc.field_1724.method_24828()) {
         BPlusWorldUtils.snapPlayer(this.lastPos);
         this.hasCentered = true;
      }

      if (!this.hasCentered && !this.mc.field_1724.method_24828()) {
         this.onGroundCenter.reset();
      }

      class_2338 roundedPos = BPlusWorldUtils.roundBlockPos(this.mc.field_1724.method_19538());
      if ((Boolean)this.onlyGround.get() && !this.mc.field_1724.method_24828() && roundedPos.method_10264() <= this.lastPos.method_10264()) {
         this.lastPos = BPlusWorldUtils.roundBlockPos(this.mc.field_1724.method_19538());
      }

      if (surroundInstanceDelay.passedMillis((long)this.timeToStart) && (this.mc.field_1724.method_24828() || !(Boolean)this.onlyGround.get())) {
         if ((Integer)this.delay.get() != 0 && this.ticks++ % (Integer)this.delay.get() != 0) {
            return;
         }

         class_742 loc = this.mc.field_1724;
         class_2338 locRounded = BPlusWorldUtils.roundBlockPos(loc.method_19538());
         if (!this.lastPos.equals(loc.method_24828() ? locRounded : loc.method_24515())) {
            if ((Boolean)this.disableOnYChange.get() && this.mc.field_1724.field_6036 < this.mc.field_1724.method_23318() || (Boolean)this.onlyGround.get() || (Boolean)this.disableOnLeaveHole.get() && (!(loc.method_19538().field_1351 <= (double)this.lastPos.method_10264() + 1.5D) || (Math.floor(loc.method_19538().field_1352) != (double)this.lastPos.method_10263() || Math.floor(loc.method_19538().field_1350) != (double)this.lastPos.method_10260()) && !(loc.method_19538().field_1351 <= (double)this.lastPos.method_10264() + 0.75D) || !this.mc.field_1687.method_8320(this.lastPos).method_26207().method_15800() && loc.method_24515() != this.lastPos || (Boolean)this.surroundUp.get() && (this.mc.field_1690.field_1903.method_1434() || this.mc.field_1724.field_3913.field_3904) && (this.mc.field_1690.field_1913.method_1434() || this.mc.field_1724.field_3913.field_3908 || this.mc.field_1690.field_1849.method_1434() || this.mc.field_1724.field_3913.field_3906 || this.mc.field_1690.field_1894.method_1434() || this.mc.field_1724.field_3913.field_3910 || this.mc.field_1690.field_1881.method_1434() || this.mc.field_1724.field_3913.field_3909))) {
               this.doToggle();
               return;
            }

            if (!(Boolean)this.onlyGround.get() && locRounded.method_10264() <= this.lastPos.method_10264() || !(Boolean)this.disableOnLeaveHole.get() || (Boolean)this.surroundUp.get() && (!this.mc.field_1690.field_1913.method_1434() || !this.mc.field_1724.field_3913.field_3908 || !this.mc.field_1690.field_1849.method_1434() || !this.mc.field_1724.field_3913.field_3906 || !this.mc.field_1690.field_1894.method_1434() || !this.mc.field_1724.field_3913.field_3910 || !this.mc.field_1690.field_1881.method_1434() || !this.mc.field_1724.field_3913.field_3909)) {
               this.lastPos = locRounded;
            }
         }

         int obbyIndex = this.findBlock();
         if (obbyIndex == -1) {
            return;
         }

         int prevSlot = this.mc.field_1724.method_31548().field_7545;
         if (this.needsToPlace()) {
            Iterator var5 = this.getPositions().iterator();

            while(var5.hasNext()) {
               class_2338 pos = (class_2338)var5.next();
               if (this.mc.field_1687.method_8320(pos).method_26207().method_15800()) {
                  this.mc.field_1724.method_31548().field_7545 = obbyIndex;
               }

               if (BPlusWorldUtils.placeBlockMainHand(pos, (Boolean)this.rotate.get(), (Boolean)this.air.get(), (Boolean)this.placeOnCrystal.get()) && (Integer)this.delay.get() != 0) {
                  this.mc.field_1724.method_31548().field_7545 = prevSlot;
                  return;
               }
            }

            this.mc.field_1724.method_31548().field_7545 = prevSlot;
         }
      }

   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      this.shouldExtra = false;
   }

   private void doToggle() {
      if ((Boolean)this.toggleBack.get()) {
         if ((Boolean)this.toggleStep.get() && !this.modules.isActive(Step.class)) {
            ((Step)this.modules.get(Step.class)).toggle();
         }

         if ((Boolean)this.toggleSpeed.get() && !this.modules.isActive(Speed.class)) {
            ((Speed)this.modules.get(Speed.class)).toggle();
         }

         if ((Boolean)this.toggleStrafe.get() && !this.modules.isActive(StrafePlus.class)) {
            ((StrafePlus)this.modules.get(StrafePlus.class)).toggle();
         }
      }

      this.toggle();
   }

   private boolean needsToPlace() {
      return this.anyAir(this.lastPos.method_10074(), this.lastPos.method_10095(), this.lastPos.method_10078(), this.lastPos.method_10072(), this.lastPos.method_10067(), this.lastPos.method_10095().method_10084(), this.lastPos.method_10078().method_10084(), this.lastPos.method_10072().method_10084(), this.lastPos.method_10067().method_10084(), this.lastPos.method_10076(2), this.lastPos.method_10089(2), this.lastPos.method_10077(2), this.lastPos.method_10088(2), this.lastPos.method_10095().method_10078(), this.lastPos.method_10078().method_10072(), this.lastPos.method_10072().method_10067(), this.lastPos.method_10067().method_10095());
   }

   private List<class_2338> getPositions() {
      List<class_2338> positions = new ArrayList();
      if (!(Boolean)this.onlyGround.get()) {
         this.add(positions, this.lastPos.method_10074());
      }

      this.add(positions, this.lastPos.method_10095());
      this.add(positions, this.lastPos.method_10078());
      this.add(positions, this.lastPos.method_10072());
      this.add(positions, this.lastPos.method_10067());
      if ((Boolean)this.doubleHeight.get() || ((Keybind)this.doubleHeightKeybind.get()).isPressed()) {
         this.add(positions, this.lastPos.method_10095().method_10084());
         this.add(positions, this.lastPos.method_10078().method_10084());
         this.add(positions, this.lastPos.method_10072().method_10084());
         this.add(positions, this.lastPos.method_10067().method_10084());
      }

      return positions;
   }

   private void add(List<class_2338> list, class_2338 pos) {
      if (this.mc.field_1687.method_8320(pos).method_26215() && this.allAir(pos.method_10095(), pos.method_10078(), pos.method_10072(), pos.method_10067(), pos.method_10084(), pos.method_10074()) && !(Boolean)this.air.get()) {
         list.add(pos.method_10074());
      }

      list.add(pos);
   }

   private boolean allAir(class_2338... pos) {
      return Arrays.stream(pos).allMatch((blockPos) -> {
         return this.mc.field_1687.method_8320(blockPos).method_26215();
      });
   }

   private boolean anyAir(class_2338... pos) {
      return Arrays.stream(pos).anyMatch((blockPos) -> {
         return this.mc.field_1687.method_8320(blockPos).method_26215();
      });
   }

   private class_2248 primaryBlock() {
      class_2248 index = null;
      if (this.primary.get() == SurroundPlus.Primary.Obsidian) {
         index = class_2246.field_10540;
      } else if (this.primary.get() == SurroundPlus.Primary.EnderChest) {
         index = class_2246.field_10443;
      } else if (this.primary.get() == SurroundPlus.Primary.CryingObsidian) {
         index = class_2246.field_22423;
      } else if (this.primary.get() == SurroundPlus.Primary.NetheriteBlock) {
         index = class_2246.field_22108;
      } else if (this.primary.get() == SurroundPlus.Primary.AncientDebris) {
         index = class_2246.field_22109;
      } else if (this.primary.get() == SurroundPlus.Primary.RespawnAnchor) {
         index = class_2246.field_23152;
      } else if (this.primary.get() == SurroundPlus.Primary.Anvil) {
         index = class_2246.field_10535;
      }

      return index;
   }

   private int findBlock() {
      int index = InvUtils.findInHotbar(this.primaryBlock().method_8389()).getSlot();
      if (index == -1 && (Boolean)this.allBlocks.get()) {
         for(int i = 0; i < 9; ++i) {
            class_1792 item = this.mc.field_1724.method_31548().method_5438(i).method_7909();
            if (item instanceof class_1747 && (item == class_1802.field_22019 || item == class_1802.field_8281 || item == class_1802.field_22421 || item == class_1802.field_8782 || item == class_1802.field_8750 || item == class_1802.field_8427 || item == class_1802.field_8657 || item == class_1802.field_8466 || item == class_1802.field_22018 || PlayerUtils.getDimension() != Dimension.Nether && item == class_1802.field_23141)) {
               return i;
            }
         }
      }

      return index;
   }

   public void onActivate() {
      this.lastPos = this.mc.field_1724.method_24828() ? BPlusWorldUtils.roundBlockPos(this.mc.field_1724.method_19538()) : this.mc.field_1724.method_24515();
      if ((Boolean)this.toggleStep.get() && this.modules.isActive(Step.class)) {
         ((Step)this.modules.get(Step.class)).toggle();
      }

      if ((Boolean)this.toggleSpeed.get() && this.modules.isActive(Speed.class)) {
         ((Speed)this.modules.get(Speed.class)).toggle();
      }

      if ((Boolean)this.toggleStrafe.get() && this.modules.isActive(StrafePlus.class)) {
         ((StrafePlus)this.modules.get(StrafePlus.class)).toggle();
      }

   }

   public void onDeactivate() {
      this.ticks = 0;
      this.doSnap = true;
      this.timeToStart = 0;
      this.hasCentered = false;
   }

   @EventHandler
   public void onBreakPacket(PacketEvent.Receive event) {
      if (event.packet instanceof class_2620) {
         class_2620 bbpp = (class_2620)event.packet;
         class_2338 bbp = bbpp.method_11277();
         if ((Boolean)this.notifyBreak.get()) {
            class_1657 breakingPlayer = (class_1657)this.mc.field_1687.method_8469(bbpp.method_11280());
            class_2338 playerBlockPos = this.mc.field_1724.method_24515();
            if (bbpp.method_11278() > 0) {
               return;
            }

            if (bbp.equals(this.prevBreakPos)) {
               return;
            }

            if (breakingPlayer == this.prevBreakingPlayer) {
               return;
            }

            if (breakingPlayer.equals(this.mc.field_1724)) {
               return;
            }

            if (bbp.equals(playerBlockPos.method_10095())) {
               this.notifySurroundBreak(class_2350.field_11043, breakingPlayer);
            } else if (bbp.equals(playerBlockPos.method_10078())) {
               this.notifySurroundBreak(class_2350.field_11034, breakingPlayer);
            } else if (bbp.equals(playerBlockPos.method_10072())) {
               this.notifySurroundBreak(class_2350.field_11035, breakingPlayer);
            } else if (bbp.equals(playerBlockPos.method_10067())) {
               this.notifySurroundBreak(class_2350.field_11039, breakingPlayer);
            }

            this.prevBreakingPlayer = breakingPlayer;
            this.prevBreakPos = bbp;
         }

      }
   }

   private void notifySurroundBreak(class_2350 direction, class_1657 player) {
      switch(direction) {
      case field_11043:
         this.warning("Your north surround block is being broken by " + player.method_5820(), new Object[0]);
         break;
      case field_11034:
         this.warning("Your east surround block is being broken by " + player.method_5820(), new Object[0]);
         break;
      case field_11035:
         this.warning("Your south surround block is being broken by " + player.method_5820(), new Object[0]);
         break;
      case field_11039:
         this.warning("Your west surround block is being broken by " + player.method_5820(), new Object[0]);
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         class_2338 bb = this.mc.field_1724.method_24515();
         Iterator var3 = this.getPositions().iterator();

         while(true) {
            class_2338 pos;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  pos = (class_2338)var3.next();
                  if (POPA.getBlock(pos) == class_2246.field_10124) {
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
                     event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)pos.method_10260(), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
                     event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
                     event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
                     event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get());
                     event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideTwoColor.get());
                  }
               } while(!(Boolean)this.alwaysRender.get());
            } while(this.shapeMode.get() != ShapeMode.Sides && this.shapeMode.get() != ShapeMode.Lines && this.shapeMode.get() != ShapeMode.Both);

            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
            event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)pos.method_10260(), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get(), (Color)this.lineTwoColor.get());
            event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
            event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
            event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideTwoColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get());
            event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideTwoColor.get());
         }
      }
   }

   public static enum Primary {
      Obsidian,
      EnderChest,
      CryingObsidian,
      NetheriteBlock,
      AncientDebris,
      RespawnAnchor,
      Anvil;

      // $FF: synthetic method
      private static SurroundPlus.Primary[] $values() {
         return new SurroundPlus.Primary[]{Obsidian, EnderChest, CryingObsidian, NetheriteBlock, AncientDebris, RespawnAnchor, Anvil};
      }
   }

   public static enum coolList {
      lines,
      sides;

      // $FF: synthetic method
      private static SurroundPlus.coolList[] $values() {
         return new SurroundPlus.coolList[]{lines, sides};
      }
   }
}
