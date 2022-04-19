package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1713;
import net.minecraft.class_1714;
import net.minecraft.class_1792;
import net.minecraft.class_1860;
import net.minecraft.class_516;

public class AutoCraft extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_1792>> items;
   private final Setting<Boolean> antiDesync;
   private final Setting<Boolean> craftAll;
   private final Setting<Boolean> drop;

   public AutoCraft() {
      super(Categories.NewCombat, "Auto-Craft", "Automatically crafts items.");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.items = this.sgGeneral.add(((ItemListSetting.Builder)((ItemListSetting.Builder)((ItemListSetting.Builder)(new ItemListSetting.Builder()).name("items")).description("Items you want to get crafted.")).defaultValue(Arrays.asList())).build());
      this.antiDesync = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-desync")).description("Try to prevent inventory desync.")).defaultValue(false)).build());
      this.craftAll = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("craft-all")).description("Crafts maximum possible amount amount per craft (shift-clicking)")).defaultValue(false)).build());
      this.drop = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("drop")).description("Automatically drops crafted items (useful for when not enough inventory space)")).defaultValue(false)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1761 != null) {
         if (!((List)this.items.get()).isEmpty()) {
            if (this.mc.field_1724.field_7512 instanceof class_1714) {
               if ((Boolean)this.antiDesync.get()) {
                  this.mc.field_1724.method_31548().method_7381();
               }

               class_1714 currentScreenHandler = (class_1714)this.mc.field_1724.field_7512;
               List<class_1792> itemList = (List)this.items.get();
               List<class_516> recipeResultCollectionList = this.mc.field_1724.method_3130().method_1393();
               Iterator var5 = recipeResultCollectionList.iterator();

               while(var5.hasNext()) {
                  class_516 recipeResultCollection = (class_516)var5.next();
                  Iterator var7 = recipeResultCollection.method_2648(true).iterator();

                  while(var7.hasNext()) {
                     class_1860<?> recipe = (class_1860)var7.next();
                     if (itemList.contains(recipe.method_8110().method_7909())) {
                        this.mc.field_1761.method_2912(currentScreenHandler.field_7763, recipe, (Boolean)this.craftAll.get());
                        this.mc.field_1761.method_2906(currentScreenHandler.field_7763, 0, 1, (Boolean)this.drop.get() ? class_1713.field_7795 : class_1713.field_7794, this.mc.field_1724);
                     }
                  }
               }

            }
         }
      }
   }
}
