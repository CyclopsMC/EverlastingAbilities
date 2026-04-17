package org.cyclops.everlastingabilities.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import org.cyclops.cyclopscore.gametest.GameTest;
import org.cyclops.everlastingabilities.EverlastingAbilitiesInstance;
import org.cyclops.everlastingabilities.Reference;
import org.cyclops.everlastingabilities.RegistryEntries;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.helper.IAbilityHelpers;
import org.cyclops.everlastingabilities.item.ItemAbilityTotem;

import java.util.List;
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected not to be addable in simulate-mode"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was not added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, Component.literal("Expect ability store of size 1"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 3, Component.literal("Expect ability level to be correct"));

            // Check if xp was lowered
            helper.assertValueEqual(player.totalExperience, 0, Component.literal("Expect XP to have been lowered"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 6);

            // Add ability
            helper.assertValueEqual(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    false,
                    true
            ).getLevel(), 5, Component.literal("Expected added ability to be of level 5 in simulate-mode"));
            helper.assertValueEqual(getAbilityHelpers().addPlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).getLevel(), 5, Component.literal("Expected added ability to be of level 5"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 5, Component.literal("Expect ability level to be correct"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability1 = new Ability(abilityType, 1);
            Ability ability2 = new Ability(abilityType, 1);
            Ability ability3 = new Ability(abilityType, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (1) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (1)"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (2) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (2)"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (3) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (3)"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 3, Component.literal("Expect ability level to be correct"));
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
            Holder<IAbilityType> abilityType1 = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Holder<IAbilityType> abilityType2 = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/darkness")));
            Ability ability1 = new Ability(abilityType1, 1);
            Ability ability2 = new Ability(abilityType2, 1);
            Ability ability3 = new Ability(abilityType1, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (1) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability1,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (1)"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (2) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (2)"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (3) in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().addPlayerAbility(
                    player,
                    ability3,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be addable (3)"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 2, Component.literal("Expect ability store of size 2"));
            helper.assertTrue(store.get().getAbility(abilityType1).getAbilityType() != null, Component.literal("Expect ability type 1 to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType1).getAbilityTypeHolder(), abilityType1, Component.literal("Expect ability type 1 to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType1).getLevel(), 2, Component.literal("Expect ability level 1 to be correct"));
            helper.assertTrue(store.get().getAbility(abilityType2).getAbilityType() != null, Component.literal("Expect ability type 2 to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType2).getAbilityTypeHolder(), abilityType2, Component.literal("Expect ability type 2 to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType2).getLevel(), 1, Component.literal("Expect ability level 2 to be correct"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
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
            ).isEmpty(), Component.literal("Expected to be removable in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be removable"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, Component.literal("Expect ability store of size 0"));

            // Check if xp was incremented again
            helper.assertValueEqual(player.totalExperience, 300, Component.literal("Expect XP to have been lowered again"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
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
            ).isEmpty(), Component.literal("Expected to be removable in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be removable"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be removable in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be removable"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    false,
                    true
            ).isEmpty(), Component.literal("Expected to be removable in simulate-mode"));
            helper.assertTrue(!getAbilityHelpers().removePlayerAbility(
                    player,
                    ability2,
                    true,
                    true
            ).isEmpty(), Component.literal("Expected to be removable"));

            // Check if player has an ability store
            Optional<IMutableAbilityStore> store = getAbilityHelpers().getEntityAbilityStore(player);
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 0, Component.literal("Expect ability store of size 0"));

            // Check if xp was incremented again
            helper.assertValueEqual(player.totalExperience, 300, Component.literal("Expect XP to have been lowered again"));
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
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
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
            helper.assertTrue(store.isPresent(), Component.literal("Player has no ability store"));

            // Check if ability was added
            helper.assertValueEqual(store.get().getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.get().getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.get().getAbility(abilityType).getLevel(), 3, Component.literal("Expect ability level to be correct"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), Component.literal("Expected to be addable"));

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.getAbility(abilityType).getLevel(), 3, Component.literal("Expect ability level to be correct"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityMultipleSameValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), Component.literal("Expected to be addable"));
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), Component.literal("Expected to be addable"));
            helper.assertTrue(!getAbilityHelpers().insert(ability, store).isEmpty(), Component.literal("Expected to be addable"));

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 1, Component.literal("Expect ability store of size 1"));
            helper.assertTrue(store.getAbility(abilityType).getAbilityType() != null, Component.literal("Expect ability type to be contained"));
            helper.assertValueEqual(store.getAbility(abilityType).getAbilityTypeHolder(), abilityType, Component.literal("Expect ability type to be correct"));
            helper.assertValueEqual(store.getAbility(abilityType).getLevel(), 3, Component.literal("Expect ability level to be correct"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityMultipleDifferentValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType1 = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Holder<IAbilityType> abilityType2 = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/darkness")));
            Ability ability1 = new Ability(abilityType1, 1);
            Ability ability2 = new Ability(abilityType2, 1);

            // Add ability
            helper.assertTrue(!getAbilityHelpers().insert(ability1, store).isEmpty(), Component.literal("Expected to be addable"));
            helper.assertTrue(!getAbilityHelpers().insert(ability2, store).isEmpty(), Component.literal("Expected to be addable"));
            helper.assertTrue(!getAbilityHelpers().insert(ability1, store).isEmpty(), Component.literal("Expected to be addable"));

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 2, Component.literal("Expect ability store of size 2"));
            helper.assertTrue(store.getAbility(abilityType1).getAbilityType() != null, Component.literal("Expect ability type 1 to be contained"));
            helper.assertValueEqual(store.getAbility(abilityType1).getAbilityTypeHolder(), abilityType1, Component.literal("Expect ability type 1 to be correct"));
            helper.assertValueEqual(store.getAbility(abilityType1).getLevel(), 2, Component.literal("Expect ability level 1 to be correct"));
            helper.assertTrue(store.getAbility(abilityType2).getAbilityType() != null, Component.literal("Expect ability type 2 to be contained"));
            helper.assertValueEqual(store.getAbility(abilityType2).getAbilityTypeHolder(), abilityType2, Component.literal("Expect ability type 2 to be correct"));
            helper.assertValueEqual(store.getAbility(abilityType2).getLevel(), 1, Component.literal("Expect ability level 2 to be correct"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testItemAbilityRemoveValid(GameTestHelper helper) {
        helper.succeedIf(() -> {
            ItemStack itemStack = new ItemStack(RegistryEntries.ITEM_ABILITY_BOTTLE);
            IMutableAbilityStore store = getAbilityHelpers().getItemAbilityStore(itemStack).get();

            // Determine an ability
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));
            Ability ability = new Ability(abilityType, 3);

            // Add ability
            getAbilityHelpers().insert(ability, store);

            // Remove ability
            helper.assertTrue(!getAbilityHelpers().extract(ability, store).isEmpty(), Component.literal("Expected to be removable"));

            // Check if ability was added
            helper.assertValueEqual(store.getAbilities().size(), 0, Component.literal("Expect ability store of size 0"));
        });
    }

    @GameTest(template = TEMPLATE_EMPTY)
    public void testTotemRecycleRecipeConsumesIngredients(GameTestHelper helper) {
        helper.succeedIf(() -> {
            // Determine an ability for the input totems
            Registry<IAbilityType> registry = getAbilityHelpers().getRegistry(helper.getLevel().registryAccess());
            Holder<IAbilityType> abilityType = registry.getOrThrow(ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(Reference.MOD_ID, "effect/speed")));

            // Create 3 totem stacks as input
            ItemStack totem1 = getAbilityHelpers().getTotem(new Ability(abilityType, 1));
            ItemStack totem2 = getAbilityHelpers().getTotem(new Ability(abilityType, 1));
            ItemStack totem3 = getAbilityHelpers().getTotem(new Ability(abilityType, 1));

            // Create a crafting input with 3 totems in a row
            CraftingInput craftingInput = CraftingInput.of(3, 1, List.of(totem1, totem2, totem3));

            // Verify the recipe is found
            Optional<RecipeHolder<CraftingRecipe>> recipeHolder = helper.getLevel().recipeAccess().getRecipeFor(
                    RecipeType.CRAFTING, craftingInput, helper.getLevel());
            helper.assertTrue(recipeHolder.isPresent(), Component.literal("Expected totem recycle recipe to be found"));

            CraftingRecipe recipe = recipeHolder.get().value();

            // Verify assemble returns a non-empty totem (registryAccess was set by matches() in getRecipeFor)
            ItemStack result = recipe.assemble(craftingInput);
            helper.assertTrue(!result.isEmpty(), Component.literal("Expected recipe result to be non-empty"));
            helper.assertTrue(result.getItem() instanceof ItemAbilityTotem, Component.literal("Expected recipe result to be a totem"));

            // Verify getRemainingItems returns all empty stacks (no NullPointerException, items are consumed)
            NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(craftingInput);
            helper.assertValueEqual(remainingItems.size(), craftingInput.size(), Component.literal("Expected remaining items size to match crafting input size"));
            for (int i = 0; i < remainingItems.size(); i++) {
                helper.assertTrue(remainingItems.get(i).isEmpty(), Component.literal("Expected remaining item at slot " + i + " to be empty (items consumed)"));
            }
        });
    }

    public static IAbilityHelpers getAbilityHelpers() {
        return EverlastingAbilitiesInstance.MOD.getAbilityHelpers();
    }

}
