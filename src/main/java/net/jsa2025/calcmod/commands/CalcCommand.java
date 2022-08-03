package net.jsa2025.calcmod.commands;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberFormat;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.math.BlockPos;

import org.mariuszgromada.math.mxparser.Expression;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;


import static dev.xpple.clientarguments.arguments.CBlockPosArgumentType.*;

import java.util.Locale;

public class CalcCommand {
    static DecimalFormat df = new DecimalFormat("#.##");
    static NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US")); 
    public static void register (CommandDispatcher<FabricClientCommandSource> dispacther, CommandRegistryAccess registry) {
        dispacther.register(ClientCommandManager.literal("calc")
        .then(ClientCommandManager.argument("expression", StringArgumentType.greedyString()).executes(ctx -> executeBasicCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "expression"))))
        .then(ClientCommandManager.literal("storage").then(ClientCommandManager.argument("timesHopperSpeed", IntegerArgumentType.integer())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), String.valueOf(IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")), 1))
        .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "itemsperhour"), IntegerArgumentType.getInteger(ctx, "timesHopperSpeed")))))
        .then(ClientCommandManager.argument("itemsperhour", StringArgumentType.greedyString())
        .executes(ctx -> executeStorageCalculation(ctx.getSource(), StringArgumentType.getString(ctx, "itemsperhour"), 1)))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "storage"))))
        .then(ClientCommandManager.literal("nether").then(ClientCommandManager.argument("pos", blockPos())
        .executes(ctx -> executeNetherCoord(ctx.getSource(), getCBlockPos(ctx, "pos"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "nether"))))
        .then(ClientCommandManager.literal("overworld").then(ClientCommandManager.argument("pos", blockPos())
        .executes(ctx -> executeOverworldCoord(ctx.getSource(), getCBlockPos(ctx, "pos"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "overworld"))))
        .then(ClientCommandManager.literal("sbtoitem").then(ClientCommandManager.argument("numberofsbs", StringArgumentType.greedyString())
        .executes(ctx -> executeSbToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofsbs"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "sbtoitem"))))
        .then(ClientCommandManager.literal("itemtosb").then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToSb(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "itemtosb"))))
        .then(ClientCommandManager.literal("SecondstoHopperClock").then(ClientCommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> executeSecondsToHopperClock(ctx.getSource(), StringArgumentType.getString(ctx, "seconds"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "seconds2hopperclock"))))
        .then(ClientCommandManager.literal("SecondsToRepeater").then(ClientCommandManager.argument("seconds", StringArgumentType.greedyString())
        .executes(ctx -> executeSecondsToRepeater(ctx.getSource(), StringArgumentType.getString(ctx, "seconds"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "seconds2repeater"))))
        .then(ClientCommandManager.literal("itemtostack").then(ClientCommandManager.argument("numberofitems", StringArgumentType.greedyString())
        .executes(ctx -> executeItemToStack(ctx.getSource(), StringArgumentType.getString(ctx, "numberofitems"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "itemtostack"))))
        .then(ClientCommandManager.literal("stacktoitem").then(ClientCommandManager.argument("numberofstacks", StringArgumentType.greedyString())
        .executes(ctx -> executeStackToItem(ctx.getSource(), StringArgumentType.getString(ctx, "numberofstacks"))))
        .then(ClientCommandManager.literal("help").executes(ctx -> executeHelp(ctx.getSource(), "stacktoitem"))))
        .then(ClientCommandManager.literal("variables")
        .executes(ctx -> executeVariables(ctx.getSource())))

        .then(ClientCommandManager.literal("help")
        .executes(ctx -> executeHelp(ctx.getSource(), "")))

        );


    }

    public static int executeBasicCalculation(FabricClientCommandSource source, String expression) {
        Expression e = new Expression(expression);
        String message = "Result: " + nf.format(e.calculate());
        sendMessage(source, message);
        return 1;
        
    }

    public static int executeStorageCalculation(FabricClientCommandSource source, String itemsperhour, int timesHopperSpeed) {
        double rates = new Expression(getParsedString(itemsperhour)).calculate();
        double hopperSpeed = (9000*timesHopperSpeed);
        double sorters = Math.ceil(rates/hopperSpeed);
        double sbsperhour = rates * 1.0 / 1728;
        String message = "Required Sorters at "+timesHopperSpeed+"xHopper Speed(9000/h): " + nf.format(sorters) + "\nSbs per hour: " +nf.format(sbsperhour);
        
        sendMessage(source, message);
        return 1;
    }

    public static int executeNetherCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Nether Coord: X: " + nf.format(pos.getX()/8) + " Z: " + nf.format(pos.getZ()/8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(pos.getX()/8) + " Z: " + nf.format(pos.getZ()/8))))));
        return 1;
    }

    public static int executeOverworldCoord(FabricClientCommandSource source, BlockPos pos ) {
        source.getPlayer().sendMessage(Text.literal("Overworld Coord: X: " + nf.format(pos.getX()*8) + " Z: " + nf.format(pos.getZ()*8) + " ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "X: " + nf.format(pos.getX()*8) + " Z: " + nf.format(pos.getZ()*8))))));
        return 1;
    }

    public static int executeSbToItem(FabricClientCommandSource source, String numberofsbs) {
        double sbs = new Expression(getParsedString(numberofsbs)).calculate();
        double items = sbs * 1728;
        String message = "Items: " + nf.format(items);
        sendMessage(source, message);
        return 1;
    }

    public static int executeItemToSb(FabricClientCommandSource source, String numberofitems) {
        double items = new Expression(getParsedString(numberofitems)).calculate();
        double sbs = items / 1728;
        String message = "Sbs: " + nf.format(sbs);


        sendMessage(source, message);
        return 1;
    }

    public static int executeSecondsToHopperClock(FabricClientCommandSource source, String seconds) {
        double secondsDouble = new Expression(getParsedString(seconds)).calculate();
        double hopperclock = Math.ceil(secondsDouble *1.25);
        String message = "Hopper Clock Items: " + nf.format(hopperclock);
        sendMessage(source, message);
        return 1;
    }

    public static int executeSecondsToRepeater(FabricClientCommandSource source, String seconds) {
        double secondsDouble = new Expression(getParsedString(seconds)).calculate();
        double ticks = secondsDouble * 10;
        double repeaters = Math.ceil(ticks/4);
        if (ticks % 4 != 0) {
            String message = "Repeaters Required: " + nf.format(repeaters) +"\nLast Repeater Tick: "+ nf.format(ticks % 4) ;
            sendMessage(source, message);
        } else {
            String message = "Repeaters Required: " + nf.format(Math.ceil(ticks/4));
            sendMessage(source, message);
        }
        return 1;
    }
    
    public static int executeItemToStack(FabricClientCommandSource source, String numberofitems) {
        double items = new Expression(getParsedString(numberofitems)).calculate();
        double stacks = Math.floor(items/64);
        double leftover = items % 64;
        String message = "Stacks Required: " + nf.format(stacks) + "\nLeftover Items: " + nf.format(leftover);
        sendMessage(source, message);        
        return 1;
        
    }

    public static int executeStackToItem(FabricClientCommandSource source, String numberofstacks) {
        double stacks = new Expression(getParsedString(numberofstacks)).calculate();
        double items = stacks * 64;
        String message = "Items: " + nf.format(items);
        sendMessage(source, message);
        return 1;
    }

    public static int executeVariables(FabricClientCommandSource source) {
        String message = """
                dub: 3456
                sb: 1728
                stack: 64
                min: 60
                hour: 3600
                """;
                source.getPlayer().sendMessage(Text.literal(message));
        return 1;
    }

    public static int executeHelp(FabricClientCommandSource source, String help) {
        var helpMessage = "";
        if (help == "storage") {
            helpMessage = """
                Storage:
                Given rate in terms of items per hour(can be in expression form) and optionally hopper speed returns the number of needed sorters and rates in terms of sbs per hour
                Usage: /calc storage <itemsperhour> <timesHopperSpeed>
                    """;
        } else if (help == "nether") {
            helpMessage = """
                Nether:
                Given a block position returns the nether coordinates
                Usage: /calc nether <x> <y> <z>
                    """;
        } else if (help == "overworld") {
            helpMessage = """
                Overworld:
                Given a block position returns the overworld coordinates
                Usage: /calc overworld <x> <y> <z>
                    """;
        } else if (help == "sbtoitem") {
            helpMessage = """
                Sb to Item:
                Given a number of sbs(can be in expression form) returns the number of items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc sbtoitem <numberofsbs>
                    """;
        } else if (help == "itemtosb") {
            helpMessage = """
                Item to Sb:
                Given a number of items(can be in expression form) returns the number of sbs
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc itemtosb <numberofitems>
                    """;
        } else if (help == "seconds2hopperclock") {
            helpMessage = """
                Seconds to Hopper Clock:
                Given a number of seconds(can be in expression form) returns the number of ticks in a hopper clock
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc seconds2hopperclock <seconds>
                    """;
        } else if (help == "seconds2repeater") {
            helpMessage = """
                Seconds to Repeater:
                Given a number of seconds(can be in expression form) returns the number of repeaters and the last tick of the last repeater
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc seconds2repeater <seconds>
                    """;
        } else if (help == "itemtostack") {
            helpMessage = """
                Item to Stack:
                Given a number of items(can be in expression form) returns the number of stacks and leftover items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc itemtostack <numberofitems>
                    """;
        } else if (help == "stacktoitem") {
            helpMessage = """
                Stack to Item:
                Given a number of stacks(can be in expression form) returns the number of items
                You can include variables in the expression to obtain a list of the variables available run /calc variables
                Usage: /calc stacktoitem <numberofstacks>
                    """;
                } else {
            helpMessage = """
            Calcmod 

            Basic(no arguments):
            Functions like a simple calculator 
            
            Storage:
            Given rate in terms of items per hour and optionally hopper speed returns the number of needed sorters and rates in terms of sbs per hour
            
            Nether:
            Returns coords in nether for building portal in correct place.
            
            Overworld:
            Same as nether but reversed 
            
            sbtoitem:
            Given number of sbs return number of items
            
            Itemtosb:
            Given number of items return amount of sbs needed
            
            SecondstoHopperClock
            Given seconds return amount of items needed in hopper clock
            2.5 items per second
            
            SecondstoRepeaterClock
            Given number of seconds return needed repeater delay 
                """;;
        }

        source.getPlayer().sendMessage(Text.of(helpMessage));
        return 1;
    }

    private static String getParsedString(String in) {
        return in.replaceAll("dub", "*3456").replaceAll("sb", "*1728").replaceAll("stack", "*64").replaceAll("min", "*60").replaceAll("hour", "*3600");
    }

    private static void sendMessage(FabricClientCommandSource source, String message) {
        source.getPlayer().sendMessage(Text.literal(message+" ").append(Text.literal("\2473[Click To Copy]").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, message)))));
    }


}