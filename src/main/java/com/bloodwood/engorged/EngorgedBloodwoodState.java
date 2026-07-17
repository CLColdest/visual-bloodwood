package com.bloodwood.engorged;

import lombok.Value;

@Value
public class EngorgedBloodwoodState
{
	EngorgedBloodwoodPhase phase;
	int progress;
	int draining;
	boolean hasEmptyBucket;
	boolean playerAnimating;
	int ticksSinceProgressChange;
	int drainingTicksRemaining;
}
