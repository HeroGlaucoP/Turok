package com.oldturok.turok.gui.mc;

import com.oldturok.turok.chatcmd.Command;
import com.oldturok.turok.TurokMod;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.GuiChat;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class TurokGuiChat extends GuiChat {
    private String startString;
    private String currentFillinLine;
    private int cursor;

    public TurokGuiChat(String startString, String historybuffer, int sentHistoryCursor) {
        super(startString);
        this.startString = startString;
        if (!startString.equals(Chat.getCommandPrefix()))
            calculateCommand(startString.substring(Chat.getCommandPrefix().length()));
        this.historyBuffer = historybuffer;
        cursor = sentHistoryCursor;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.sentHistoryCursor = cursor;
        super.keyTyped(typedChar, keyCode);
        cursor = this.sentHistoryCursor;

        String chatLine = this.inputField.getText();

        if (!chatLine.startsWith(Chat.getCommandPrefix())){
            GuiChat newGUI = new GuiChat(chatLine) {
                int cursor = TurokGuiChat.this.cursor;
                @Override
                protected void keyTyped(char typedChar, int keyCode) throws IOException {
                    this.sentHistoryCursor = cursor;
                    super.keyTyped(typedChar, keyCode);
                    cursor = this.sentHistoryCursor;
                }
            };
            newGUI.historyBuffer = this.historyBuffer;
            mc.displayGuiScreen(newGUI);
            return;
        }

        if (chatLine.equals(Chat.getCommandPrefix())) {
            currentFillinLine = "";
            return;
        }

        calculateCommand(chatLine.substring(Chat.getCommandPrefix().length()));
    }

    protected void calculateCommand(String line){
        String[] args = line.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        HashMap<String, Chat> options = new HashMap<String, Chat>();

        if (args.length == 0) return;

        for (Chat c : TurokMod.getInstance().getCommandManager().getCommands()) {
            if ((c.getLabel().startsWith(args[0]) && !line.endsWith(" ")) || c.getLabel().equals(args[0])) {
                options.put(c.getLabel(), c);
            }
        }

        if (options.isEmpty()) {
            currentFillinLine = "";
            return;
        }

        TreeMap<String, Chat> map = new TreeMap<String, Chat>(options);

        Chat alphaCommand = map.firstEntry().getValue();

        currentFillinLine = alphaCommand.getLabel().substring(args[0].length());

        if (alphaCommand.getSyntaxChunks() == null || alphaCommand.getSyntaxChunks().length == 0)
            return;

        if (!line.endsWith(" "))
            currentFillinLine += " ";

        boolean cutSpace = false;

        if (cutSpace)
            currentFillinLine = currentFillinLine.substring(1);
    }
}
