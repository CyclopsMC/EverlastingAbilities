package org.cyclops.everlastingabilities.helper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.cyclops.everlastingabilities.GeneralConfig;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityCondition;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IAbilityStore;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author rubensworks
 */
public interface IAbilityHelpers {

    /**
     * This value is synced with {@link GeneralConfig#maxPlayerAbilities} from the server.
     * This is to ensure that clients can not hack around the ability limit.
     */
    public int getMaxPlayerAbilitiesClient();
    public void setMaxPlayerAbilitiesClient(int maxPlayerAbilitiesClient);

    public int[] getRarityColors();

    public Predicate<Holder<IAbilityType>> getPredicateAbilityEnabled();

    public Registry<IAbilityType> getRegistry(RegistryAccess registryAccess);

    public HolderLookup.RegistryLookup<IAbilityType> getRegistryLookup(HolderLookup.Provider holderLookupProvider);

    public int getExperienceForLevel(int level);

    public int getLevelForExperience(int experience);

    public Predicate<Holder<IAbilityType>> createRarityPredicate(Rarity rarity);

    public List<Holder<IAbilityType>> getAbilityTypes(Registry<IAbilityType> registry, Predicate<Holder<IAbilityType>> abilityFilter);
    public List<Holder<IAbilityType>> getAbilityTypes(HolderLookup.Provider holderLookupProvider, Predicate<Holder<IAbilityType>> abilityFilter);
    public List<Holder<IAbilityType>> getAbilityTypesPlayerSpawn(Registry<IAbilityType> registry);
    public List<Holder<IAbilityType>> getAbilityTypesMobSpawn(Registry<IAbilityType> registry);
    public List<Holder<IAbilityType>> getAbilityTypesCrafting(Registry<IAbilityType> registry);
    public List<Holder<IAbilityType>> getAbilityTypesCrafting(HolderLookup.Provider provider);
    public List<Holder<IAbilityType>> getAbilityTypesLoot(Registry<IAbilityType> registry);

    public void onPlayerAbilityChanged(Player player, IAbilityType abilityType, int oldLevel, int newLevel);

    public int getMaxPlayerAbilities(Level world);

    /**
     * Add the given ability.
     * @param player The player.
     * @param ability The ability.
     * @param doAdd If the addition should actually be done.
     * @param modifyXp Whether to require player to have enough XP before adding
     * @return The ability part that was added.
     */
    public Ability addPlayerAbility(Player player, Ability ability, boolean doAdd, boolean modifyXp);

    /**
     * Remove the given ability.
     * @param player The player.
     * @param ability The ability.
     * @param doRemove If the removal should actually be done.
     * @param modifyXp Whether to refund XP cost of ability
     * @return The ability part that was removed.
     */
    public Ability removePlayerAbility(Player player, Ability ability, boolean doRemove, boolean modifyXp);

    public int getExperience(Ability ability);

    public void setPlayerAbilities(ServerPlayer player, Map<Holder<IAbilityType>, Integer> abilityTypes);

    public boolean canInsert(Ability ability, IMutableAbilityStore mutableAbilityStore);

    public boolean canExtract(Ability ability, IMutableAbilityStore mutableAbilityStore);

    public boolean canInsertToPlayer(Ability ability, Player player);

    public Ability insert(Ability ability, IMutableAbilityStore mutableAbilityStore);

    public Ability extract(Ability ability, IMutableAbilityStore mutableAbilityStore);

    public Optional<Holder<IAbilityType>> getRandomAbility(List<Holder<IAbilityType>> abilityTypes, RandomSource random, Rarity rarity);

    public Optional<Holder<IAbilityType>> getRandomAbilityUntilRarity(List<Holder<IAbilityType>> abilityTypes, RandomSource random, Rarity rarity, boolean inclusive);

    public Optional<ItemStack> getRandomTotem(List<Holder<IAbilityType>> abilityTypes, Rarity rarity, RandomSource rand);

    public ItemStack getTotem(Ability ability);

    public Optional<Rarity> getRandomRarity(List<Holder<IAbilityType>> abilityTypes, RandomSource rand);

    public boolean hasRarityAbilities(List<Holder<IAbilityType>> abilityTypes, Rarity rarity);

    public NavigableSet<Rarity> getValidAbilityRarities(List<Holder<IAbilityType>> abilityTypes);

    public Triple<Integer, Integer, Integer> getAverageRarityColor(IAbilityStore abilityStore);

    public Supplier<Rarity> getSafeRarity(Supplier<Integer> rarityGetter);

    public Tag serialize(Registry<IAbilityType> registry, IMutableAbilityStore capability);

    public void deserialize(Registry<IAbilityType> registry, IMutableAbilityStore capability, Tag nbt);

    public IAbilityCondition getAbilityConditionTrue();

    public IAbilityCondition getAbilityConditionFalse();

    public Codec<IAbilityCondition> getAbilityConditionCodec();

    public boolean hasPlayerStateLastFlight(Player player);
    public boolean isPlayerStateLastFlight(Player player);
    public void removePlayerStateLastFlight(Player player);
    public void setPlayerStateLastFlight(Player player, boolean lastFlight);

}
