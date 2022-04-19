package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2793;

public class PredictChorus extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> accurate;
   private final Setting<Boolean> onSneak;
   private final Setting<Keybind> key;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> tracerColor;
   private int teleportId;
   private class_2338 bpos;
   private class_243 pos;

   public PredictChorus() {
      super(Categories.NewCombat, "Predict-Chorus", "Predicts the spot where the chorus-fruit will teleport you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.accurate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate")).description("Whether or not to render the position accurate.")).defaultValue(false)).build());
      this.onSneak = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("on-sneak")).description("Only predicts when you are sneaking.")).defaultValue(true)).build());
      this.key = this.sgGeneral.add(((KeybindSetting.Builder)((KeybindSetting.Builder)((KeybindSetting.Builder)(new KeybindSetting.Builder()).name("tp-key")).description("The key that teleports you to the current spot.")).defaultValue(Keybind.fromKey(-1))).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the target block rendering.")).defaultValue(new SettingColor(255, 255, 255, 10))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the target block rendering.")).defaultValue(new SettingColor(255, 255, 255))).build());
      this.tracerColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tracer-color")).description("The color of the tracer.")).defaultValue(new SettingColor(255, 255, 255, 255))).build());
   }

   public void onActivate() {
      this.teleportId = -1;
      this.bpos = null;
      this.pos = null;
   }

   @EventHandler
   private void onPreTick(TickEvent.Pre event) {
      if ((!this.mc.field_1724.method_5715() && (Boolean)this.onSneak.get() || ((Keybind)this.key.get()).isPressed()) && this.teleportId != -1) {
         this.mc.method_1562().method_2883(new class_2793(this.teleportId));
         this.teleportId = -1;
         this.bpos = null;
         this.pos = null;
      }

   }

   @EventHandler
   private void onReceivePacket(PacketEvent.Receive event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2708) {
         class_2708 packet = (class_2708)var3;
         if ((this.mc.field_1724.method_5715() || !(Boolean)this.onSneak.get()) && this.mc.field_1724.method_6047().method_7909() == class_1802.field_8233 && this.mc.field_1724.method_6115() && (this.mc.field_1724.method_6047().method_7909().method_19263() || this.mc.field_1724.method_6079().method_7909().method_19263())) {
            this.teleportId = packet.method_11737();
            this.bpos = new class_2338(packet.method_11734(), packet.method_11735(), packet.method_11738());
            this.pos = new class_243(packet.method_11734(), packet.method_11735(), packet.method_11738());
            event.cancel();
            return;
         }
      }

   }

   @EventHandler
   private void onSentPacket(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2793) {
         class_2793 packet = (class_2793)var3;
         if (packet.method_12086() == this.teleportId) {
            event.cancel();
            this.teleportId = -1;
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.bpos != null && !(Boolean)this.accurate.get()) {
         event.renderer.box((class_2338)this.bpos, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, (double)this.bpos.method_10263() + 0.5D, (double)this.bpos.method_10264() + 0.5D, (double)this.bpos.method_10260() + 0.5D, (Color)this.tracerColor.get());
      } else if (this.pos != null && (Boolean)this.accurate.get()) {
         event.renderer.box(this.pos.field_1352 - 0.25D, this.pos.field_1351, this.pos.field_1350 - 0.25D, this.pos.field_1352 + 0.25D, this.pos.field_1351 + 1.5D, this.pos.field_1350 + 0.25D, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         event.renderer.line(RenderUtils.center.field_1352, RenderUtils.center.field_1351, RenderUtils.center.field_1350, this.pos.method_10216(), this.pos.method_10214() + 0.75D, this.pos.method_10215(), (Color)this.tracerColor.get());
      }

   }
}
