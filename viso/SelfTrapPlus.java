package meteordevelopment.meteorclient.systems.modules.viso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2828.class_2829;

public class SelfTrapPlus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> tickDelay;
   private final Setting<SelfTrapPlus.ecenter> center;
   private final Setting<SelfTrapPlus.SurrMode> mode;
   private final Setting<SelfTrapPlus.Version> version;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> selfProtector;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> disableOnJump;
   private final Setting<Boolean> rotate;
   private static class_310 mc = class_310.method_1551();
   private int ticks;
   private class_1657 target;
   class_2338 pos;
   private static final ArrayList<class_243> norm = new ArrayList<class_243>() {
      {
         this.add(new class_243(0.0D, 2.0D, 0.0D));
         this.add(new class_243(1.0D, 1.0D, 0.0D));
         this.add(new class_243(-1.0D, 1.0D, 0.0D));
         this.add(new class_243(0.0D, 1.0D, 1.0D));
         this.add(new class_243(0.0D, 1.0D, -1.0D));
      }
   };
   private static final class_2339 blockPos = new class_2339();

   public SelfTrapPlus() {
      super(Categories.NewCombat, "Self-Trap-V2", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.tickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("Delay")).description("Delay per ticks.")).defaultValue(1)).min(0).max(20).sliderMin(0).sliderMax(20).build());
      this.center = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("centerTP")).description("Teleport to center block.")).defaultValue(SelfTrapPlus.ecenter.legit)).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("Mode of the surround.")).defaultValue(SelfTrapPlus.SurrMode.Normal)).build());
      this.version = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("version")).description("Version of server where u will be pvp.")).defaultValue(SelfTrapPlus.Version.Old)).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block")).description("What blocks to use for surround.")).defaultValue(Collections.singletonList(class_2246.field_10540))).filter(this::blockFilter).build());
      this.selfProtector = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("breaks-crystal")).description("Automatically breaks crystal near ur surround.")).defaultValue(true)).build());
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you standing on blocks.")).defaultValue(false)).build());
      this.disableOnJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-jump")).description("Automatically disables when you jump.")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the obsidian being placed.")).defaultValue(false)).build());
      this.pos = null;
   }

   public void onActivate() {
      this.ticks = 0;
      if (this.center.get() == SelfTrapPlus.ecenter.fast) {
         double tx = 0.0D;
         double tz = 0.0D;
         class_243 p = mc.field_1724.method_19538();
         if (p.field_1352 > 0.0D && this.gp(p.field_1352) < 3L) {
            tx = 0.3D;
         }

         if (p.field_1352 > 0.0D && this.gp(p.field_1352) > 6L) {
            tx = -0.3D;
         }

         if (p.field_1352 < 0.0D && this.gp(p.field_1352) < 3L) {
            tx = -0.3D;
         }

         if (p.field_1352 < 0.0D && this.gp(p.field_1352) > 6L) {
            tx = 0.3D;
         }

         if (p.field_1350 > 0.0D && this.gp(p.field_1350) < 3L) {
            tz = 0.3D;
         }

         if (p.field_1350 > 0.0D && this.gp(p.field_1350) > 6L) {
            tz = -0.3D;
         }

         if (p.field_1350 < 0.0D && this.gp(p.field_1350) < 3L) {
            tz = -0.3D;
         }

         if (p.field_1350 < 0.0D && this.gp(p.field_1350) > 6L) {
            tz = 0.3D;
         }

         if (tx != 0.0D || tz != 0.0D) {
            double posx = mc.field_1724.method_23317() + tx;
            double posz = mc.field_1724.method_23321() + tz;
            mc.field_1724.method_30634(posx, mc.field_1724.method_23318(), posz);
            mc.field_1724.field_3944.method_2883(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), mc.field_1724.method_24828()));
         }
      }

   }

   private long gp(double v) {
      BigDecimal v1 = BigDecimal.valueOf(v);
      BigDecimal v2 = v1.remainder(BigDecimal.ONE);
      return (long)Byte.parseByte(String.valueOf(String.valueOf(v2).replace("0.", "").replace("-", "").charAt(0)));
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      class_2338 bb;
      if (this.ticks < (Integer)this.tickDelay.get()) {
         ++this.ticks;
      } else {
         this.ticks = 0;
         if (this.center.get() == SelfTrapPlus.ecenter.legit) {
            double tx = 0.0D;
            double tz = 0.0D;
            class_243 p = mc.field_1724.method_19538();
            if (p.field_1352 > 0.0D && this.gp(p.field_1352) < 3L) {
               tx = 0.185D;
            }

            if (p.field_1352 > 0.0D && this.gp(p.field_1352) > 6L) {
               tx = -0.185D;
            }

            if (p.field_1352 < 0.0D && this.gp(p.field_1352) < 3L) {
               tx = -0.185D;
            }

            if (p.field_1352 < 0.0D && this.gp(p.field_1352) > 6L) {
               tx = 0.185D;
            }

            if (p.field_1350 > 0.0D && this.gp(p.field_1350) < 3L) {
               tz = 0.185D;
            }

            if (p.field_1350 > 0.0D && this.gp(p.field_1350) > 6L) {
               tz = -0.185D;
            }

            if (p.field_1350 < 0.0D && this.gp(p.field_1350) < 3L) {
               tz = -0.185D;
            }

            if (p.field_1350 < 0.0D && this.gp(p.field_1350) > 6L) {
               tz = 0.185D;
            }

            if (tx != 0.0D || tz != 0.0D) {
               double posx = mc.field_1724.method_23317() + tx;
               double posz = mc.field_1724.method_23321() + tz;
               mc.field_1724.method_30634(posx, mc.field_1724.method_23318(), posz);
               mc.field_1724.field_3944.method_2883(new class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), mc.field_1724.method_24828()));
               return;
            }
         }

         if ((Boolean)this.disableOnJump.get() && mc.field_1690.field_1903.method_1434()) {
            this.toggle();
            return;
         }

         if ((Boolean)this.onlyOnGround.get() && !mc.field_1724.method_24828()) {
            return;
         }

         if (this.version.get() == SelfTrapPlus.Version.New) {
            if (this.mode.get() == SelfTrapPlus.SurrMode.Normal) {
               if (this.p(0, 2, 0)) {
                  return;
               }

               if (this.p(1, 1, 0)) {
                  return;
               }

               if (this.p(-1, 1, 0)) {
                  return;
               }

               if (this.p(0, 1, 1)) {
                  return;
               }

               if (this.p(0, 1, -1)) {
                  return;
               }
            }
         } else if (this.version.get() == SelfTrapPlus.Version.Old) {
            if ((Boolean)this.disableOnJump.get() && mc.field_1690.field_1903.method_1434()) {
               this.toggle();
               return;
            }

            if ((Boolean)this.onlyOnGround.get() && !mc.field_1724.method_24828()) {
               return;
            }

            if (!isVecComplete(this.getSurrDesign())) {
               class_2338 ppos = mc.field_1724.method_24515();
               Iterator var4 = this.getSurrDesign().iterator();

               while(var4.hasNext()) {
                  class_243 b = (class_243)var4.next();
                  bb = ppos.method_10080(b.field_1352, b.field_1351, b.field_1350);
                  if (getBlock(bb) == class_2246.field_10124 && (Boolean)this.selfProtector.get()) {
                     BlockUtils.place(bb, InvUtils.findInHotbar((itemStack) -> {
                        return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
                     }), (Boolean)this.rotate.get(), 100, false);
                  }
               }
            }
         }
      }

      if ((Boolean)this.selfProtector.get()) {
         Iterator var14 = mc.field_1687.method_18112().iterator();

         while(var14.hasNext()) {
            class_1297 entity = (class_1297)var14.next();
            if (entity instanceof class_1511) {
               int slot1 = InvUtils.findInHotbar(class_1802.field_8281).getSlot();
               bb = entity.method_24515();
               if (this.isDangerousCrystal(bb)) {
                  mc.field_1761.method_2918(mc.field_1724, entity);
                  entity.method_5650(class_5529.field_26998);
                  Ezz.BlockPlace(bb, slot1, (Boolean)this.rotate.get());
                  return;
               }
            }
         }
      }

   }

   private ArrayList<class_243> getSurrDesign() {
      ArrayList<class_243> surrDesign = new ArrayList(norm);
      return surrDesign;
   }

   private boolean isDangerousCrystal(class_2338 bp) {
      class_2338 ppos = mc.field_1724.method_24515();
      Iterator var3 = this.getSurrDesign().iterator();

      class_2338 bb;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         class_243 b = (class_243)var3.next();
         bb = ppos.method_10080(b.field_1352, b.field_1351, b.field_1350);
      } while(bp.equals(bb) || !(distanceBetween(bb, bp) <= 2.0D));

      return true;
   }

   private boolean p(int x, int y, int z) {
      return Ezz.BlockPlace(Ezz.SetRelative(x, y, z), InvUtils.findInHotbar(class_1802.field_8281).getSlot(), (Boolean)this.rotate.get());
   }

   private boolean e(int x, int y, int z) {
      return Ezz.BlockPlace(Ezz.SetRelative(x, y, z), InvUtils.findInHotbar(class_1802.field_8466).getSlot(), (Boolean)this.rotate.get());
   }

   public static double distanceBetween(class_2338 pos1, class_2338 pos2) {
      double d = (double)(pos1.method_10263() - pos2.method_10263());
      double e = (double)(pos1.method_10264() - pos2.method_10264());
      double f = (double)(pos1.method_10260() - pos2.method_10260());
      return (double)class_3532.method_15355((float)(d * d + e * e + f * f));
   }

   public static boolean isVecComplete(ArrayList<class_243> vlist) {
      class_2338 ppos = mc.field_1724.method_24515();
      Iterator var2 = vlist.iterator();

      while(var2.hasNext()) {
         class_243 b = (class_243)var2.next();
         class_2338 bb = ppos.method_10080(b.field_1352, b.field_1351, b.field_1350);
         if (getBlock(bb) == class_2246.field_10124) {
            return false;
         }
      }

      return true;
   }

   public static class_2248 getBlock(class_2338 p) {
      return p == null ? null : mc.field_1687.method_8320(p).method_26204();
   }

   private boolean blockFilter(class_2248 block) {
      return block == class_2246.field_10540;
   }

   public static enum ecenter {
      fast,
      legit,
      NoTP;

      // $FF: synthetic method
      private static SelfTrapPlus.ecenter[] $values() {
         return new SelfTrapPlus.ecenter[]{fast, legit, NoTP};
      }
   }

   public static enum SurrMode {
      Normal;

      // $FF: synthetic method
      private static SelfTrapPlus.SurrMode[] $values() {
         return new SelfTrapPlus.SurrMode[]{Normal};
      }
   }

   public static enum Version {
      Old,
      New;

      // $FF: synthetic method
      private static SelfTrapPlus.Version[] $values() {
         return new SelfTrapPlus.Version[]{Old, New};
      }
   }

   public static enum antcry {
      Yes,
      No;

      // $FF: synthetic method
      private static SelfTrapPlus.antcry[] $values() {
         return new SelfTrapPlus.antcry[]{Yes, No};
      }
   }
}
