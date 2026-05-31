package com.outrightwings;

import com.outrightwings.command.ListCommand;
import com.outrightwings.data.FabricDataReloaders;
import com.outrightwings.data.MegaTreeDataReloadListener;
import com.outrightwings.data.SingleTreeDataReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

import static net.minecraft.commands.Commands.literal;

public class FabricMain implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonMain.init();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricDataReloaders.FabricSingleTreeDataReloadListener());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricDataReloaders.FabricMegaTreeDataReloadListener());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal(ListCommand.NAME)
                .executes(ListCommand::runCommand)));
    }
}
