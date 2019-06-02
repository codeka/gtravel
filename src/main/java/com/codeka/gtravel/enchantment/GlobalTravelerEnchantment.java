package com.codeka.gtravel.enchantment;

import com.codeka.gtravel.GTravelMod;
import com.codeka.gtravel.Registry;
import com.codeka.gtravel.util.CoordDim;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ListIterator;
import java.util.Map;

public class GlobalTravelerEnchantment extends Enchantment {
    private static Rarity rarity = Rarity.RARE;

    /** Constructs a new GlobalTravelerEnchantment. */
    public GlobalTravelerEnchantment() {
        super(rarity, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});

        setName(GTravelMod.MODID + ".global_traveler");
        setRegistryName("global_traveler");

        MinecraftForge.EVENT_BUS.register(this);
    }

    /** Initializes config for GlobalTravelerEnchantment. Called before the class is registered. */
    public static void init(Configuration config) {
        Property prop = config.get(
                "globalTraveler",
                "rarity",
                Enchantment.Rarity.RARE.toString(),
                "Rarity");
        rarity = Rarity.valueOf(prop.getString());
    }

    /**
     * Handles right-click on nothing. If you're holding a tool with the global traveler enchant, then enable/disable
     * global traveler mode.
     */
    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        // If we're on the client, the event is cancelled or the thing you right-clicked isn't a face then there's
        // nothing to do.
        if (event.getWorld().isRemote || event.isCanceled()) {
            return;
        }

        // TODO if they right-clicked something then doesn't count.

        // If the player isn't sneaking, then nothing to do.
        if (!event.getEntityPlayer().isSneaking()) {
            return;
        }

        // If the tool you're holding doesn't have global traveler, then nothing to do.
        if (!hasGlobalTraveler(event.getItemStack())) {
            return;
        }

        NBTTagCompound itemTag = event.getItemStack().getTagCompound();
        if (itemTag == null) {
            // No tag, it can't have global traveler anyway.
            return;
        }
        NBTTagCompound globalTravelerNbt = itemTag.getCompoundTag("globalTraveler");
        boolean disabled = globalTravelerNbt.getBoolean("disabled");
        globalTravelerNbt.setBoolean("disabled", !disabled);

        if (disabled) {
            event.getEntityPlayer().sendMessage(new TextComponentTranslation("msg.gtravel.enabled_msg"));
        } else {
            event.getEntityPlayer().sendMessage(new TextComponentTranslation("msg.gtravel.disabled_msg"));
        }
    }

    /**
     * Handles right-click on stuff. If you're holding a tool with the global traveler enchant, and you're sneaking,
     * then bind the tool that inventory.
     */
    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        // If we're on the client, the event is cancelled or the thing you right-clicked isn't a face then there's
        // nothing to do.
        if (event.getWorld().isRemote || event.isCanceled() || event.getFace() == null) {
            return;
        }

        // If the player isn't sneaking, then nothing to do.
        if (!event.getEntityPlayer().isSneaking()) {
            return;
        }

        // If the tool you're holding doesn't have global traveler, then nothing to do.
        if (!hasGlobalTraveler(event.getItemStack())) {
            return;
        }

        // Does the thing you right-clicked on have an inventory?
        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());
        if (tileEntity == null) {
            return;
        }
        if (tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()) == null) {
            return;
        }

        // OK, save the coordinates and details of the entity in the tool's tags.
        NBTTagCompound tag = new NBTTagCompound();
        CoordDim location = new CoordDim(event.getPos(), event.getWorld());
        location.toNBT(tag);
        tag.setByte("face", (byte) event.getFace().ordinal());

        NBTTagCompound itemTag = event.getItemStack().getTagCompound();
        if (itemTag == null) {
            itemTag = new NBTTagCompound();
        }
        itemTag.setTag("globalTraveler", tag);
        event.getItemStack().setTagCompound(itemTag);

        // Send the player a message to say that they bound to the given inventory.
        ITextComponent inventoryName = tileEntity.getDisplayName();
        if (inventoryName == null) {
            inventoryName = new TextComponentTranslation("msg.gtravel.unknown_inventory");
        }

        DimensionType dimType = DimensionType.getById(location.dim);

        event.getEntityPlayer().sendMessage(new TextComponentTranslation(
                "msg.gtravel.bound_msg",
                inventoryName, dimType.getName(),
                location.x, location.y, location.z));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void blockDrops(BlockEvent.HarvestDropsEvent event) {
        // If we're running on the client, or the block wasn't harvested by a tool, nothing to do.
        if (event.getWorld().isRemote || event.getHarvester() == null) {
            return;
        }

        ItemStack tool = event.getHarvester().getHeldItemMainhand();
        if (!hasGlobalTraveler(tool)) {
            return;
        }

        NBTTagCompound toolNbt = tool.getTagCompound();
        if (toolNbt == null) {
            return;
        }

        if (!isToolEffective(tool, event.getState())) {
            return;
        }

        if (!toolNbt.hasKey("globalTraveler")) {
            return;
        }

        NBTTagCompound globalTravelerNbt = toolNbt.getCompoundTag("globalTraveler");
        if (globalTravelerNbt.getBoolean("disabled")) {
            return;
        }
        CoordDim boundLocation = CoordDim.fromNBT(globalTravelerNbt);
        if (boundLocation == null) {
            return;
        }
        CoordDim blockLocation = new CoordDim(event.getPos(), event.getWorld());
        if (boundLocation.equals(blockLocation)) {
            // If you broke the thing you're bound to, do nothing
            return;
        }

        TileEntity inventoryEntity = boundLocation.getTileEntity();
        if (inventoryEntity == null) {
            return;
        }

        IItemHandler itemHandler =
                inventoryEntity.getCapability(
                        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                        EnumFacing.VALUES[globalTravelerNbt.getByte("facing")]);
        if (itemHandler == null) {
            return;
        }

        // Try inserting each dropped item into the item handler's inventory.
        ListIterator<ItemStack> it = event.getDrops().listIterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack remaining = itemHandler.insertItem(i, stack, false);
                if (!remaining.isEmpty()) {
                    it.set(remaining);
                    stack = remaining;
                } else {
                    it.remove();
                    break;
                }
            }
        }

        inventoryEntity.markDirty();
    }

    private static boolean hasGlobalTraveler(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) {
            return false;
        }

        if (!itemStack.isItemEnchanted()) {
            return false;
        }
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
        if (!enchantments.containsKey(Registry.global_traveler)) {
            return false;
        }
        return true;
    }

    /** Could this tool have broken this block? */
    private static boolean isToolEffective(ItemStack stack, IBlockState state) {
        for(String type : stack.getItem().getToolClasses(stack)) {
            if(state.getBlock().isToolEffective(type, state)) {
                return true;
            }
        }

        return false;
    }
}
