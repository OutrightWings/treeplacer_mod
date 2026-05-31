package com.outrightwings.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.outrightwings.growth.TreeOverrideFinder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;

public class ListCommand {
    public static final String NAME = "list_treeplacements";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(NAME)
                .executes(ListCommand::runCommand)
        );
    }

    public static int runCommand(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        String message = "";
        message += "Single Overrides:\n";
        message += TreeOverrideFinder.singleSaplingOverrides.toString();
        message += "\nMega Overrides:\n";
        message += TreeOverrideFinder.megaSaplingOverrides.toString();

        String finalMessage = message;
        source.sendSuccess(() -> Component.literal(finalMessage), true);

        return 1;
    }
}
