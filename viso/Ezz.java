package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;

public class Ezz {
   private static final class_310 mc = class_310.method_1551();

   public static void clickSlot(int slot, int button, class_1713 action) {
      mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, slot, button, action, mc.field_1724);
   }

   public static Modules get() {
      return (Modules)Systems.get(Modules.class);
   }

   public static int invIndexToSlotId(int invIndex) {
      return invIndex < 9 && invIndex != -1 ? 44 - (8 - invIndex) : invIndex;
   }

   public static void swap(int slot) {
      if (slot != mc.field_1724.method_31548().field_7545 && slot >= 0 && slot < 9) {
         mc.field_1724.method_31548().field_7545 = slot;
      }

   }

   public static boolean equalsBlockPos(class_2338 p1, class_2338 p2) {
      if (p1 != null && p2 != null) {
         if (p1.method_10263() != p2.method_10263()) {
            return false;
         } else if (p1.method_10264() != p2.method_10264()) {
            return false;
         } else {
            return p1.method_10260() == p2.method_10260();
         }
      } else {
         return false;
      }
   }

   public static class_2338 SetRelative(int x, int y, int z) {
      return new class_2338(mc.field_1724.method_23317() + (double)x, mc.field_1724.method_23318() + (double)y, mc.field_1724.method_23321() + (double)z);
   }

   public static boolean BlockPlace(int x, int y, int z, int HotbarSlot, boolean Rotate) {
      return BlockPlace(new class_2338(x, y, z), HotbarSlot, Rotate);
   }

   public static boolean BlockPlace(class_2338 BlockPos, int HotbarSlot, boolean Rotate) {
      if (HotbarSlot == -1) {
         return false;
      } else if (!BlockUtils.canPlace(BlockPos, true)) {
         return false;
      } else {
         int PreSlot = mc.field_1724.method_31548().field_7545;
         swap(HotbarSlot);
         if (Rotate) {
            class_243 hitPos = new class_243(0.0D, 0.0D, 0.0D);
            ((IVec3d)hitPos).set((double)BlockPos.method_10263() + 0.5D, (double)BlockPos.method_10264() + 0.5D, (double)BlockPos.method_10260() + 0.5D);
            Rotations.rotate(Rotations.getYaw(hitPos), Rotations.getPitch(hitPos));
         }

         mc.field_1761.method_2896(mc.field_1724, mc.field_1687, class_1268.field_5808, new class_3965(mc.field_1724.method_19538(), class_2350.field_11033, BlockPos, true));
         swap(PreSlot);
         return true;
      }
   }

   public static double DistanceTo(class_2338 pos) {
      return DistanceTo(pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260());
   }

   public static double DistanceTo(int x, double y, double z) {
      double X = (double)x;
      if (X >= 0.0D) {
         X += 0.5D;
      } else {
         X -= 0.5D;
      }

      double Y;
      if (y >= 0.0D) {
         Y = y + 0.5D;
      } else {
         Y = y - 0.5D;
      }

      double Z;
      if (z >= 0.0D) {
         Z = z + 0.5D;
      } else {
         Z = z - 0.5D;
      }

      double f = mc.field_1724.method_23317() - X;
      double g = mc.field_1724.method_23318() - Y;
      double h = mc.field_1724.method_23321() - Z;
      return Math.sqrt(f * f + g * g + h * h);
   }

   public static void interact(class_2338 pos, int HotbarSlot, class_2350 dir) {
      int PreSlot = mc.field_1724.method_31548().field_7545;
      swap(HotbarSlot);
      mc.field_1761.method_2896(mc.field_1724, mc.field_1687, class_1268.field_5808, new class_3965(mc.field_1724.method_19538(), dir, pos, true));
      swap(PreSlot);
   }

   public static void attackEntity(class_1297 entity) {
      mc.field_1761.method_2918(mc.field_1724, entity);
   }

   public static boolean isFriend(class_1657 player) {
      return Friends.get().isFriend(player);
   }

   public static boolean isFriend(String playerName) {
      return Friends.get().get(playerName) != null;
   }

   public static double distanceToBlockAnge(class_2338 pos) {
      double x1 = mc.field_1724.method_23317();
      double y1 = mc.field_1724.method_23318() + 1.0D;
      double z1 = mc.field_1724.method_23321();
      double x2 = (double)pos.method_10263();
      double y2 = (double)pos.method_10264();
      double z2 = (double)pos.method_10260();
      if (y2 == floor(y1)) {
         y2 = y1;
      }

      if (x2 > 0.0D && x2 == floor(x1)) {
         x2 = x1;
      }

      if (x2 < 0.0D && x2 + 1.0D == floor(x1)) {
         x2 = x1;
      }

      if (z2 > 0.0D && z2 == floor(z1)) {
         z2 = z1;
      }

      if (z2 < 0.0D && z2 + 1.0D == floor(z1)) {
         z2 = z1;
      }

      if (x2 < x1) {
         ++x2;
      }

      if (y2 < y1) {
         ++y2;
      }

      if (z2 < z1) {
         ++z2;
      }

      double dX = x2 - x1;
      double dY = y2 - y1;
      double dZ = z2 - z1;
      return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
   }

   public static double floor(double d) {
      return d;
   }
}
