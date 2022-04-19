package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
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
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class SmartHoleFiller extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgBlocks;
   private final SettingGroup sgTarget;
   private final SettingGroup sgRender;
   private final Setting<Integer> horizontalRadius;
   private final Setting<Integer> verticalRadius;
   private final Setting<Boolean> doubles;
   private final Setting<Integer> placeDelay;
   private final Setting<Double> targetRange;
   private final Setting<Double> distance;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> render;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nextSideColor;
   private final Setting<SettingColor> nextLineColor;
   private final Pool<SmartHoleFiller.Hole> holePool;
   private final List<SmartHoleFiller.Hole> holes;
   private final byte NULL;
   private class_1657 target;
   private int timer;
   public static ArrayList<class_2248> obsidian = new ArrayList<class_2248>() {
      {
         this.add(class_2246.field_10540);
      }
   };

   public SmartHoleFiller() {
      super(Categories.NewCombat, "Hole-Filler", "Fills holes when the player wants to jump into them.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgBlocks = this.settings.createGroup("Blocks");
      this.sgTarget = this.settings.createGroup("Target");
      this.sgRender = this.settings.createGroup("Render");
      this.horizontalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(4)).min(0).sliderMax(6).build());
      this.verticalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("vertical-radius")).description("Vertical radius in which to search for holes.")).defaultValue(4)).min(0).sliderMax(6).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Fills double holes.")).defaultValue(true)).build());
      this.placeDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("The ticks delay between placement.")).defaultValue(1)).min(0).build());
      this.targetRange = this.sgTarget.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("target-range")).description("The minimum distance between the hole and the player for the hole to be filled.")).defaultValue(10.0D).min(0.0D).sliderMax(15.0D).max(15.0D).build());
      this.distance = this.sgTarget.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("min-distance")).description("The minimum distance between the hole and the player for the hole to be filled.")).defaultValue(2.0D).min(1.0D).sliderMax(5.0D).max(5.0D).build());
      this.rotate = this.sgBlocks.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically rotates towards the holes being filled.")).defaultValue(true)).build());
      this.render = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render")).description("Renders an overlay where blocks will be placed.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232, 10))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(197, 137, 232))).build());
      this.nextSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-side-color")).description("The side color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245, 10))).build());
      this.nextLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("next-line-color")).description("The line color of the next block to be placed.")).defaultValue(new SettingColor(227, 196, 245))).build());
      this.holePool = new Pool(SmartHoleFiller.Hole::new);
      this.holes = new ArrayList();
      this.NULL = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.target = CityUtils.getPlayerTarget((Double)this.targetRange.get());
      Iterator var2 = this.holes.iterator();

      while(var2.hasNext()) {
         SmartHoleFiller.Hole hole = (SmartHoleFiller.Hole)var2.next();
         this.holePool.free(hole);
      }

      this.holes.clear();
      FindItemResult block = InvUtils.findInHotbar((itemStack) -> {
         return itemStack.method_7909() instanceof class_1747 && obsidian.contains(class_2248.method_9503(itemStack.method_7909()));
      });
      if (!block.found()) {
         this.error("No obsidian in hotbar!", new Object[0]);
         this.toggle();
      } else {
         BlockIterator.register((Integer)this.horizontalRadius.get(), (Integer)this.verticalRadius.get(), (blockPos, blockState) -> {
            if (this.validHole(blockPos)) {
               int bedrock = 0;
               int obsidian = 0;
               class_2350 air = null;
               class_2350[] var6 = class_2350.values();
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  class_2350 direction = var6[var8];
                  if (direction != class_2350.field_11036) {
                     class_2680 state = this.mc.field_1687.method_8320(blockPos.method_10093(direction));
                     if (state.method_26204() == class_2246.field_9987) {
                        ++bedrock;
                     } else if (state.method_26204() == class_2246.field_10540) {
                        ++obsidian;
                     } else {
                        if (direction == class_2350.field_11033) {
                           return;
                        }

                        if (this.validHole(blockPos.method_10093(direction)) && air == null) {
                           class_2350[] var11 = class_2350.values();
                           int var12 = var11.length;

                           for(int var13 = 0; var13 < var12; ++var13) {
                              class_2350 dir = var11[var13];
                              if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                                 class_2680 blockState1 = this.mc.field_1687.method_8320(blockPos.method_10093(direction).method_10093(dir));
                                 if (blockState1.method_26204() == class_2246.field_9987) {
                                    ++bedrock;
                                 } else {
                                    if (blockState1.method_26204() != class_2246.field_10540) {
                                       return;
                                    }

                                    ++obsidian;
                                 }
                              }
                           }

                           air = direction;
                        }
                     }
                  }
               }

               if (this.target != null) {
                  if (obsidian + bedrock == 5 && air == null && BlockUtilsWorld.distanceBetween(this.target.method_24515(), blockPos) <= (Double)this.distance.get()) {
                     this.holes.add(((SmartHoleFiller.Hole)this.holePool.get()).set(blockPos, (byte)0));
                  } else if (obsidian + bedrock == 8 && (Boolean)this.doubles.get() && air != null && BlockUtilsWorld.distanceBetween(this.target.method_24515(), blockPos) <= (Double)this.distance.get()) {
                     this.holes.add(((SmartHoleFiller.Hole)this.holePool.get()).set(blockPos, Dir.get(air)));
                  }
               }

            }
         });
      }
   }

   @EventHandler
   private void onTickPost(TickEvent.Post event) {
      this.target = CityUtils.getPlayerTarget((Double)this.targetRange.get());
      if (this.timer <= 0 && !this.holes.isEmpty() && this.target != null) {
         FindItemResult block = InvUtils.findInHotbar((itemStack) -> {
            return itemStack.method_7909() instanceof class_1747 && obsidian.contains(class_2248.method_9503(itemStack.method_7909()));
         });
         if (!block.found()) {
            this.error("No obsidian in hotbar!", new Object[0]);
            this.toggle();
            return;
         }

         if (BlockUtilsWorld.distanceBetween(this.target.method_24515(), ((SmartHoleFiller.Hole)this.holes.get(0)).blockPos) <= (Double)this.distance.get()) {
            BlockUtils.place(((SmartHoleFiller.Hole)this.holes.get(0)).blockPos, block, (Boolean)this.rotate.get(), 10, true);
         }

         this.timer = (Integer)this.placeDelay.get();
      }

      --this.timer;
   }

   private boolean validHole(class_2338 pos) {
      if (this.mc.field_1724.method_24515().equals(pos)) {
         return false;
      } else if (((AbstractBlockAccessor)this.mc.field_1687.method_8320(pos).method_26204()).isCollidable()) {
         return false;
      } else {
         return !((AbstractBlockAccessor)this.mc.field_1687.method_8320(pos.method_10084()).method_26204()).isCollidable();
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onRender(Render3DEvent event) {
      if ((Boolean)this.render.get()) {
         Iterator var2 = this.holes.iterator();

         while(var2.hasNext()) {
            SmartHoleFiller.Hole hole = (SmartHoleFiller.Hole)var2.next();
            boolean isFirst = hole == this.holes.get(0);
            Color side = isFirst ? (Color)this.nextSideColor.get() : (Color)this.sideColor.get();
            Color line = isFirst ? (Color)this.nextLineColor.get() : (Color)this.lineColor.get();
            event.renderer.box((class_2338)hole.blockPos, side, line, (ShapeMode)this.shapeMode.get(), hole.exclude);
         }

      }
   }

   private static class Hole {
      public class_2339 blockPos = new class_2339();
      public byte exclude;

      public SmartHoleFiller.Hole set(class_2338 blockPos, byte exclude) {
         this.blockPos.method_10101(blockPos);
         this.exclude = exclude;
         return this;
      }
   }
}
