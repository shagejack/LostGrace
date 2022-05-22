package shagejack.lostgrace.contents.item.blackKnife;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import shagejack.lostgrace.contents.block.bloodAltar.BloodAltarTileEntity;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.registries.AllTiers;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.fluid.AllFluids;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class BlackKnife extends SwordItem {

    protected static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("66040BBB-60CA-4083-85C4-518A93F3DC43");
    protected static final UUID ATTACK_RANGE_UUID = UUID.fromString("E141442D-10FC-431F-8A5F-EA2373F35ED9");

    private final float baseAttackDamage;

    public BlackKnife(Properties pProperties) {
        super(AllTiers.DESTINED, 1, -1.5F, pProperties);
        this.baseAttackDamage = AllTiers.DESTINED.getAttackDamageBonus();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (isInit(stack))
            init(stack);

        int blood = getBlood(stack);
        int invisible = getInvisibleTicks(stack);
        if (blood > 0) {
            setBlood(stack, blood - 1);
        } else {
            setBlood(stack, 0);
            setHumanBlood(stack, false);
        }

        if (invisible > 0) {
            setInvisible(stack, invisible - 1);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        ItemStack stack = context.getItemInHand();

        if (state.is(AllBlocks.bloodAltar.block().get())) {
            int blood = getBlood(stack);
            boolean isHumanBlood = isHumanBlood(stack);
            Optional<BloodAltarTileEntity> te = TileEntityUtils.get(BloodAltarTileEntity.class, level, pos, true);

            if (te.isPresent() && te.get().isEmpty()) {
                FluidStack fluid = new FluidStack(isHumanBlood ? AllFluids.sacredBlood.still().get() :  AllFluids.profaneBlood.still().get(), getBlood(stack));
                int fill = te.get().bloodTank.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
                if (fill > 0) {
                    te.get().bloodTank.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    setBlood(stack, blood - fill);
                }
            }

            if (getBlood(stack) <= 0)
                setHumanBlood(stack, false);
        }

        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        if (player.isShiftKeyDown()) {
            if (usedHand == InteractionHand.OFF_HAND) {
                sacrifice(player.getItemInHand(usedHand), player);
            } else {
                setInvisible(player.getItemInHand(usedHand), true);
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 2, false, false));
            }
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        }

        return super.use(level, player, usedHand);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (isInit(stack))
            init(stack);

        return super.initCapabilities(stack, nbt);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MobEffectInstance effect = attacker.getEffect(MobEffects.INVISIBILITY);
        if (effect != null && effect.getAmplifier() == 0) {
            attacker.removeEffect(MobEffects.INVISIBILITY);
            attacker.removeEffect(MobEffects.MOVEMENT_SPEED);
        }

        if (target.getMaxHealth() > 2.0F) {
            Objects.requireNonNull(target.getAttribute(Attributes.MAX_HEALTH)).addPermanentModifier(new AttributeModifier("Destined death", -2.0F - target.getMaxHealth() * 0.05, AttributeModifier.Operation.ADDITION));
        } else {
            target.kill();
        }

        int blood = getBlood(stack);
        boolean isHumanBlood = isHumanBlood(stack);

        if (blood > 0) {
            if (isHumanBlood) {
                target.setHealth(target.getMaxHealth() - (float) blood / 50);
            } else {
                target.setHealth(target.getMaxHealth() - (float) blood / 100);
            }
            target.setInvulnerable(false);
        }

        if (target.getHealth() < 0.5F) {
            target.kill();
        }

        setHumanBlood(stack, (blood == 0 || isHumanBlood) && target instanceof Player);

        if (blood <= 900) {
            setBlood(stack, blood + 100);
        } else {
            setBlood(stack, 1000);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    public void sacrifice(ItemStack stack, Player target) {
        if (target.getMaxHealth() > 2.0F) {
            Objects.requireNonNull(target.getAttribute(Attributes.MAX_HEALTH)).addPermanentModifier(new AttributeModifier("Destined death", -2.0F - target.getMaxHealth() * 0.05, AttributeModifier.Operation.ADDITION));
        } else {
            target.kill();
        }
        int blood = getBlood(stack);
        boolean isHumanBlood = isHumanBlood(stack);

        if (blood > 0) {
            if (isHumanBlood) {
                target.setHealth(target.getMaxHealth() - (float) blood / 50);
            } else {
                target.setHealth(target.getMaxHealth() - (float) blood / 100);
            }
            target.setInvulnerable(false);
        }

        if (target.getHealth() < 0.5F) {
            target.kill();
        }

        setHumanBlood(stack, blood == 0 || isHumanBlood(stack));

        if (blood <= 900) {
            setBlood(stack, blood + 100);
        } else {
            setBlood(stack, 1000);
        }
    }

    @Override
    public float getDamage() {
        return this.baseAttackDamage;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", this.baseAttackDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -0.5, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MOVEMENT_SPEED_UUID, "Weapon modifier", 0.05, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(ATTACK_RANGE_UUID, "Weapon modifier", -0.8, AttributeModifier.Operation.ADDITION));
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? builder.build() : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        MutableComponent infoBloodText = new TranslatableComponent("lostgrace.info.destined_death").withStyle(ChatFormatting.DARK_RED).withStyle(ChatFormatting.BOLD);
        tooltipComponents.add(infoBloodText);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static int getBlood(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Blood");
    }

    public static void setBlood(ItemStack stack, int amount) {
        stack.getOrCreateTag().putInt("Blood", amount);
    }

    public static boolean isHumanBlood(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("HumanBlood");
    }

    public static void setHumanBlood(ItemStack stack, boolean isHumanBlood) {
        stack.getOrCreateTag().putBoolean("HumanBlood", isHumanBlood);
    }

    public static int getInvisibleTicks(ItemStack stack) {
        return stack.getOrCreateTag().getInt("Invisible");
    }

    public static boolean isInvisible(ItemStack stack) {
        return getInvisibleTicks(stack) > 0;
    }

    public static void setInvisible(ItemStack stack, int invisibleTicks) {
        stack.getOrCreateTag().putInt("Invisible", invisibleTicks);
    }

    public static void setInvisible(ItemStack stack, boolean invisible) {
        if (invisible) {
            stack.getOrCreateTag().putInt("Invisible", 100);
        } else {
            stack.getOrCreateTag().putInt("Invisible", 0);
        }
    }

    public static boolean isInit(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("Init");
    }

    public static void init(ItemStack stack) {
        setBlood(stack, 0);
        setInvisible(stack, false);
        setHumanBlood(stack, false);
        stack.getOrCreateTag().putBoolean("Unbreakable", true);
        stack.getOrCreateTag().putBoolean("Init", true);
    }


}
