package com.outrightwings;

import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Treeplacer implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("treeplacer");

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SingleTreeDataReloadListener());
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new MegaTreeDataReloadListener());
	}
}