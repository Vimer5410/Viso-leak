package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1802;

public class Modeimpapovskii extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgPlace;
   private final Setting<Double> range;
   private final Setting<SortPriority> priority;
   private final Setting<Boolean> doubles;
   private final Setting<Boolean> doubless;
   private final Setting<Boolean> rotate;
   private class_1657 target;

   public Modeimpapovskii() {
      super(Categories.NewCombat, "Mode-Lever-Kick", "");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.sgPlace = this.settings.createGroup("_Place_");
      this.range = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Place-Range")).description("The maximum distance to target players.")).defaultValue(4.0D).range(0.0D, 5.0D).sliderMax(5.0D).build());
      this.priority = this.sgPlace.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Priority")).description("How to select the player to target.")).defaultValue(SortPriority.LowestDistance)).build());
      this.doubles = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("SelfTrap")).description("")).defaultValue(true)).build());
      this.doubless = this.sgPlace.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Surround")).description("")).defaultValue(true)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Rotates towards the webs when placing.")).defaultValue(false)).build());
      this.target = null;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
         this.target = TargetUtils.getPlayerTarget((Double)this.range.get(), (SortPriority)this.priority.get());
      }

      if (!TargetUtils.isBadTarget(this.target, (Double)this.range.get())) {
         BlockUtils.place(this.target.method_24515(), InvUtils.findInHotbar(class_1802.field_8281), (Boolean)this.rotate.get(), -1, false);
         if ((Boolean)this.doubles.get()) {
            BlockUtils.place(this.target.method_24515().method_10069(0, 2, 0), InvUtils.findInHotbar(class_1802.field_8281), (Boolean)this.rotate.get(), 0, false);
            BlockUtils.place(this.target.method_24515().method_10069(0, 1, 0), InvUtils.findInHotbar(class_1802.field_8865), (Boolean)this.rotate.get(), 0, false);
         }

         if ((Boolean)this.doubless.get()) {
            BlockUtils.place(this.target.method_24515().method_10069(1, -1, 0), InvUtils.findInHotbar(class_1802.field_8865), (Boolean)this.rotate.get(), 0, false);
            BlockUtils.place(this.target.method_24515().method_10069(-1, -1, 0), InvUtils.findInHotbar(class_1802.field_8865), (Boolean)this.rotate.get(), 0, false);
            BlockUtils.place(this.target.method_24515().method_10069(0, -1, 1), InvUtils.findInHotbar(class_1802.field_8865), (Boolean)this.rotate.get(), 0, false);
            BlockUtils.place(this.target.method_24515().method_10069(0, -1, -1), InvUtils.findInHotbar(class_1802.field_8865), (Boolean)this.rotate.get(), 0, false);
         }

      }
   }
}
