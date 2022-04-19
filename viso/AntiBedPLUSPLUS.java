package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
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
import net.minecraft.class_2680;
import net.minecraft.class_2338.class_2339;

public class AntiBedPLUSPLUS extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgOp;
   private final Setting<Boolean> doubleHeight;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<Boolean> onlyWhenSneaking;
   private final Setting<Boolean> turnOff;
   private final Setting<Boolean> center;
   private final Setting<Boolean> disableOnJump;
   private final Setting<Boolean> disableOnYChange;
   private final Setting<Boolean> toggleOnComplete;
   private final Setting<Boolean> rotate;
   private final Setting<List<class_2248>> blocks;
   private final class_2339 blockPos;
   private boolean return_;

   public AntiBedPLUSPLUS() {
      super(Categories.NewCombat, "Anti-Bed-Mode", "prevents the player from causing damage from the beds");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.sgOp = this.settings.createGroup("_Option_");
      this.doubleHeight = this.sgOp.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("head")).description(".")).defaultValue(true)).build());
      this.onlyOnGround = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-ground")).description("Works only when you standing on blocks.")).defaultValue(true)).build());
      this.onlyWhenSneaking = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-when-sneaking")).description("Places blocks only after sneaking.")).defaultValue(false)).build());
      this.turnOff = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("turn-off")).description("Toggles off when all blocks are placed.")).defaultValue(false)).build());
      this.center = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("center")).description("Teleports you to the center of the block.")).defaultValue(true)).build());
      this.disableOnJump = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-jump")).description("Automatically disables when you jump.")).defaultValue(true)).build());
      this.disableOnYChange = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("disable-on-y-change")).description("Automatically disables when your y level (step, jumping, atc).")).defaultValue(true)).build());
      this.toggleOnComplete = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("toggle")).description("Toggles off when all blocks are placed.")).defaultValue(false)).build());
      this.rotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("rotate")).description("Automatically faces towards the obsidian being placed.")).defaultValue(true)).build());
      this.blocks = this.sgGeneral.add(((BlockListSetting.Builder)((BlockListSetting.Builder)(new BlockListSetting.Builder()).name("block")).description("What blocks to use for surround.")).defaultValue(class_2246.field_10363).filter(this::blockFilter).build());
      this.blockPos = new class_2339();
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
            boolean p1 = this.place(0, 0, 0);
            if (!this.return_) {
               boolean p2 = this.place(0, 0, 0);
               boolean doubleHeightPlaced = false;
               if ((Boolean)this.doubleHeight.get()) {
                  boolean p6 = this.place(0, 1, 0);
                  if (this.return_) {
                     return;
                  }

                  if (p6) {
                     doubleHeightPlaced = true;
                  }
               }

               if ((Boolean)this.turnOff.get() && p1 && p2 && (doubleHeightPlaced || !(Boolean)this.doubleHeight.get())) {
                  this.toggle();
               }

            }
         }
      }
   }

   private boolean blockFilter(class_2248 block) {
      return block == class_2246.field_10363 || block == class_2246.field_10417 || block == class_2246.field_10494 || block == class_2246.field_10057 || block == class_2246.field_10066;
   }

   private boolean place(int x, int y, int z) {
      this.setBlockPos(x, y, z);
      class_2680 blockState = this.mc.field_1687.method_8320(this.blockPos);
      if (!blockState.method_26207().method_15800()) {
         return true;
      } else {
         if (BlockUtils.place(this.blockPos, InvUtils.findInHotbar((itemStack) -> {
            return ((List)this.blocks.get()).contains(class_2248.method_9503(itemStack.method_7909()));
         }), (Boolean)this.rotate.get(), 100, true)) {
            this.return_ = true;
         }

         return false;
      }
   }

   private void setBlockPos(int x, int y, int z) {
      this.blockPos.method_10102(this.mc.field_1724.method_23317() + (double)x, this.mc.field_1724.method_23318() + (double)y, this.mc.field_1724.method_23321() + (double)z);
   }
}
