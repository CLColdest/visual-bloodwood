package com.bloodwood;

import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;

class BloodwoodSession
{
	private final Instant start = Instant.now();
	@Getter(AccessLevel.PACKAGE)
	private Instant lastSapCollected;
	@Getter(AccessLevel.PACKAGE)
	private int sapCollected;
	@Getter(AccessLevel.PACKAGE)
	private int sapPerHour;

	void addSap(int amount)
	{
		sapCollected += amount;
		lastSapCollected = Instant.now();

		long elapsedMs = Duration.between(start, lastSapCollected).toMillis();
		if (elapsedMs > 0)
		{
			sapPerHour = (int) ((double) sapCollected * Duration.ofHours(1).toMillis() / elapsedMs);
		}
	}
}
