package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IPlayerInteractEntityC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IPlayerMoveC2SPacket;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2879;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2828.class_2829;

public class Criticalsplus extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Criticalsplus.Mode> mode;
   private final Setting<Boolean> ka;
   private class_2824 attackPacket;
   private class_2879 swingPacket;
   private boolean sendPackets;
   private int sendTimer;

   public Criticalsplus() {
      super(Categories.NewCombat, "Criticals-V2", "Performs critical attacks when you hit your target.");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("The mode on how Criticals will function.")).defaultValue(Criticalsplus.Mode.Packet)).build());
      this.ka = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-mode-sword")).description("Only performs crits when using killaura.")).defaultValue(false)).build());
   }

   public void onActivate() {
      this.attackPacket = null;
      this.swingPacket = null;
      this.sendPackets = false;
      this.sendTimer = 0;
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof IPlayerInteractEntityC2SPacket) {
         IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket)var3;
         if (packet.getType() == class_5907.field_29172) {
            if (this.skipCrit()) {
               return;
            }

            class_1297 entity = packet.getEntity();
            if (!(entity instanceof class_1309) || entity != ((KillAura)Modules.get().get(KillAura.class)).getTarget() && entity != ((ModeKill)Modules.get().get(ModeKill.class)).getTarget() && (Boolean)this.ka.get()) {
               return;
            }

            switch((Criticalsplus.Mode)this.mode.get()) {
            case Packet:
               this.sendPacket(0.0625D);
               this.sendPacket(0.0D);
               return;
            case Bypass:
               this.sendPacket(0.11D);
               this.sendPacket(0.1100013579D);
               this.sendPacket(1.3579E-6D);
               return;
            default:
               if (!this.sendPackets) {
                  this.sendPackets = true;
                  this.sendTimer = this.mode.get() == Criticalsplus.Mode.Jump ? 6 : 4;
                  this.attackPacket = (class_2824)event.packet;
                  if (this.mode.get() == Criticalsplus.Mode.Jump) {
                     this.mc.field_1724.method_6043();
                  } else {
                     ((IVec3d)this.mc.field_1724.method_18798()).setY(0.25D);
                  }

                  event.cancel();
               }

               return;
            }
         }
      }

      if (event.packet instanceof class_2879 && this.mode.get() != Criticalsplus.Mode.Packet) {
         if (this.skipCrit()) {
            return;
         }

         if (this.sendPackets && this.swingPacket == null) {
            this.swingPacket = (class_2879)event.packet;
            event.cancel();
         }
      }

   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.sendPackets) {
         if (this.sendTimer <= 0) {
            this.sendPackets = false;
            if (this.attackPacket == null || this.swingPacket == null) {
               return;
            }

            this.mc.method_1562().method_2883(this.attackPacket);
            this.mc.method_1562().method_2883(this.swingPacket);
            this.attackPacket = null;
            this.swingPacket = null;
         } else {
            --this.sendTimer;
         }
      }

   }

   private void sendPacket(double height) {
      double x = this.mc.field_1724.method_23317();
      double y = this.mc.field_1724.method_23318();
      double z = this.mc.field_1724.method_23321();
      class_2828 packet = new class_2829(x, y + height, z, false);
      ((IPlayerMoveC2SPacket)packet).setTag(1337);
      this.mc.field_1724.field_3944.method_2883(packet);
   }

   private boolean skipCrit() {
      return !this.mc.field_1724.method_24828() || this.mc.field_1724.method_5869() || this.mc.field_1724.method_5771() || this.mc.field_1724.method_6101();
   }

   public String getInfoString() {
      return ((Criticalsplus.Mode)this.mode.get()).name();
   }

   public static enum Mode {
      Packet,
      Bypass,
      Jump,
      MiniJump;

      // $FF: synthetic method
      private static Criticalsplus.Mode[] $values() {
         return new Criticalsplus.Mode[]{Packet, Bypass, Jump, MiniJump};
      }
   }
}
