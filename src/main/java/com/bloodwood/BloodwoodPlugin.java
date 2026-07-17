package com.bloodwood;

import com.bloodwood.engorged.EngorgedBloodwoodIds;
import com.bloodwood.engorged.EngorgedBloodwoodPhase;
import com.bloodwood.engorged.EngorgedBloodwoodState;
import com.google.inject.Provides;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Visual Bloodwood",
	description = "Displays passive state and session information for bloodwood trees",
	tags = {"bloodwood", "sap", "skilling", "woodcutting"}
)
public class BloodwoodPlugin extends Plugin
{
	private static final Set<Integer> BLOODWOOD_TREES = Set.of(33393, 33394, 33395, 33396, 33397, 33398);
	private static final Set<Integer> BLOODWOOD_SPOTS = Set.of(33399, 33400, 33401, 33402, 33403, 33404);
	private static final int ENGORGED_FULL_PROGRESS = 3800;
	private static final int ENGORGED_DRAINING_TICKS = 75;
	private static final int ENGORGED_PROGRESS_GRACE_TICKS = 4;
	private static final int[] CHOPPING_PROGRESS_VARBITS = {
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS1,
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS2,
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS3,
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS4,
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS5,
		VarbitID.BLOODWOOD_TREE_CHOPPING_PROGRESS6
	};
	private static final int[] BLEEDING_PROGRESS_VARBITS = {
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS1,
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS2,
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS3,
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS4,
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS5,
		VarbitID.BLOODWOOD_TREE_BLEEDING_PROGRESS6
	};
	private static final int[] BUCKET_PLACED_VARBITS = {
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED1,
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED2,
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED3,
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED4,
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED5,
		VarbitID.BLOODWOOD_TREE_BUCKET_PLACED6
	};
	@Inject
	private Client client;

	@Inject
	private BloodwoodConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private BloodwoodOverlay overlay;

	@Inject
	private BloodwoodSceneOverlay sceneOverlay;

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> bloodwoodTrees = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> bloodwoodSpots = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	private final Set<GameObject> engorgedBloodwoodTrees = new HashSet<>();

	@Getter(AccessLevel.PACKAGE)
	@Nullable
	private BloodwoodSession session;

	@Getter(AccessLevel.PACKAGE)
	private int emptyBuckets;

	@Getter(AccessLevel.PACKAGE)
	private int sapBuckets;

	@Getter(AccessLevel.PACKAGE)
	private boolean inventoryFull;

