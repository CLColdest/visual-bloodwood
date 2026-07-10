package com.bloodwood;

import lombok.Value;

@Value
class BloodwoodTreeState
{
	int index;
	int choppingProgress;
	int bleedingProgress;
	int bucketPlaced;
	int chops;
	int lastChops;

	boolean isCollectReady()
	{
		return bucketPlaced == 2;
	}

	int getChops()
	{
		return chops;
	}
}
