package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2846;
import net.minecraft.class_2879;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;

public class ModeTNT extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgAutoBreak;
   private final SettingGroup sgPause;
   private final SettingGroup sgObsidianRender;
   private final SettingGroup sgTNTRender;
   private final SettingGroup sgBreakRender;
   private final Setting<Integer> range;
   private final Setting<Integer> delay;
   private final Setting<Boolean> rotate;
   private final Setting<ModeTNT.tntPlaceMode> tntPlaceModeSetting;
   private final Setting<Boolean> autoBreak;
   public final Setting<ModeTNT.mineMode> breakMode;
   private final Setting<Boolean> antiSelf;
   private final Setting<Boolean> holePause;
   private final Setting<Boolean> obsidianRender;
   private final Setting<ShapeMode> obsidianShapeMode;
   private final Setting<SettingColor> obsidianSideColor;
   private final Setting<SettingColor> obsidianLineColor;
   private final Setting<SettingColor> obsidianNextSideColor;
   private final Setting<SettingColor> obsidianNextLineColor;
   private final Setting<Boolean> tntRender;
   private final Setting<ShapeMode> tntShapeMode;
   private final Setting<SettingColor> tntSideColor;
   private final Setting<SettingColor> tntLineColor;
   private final Setting<Boolean> breakRender;
   private final Setting<ShapeMode> breakShapeMode;
   private final Setting<SettingColor> breakSideColor;
   private final Setting<SettingColor> breakLineColor;
   private class_1657 target;
   private final List<class_2338> obsidianPos;
   private int ticks;
   private class_2350 direction;
   private boolean rofl;
   private boolean toggled;

   public ModeTNT() {
      super(Categories.NewCombat, "Mode-Tnt", "");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.sgAutoBreak = this.settings.createGroup("_Auto-Break_");
      this.sgPause = this.settings.createGroup("_Pause_");
      this.sgObsidianRender = this.settings.createGroup("_Obsidian-Render_");
      this.sgTNTRender = this.settings.createGroup("_TNT-Render_");
      this.sgBreakRender = this.settings.createGroup("_Break-Render_");
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("target-range")).description("max range to target")).defaultValue(5)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between obsidian placement")).defaultValue(0)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards blocks when interacting")).defaultValue(false)).build());
      this.tntPlaceModeSetting = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("How to select the player to target.")).defaultValue(ModeTNT.tntPlaceMode.Head)).build());
      this.autoBreak = this.sgAutoBreak.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-break")).description("attemps to auto break")).defaultValue(true)).build());
      this.breakMode = this.sgAutoBreak.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("break-mode")).defaultValue(ModeTNT.mineMode.Normal)).visible(() -> {
         return (Boolean)this.autoBreak.get();
      })).build());
      this.antiSelf = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-self")).description("pause if enemy in your hole")).defaultValue(true)).build());
      this.holePause = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-in-hole")).description("pause if enemy isnt in hole")).defaultValue(false)).build());
      this.obsidianRender = this.sgObsidianRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.obsidianShapeMode = this.sgObsidianRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).build());
      this.obsidianSideColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 0, 99))).build());
      this.obsidianLineColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 0, 99))).build());
      this.obsidianNextSideColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(0, 0, 0, 99))).build());
      this.obsidianNextLineColor = this.sgObsidianRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(0, 0, 0, 99))).build());
      this.tntRender = this.sgTNTRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.tntShapeMode = this.sgTNTRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).build());
      this.tntSideColor = this.sgTNTRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(5, 146, 0, 50))).build());
      this.tntLineColor = this.sgTNTRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 205, 255, 255))).build());
      this.breakRender = this.sgBreakRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.breakShapeMode = this.sgBreakRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Lines)).build());
      this.breakSideColor = this.sgBreakRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(255, 215, 0))).build());
      this.breakLineColor = this.sgBreakRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(0, 0, 0))).build());
      this.obsidianPos = new ArrayList();
   }

   public void onActivate() {
      this.obsidianPos.clear();
      this.ticks = 0;
      this.rofl = false;
      this.toggled = false;
   }

   public void onDeactivate() {
      this.obsidianPos.clear();
   }

   private void onStartBreakingBlock(StartBreakingBlockEvent event) {
      this.direction = event.direction;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult obsidian = InvUtils.findInHotbar(class_1802.field_8281);
      if (!obsidian.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         ChatUtils.error("No obsidian");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult flint = InvUtils.findInHotbar(class_1802.field_8884);
      if (!flint.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         ChatUtils.error("No flint and steel");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult tnt = InvUtils.findInHotbar(class_1802.field_8626);
      if (!tnt.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         ChatUtils.error("No TNT");
         this.toggle();
         this.toggled = true;
      }

      FindItemResult pickaxe = InvUtils.find((itemStack) -> {
         return itemStack.method_7909() == class_1802.field_8377 || itemStack.method_7909() == class_1802.field_22024;
      });
      if (!pickaxe.isHotbar() && !this.toggled) {
         this.obsidianPos.clear();
         ChatUtils.error("No pickaxe");
         this.toggle();
         this.toggled = true;
      }

      if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), SortPriority.LowestDistance);
      }

      if (this.target != null) {
         if ((Boolean)this.antiSelf.get() && this.antiSelf(this.target) && !this.toggled) {
            this.obsidianPos.clear();
            ChatUtils.error("retarded");
            this.toggle();
            this.toggled = true;
         }

         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get()) && !this.toggled) {
            ChatUtils.error("daleko");
            this.toggle();
            this.toggled = true;
         }

         if (this.allowTNT(this.target)) {
            this.placeTNT(this.target);
            this.igniteTNT(this.target.method_24515().method_10086(2), flint);
         }

         if (!this.mineBlockstate(this.target.method_24515().method_10086(2)) && (Boolean)this.autoBreak.get()) {
            this.mine(this.target.method_24515().method_10086(2), pickaxe);
         }

         this.placeObsidian(this.target);
         if (this.ticks >= (Integer)this.delay.get() && this.obsidianPos.size() > 0) {
            class_2338 blockPos = (class_2338)this.obsidianPos.get(this.obsidianPos.size() - 1);
            if (BlockUtils.place(blockPos, obsidian, (Boolean)this.rotate.get(), 50, true)) {
               this.obsidianPos.remove(blockPos);
            }

            this.ticks = 0;
         } else {
            ++this.ticks;
         }

      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.obsidianRender.get() && !this.obsidianPos.isEmpty()) {
         Iterator var2 = this.obsidianPos.iterator();

         while(var2.hasNext()) {
            class_2338 pos = (class_2338)var2.next();
            boolean isFirst = pos.equals(this.obsidianPos.get(this.obsidianPos.size() - 1));
            Color side = isFirst ? (Color)this.obsidianNextSideColor.get() : (Color)this.obsidianSideColor.get();
            Color line = isFirst ? (Color)this.obsidianNextLineColor.get() : (Color)this.obsidianLineColor.get();
            event.renderer.box((class_2338)pos, side, line, (ShapeMode)this.obsidianShapeMode.get(), 0);
         }
      }

      if ((Boolean)this.tntRender.get() && this.target != null && this.allowTNT(this.target) && this.tntBlockstate(this.target.method_24515().method_10069(0, 2, 0))) {
         event.renderer.box((class_2338)this.target.method_24515().method_10069(0, 2, 0), (Color)this.tntSideColor.get(), (Color)this.tntLineColor.get(), (ShapeMode)this.tntShapeMode.get(), 0);
      }

      if ((Boolean)this.breakRender.get() && this.target != null && (Boolean)this.autoBreak.get() && !this.mineBlockstate(this.target.method_24515().method_10069(0, 2, 0))) {
         event.renderer.box((class_2338)this.target.method_24515().method_10069(0, 2, 0), (Color)this.breakSideColor.get(), (Color)this.breakLineColor.get(), (ShapeMode)this.breakShapeMode.get(), 0);
      }

   }

   private void placeObsidian(class_1657 target) {
      FindItemResult obsidian = InvUtils.findInHotbar(class_1802.field_8281);
      this.obsidianPos.clear();
      class_2338 targetPos = target.method_24515();
      this.add(targetPos.method_10069(0, 3, 0));
      this.add(targetPos.method_10069(1, 2, 0));
      this.add(targetPos.method_10069(-1, 2, 0));
      this.add(targetPos.method_10069(0, 2, 1));
      this.add(targetPos.method_10069(0, 2, -1));
      this.add(targetPos.method_10069(1, 1, 0));
      this.add(targetPos.method_10069(-1, 1, 0));
      this.add(targetPos.method_10069(0, 1, 1));
      this.add(targetPos.method_10069(0, 1, -1));
   }

   private void placeTNT(class_1657 target) {
      FindItemResult tnt = InvUtils.findInHotbar(class_1802.field_8626);
      class_2338 targetPos = target.method_24515();
      BlockUtils.place(targetPos.method_10069(0, 2, 0), tnt, (Boolean)this.rotate.get(), 50, true, true);
   }

   private void add(class_2338 blockPos) {
      if (!this.obsidianPos.contains(blockPos) && BlockUtils.canPlace(blockPos)) {
         this.obsidianPos.add(blockPos);
      }

   }

   private void igniteTNT(class_2338 pos, FindItemResult item) {
      InvUtils.swap(item.getSlot(), true);
      this.mc.field_1761.method_2896(this.mc.field_1724, this.mc.field_1687, class_1268.field_5808, new class_3965(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D), class_2350.field_11036, pos, true));
      InvUtils.swapBack();
   }

   public boolean tntBlockstate(class_2338 Pos) {
      return this.mc.field_1687.method_8320(Pos).method_26204() == class_2246.field_10124 || this.mc.field_1687.method_8320(Pos).method_26204() == class_2246.field_10375;
   }

   public boolean allowTNT(class_1309 target) {
      assert this.mc.field_1687 != null;

      return !this.mc.field_1687.method_8320(target.method_24515().method_10069(1, 2, 0)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(-1, 2, 0)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(0, 2, 1)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(0, 2, -1)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(0, 3, 0)).method_26215();
   }

   public boolean mineBlockstate(class_2338 Pos) {
      return this.mc.field_1687.method_8320(Pos).method_26204() == class_2246.field_10124 || this.mc.field_1687.method_8320(Pos).method_26204() == class_2246.field_10375 || this.mc.field_1687.method_8320(Pos).method_26204() == class_2246.field_9987;
   }

   public void mine(class_2338 blockPos, FindItemResult item) {
      if (this.breakMode.get() == ModeTNT.mineMode.Normal) {
         InvUtils.swap(item.getSlot(), false);
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
         this.mc.field_1724.method_6104(class_1268.field_5808);
         this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, class_2350.field_11036));
      }

      if (this.breakMode.get() == ModeTNT.mineMode.Instant) {
         InvUtils.swap(item.getSlot(), false);
         if (!this.rofl) {
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, blockPos, class_2350.field_11036));
            this.rofl = true;
         }

         if ((Boolean)this.rotate.get()) {
            Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> {
               this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, this.direction));
            });
         } else {
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, blockPos, this.direction));
         }

         this.mc.method_1562().method_2883(new class_2879(class_1268.field_5808));
      }

   }

   private boolean isBurrowed(class_1309 target) {
      assert this.mc.field_1687 != null;

      return !this.mc.field_1687.method_8320(target.method_24515()).method_26215();
   }

   private boolean isSurrounded(class_1309 target) {
      assert this.mc.field_1687 != null;

      return !this.mc.field_1687.method_8320(target.method_24515().method_10069(1, 0, 0)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(-1, 0, 0)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(0, 0, 1)).method_26215() && !this.mc.field_1687.method_8320(target.method_24515().method_10069(0, 0, -1)).method_26215();
   }

   private boolean antiSelf(class_1309 target) {
      return this.mc.field_1724.method_24515().method_10263() == target.method_24515().method_10263() && this.mc.field_1724.method_24515().method_10260() == target.method_24515().method_10260() && this.mc.field_1724.method_24515().method_10264() == target.method_24515().method_10264();
   }

   public static enum tntPlaceMode {
      Head,
      Legs;

      // $FF: synthetic method
      private static ModeTNT.tntPlaceMode[] $values() {
         return new ModeTNT.tntPlaceMode[]{Head, Legs};
      }
   }

   public static enum mineMode {
      Normal,
      Instant;

      // $FF: synthetic method
      private static ModeTNT.mineMode[] $values() {
         return new ModeTNT.mineMode[]{Normal, Instant};
      }
   }
}