	private boolean inventorySeen;
	private final int[] lastBleedingProgress = new int[BLEEDING_PROGRESS_VARBITS.length];
	private final int[] lastChoppingProgress = new int[CHOPPING_PROGRESS_VARBITS.length];
	private final int[] lastBucketPlaced = new int[BUCKET_PLACED_VARBITS.length];
	private final int[] currentChops = new int[CHOPPING_PROGRESS_VARBITS.length];
	private final boolean[] bleedingDraining = new boolean[BLEEDING_PROGRESS_VARBITS.length];
	private int lastObservedChops;
	private int lastEngorgedProgress;
	private int lastEngorgedProgressDelta;
	private int engorgedTicksSinceProgressChange = ENGORGED_PROGRESS_GRACE_TICKS + 1;
	private int engorgedDrainingTicksRemaining;
	private boolean bloodwoodActive;
	private boolean treeStateSeen;
	private boolean engorgedStateSeen;
	@Nullable
	private EngorgedBloodwoodPhase lastEngorgedPhase;
	private boolean engorgedPhaseSeen;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(sceneOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(sceneOverlay);
		bloodwoodTrees.clear();
		bloodwoodSpots.clear();
		engorgedBloodwoodTrees.clear();
		session = null;
		emptyBuckets = 0;
		sapBuckets = 0;
		inventoryFull = false;
		inventorySeen = false;
		Arrays.fill(lastBleedingProgress, 0);
		Arrays.fill(lastChoppingProgress, 0);
		Arrays.fill(lastBucketPlaced, 0);
		Arrays.fill(currentChops, 0);
		Arrays.fill(bleedingDraining, false);
		lastObservedChops = 0;
		lastEngorgedProgress = 0;
		lastEngorgedProgressDelta = 0;
		engorgedTicksSinceProgressChange = ENGORGED_PROGRESS_GRACE_TICKS + 1;
		engorgedDrainingTicksRemaining = 0;
		bloodwoodActive = false;
		treeStateSeen = false;
		engorgedStateSeen = false;
		lastEngorgedPhase = null;
		engorgedPhaseSeen = false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOADING || event.getGameState() == GameState.HOPPING)
		{
			bloodwoodTrees.clear();
			bloodwoodSpots.clear();
			engorgedBloodwoodTrees.clear();
			bloodwoodActive = false;
			engorgedDrainingTicksRemaining = 0;
			lastEngorgedPhase = null;
			engorgedPhaseSeen = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (int i = 0; i < BLEEDING_PROGRESS_VARBITS.length; ++i)
		{
			int choppingProgress = client.getVarbitValue(CHOPPING_PROGRESS_VARBITS[i]);
			int bleedingProgress = client.getVarbitValue(BLEEDING_PROGRESS_VARBITS[i]);
			int bucketPlaced = client.getVarbitValue(BUCKET_PLACED_VARBITS[i]);

			if (!treeStateSeen)
			{
				lastChoppingProgress[i] = choppingProgress;
				lastBleedingProgress[i] = bleedingProgress;
				lastBucketPlaced[i] = bucketPlaced;
				continue;
			}

			if (choppingProgress > lastChoppingProgress[i])
			{
				++currentChops[i];
				markBloodwoodActivity();
			}

			if (bleedingProgress > 0 && currentChops[i] > 0)
			{
				lastObservedChops = currentChops[i];
				currentChops[i] = 0;
			}

			if (bleedingProgress != lastBleedingProgress[i] || bucketPlaced != lastBucketPlaced[i])
			{
				markBloodwoodActivity();
			}

			bleedingDraining[i] = bleedingProgress > 0 && bleedingProgress < lastBleedingProgress[i];
			lastChoppingProgress[i] = choppingProgress;
			lastBleedingProgress[i] = bleedingProgress;
			lastBucketPlaced[i] = bucketPlaced;
		}

		treeStateSeen = true;

		int engorgedProgress = client.getVarbitValue(EngorgedBloodwoodIds.PROGRESS_VARBIT);
		if (!engorgedStateSeen)
		{
			lastEngorgedProgress = engorgedProgress;
			lastEngorgedProgressDelta = 0;
			engorgedTicksSinceProgressChange = ENGORGED_PROGRESS_GRACE_TICKS + 1;
			engorgedStateSeen = true;
			return;
		}

		if (engorgedProgress != lastEngorgedProgress)
		{
			lastEngorgedProgressDelta = engorgedProgress - lastEngorgedProgress;
			engorgedTicksSinceProgressChange = 0;
			markBloodwoodActivity();
		}
		else
		{
			++engorgedTicksSinceProgressChange;
		}

		lastEngorgedProgress = engorgedProgress;
		updateEngorgedStateTracking();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (BLOODWOOD_TREES.contains(gameObject.getId()))
		{
			bloodwoodTrees.add(gameObject);
		}
		else if (BLOODWOOD_SPOTS.contains(gameObject.getId()))
		{
			bloodwoodSpots.add(gameObject);
		}
		else if (gameObject.getId() == EngorgedBloodwoodIds.TREE)
		{
			engorgedBloodwoodTrees.add(gameObject);
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (BLOODWOOD_TREES.contains(gameObject.getId()))
		{
			bloodwoodTrees.remove(gameObject);
		}
		else if (BLOODWOOD_SPOTS.contains(gameObject.getId()))
		{
			bloodwoodSpots.remove(gameObject);
		}
		else if (gameObject.getId() == EngorgedBloodwoodIds.TREE)
		{
			engorgedBloodwoodTrees.remove(gameObject);
			engorgedDrainingTicksRemaining = 0;
			lastEngorgedPhase = null;
			engorgedPhaseSeen = false;
		}

		if (!isInBloodwoodArea())
		{
			bloodwoodActive = false;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() != InventoryID.INV)
		{
			return;
		}

		ItemContainer inventory = event.getItemContainer();
		int previousSapBuckets = sapBuckets;
		emptyBuckets = inventory.count(ItemID.BUCKET_EMPTY);
		sapBuckets = inventory.count(ItemID.BUCKET_OF_BLOODWOOD_SAP);
		inventoryFull = inventory.count() >= inventory.size();
		if (!inventorySeen)
		{
			inventorySeen = true;
			return;
		}

		if (!isInBloodwoodArea() || sapBuckets <= previousSapBuckets)
		{
			return;
		}

		markBloodwoodActivity();

		if (session == null)
		{
			session = new BloodwoodSession();
		}

		session.addSap(sapBuckets - previousSapBuckets);
	}

	boolean isInBloodwoodArea()
	{
		return !bloodwoodTrees.isEmpty() || !bloodwoodSpots.isEmpty() || !engorgedBloodwoodTrees.isEmpty();
	}

	boolean isBloodwoodActive()
	{
		return isInBloodwoodArea() && bloodwoodActive;
	}

	boolean shouldRenderTreeState()
	{
		return isBloodwoodActive() || !engorgedBloodwoodTrees.isEmpty();
	}

	private void markBloodwoodActivity()
	{
		bloodwoodActive = true;
	}

	private void updateEngorgedStateTracking()
	{
		if (engorgedBloodwoodTrees.isEmpty())
		{
			lastEngorgedPhase = null;
			engorgedPhaseSeen = false;
			engorgedDrainingTicksRemaining = 0;
			return;
		}

		EngorgedBloodwoodPhase phase = getEngorgedState().getPhase();
		if (phase == EngorgedBloodwoodPhase.DRAINING)
		{
			engorgedDrainingTicksRemaining = lastEngorgedPhase == EngorgedBloodwoodPhase.DRAINING
				? Math.max(0, engorgedDrainingTicksRemaining - 1)
				: ENGORGED_DRAINING_TICKS;
		}
		else
		{
			engorgedDrainingTicksRemaining = 0;
		}

		if (!engorgedPhaseSeen)
		{
			lastEngorgedPhase = phase;
			engorgedPhaseSeen = true;
			return;
		}

		if (phase == lastEngorgedPhase)
		{
			return;
		}

		if (phase == EngorgedBloodwoodPhase.READY_TO_DRAIN)
		{
			notifier.notify(config.engorgedClickNotification(), "Engorged Bloodwood tree needs another click.");
		}
		else if (phase == EngorgedBloodwoodPhase.READY && lastEngorgedPhase == EngorgedBloodwoodPhase.DRAINING)
		{
			notifier.notify(config.engorgedDrainingCompleteNotification(), "Engorged Bloodwood tree is ready to chop again.");
		}

		lastEngorgedPhase = phase;
	}

	@Nullable
	BloodwoodTreeState getState(GameObject gameObject)
	{
		int index;
		switch (gameObject.getId())
		{
			case 33393:
				index = 1;
				break;
			case 33394:
				index = 2;
				break;
			case 33395:
				index = 3;
				break;
			case 33396:
				index = 4;
				break;
			case 33397:
				index = 5;
				break;
			case 33398:
				index = 6;
				break;
			default:
				return null;
		}

		int i = index - 1;
		return new BloodwoodTreeState(
			index,
			client.getVarbitValue(CHOPPING_PROGRESS_VARBITS[i]),
			client.getVarbitValue(BLEEDING_PROGRESS_VARBITS[i]),
			client.getVarbitValue(BUCKET_PLACED_VARBITS[i]),
			currentChops[i],
			lastObservedChops
		);
	}

	boolean isBleedingDraining(BloodwoodTreeState state)
	{
		int index = state.getIndex() - 1;
		return index >= 0 && index < bleedingDraining.length && bleedingDraining[index];
	}

	EngorgedBloodwoodState getEngorgedState()
	{
		int progress = client.getVarbitValue(EngorgedBloodwoodIds.PROGRESS_VARBIT);
		int draining = client.getVarbitValue(EngorgedBloodwoodIds.DRAINING_VARBIT);
		boolean hasEmptyBucket = emptyBuckets > 0;
		boolean playerAnimating = client.getLocalPlayer() != null && client.getLocalPlayer().getAnimation() != -1;

		return new EngorgedBloodwoodState(
			getEngorgedPhase(progress, draining, hasEmptyBucket, playerAnimating, engorgedTicksSinceProgressChange),
			progress,
			draining,
			hasEmptyBucket,
			playerAnimating,
			engorgedTicksSinceProgressChange,
			engorgedDrainingTicksRemaining
		);
	}

	private EngorgedBloodwoodPhase getEngorgedPhase(
		int progress,
		int draining,
		boolean hasEmptyBucket,
		boolean playerAnimating,
		int ticksSinceProgressChange
	)
	{
		if (draining > 0 && progress < ENGORGED_FULL_PROGRESS)
		{
			return EngorgedBloodwoodPhase.DRAINING;
		}

		if (!hasEmptyBucket)
		{
			return EngorgedBloodwoodPhase.NO_BUCKET;
		}

		if (progress == 0 && draining == 0)
		{
			return EngorgedBloodwoodPhase.READY;
		}

		if (draining > 0 && progress >= ENGORGED_FULL_PROGRESS)
		{
			return EngorgedBloodwoodPhase.READY_TO_DRAIN;
		}

		if (playerAnimating || lastEngorgedProgressDelta > 0 && ticksSinceProgressChange <= ENGORGED_PROGRESS_GRACE_TICKS)
		{
			return EngorgedBloodwoodPhase.BLEEDING;
		}

		return EngorgedBloodwoodPhase.READY;
	}

	@Provides
	BloodwoodConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BloodwoodConfig.class);
	}
}
