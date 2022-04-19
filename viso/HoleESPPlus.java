package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.renderer.Renderer3D;
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
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.Dir;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class HoleESPPlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> horizontalRadius;
   private final Setting<Integer> verticalRadius;
   private final Setting<Integer> holeHeight;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> ignoreOwn;
   private final Setting<Boolean> webs;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<Double> height;
   private final Setting<Boolean> topQuad;
   private final Setting<Boolean> bottomQuad;
   private final Setting<SettingColor> bedrockColorTop;
   private final Setting<SettingColor> bedrockColorBottom;
   private final Setting<SettingColor> obsidianColorTop;
   private final Setting<SettingColor> obsidianColorBottom;
   private final Setting<SettingColor> mixedColorTop;
   private final Setting<SettingColor> mixedColorBottom;
   private final Pool<HoleESPPlus.Hole> holePool;
   private final List<HoleESPPlus.Hole> holes;
   private final byte NULL;

   public HoleESPPlus() {
      super(Categories.NewCombat, "HoleEsp-V2", "Displays more  holes that you will take less damage in.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("_Render_");
      this.horizontalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("horizontal-radius")).description("Horizontal radius in which to search for holes.")).defaultValue(10)).min(0).sliderMax(32).build());
      this.verticalRadius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("vertical-radius")).description("Vertical radius in which to search for holes.")).defaultValue(5)).min(0).sliderMax(32).build());
      this.holeHeight = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-height")).description("Minimum hole height required to be rendered.")).defaultValue(2)).min(1).build());
      this.doubles = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("doubles")).description("Highlights double holes that can be stood across.")).defaultValue(true)).build());
      this.ignoreOwn = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-own")).description("Ignores rendering the hole you are currently standing in.")).defaultValue(false)).build());
      this.webs = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("webs")).description("Whether to show holes that have webs inside of them.")).defaultValue(false)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.height = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("The height of rendering.")).defaultValue(1.0D).min(0.0D).build());
      this.topQuad = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("top-quad")).description("Whether to render a quad at the top of the hole.")).defaultValue(false)).build());
      this.bottomQuad = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("bottom-quad")).description("Whether to render a quad at the bottom of the hole.")).defaultValue(false)).build());
      this.bedrockColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("bedrock-top")).description("The top color for holes that are completely bedrock.")).defaultValue(new SettingColor(8, 0, 255, 11))).build());
      this.bedrockColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("bedrock-bottom")).description("The bottom color for holes that are completely bedrock.")).defaultValue(new SettingColor(61, 21, 175, 255))).build());
      this.obsidianColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("obsidian-top")).description("The top color for holes that are completely obsidian.")).defaultValue(new SettingColor(235, 251, 0, 10))).build());
      this.obsidianColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("obsidian-bottom")).description("The bottom color for holes that are completely obsidian.")).defaultValue(new SettingColor(233, 255, 0, 255))).build());
      this.mixedColorTop = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("mixed-top")).description("The top color for holes that have mixed bedrock and obsidian.")).defaultValue(new SettingColor(255, 255, 255, 11))).build());
      this.mixedColorBottom = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("mixed-bottom")).description("The bottom color for holes that have mixed bedrock and obsidian.")).defaultValue(new SettingColor(0, 220, 255, 255))).build());
      this.holePool = new Pool(HoleESPPlus.Hole::new);
      this.holes = new ArrayList();
      this.NULL = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      Iterator var2 = this.holes.iterator();

      while(var2.hasNext()) {
         HoleESPPlus.Hole hole = (HoleESPPlus.Hole)var2.next();
         this.holePool.free(hole);
      }

      this.holes.clear();
      BlockIterator.register((Integer)this.horizontalRadius.get(), (Integer)this.verticalRadius.get(), (blockPos, blockState) -> {
         if (this.validHole(blockPos)) {
            int bedrock = 0;
            int obsidian = 0;
            int crying_obi = 0;
            int netherite_block = 0;
            int respawn_anchor = 0;
            int ender_chest = 0;
            int ancient_debris = 0;
            int enchanting_table = 0;
            int anvil = 0;
            int anvilC = 0;
            int anvilD = 0;
            class_2350 air = null;
            class_2350[] var15 = class_2350.values();
            int var16 = var15.length;

            for(int var17 = 0; var17 < var16; ++var17) {
               class_2350 direction = var15[var17];
               if (direction != class_2350.field_11036) {
                  class_2680 state = this.mc.field_1687.method_8320(blockPos.method_10093(direction));
                  if (state.method_26204() == class_2246.field_9987) {
                     ++bedrock;
                  } else if (state.method_26204() == class_2246.field_10540) {
                     ++obsidian;
                  } else if (state.method_26204() == class_2246.field_23152) {
                     ++respawn_anchor;
                  } else if (state.method_26204() == class_2246.field_22108) {
                     ++netherite_block;
                  } else if (state.method_26204() == class_2246.field_22423) {
                     ++crying_obi;
                  } else if (state.method_26204() == class_2246.field_10443) {
                     ++ender_chest;
                  } else if (state.method_26204() == class_2246.field_22109) {
                     ++ancient_debris;
                  } else if (state.method_26204() == class_2246.field_10485) {
                     ++enchanting_table;
                  } else if (state.method_26204() == class_2246.field_10535) {
                     ++anvil;
                  } else if (state.method_26204() == class_2246.field_10105) {
                     ++anvilC;
                  } else if (state.method_26204() == class_2246.field_10414) {
                     ++anvilD;
                  } else {
                     if (direction == class_2350.field_11033) {
                        return;
                     }

                     if (this.validHole(blockPos.method_10093(direction)) && air == null) {
                        class_2350[] var20 = class_2350.values();
                        int var21 = var20.length;

                        for(int var22 = 0; var22 < var21; ++var22) {
                           class_2350 dir = var20[var22];
                           if (dir != direction.method_10153() && dir != class_2350.field_11036) {
                              class_2680 blockState1 = this.mc.field_1687.method_8320(blockPos.method_10093(direction).method_10093(dir));
                              if (blockState1.method_26204() == class_2246.field_9987) {
                                 ++bedrock;
                              } else if (blockState1.method_26204() == class_2246.field_10540) {
                                 ++obsidian;
                              } else if (blockState1.method_26204() == class_2246.field_23152) {
                                 ++respawn_anchor;
                              } else if (blockState1.method_26204() == class_2246.field_22108) {
                                 ++netherite_block;
                              } else if (blockState1.method_26204() == class_2246.field_22423) {
                                 ++crying_obi;
                              } else if (blockState1.method_26204() == class_2246.field_10443) {
                                 ++ender_chest;
                              } else if (blockState1.method_26204() == class_2246.field_22109) {
                                 ++ancient_debris;
                              } else if (blockState1.method_26204() == class_2246.field_10485) {
                                 ++enchanting_table;
                              } else if (blockState1.method_26204() == class_2246.field_10535) {
                                 ++anvil;
                              } else if (blockState1.method_26204() == class_2246.field_10105) {
                                 ++anvilC;
                              } else {
                                 if (blockState1.method_26204() != class_2246.field_10414) {
                                    return;
                                 }

                                 ++anvilD;
                              }
                           }
                        }

                        air = direction;
                     }
                  }
               }
            }

            if (obsidian + respawn_anchor + netherite_block + crying_obi + ender_chest + ancient_debris + enchanting_table + anvil == 5 && air == null) {
               this.holes.add(((HoleESPPlus.Hole)this.holePool.get()).set(blockPos, obsidian == 5 ? HoleESPPlus.Hole.Type.Obsidian : (respawn_anchor == 5 ? HoleESPPlus.Hole.Type.Respawn_anchor : (netherite_block == 5 ? HoleESPPlus.Hole.Type.Netherite_block : (crying_obi == 5 ? HoleESPPlus.Hole.Type.Crying_obi : (ender_chest == 5 ? HoleESPPlus.Hole.Type.Ender_chest : (ancient_debris == 5 ? HoleESPPlus.Hole.Type.Ancient_debris : (enchanting_table == 5 ? HoleESPPlus.Hole.Type.Enchanting_table : (anvil == 5 ? HoleESPPlus.Hole.Type.Anvil : (anvilC == 5 ? HoleESPPlus.Hole.Type.Chipped_anvil : (anvilD == 5 ? HoleESPPlus.Hole.Type.Damaged_anvil : HoleESPPlus.Hole.Type.MixedNoBed))))))))), (byte)0));
            } else if (obsidian + bedrock + respawn_anchor + netherite_block + crying_obi + ender_chest + ancient_debris + enchanting_table + anvil == 5 && air == null) {
               this.holes.add(((HoleESPPlus.Hole)this.holePool.get()).set(blockPos, obsidian == 5 ? HoleESPPlus.Hole.Type.Obsidian : (respawn_anchor == 5 ? HoleESPPlus.Hole.Type.Respawn_anchor : (netherite_block == 5 ? HoleESPPlus.Hole.Type.Netherite_block : (crying_obi == 5 ? HoleESPPlus.Hole.Type.Crying_obi : (ender_chest == 5 ? HoleESPPlus.Hole.Type.Ender_chest : (ancient_debris == 5 ? HoleESPPlus.Hole.Type.Ancient_debris : (enchanting_table == 5 ? HoleESPPlus.Hole.Type.Enchanting_table : (anvil == 5 ? HoleESPPlus.Hole.Type.Anvil : (anvilC == 5 ? HoleESPPlus.Hole.Type.Chipped_anvil : (anvilD == 5 ? HoleESPPlus.Hole.Type.Damaged_anvil : (bedrock == 5 ? HoleESPPlus.Hole.Type.Bedrock : HoleESPPlus.Hole.Type.Mixed)))))))))), (byte)0));
            } else if (obsidian + bedrock + respawn_anchor + netherite_block + crying_obi + ender_chest + ancient_debris + enchanting_table + anvil == 8 && (Boolean)this.doubles.get() && air != null) {
               this.holes.add(((HoleESPPlus.Hole)this.holePool.get()).set(blockPos, obsidian == 8 ? HoleESPPlus.Hole.Type.Obsidian : (respawn_anchor == 8 ? HoleESPPlus.Hole.Type.Respawn_anchor : (netherite_block == 8 ? HoleESPPlus.Hole.Type.Netherite_block : (crying_obi == 8 ? HoleESPPlus.Hole.Type.Crying_obi : (ender_chest == 8 ? HoleESPPlus.Hole.Type.Ender_chest : (ancient_debris == 8 ? HoleESPPlus.Hole.Type.Ancient_debris : (enchanting_table == 8 ? HoleESPPlus.Hole.Type.Enchanting_table : (anvil == 8 ? HoleESPPlus.Hole.Type.Anvil : (anvilC == 5 ? HoleESPPlus.Hole.Type.Chipped_anvil : (anvilD == 5 ? HoleESPPlus.Hole.Type.Damaged_anvil : (bedrock == 8 ? HoleESPPlus.Hole.Type.Bedrock : (bedrock == 1 ? HoleESPPlus.Hole.Type.Mixed : HoleESPPlus.Hole.Type.Mixed))))))))))), Dir.get(air)));
            }

         }
      });
   }

   private boolean validHole(class_2338 pos) {
      if ((Boolean)this.ignoreOwn.get() && this.mc.field_1724.method_24515().equals(pos)) {
         return false;
      } else if (!(Boolean)this.webs.get() && this.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_10343) {
         return false;
      } else if (((AbstractBlockAccessor)this.mc.field_1687.method_8320(pos).method_26204()).isCollidable()) {
         return false;
      } else {
         for(int i = 0; i < (Integer)this.holeHeight.get(); ++i) {
            if (((AbstractBlockAccessor)this.mc.field_1687.method_8320(pos.method_10086(i)).method_26204()).isCollidable()) {
               return false;
            }
         }

         return true;
      }
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.holes.iterator();

      while(var2.hasNext()) {
         HoleESPPlus.Hole hole = (HoleESPPlus.Hole)var2.next();
         hole.render(event.renderer, (ShapeMode)this.shapeMode.get(), (Double)this.height.get(), (Boolean)this.topQuad.get(), (Boolean)this.bottomQuad.get());
      }

   }

   private static class Hole {
      public class_2339 blockPos = new class_2339();
      public byte exclude;
      public HoleESPPlus.Hole.Type type;

      public HoleESPPlus.Hole set(class_2338 blockPos, HoleESPPlus.Hole.Type type, byte exclude) {
         this.blockPos.method_10101(blockPos);
         this.exclude = exclude;
         this.type = type;
         return this;
      }

      public Color getTopColor() {
         SettingColor var10000;
         switch(this.type) {
         case Obsidian:
         case Crying_obi:
         case Netherite_block:
         case Respawn_anchor:
         case Ender_chest:
         case Ancient_debris:
         case Enchanting_table:
         case Anvil:
         case Chipped_anvil:
         case Damaged_anvil:
         case MixedNoBed:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).obsidianColorTop.get();
            break;
         case Bedrock:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).bedrockColorTop.get();
            break;
         default:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).mixedColorTop.get();
         }

         return var10000;
      }

      public Color getBottomColor() {
         SettingColor var10000;
         switch(this.type) {
         case Obsidian:
         case Crying_obi:
         case Netherite_block:
         case Respawn_anchor:
         case Ender_chest:
         case Ancient_debris:
         case Enchanting_table:
         case Anvil:
         case Chipped_anvil:
         case Damaged_anvil:
         case MixedNoBed:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).obsidianColorBottom.get();
            break;
         case Bedrock:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).bedrockColorBottom.get();
            break;
         default:
            var10000 = (SettingColor)((HoleESPPlus)Modules.get().get(HoleESPPlus.class)).mixedColorBottom.get();
         }

         return var10000;
      }

      public void render(Renderer3D renderer, ShapeMode mode, double height, boolean topQuad, boolean bottomQuad) {
         int x = this.blockPos.method_10263();
         int y = this.blockPos.method_10264();
         int z = this.blockPos.method_10260();
         Color top = this.getTopColor();
         Color bottom = this.getBottomColor();
         int originalTopA = top.a;
         int originalBottompA = bottom.a;
         if (mode.lines()) {
            if (Dir.isNot(this.exclude, (byte)32) && Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y, (double)z, (double)x, (double)y + height, (double)z, bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)32) && Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y, (double)(z + 1), (double)x, (double)y + height, (double)(z + 1), bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)64) && Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y + height, (double)z, bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)64) && Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)(x + 1), (double)y, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), bottom, top);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y, (double)z, (double)(x + 1), (double)y, (double)z, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.line((double)x, (double)y + height, (double)z, (double)(x + 1), (double)y + height, (double)z, top);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.line((double)x, (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), top);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.line((double)x, (double)y, (double)z, (double)x, (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.line((double)x, (double)y + height, (double)z, (double)x, (double)y + height, (double)(z + 1), top);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.line((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y, (double)(z + 1), bottom);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.line((double)(x + 1), (double)y + height, (double)z, (double)(x + 1), (double)y + height, (double)(z + 1), top);
            }
         }

         if (mode.sides()) {
            top.a = originalTopA / 2;
            bottom.a = originalBottompA / 2;
            if (Dir.isNot(this.exclude, (byte)2) && topQuad) {
               renderer.quad((double)x, (double)y + height, (double)z, (double)x, (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), (double)(x + 1), (double)y + height, (double)z, top);
            }

            if (Dir.isNot(this.exclude, (byte)4) && bottomQuad) {
               renderer.quad((double)x, (double)y, (double)z, (double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)(z + 1), (double)(x + 1), (double)y, (double)z, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)8)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)(x + 1), (double)y + height, (double)z, top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)16)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)(z + 1), (double)(x + 1), (double)y + height, (double)(z + 1), top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)32)) {
               renderer.gradientQuadVertical((double)x, (double)y, (double)z, (double)x, (double)y + height, (double)(z + 1), top, bottom);
            }

            if (Dir.isNot(this.exclude, (byte)64)) {
               renderer.gradientQuadVertical((double)(x + 1), (double)y, (double)z, (double)(x + 1), (double)y + height, (double)(z + 1), top, bottom);
            }

            top.a = originalTopA;
            bottom.a = originalBottompA;
         }

      }

      public static enum Type {
         Bedrock,
         Obsidian,
         Crying_obi,
         Netherite_block,
         Respawn_anchor,
         Ender_chest,
         Ancient_debris,
         Enchanting_table,
         Anvil,
         Chipped_anvil,
         Damaged_anvil,
         MixedNoBed,
         Mixed;

         // $FF: synthetic method
         private static HoleESPPlus.Hole.Type[] $values() {
            return new HoleESPPlus.Hole.Type[]{Bedrock, Obsidian, Crying_obi, Netherite_block, Respawn_anchor, Ender_chest, Ancient_debris, Enchanting_table, Anvil, Chipped_anvil, Damaged_anvil, MixedNoBed, Mixed};
         }
      }
   }
}
