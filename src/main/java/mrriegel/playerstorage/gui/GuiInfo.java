package mrriegel.playerstorage.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import mrriegel.limelib.gui.CommonGuiScreenSub;
import mrriegel.limelib.gui.GuiDrawer;
import mrriegel.limelib.gui.button.CommonGuiButton;
import mrriegel.limelib.gui.button.CommonGuiButton.Design;
import mrriegel.limelib.helper.ColorHelper;
import mrriegel.limelib.helper.NBTHelper;
import mrriegel.limelib.network.PacketHandler;
import mrriegel.limelib.util.Utils;
import mrriegel.playerstorage.ClientProxy;
import mrriegel.playerstorage.ConfigHandler;
import mrriegel.playerstorage.Enums.MessageAction;
import mrriegel.playerstorage.ExInventory;
import mrriegel.playerstorage.Message2Server;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;

public class GuiInfo extends CommonGuiScreenSub {

    ExInventory ei;
    private static int index = 0;
    List<Tab> tabs = new ArrayList<>();
    Integer active = null;
    long lastInvite = 0L;
    List<String> team, other;

    public GuiInfo() {
        super();
        this.ei = ExInventory.getInventory(Minecraft.getMinecraft().player);
        xSize = 230;
        ySize = 160;
        tabs.add(new Tab("Settings") {

            @Override
            void tooltip() {
                if (buttonList.get(0).isMouseOver()) {
                    drawHoveringText(Lists.newArrayList("Insert picked up items into your storage.", "Hold " + Keyboard.getKeyName(ClientProxy.INVERTPICKUP.getKeyCode()) + " to invert temporarily."), GuiDrawer.getMouseX(), GuiDrawer.getMouseY());
                } else if (buttonList.get(1).isMouseOver()) {
                    drawHoveringText("Usually you use shift-click to transfer items into the player storage. When this is enabled you transfer items with CTRL, so you can use shift to transfer items between player inventory and hotbar.", GuiDrawer.getMouseX(), GuiDrawer.getMouseY());
                } else if (buttonList.get(2).isMouseOver()) {
                    drawHoveringText("If enabled broken tools/used items will be replaced with items from the player storage.", GuiDrawer.getMouseX(), GuiDrawer.getMouseY());
                }
            }

            @Override
            void init() {
                buttonList.add(new GuiCheckBox(MessageAction.PICKUP.ordinal(), guiLeft + 10, guiTop + 10, "Auto Pickup", ei.autoPickup));
                buttonList.add(new GuiCheckBox(MessageAction.NOSHIFT.ordinal(), guiLeft + 10, guiTop + 24, "CTRL <-> SHIFT", ei.noshift));
                buttonList.add(new GuiCheckBox(MessageAction.REFILL.ordinal(), guiLeft + 10, guiTop + 38, "Auto Refill", ei.refill));
            }

            @Override
            void draw() {
                drawer.drawColoredRectangle(7, 7, 216, 45 + 14, 0x44000000);
            }

            @Override
            void click(GuiButton button) {
                if (button.id == MessageAction.PICKUP.ordinal()) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    MessageAction.PICKUP.set(nbt);
                    NBTHelper.set(nbt, "pick", ((GuiCheckBox) button).isChecked());
                    PacketHandler.sendToServer(new Message2Server(nbt));
                    new Message2Server().handleMessage(mc.player, nbt, Side.CLIENT);
                } else if (button.id == MessageAction.NOSHIFT.ordinal()) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    MessageAction.NOSHIFT.set(nbt);
                    NBTHelper.set(nbt, "shift", ((GuiCheckBox) button).isChecked());
                    PacketHandler.sendToServer(new Message2Server(nbt));
                    new Message2Server().handleMessage(mc.player, nbt, Side.CLIENT);
                } else if (button.id == MessageAction.REFILL.ordinal()) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    MessageAction.REFILL.set(nbt);
                    NBTHelper.set(nbt, "refill", ((GuiCheckBox) button).isChecked());
                    PacketHandler.sendToServer(new Message2Server(nbt));
                    new Message2Server().handleMessage(mc.player, nbt, Side.CLIENT);
                }
            }
        });
        if (false) {
            tabs.add(new Tab("Crafting") {
                int pos, maxpos;

                @Override
                void tooltip() {
                    //				drawHoveringText(Arrays.asList("micha"), drawer.getMouseX(), drawer.getMouseY());
                }

                @Override
                void init() {
                    for (int i = 0; i < Math.min(6, ei.recipes.size()); i++) {
                        buttonList.add(new CommonGuiButton(i, guiLeft + 15, guiTop + 23 + 20 * i, 18, 18, "").setDesign(Design.SIMPLE));
                        //					buttonList.add(new CommonGuiButton(i + 100, guiLeft + 206, guiTop + 21 + 10 * i, 14, 8, TextFormatting.GREEN + "" + TextFormatting.BOLD + "+").setTooltip("Invite player"));
                    }
                }

                @Override
                void draw() {
                    maxpos = Math.max(ei.recipes.size() - 6, 0);
                    for (GuiButton but : buttonList) {
                        but.visible = true;
                    }

                    for (int i = 0; i < Math.min(buttonList.size(), ei.recipes.size()); i++) {
                        CommonGuiButton but = (CommonGuiButton) buttonList.get(i);
                        ItemStack s = ei.recipes.get(i + pos).output;
                        but.setStack(s);
                        but.setTooltip(s.getDisplayName());

                    }
                    int x = 12 + guiLeft, y = 12 + guiTop;
                    drawer.drawColoredRectangle(8, 8, 100, 142, 0xffa2a2a2);
                    drawer.drawFrame(8, 8, 100, 142, 1, 0xff080808);
                    for (String s : Stream.concat(Stream.of(TextFormatting.DARK_GRAY + "" + TextFormatting.BOLD + "Recipes"), new ArrayList<String>().stream()).collect(Collectors.toList())) {
                        fontRenderer.drawString(s, x, y, 0x2a2a2a);
                        y += 10;
                    }
                    //				drawer.drawColoredRectangle(119, 8, 100, 142, 0xffa2a2a2);
                    //				drawer.drawFrame(119, 8, 100, 142, 1, 0xff080808);
                    //				x = 123 + guiLeft;
                    //				y = 12 + guiTop;
                    //				for (String s : Stream.concat(Stream.of(TextFormatting.DARK_GRAY + "" + TextFormatting.BOLD + "Players"), other.stream()).collect(Collectors.toList())) {
                    //					fontRenderer.drawString(s, x, y, 0x2a2a2a);
                    //					y += 10;
                    //				}
                }

                @Override
                void click(GuiButton button) {
                }
            });
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        tabs.get(index).init();
    }

    public void getTeamSize() {
        team.size();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        drawDefaultBackground();
        int x = 0;
        active = null;
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            String name = (i == index ? TextFormatting.BOLD : "") + tab.name;
            int w = fontRenderer.getStringWidth(name) + 10;
            drawer.drawBackgroundTexture(x, -15, w, 20);
            boolean in;
            if (in = isPointInRegion(x, -15, w - 2, 17, mouseX, mouseY)) {
                active = i;
            }
            int c = !in && i != index ? 0x6e6e6e : 0x3e3e3e;
            fontRenderer.drawString(name, x + 5 + guiLeft, -9 + guiTop, c);
            x += w;
        }
        drawer.drawBackgroundTexture();
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        //		GlStateManager.disableLighting();
        tabs.get(index).draw();
        //		GlStateManager.enableLighting();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        tabs.get(index).tooltip();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        tabs.get(index).click(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            if (active != null) {
                index = active;
                //				buttonList.clear();
                //				elementList.clear();
                //				tabs.get(index).init.run();
                //				initGui();
                setWorldAndResolution(mc, width, height);
            }

        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void invite(String p) {
        if (System.currentTimeMillis() - lastInvite < 5000) {
            mc.player.sendMessage(new TextComponentString("Wait a bit... (Spam Protection)"));
            return;
        }
        if (mc.player.getName().equals(p) || ei.members.contains(p)) {
            return;
        }
        lastInvite = System.currentTimeMillis();
        EntityPlayer player = mc.world.getPlayerEntityByName(p);
        if (player == null) {
            return;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        MessageAction.TEAMINVITE.set(nbt);
        NBTHelper.set(nbt, "player1", mc.player.getName());
        NBTHelper.set(nbt, "player2", p);
        PacketHandler.sendToServer(new Message2Server(nbt));
    }

    static abstract class Tab {

        String name;

        public Tab(String name) {
            this.name = name;
        }

        abstract void init();

        abstract void draw();

        abstract void tooltip();

        abstract void click(GuiButton button);

    }

}
