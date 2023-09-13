package com.enhanced;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Enhanced implements ModInitializer {

  public static final Logger LOGGER = LoggerFactory.getLogger("enhanced");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}

}
