package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Arrays;
import java.util.List;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2530;
import net.minecraft.class_2620;
import net.minecraft.class_2626;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2846.class_2847;

public class SelfProrect extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Boolean> tnt;
   private final Setting<Boolean> crystalHead;
   private final Setting<Boolean> rotate;

   public SelfProrect() {
      super(Categories.NewCombat, "Self-Prorect", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.tnt = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-tnt-aura")).description("Break near tnt blocks")).defaultValue(true)).build());
      this.crystalHead = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-crystal-head")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Look at block or crystal.")).defaultValue(false)).build());
   }

   @EventHandler
   private void a(PacketEvent.Receive e) {
      if (this.online()) {
         class_2338 p;
         List safe;
         if ((Boolean)this.crystalHead.get() && e.packet instanceof class_2620) {
            class_2620 w = (class_2620)e.packet;
            p = this.mc.field_1724.method_24515();
            safe = Arrays.asList(p.method_10084(), p.method_10086(2), p.method_10086(3));
            if (safe.contains(w.method_11277())) {
               safe.forEach((s) -> {
                  this.place_obsidian((class_2338)s);
               });
               this.mc.field_1687.method_18112().forEach((s) -> {
                  if (s instanceof class_1511 && safe.contains(s.method_24515())) {
                     this.kill(s);
                  }

               });
            }
         }

         if ((Boolean)this.tnt.get() && e.packet instanceof class_2626) {
            class_2626 w = (class_2626)e.packet;
            if ((Boolean)this.tnt.get() && w.method_11308().method_26204() instanceof class_2530) {
               p = this.mc.field_1724.method_24515();
               safe = Arrays.asList(p, p.method_10074(), p.method_10084(), p.method_10086(2), p.method_10086(3), p.method_10078(), p.method_10067(), p.method_10095(), p.method_10072(), p.method_10084().method_10078(), p.method_10084().method_10067(), p.method_10084().method_10095(), p.method_10084().method_10072(), p.method_10086(2).method_10078(), p.method_10086(2).method_10067(), p.method_10086(2).method_10095(), p.method_10086(2).method_10072());
               class_2338 a = w.method_11309();
               if (safe.contains(a)) {
                  this.look(a);
                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12968, a, class_2350.field_11036));
                  this.mc.method_1562().method_2883(new class_2846(class_2847.field_12973, a, class_2350.field_11036));
                  this.place_obsidian(a);
               }
            }
         }
      }

   }

   @EventHandler
   private void a(EntityAddedEvent e) {
      if (this.online() && (Boolean)this.crystalHead.get() && e.entity instanceof class_1511) {
         class_2338 p = this.mc.field_1724.method_24515();
         List<class_2338> safe = Arrays.asList(p.method_10086(2), p.method_10086(3), p.method_10086(4), p.method_10086(2).method_10078(), p.method_10086(2).method_10067(), p.method_10086(2).method_10095(), p.method_10086(2).method_10072());
         class_2338 a = e.entity.method_24515();
         if (safe.contains(a)) {
            this.place_obsidian(a.method_10074());
            this.kill(e.entity);
            this.place_obsidian(a);
            this.place_obsidian(a.method_10084());
         }
      }

   }

   private boolean online() {
      return this.mc.field_1687 != null && this.mc.field_1724 != null && this.mc.field_1687.method_18456().size() > 1;
   }

   private void kill(class_1297 a) {
      this.look(a.method_24515());
      this.mc.field_1761.method_2918(this.mc.field_1724, a);
      a.method_5650(class_5529.field_26998);
   }

   private void place_obsidian(class_2338 a) {
      if (BlockUtils.canPlace(a) && this.mc.field_1724.method_24515().method_10264() - a.method_10264() <= 2) {
         int obsidian = InvUtils.findInHotbar(class_1802.field_8281).getSlot();
         if (obsidian > -1) {
            this.look(a);
            int pre = this.mc.field_1724.method_31548().field_7545;
            this.swap(obsidian);
            this.mc.method_1562().method_2883(new class_2885(class_1268.field_5808, new class_3965(this.mc.field_1724.method_19538(), class_2350.field_11033, a, true)));
            this.swap(pre);
         }
      }

   }

   private void swap(int a) {
      if (a != this.mc.field_1724.method_31548().field_7545) {
         this.mc.method_1562().method_2883(new class_2868(a));
         this.mc.field_1724.method_31548().field_7545 = a;
      }

   }

   private void look(class_2338 a) {
      if ((Boolean)this.rotate.get()) {
         class_243 hitPos = new class_243(0.0D, 0.0D, 0.0D);
         ((IVec3d)hitPos).set((double)a.method_10263() + 0.5D, (double)a.method_10264() + 0.5D, (double)a.method_10260() + 0.5D);
         Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos));
      }

   }
}
