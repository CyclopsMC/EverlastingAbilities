package org.cyclops.everlastingabilities.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;

import java.util.Optional;

/**
 * @author rubensworks
 */
public class GameTestsCommon {

    public static final String TEMPLATE_EMPTY = "cyclopscore:empty";
    public static final BlockPos POS = BlockPos.ZERO;

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerAbilityNotEnoughXp(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 0;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getHolderOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).isEmpty(), "Expected not to be addable in simulate-mode");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was not added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, "Expect ability store of size 1");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerAbilityValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 300;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getHolderOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).isEmpty(), "Expected to be addable in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).isEmpty(), "Expected to be addable");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, "Expect ability store of size 1");
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, "Expect ability type to be contained");
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, "Expect ability type to be correct");
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 3, "Expect ability level to be correct");
        });
    }

    // TODO: max level exceeded

    // TODO: add same ab multiple times

    // TODO: add multiple abs

    // TODO: remove ab

    // TODO: onPlayerClone

    // TODO: interact with abilities of a totem/bottle item

    // TODO: commands?

    public static IAbilityHelpers getAbilityHelpers() {
        return EverlastingAbilitiesInstance.MOD.getAbilityHelpers();
    }

}
