package org.cyclops.everlastingabilities.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.IAbilityTypeRegistry;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Default ability type registry.
 * @author rubensworks
 */
public class AbilityTypeRegistry implements IAbilityTypeRegistry {

    private static final AbilityTypeRegistry INSTANCE = new AbilityTypeRegistry();

    private final Map<String, IAbilityType> abilities = Maps.newHashMap();

    private AbilityTypeRegistry() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static AbilityTypeRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <A extends IAbilityType> A register(A abilityType) {
        abilities.put(abilityType.getUnlocalizedName(), abilityType);
        return abilityType;
    }

    @Override
    public IAbilityType getAbilityType(String unlocalizedName) {
        return abilities.get(unlocalizedName);
    }

    @Override
    public Collection<IAbilityType> getAbilityTypes() {
        return Collections.unmodifiableCollection(abilities.values());
    }

    @Override
    public List<IAbilityType> getAbilityTypes(EnumRarity rarity) {
        List<IAbilityType> abilityTypes = Lists.newArrayList();
        for (IAbilityType abilityType : abilities.values()) {
            if (abilityType.getRarity().ordinal() <= rarity.ordinal()) {
                abilityTypes.add(abilityType);
            }
        }
        return abilityTypes;
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IAbilityStore abilityStore = player.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
            for (Ability ability : abilityStore.getAbilities()) {
                ability.getAbilityType().onTick(player, ability.getLevel());
            }
        }
    }
}
