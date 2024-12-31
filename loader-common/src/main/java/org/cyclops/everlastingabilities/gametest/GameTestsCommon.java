package org.cyclops.everlastingabilities.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
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

            // Check if xp was lowered
            helper.assertValueEqual(player.totalExperience, 0, "Expect XP to have been lowered");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerAbilityInvalidLevelTooHigh(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 600;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 6);

            // Add ability
            helper.assertValueEqual(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).getLevel(), 5, "Expected added ability to be of level 5 in simulate-mode");
            helper.assertValueEqual(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).getLevel(), 5, "Expected added ability to be of level 5");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, "Expect ability store of size 1");
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, "Expect ability type to be contained");
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, "Expect ability type to be correct");
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 5, "Expect ability level to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerAbilityMultipleSameValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 300;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability1 = new Ability(abilityType, 1);
            Ability ability2 = new Ability(abilityType, 1);
            Ability ability3 = new Ability(abilityType, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (1) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (1)");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (2) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (2)");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (3) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (3)");

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

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerAbilityMultipleDifferentValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 1000;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType1 = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Holder<IAbilityType> abilityType2 = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/darkness")));
            Ability ability1 = new Ability(abilityType1, 1);
            Ability ability2 = new Ability(abilityType2, 1);
            Ability ability3 = new Ability(abilityType1, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (1) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (1)");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (2) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (2)");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    false,
                    true
            ).isEmpty(), "Expected to be addable (3) in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    true,
                    true
            ).isEmpty(), "Expected to be addable (3)");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 2, "Expect ability store of size 2");
            helper.assertTrue(store.get().getAbility(abilityType1).getAbilityType() != null, "Expect ability type 1 to be contained");
            helper.assertValueEqual(store.get().getAbility(abilityType1).getAbilityTypeHolder(), abilityType1, "Expect ability type 1 to be correct");
            helper.assertValueEqual(store.get().getAbility(abilityType1).getLevel(), 2, "Expect ability level 1 to be correct");
            helper.assertTrue(store.get().getAbility(abilityType2).getAbilityType() != null, "Expect ability type 2 to be contained");
            helper.assertValueEqual(store.get().getAbility(abilityType2).getAbilityTypeHolder(), abilityType2, "Expect ability type 2 to be correct");
            helper.assertValueEqual(store.get().getAbility(abilityType2).getLevel(), 1, "Expect ability level 2 to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerRemoveAbilityValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 300;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    true,
                    true
            );

            // Remove ability
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).isEmpty(), "Expected to be removable in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).isEmpty(), "Expected to be removable");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, "Expect ability store of size 0");

            // Check if xp was incremented again
            helper.assertValueEqual(player.totalExperience, 300, "Expect XP to have been lowered again");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerRemoveAbilityMultipleSameValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player.totalExperience = 300;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability1 = new Ability(abilityType, 3);
            Ability ability2 = new Ability(abilityType, 1);

            // Add ability
            getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    true,
                    true
            );

            // Remove ability
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), "Expected to be removable in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), "Expected to be removable");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), "Expected to be removable in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), "Expected to be removable");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), "Expected to be removable in simulate-mode");
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), "Expected to be removable");

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, "Expect ability store of size 0");

            // Check if xp was incremented again
            helper.assertValueEqual(player.totalExperience, 300, "Expect XP to have been lowered again");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testPlayerClone(GameTestHelper helper) {
        helper.succeedIf(() -> {
            Player player1 = helper.makeMockPlayer(GameType.SURVIVAL);
            Player player2 = helper.makeMockPlayer(GameType.SURVIVAL);

            // Assign XP to player
            player1.totalExperience = 300;

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            getAbilityHelpers().addPlayerAbility(
                    player1,
                    ability,
                    true,
                    true
            );

            // Clone player
            getAbilityHelpers().onPlayerClone(player1, player2);

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player2);
            helper.assertTrue(store.isPresent(), "Player has no ability store");

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, "Expect ability store of size 1");
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, "Expect ability type to be contained");
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, "Expect ability type to be correct");
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 3, "Expect ability level to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), "Expected to be addable");

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 1, "Expect ability store of size 1");
            helper.assertTrue(store.getAbility(abilityType).getAbilityType() != null, "Expect ability type to be contained");
            helper.assertValueEqual(store.getAbility(abilityType).getAbilityTypeHolder(), abilityType, "Expect ability type to be correct");
            helper.assertValueEqual(store.getAbility(abilityType).getLevel(), 3, "Expect ability level to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityMultipleSameValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), "Expected to be addable");
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), "Expected to be addable");
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), "Expected to be addable");

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 1, "Expect ability store of size 1");
            helper.assertTrue(store.getAbility(abilityType).getAbilityType() != null, "Expect ability type to be contained");
            helper.assertValueEqual(store.getAbility(abilityType).getAbilityTypeHolder(), abilityType, "Expect ability type to be correct");
            helper.assertValueEqual(store.getAbility(abilityType).getLevel(), 3, "Expect ability level to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityMultipleDifferentValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType1 = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Holder<IAbilityType> abilityType2 = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/darkness")));
            Ability ability1 = new Ability(abilityType1, 1);
            Ability ability2 = new Ability(abilityType2, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability1, store).isEmpty(), "Expected to be addable");
            helper.assertTrue(!getAbilityHelpers().insert(ability2, store).isEmpty(), "Expected to be addable");
            helper.assertTrue(!getAbilityHelpers().insert(ability1, store).isEmpty(), "Expected to be addable");

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 2, "Expect ability store of size 2");
            helper.assertTrue(store.getAbility(abilityType1).getAbilityType() != null, "Expect ability type 1 to be contained");
            helper.assertValueEqual(store.getAbility(abilityType1).getAbilityTypeHolder(), abilityType1, "Expect ability type 1 to be correct");
            helper.assertValueEqual(store.getAbility(abilityType1).getLevel(), 2, "Expect ability level 1 to be correct");
            helper.assertTrue(store.getAbility(abilityType2).getAbilityType() != null, "Expect ability type 2 to be contained");
            helper.assertValueEqual(store.getAbility(abilityType2).getAbilityTypeHolder(), abilityType2, "Expect ability type 2 to be correct");
            helper.assertValueEqual(store.getAbility(abilityType2).getLevel(), 1, "Expect ability level 2 to be correct");
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityRemoveValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            getAbilityHelpers().insert(ability, store);

            // Remove ability
            helper.assertTrue(!getAbilityHelpers().extract(ability, store).isEmpty(), "Expected to be removable");

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 0, "Expect ability store of size 0");
        });
    }

    public static IAbilityHelpers getAbilityHelpers() {
        return EverlastingAbilitiesInstance.MOD.getAbilityHelpers();
    }

}
