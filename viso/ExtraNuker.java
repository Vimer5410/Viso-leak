package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1829;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_259;
import net.minecraft.class_2846;
import net.minecraft.class_2846.class_2847;

public class ExtraNuker extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup gsize;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<ExtraNuker.eType> itemsaver;
   private final Setting<Boolean> sword;
   private final Setting<Boolean> checkchunk;
   private final Setting<Boolean> ignoreChests;
   private final Setting<ExtraNuker.SortMode> sortMode;
   private final Setting<Integer> spamlimit;
   private final Setting<Double> lagg;
   private final Setting<Double> Distance;
   private final Setting<Boolean> onlySelected;
   private final Setting<List<class_2248>> selectedBlocks;
   private final Setting<Integer> xmin;
   private final Setting<Integer> xmax;
   private final Setting<Integer> zmin;
   private final Setting<Integer> zmax;
   private final Setting<Integer> ymin;
   private final Setting<Integer> ymax;
   int limit;
   byte pause;
   private final List<class_2338> blocks;

   public ExtraNuker() {
      super(Categories.NewCombat, "Nuker-V2", "Breaks a large amount of specified blocks around you.");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.gsize = this.settings.createGroup("_Size_");
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you standing on blocks.")).defaultValue(true)).build());
      this.itemsaver = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("item-saver")).description("Prevent destruction of tools.")).defaultValue(ExtraNuker.eType.Replace)).build());
      this.sword = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("stop-on-sword")).description("Pause nuker if sword in main hand.")).defaultValue(true)).build());
      this.checkchunk = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chunk-border")).description("Break blocks in only current chunk.")).defaultValue(false)).build());
      this.ignoreChests = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-chests")).description("Ignore chests and shulker box.")).defaultValue(true)).build());
      this.sortMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("sort-mode")).description("The blocks you want to mine first.")).defaultValue(ExtraNuker.SortMode.Closest)).build());
      this.spamlimit = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("speed")).description("Block break speed.")).defaultValue(29)).min(1).sliderMin(1).sliderMax(100).build());
      this.lagg = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("stop-on-lags")).description("Pause on server lagging. (Time since last tick)")).defaultValue(0.8D).min(0.1D).max(5.0D).sliderMin(0.1D).sliderMax(5.0D).build());
      this.Distance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("distance")).description("Maximum distance.")).min(1.0D).defaultValue(6.6D).build());
      this.onlySelected = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-selected")).description("Only mines your selected blocks.")).defaultValue(false)).build());
      this.selectedBlocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("selected-blocks")).description("The certain type of blocks you want to mine.")).defaultValue(new ArrayList(0))).build());
      this.xmin = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("x-min")).defaultValue(1)).min(0).max(6).sliderMin(0).sliderMax(6).build());
      this.xmax = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("x-max")).defaultValue(1)).min(0).max(6).sliderMin(0).sliderMax(6).build());
      this.zmin = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("z-min")).defaultValue(1)).min(0).max(6).sliderMin(0).sliderMax(6).build());
      this.zmax = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("z-max")).defaultValue(1)).min(0).max(6).sliderMin(0).sliderMax(6).build());
      this.ymin = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("up")).defaultValue(1)).min(1).max(6).sliderMin(1).sliderMax(6).build());
      this.ymax = this.gsize.add(((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("down")).defaultValue(0)).min(0).max(7).sliderMin(0).sliderMax(7).build());
      this.limit = 0;
      this.pause = 0;
      this.blocks = new ArrayList();
   }

   public void onActivate() {
      this.limit = 0;
      this.pause = 0;
   }

   @EventHandler(
      priority = Integer.MIN_VALUE
   )
   private void ADD_LIMIT(PacketEvent.Send e) {
      if (!e.isCancelled()) {
         ++this.limit;
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      try {
         this.blocks.clear();
         if (this.pause > 0) {
            --this.pause;
            return;
         }

         if ((Boolean)this.onlyOnGround.get() && !this.mc.field_1724.method_24828()) {
            return;
         }

         if ((double)TickRate.INSTANCE.getTimeSinceLastTick() >= (Double)this.lagg.get()) {
            return;
         }

         if ((Boolean)this.sword.get() && this.mc.field_1724.method_6047().method_7909() instanceof class_1829) {
            return;
         }

         this.limit = 0;
         int px = this.mc.field_1724.method_24515().method_10263();
         int py = this.mc.field_1724.method_24515().method_10264();
         int pz = this.mc.field_1724.method_24515().method_10260();

         for(int x = px - (Integer)this.xmin.get(); x <= px + (Integer)this.xmax.get(); ++x) {
            for(int z = pz - (Integer)this.zmin.get(); z <= pz + (Integer)this.zmax.get(); ++z) {
               for(int y = py - (Integer)this.ymax.get(); y <= py + (Integer)this.ymin.get() - 1; ++y) {
                  class_2338 pos = new class_2338(x, y, z);
                  class_2248 b = this.mc.field_1687.method_8320(pos).method_26204();
                  if ((!(Boolean)this.checkchunk.get() || this.mc.field_1687.method_22350(pos).method_12004() == this.mc.field_1687.method_22350(this.mc.field_1724.method_24515()).method_12004()) && this.mc.field_1687.method_8320(pos).method_26218(this.mc.field_1687, pos) != class_259.method_1073() && b != class_2246.field_9987 && !(this.distance((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260()) >= (Double)this.Distance.get()) && (!(Boolean)this.onlySelected.get() || ((List)this.selectedBlocks.get()).contains(b)) && (!(Boolean)this.ignoreChests.get() || b != class_2246.field_10034 && b != class_2246.field_10380 && b != class_2246.field_10443 && b != class_2246.field_10603 && !b.toString().contains("_shulker_box"))) {
                     this.blocks.add(pos);
                  }
               }
            }
         }

         double pX = this.mc.field_1724.method_23317() - 0.5D;
         double pY = this.mc.field_1724.method_23318();
         double pZ = this.mc.field_1724.method_23321() - 0.5D;
         if (this.sortMode.get() != ExtraNuker.SortMode.None) {
            this.blocks.sort(Comparator.comparingDouble((value) -> {
               return Utils.squaredDistance(pX, pY, pZ, (double)value.method_10263(), (double)value.method_10264(), (double)value.method_10260()) * (double)(this.sortMode.get() == ExtraNuker.SortMode.Closest ? 1 : -1);
            }));
         }

         int q;
         switch((ExtraNuker.eType)this.itemsaver.get()) {
         case Save:
            if (this.isbreak()) {
               this.warning("save mode...!", new Object[0]);
               this.toggle();
               return;
            }
         case Replace:
            if (this.isbreak()) {
               if (this.swap_item()) {
                  this.pause = 5;
                  return;
               }

               this.warning("replace mode...!", new Object[0]);
               this.toggle();
               return;
            }
         case None:
         default:
            q = 0;
         }

         while(q < this.blocks.size()) {
            if (this.limit > (Integer)this.spamlimit.get()) {
               return;
            }

            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, (class_2338)this.blocks.get(q), class_2350.field_11036));
            this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, (class_2338)this.blocks.get(q), class_2350.field_11036));
            ++q;
         }
      } catch (Exception var12) {
         var12.fillInStackTrace();
      }

   }

   private boolean isbreak() {
      return this.mc.field_1724.method_6047().method_7919() != 0 && this.mc.field_1724.method_6047().method_7936() - this.mc.field_1724.method_6047().method_7919() < 31;
   }

   private boolean swap_item() {
      class_1792 item = this.mc.field_1724.method_6047().method_7909();

      for(int x = 0; x < this.mc.field_1724.method_31548().method_5439(); ++x) {
         if (this.mc.field_1724.method_31548().method_5438(x).method_7909() == item && this.mc.field_1724.method_31548().method_5438(x).method_7936() - this.mc.field_1724.method_31548().method_5438(x).method_7919() >= 31) {
            Ezz.clickSlot(Ezz.invIndexToSlotId(x), this.mc.field_1724.method_31548().field_7545, class_1713.field_7791);
            return true;
         }
      }

      return false;
   }

   private double distance(double x, double y, double z) {
      if (x > 0.0D) {
         x += 0.5D;
      } else {
         x -= 0.5D;
      }

      if (y > 0.0D) {
         y += 0.5D;
      } else {
         y -= 0.5D;
      }

      if (z > 0.0D) {
         z += 0.5D;
      } else {
         z -= 0.5D;
      }

      double d = this.mc.field_1724.method_19538().method_10216() - x;
      if (d < 0.0D) {
         --d;
      }

      double e = this.mc.field_1724.method_19538().method_10214() + 1.0D - y;
      double f = this.mc.field_1724.method_19538().method_10215() - z;
      if (f < 0.0D) {
         --f;
      }

      return Math.sqrt(d * d + e * e + f * f);
   }

   public static enum eType {
      None,
      Save,
      Replace;

      // $FF: synthetic method
      private static ExtraNuker.eType[] $values() {
         return new ExtraNuker.eType[]{None, Save, Replace};
      }
   }

   public static enum SortMode {
      None,
      Closest,
      Furthest;

      // $FF: synthetic method
      private static ExtraNuker.SortMode[] $values() {
         return new ExtraNuker.SortMode[]{None, Closest, Furthest};
      }
   }
}
