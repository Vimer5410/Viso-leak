package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2338;

public class ModeAnvil extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPause;
   private final SettingGroup sgplac;
   private final SettingGroup sgRender;
   private final Setting<Integer> range;
   private final Setting<Integer> delay;
   private final Setting<SortPriority> priority;
   private final Setting<ModeAnvil.TopMode> topPlacement;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> pauseOnEat;
   private final Setting<Boolean> pauseOnDrink;
   private final Setting<Boolean> pauseOnMine;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nextSideColor;
   private final Setting<SettingColor> nextLineColor;
   private final List<class_2338> placePositions;
   private class_1657 target;
   private boolean placed;
   private int timer;

   public ModeAnvil() {
      super(Categories.NewCombat, "Mode-Anvil", "puts an anvil in the opponent/on the opponent");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.sgPause = this.settings.createGroup("_Pause_");
      this.sgplac = this.settings.createGroup("_Placing_");
      this.sgRender = this.settings.createGroup("_Render_");
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("range")).description("The range players can be targeted.")).defaultValue(5)).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between block placements.")).defaultValue(1)).build());
      this.priority = this.sgplac.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestHealth)).build());
      this.topPlacement = this.sgplac.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("placing")).description("where to put?")).defaultValue(ModeAnvil.TopMode.Surround)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards blocks when placing.")).defaultValue(true)).build());
      this.pauseOnEat = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-eat")).description("Pauses while eating.")).defaultValue(false)).build());
      this.pauseOnDrink = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-drink")).description("Pauses while drinking.")).defaultValue(false)).build());
      this.pauseOnMine = this.sgPause.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-mine")).description("Pauses while mining.")).defaultValue(false)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232))).build());
      this.nextSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245, 10))).build());
      this.nextLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245))).build());
      this.placePositions = new ArrayList();
   }

   public void onActivate() {
      this.target = null;
      this.placePositions.clear();
      this.timer = 0;
      this.placed = false;
   }

   public void onDeactivate() {
      this.placePositions.clear();
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult obsidian = InvUtils.findInHotbar(class_1802.field_8782);
      if (!obsidian.isHotbar() && !obsidian.isOffhand()) {
         this.placePositions.clear();
         this.placed = false;
      } else {
         if (TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
            this.target = TargetUtils.getPlayerTarget((double)(Integer)this.range.get(), (SortPriority)this.priority.get());
         }

         if (!TargetUtils.isBadTarget(this.target, (double)(Integer)this.range.get())) {
            this.fillPlaceArray(this.target);
            if (this.timer >= (Integer)this.delay.get() && this.placePositions.size() > 0) {
               class_2338 blockPos = (class_2338)this.placePositions.get(this.placePositions.size() - 1);
               if (BlockUtils.place(blockPos, obsidian, (Boolean)this.rotate.get(), 50, true)) {
                  this.placePositions.remove(blockPos);
                  this.placed = true;
               }

               this.timer = 0;
            } else {
               ++this.timer;
            }

         }
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get() && !this.placePositions.isEmpty()) {
         Iterator var2 = this.placePositions.iterator();

         while(var2.hasNext()) {
            class_2338 pos = (class_2338)var2.next();
            boolean isFirst = pos.equals(this.placePositions.get(this.placePositions.size() - 1));
            Color side = isFirst ? (Color)this.nextSideColor.get() : (Color)this.sideColor.get();
            Color line = isFirst ? (Color)this.nextLineColor.get() : (Color)this.lineColor.get();
            event.renderer.box((class_2338)pos, side, line, (ShapeMode)this.shapeMode.get(), 0);
         }

      }
   }

   private void fillPlaceArray(class_1657 target) {
      this.placePositions.clear();
      class_2338 targetPos = target.method_24515();
      switch((ModeAnvil.TopMode)this.topPlacement.get()) {
      case OnOnHead:
         this.add(targetPos.method_10069(0, 4, 0));
         break;
      case OnHead:
         this.add(targetPos.method_10069(0, 3, 0));
         break;
      case Surround:
         this.add(targetPos.method_10069(1, 0, 0));
         this.add(targetPos.method_10069(-1, 0, 0));
         this.add(targetPos.method_10069(0, 0, 1));
         this.add(targetPos.method_10069(0, 0, -1));
         break;
      case OnSurround:
         this.add(targetPos.method_10069(1, 1, 0));
         this.add(targetPos.method_10069(-1, 1, 0));
         this.add(targetPos.method_10069(0, 1, 1));
         this.add(targetPos.method_10069(0, 1, -1));
         break;
      case LongSurround:
         this.add(targetPos.method_10069(1, 0, 0));
         this.add(targetPos.method_10069(-1, 0, 0));
         this.add(targetPos.method_10069(0, 0, 1));
         this.add(targetPos.method_10069(0, 0, -1));
         this.add(targetPos.method_10069(1, 0, 1));
         this.add(targetPos.method_10069(-1, 0, -1));
         this.add(targetPos.method_10069(1, 0, -1));
         this.add(targetPos.method_10069(-1, 0, 1));
      }

   }

   private void add(class_2338 blockPos) {
      if (!this.placePositions.contains(blockPos) && BlockUtils.canPlace(blockPos)) {
         this.placePositions.add(blockPos);
      }

   }

   public String getInfoString() {
      return EntityUtils.getName(this.target);
   }

   public static enum TopMode {
      OnOnHead,
      OnHead,
      Surround,
      OnSurround,
      LongSurround;

      // $FF: synthetic method
      private static ModeAnvil.TopMode[] $values() {
         return new ModeAnvil.TopMode[]{OnOnHead, OnHead, Surround, OnSurround, LongSurround};
      }
   }
}
