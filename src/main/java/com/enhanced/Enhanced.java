package com.enhanced;

// IMPORTS
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// CLASS
public class Enhanced implements ModInitializer {

  // LOGGER
  public static final Logger LOGGER = LoggerFactory.getLogger("enhanced");

  // INITIALIZE
  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }

}
