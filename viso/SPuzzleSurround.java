package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Collections;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class SPuzzleSurround extends Module {
   private final SettingGroup sgGay;
   private final SettingGroup sgSettings;
   private final SettingGroup sgRender;
   private final Setting<SPuzzleSurround.under> Under;
   private final Setting<SPuzzleSurround.extra> Extra;
   private final Setting<SPuzzleSurround.doublelol> Double;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> onlyWhenSneaking;
   private final Setting<Boolean> center;
   private final Setting<Boolean> disableOnJump;
   private final Setting<Boolean> disableOnYChange;
   private final Setting<Boolean> rotate;
   private final Setting<Integer> delay;
   private final Setting<List<class_2248>> blocks;
   private final class_2339 blockPos;
   private boolean return_;
   private int timer;
   private class_2338 prevBreakPos;
   private boolean ceved;
   Boolean obb;
   volatile boolean faceplaced;

   public SPuzzleSurround() {
      super(Categories.NewCombat, "Defender-Protect", "protects you from crystals");
      this.sgGay = this.settings.createGroup("General");
      this.sgSettings = this.settings.createGroup("Settings");
      this.sgRender = this.settings.createGroup("Render");
      this.Under = this.sgGay.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("self-trap")).description("I put blocks above and below myself.")).defaultValue(SPuzzleSurround.under.None)).build());
      this.Extra = this.sgGay.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Extra")).description("good building.")).defaultValue(SPuzzleSurround.extra.On)).build());
      this.Double = this.sgGay.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("TRIPLE")).description("builds on 3 blocks.")).defaultValue(SPuzzleSurround.doublelol.None)).build());
      this.onlyOnGround = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you standing on blocks.")).defaultValue(true)).build());
      this.onlyWhenSneaking = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-when-sneaking")).description("Places blocks only after sneaking.")).defaultValue(false)).build());
      this.center = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("center")).description("Teleports you to the center of the block.")).defaultValue(true)).build());
      this.disableOnJump = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-jump")).description("Automatically disables when you jump.")).defaultValue(true)).build());
      this.disableOnYChange = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-y-change")).description("Automatically disables when your y level (step, jumping, atc).")).defaultValue(true)).build());
      this.rotate = this.sgSettings.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the obsidian being placed.")).defaultValue(false)).build());
      this.delay = this.sgSettings.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("place-delay")).description("How many ticks between block placements.")).defaultValue(0)).build());
      this.blocks = this.sgSettings.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block")).description("What blocks to use for surround.")).defaultValue(Collections.singletonList(class_2246.field_10540))).filter(this::blockFilter).build());
      this.blockPos = new class_2339();
      this.ceved = false;
      this.faceplaced = false;
   }

   public void onActivate() {
      if ((Boolean)this.center.get()) {
         PlayerUtils.centerPlayer();
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if ((Boolean)this.disableOnJump.get() && (this.mc.field_1690.field_1903.method_1434() || this.mc.field_1724.field_3913.field_3904) || (Boolean)this.disableOnYChange.get() && this.mc.field_1724.field_6036 < this.mc.field_1724.method_23318()) {
         this.toggle();
      } else if (!(Boolean)this.onlyOnGround.get() || this.mc.field_1724.method_24828()) {
         if (!(Boolean)this.onlyWhenSneaking.get() || this.mc.field_1690.field_1832.method_1434()) {
            this.return_ = false;
            boolean p1 = this.place(0, -1, 0);
            if (!this.return_) {
               boolean p2 = this.place(1, 0, 0);
               if (!this.return_) {
                  boolean p3 = this.place(-1, 0, 0);
                  if (!this.return_) {
                     boolean p4 = this.place(0, 0, 1);
                     if (!this.return_) {
                        boolean p5 = this.place(0, 0, -1);
                        if (!this.return_) {
                           boolean p23;
                           boolean p24;
                           boolean p25;
                           boolean p26;
                           boolean p27;
                           if (this.Under.get() == SPuzzleSurround.under.On) {
                              p23 = this.place(0, 2, 0);
                              if (this.return_) {
                                 return;
                              }

                              p24 = this.place(0, 3, 0);
                              if (this.return_) {
                                 return;
                              }

                              p25 = this.place(0, 4, 0);
                              if (this.return_) {
                                 return;
                              }

                              p26 = this.place(0, -1, 0);
                              if (this.return_) {
                                 return;
                              }

                              p27 = this.place(0, -2, 0);
                              if (this.return_) {
                                 return;
                              }
                           }

                           boolean p28;
                           boolean p29;
                           boolean p30;
                           if (this.Extra.get() == SPuzzleSurround.extra.On) {
                              p23 = this.place(2, 0, 0);
                              if (this.return_) {
                                 return;
                              }

                              p24 = this.place(-2, 0, 0);
                              if (this.return_) {
                                 return;
                              }

                              p25 = this.place(0, 0, 2);
                              if (this.return_) {
                                 return;
                              }

                              p26 = this.place(0, 0, -2);
                              if (this.return_) {
                                 return;
                              }

                              p27 = this.place(1, 0, 1);
                              if (this.return_) {
                                 return;
                              }

                              p28 = this.place(1, 0, -1);
                              if (this.return_) {
                                 return;
                              }

                              p29 = this.place(-1, 0, 1);
                              if (this.return_) {
                                 return;
                              }

                              p30 = this.place(-1, 0, -1);
                              if (this.return_) {
                                 return;
                              }
                           }

                           if (this.Double.get() == SPuzzleSurround.doublelol.On) {
                              p23 = this.place(1, 1, 0);
                              if (this.return_) {
                                 return;
                              }

                              p24 = this.place(-1, 1, 0);
                              if (this.return_) {
                                 return;
                              }

                              p25 = this.place(0, 1, 1);
                              if (this.return_) {
                                 return;
                              }

                              p26 = this.place(0, 1, -1);
                              if (this.return_) {
                                 return;
                              }

                              p27 = this.place(1, 2, 0);
                              if (this.return_) {
                                 return;
                              }

                              p28 = this.place(-1, 2, 0);
                              if (this.return_) {
                                 return;
                              }

                              p29 = this.place(0, 2, 1);
                              if (this.return_) {
                                 return;
                              }

                              p30 = this.place(0, 2, -1);
                              if (this.return_) {
                                 return;
                              }
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean blockFilter(class_2248 block) {
      return block == class_2246.field_10540 || block == class_2246.field_22423 || block == class_2246.field_22108 || block == class_2246.field_10443 || block == class_2246.field_23152;
   }

   private boolean place(int x, int y, int z) {
      if (this.timer >= (Integer)this.delay.get()) {
         this.timer = 0;
         this.setBlockPos(x, y, z);
         class_2680 blockState = this.mc.field_1687.method_8320(this.blockPos);
         if (!blockState.method_26207().method_15800()) {
            return true;
         }

         if (BlockUtils.place(this.blockPos, InvUtils.findInHotbar((itemStack) -> {
            return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
         }), (Boolean)this.rotate.get(), 100, true)) {
            this.return_ = true;
         }
      } else {
         ++this.timer;
      }

      return false;
   }

   private void setBlockPos(int x, int y, int z) {
      this.blockPos.method_10102(this.mc.field_1724.method_23317() + (double)x, this.mc.field_1724.method_23318() + (double)y, this.mc.field_1724.method_23321() + (double)z);
   }

   public static enum under {
      On,
      None;

      // $FF: synthetic method
      private static SPuzzleSurround.under[] $values() {
         return new SPuzzleSurround.under[]{On, None};
      }
   }

   public static enum extra {
      On,
      None;

      // $FF: synthetic method
      private static SPuzzleSurround.extra[] $values() {
         return new SPuzzleSurround.extra[]{On, None};
      }
   }

   public static enum doublelol {
      On,
      None;

      // $FF: synthetic method
      private static SPuzzleSurround.doublelol[] $values() {
         return new SPuzzleSurround.doublelol[]{On, None};
      }
   }
}
