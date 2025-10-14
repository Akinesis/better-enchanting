package cutefox.betterenchanting.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import cutefox.betterenchanting.Util.ModEnchantmentHelper;
import cutefox.betterenchanting.Util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class CustomEnchantmentScreen extends HandledScreen<CustomEnchantmentScreenHandler> {
    private static final Identifier ENCHANTMENT_BOOK_DISABLED = Utils.id("container/enchanting_table/enchantment_book_disabled");
    private static final Identifier ENCHANTMENT_BOOK_HIGHLIGHTED = Utils.id("container/enchanting_table/enchantment_book_highlighted");
    private static final Identifier SCROLLER = Utils.id("container/enchanting_table/scroller");
    private static final Identifier SCROLLER_DISABLED = Utils.id("container/enchanting_table/scroller_disabled");
    private static final Identifier BOOK_SLOT_SELECTOR = Utils.id("container/enchanting_table/book_slot_selector");
    private static final Identifier BOOK_GRAY_OVERLAY = Utils.id("container/enchanting_table/book_gray_overlay");
    private static final Identifier ENCHANTING_TABLE_BACKGROUND = Utils.id("textures/gui/container/custom_enchanting_table.png");
    private static final Identifier MAGIC_SHARD_FULL = Utils.id("container/enchanting_table/magic_shard_full");
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla("textures/entity/enchanting_table_book.png");
    private static final Identifier CHECKMARK = Identifier.ofVanilla("icon/checkmark");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack;
    int indexStartOffset;
    private boolean scrolling;
    private int[] selectedSlot;

    public CustomEnchantmentScreen(CustomEnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 222;
        this.backgroundWidth = 182;
        this.stack = ItemStack.EMPTY;
        this.playerInventoryTitleY = 129;
        this.playerInventoryTitleX = 11;
        indexStartOffset = 0;
        this.titleX = 6;
        selectedSlot =  new int[]{-1, -1};
    }

    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.client.getEntityModelLoader().getModelPart(EntityModelLayers.BOOK));
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.doTick();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int width = (this.width - this.backgroundWidth) / 2;
        int height = (this.height - this.backgroundHeight) / 2;

        this.scrolling = false;
        if (this.canScroll(handler.totalEnchantForItem())
                && mouseX > (double)(width + 56) && mouseX < (double)(width + 56 + 6)
                && mouseY > (double)(height + 13) && mouseY <= (double)(height + 18 + 113)) {
            this.scrolling = true;
        }

        if(this.handler.enchantmentId[0] == -5){
            double r = mouseX - (width+72);
            double s = mouseY - (height+14);

            if (r >= 0 && s >= 0 && r < 15 && s < 15 && this.handler.onButtonClick(this.client.player, -5)) {
                selectedSlot[0] = 0;
                selectedSlot[1] = 0;
                this.client.interactionManager.clickButton(this.handler.syncId, -5);
                return true;
            }
        }

        for(int k = 0; k < handler.ENCHANT_ARRAY_SIZE ; ++k) {
            if(k < indexStartOffset)
                continue;
            if(this.handler.enchantmentId[k] > -1){
                Optional<RegistryEntry.Reference<Enchantment>> enchant = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.handler.enchantmentId[k]);
                for(int l = 0; l < this.handler.enchantmentLevel[k]; l++){
                    double r = mouseX - (width+72+(16*l)+(4*l));
                    double s = mouseY - (height+14+(16*(k-indexStartOffset)));

                    int computedButtonId = (k*10)+l;

                    if (k >=indexStartOffset+7)
                        return super.mouseClicked(mouseX, mouseY, button);

                    if (r >= 0 && s >= 0 && r < 15 && s < 15 && this.handler.onButtonClick(this.client.player, computedButtonId)) {
                        selectedSlot[0] = k;
                        selectedSlot[1] = l;
                        this.client.interactionManager.clickButton(this.handler.syncId, computedButtonId);
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context,mouseX,mouseY);
        if(!client.player.isInCreativeMode())
            context.drawText(this.textRenderer, Text.of("XP : "+this.client.player.experienceLevel),10, 74, Colors.GREEN, true);

        int localWidth = (this.width - this.backgroundWidth) / 2;
        int localHeight = (this.height - this.backgroundHeight) / 2;

        boolean playerInCreative = client.player.isInCreativeMode();
        int q = Colors.GREEN;

        int numberOfPossibleEnchants = 0;
        Identifier bookToDraw;

        if(handler.getSlot(0).getStack().isEmpty())
            indexStartOffset = 0;

        if(this.handler.enchantmentId[0] == -5){
            if(this.client.player.experienceLevel < CustomEnchantmentScreenHandler.SHARD_FILLING_EXPERIENCE_COST)
                q = Colors.RED;

            context.drawGuiTexture(MAGIC_SHARD_FULL, 72,14,16,16);

            if(!playerInCreative)
                context.drawTextWithShadow(this.textRenderer, ""+CustomEnchantmentScreenHandler.SHARD_FILLING_EXPERIENCE_COST, 18+72 - this.textRenderer.getWidth(""+CustomEnchantmentScreenHandler.SHARD_FILLING_EXPERIENCE_COST), 14+8, q);

            return;
        }

        World playerWorld = client.world;

        for(int k = 0; k<handler.ENCHANT_ARRAY_SIZE; k++){

            if(k<indexStartOffset)
                continue;

            //If item is not Magic Shard, draw books
            if(this.handler.enchantmentId[k] > -1){

                bookToDraw = null;
                if(numberOfPossibleEnchants >= 7)
                    break;

                numberOfPossibleEnchants++;

                Optional<RegistryEntry.Reference<Enchantment>> enchant = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.handler.enchantmentId[k]);

                //Draw books and connexions
                for(int l = 0; l < this.handler.enchantmentLevel[k]; l++){

                    q = Colors.GREEN;
                    int r = mouseX - (localWidth+72+(16*l)+(4*l));
                    int s = mouseY - (localHeight+14+(16*(k-indexStartOffset)));


                    if (enchant != null && !enchant.isEmpty()) {
                        int enchantLevelCost = ModEnchantmentHelper.getEnchantmentLevelCost(enchant.get().value(),l+1, stack, playerWorld);
                        int enchantLevelReq = ModEnchantmentHelper.getEnchantmentLeveRequierment(enchant.get().value(),l);
                        RegistryEntry<Enchantment> enchantEntry = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchant.get().value());
                        boolean hasEnchantLevel = EnchantmentHelper.getLevel(enchantEntry,stack)>=l+1;

                        /*if (r >= 0 && s >= 0 && r < 15 && s < 15 && !hasEnchantLevel) {
                            //bookToDraw = ENCHANTMENT_BOOK_HIGHLIGHTED;
                            frameBook = true;
                        }*/
                        boolean frameBook = (r >= 0 && s >= 0 && r < 15 && s < 15 && !hasEnchantLevel);
                        RenderSystem.enableBlend();

                        if(this.client.player.experienceLevel < enchantLevelReq && !hasEnchantLevel && !playerInCreative){
                            bookToDraw = ENCHANTMENT_BOOK_DISABLED;
                        }

                        if(l > 0 && !ModEnchantmentHelper.itemHasPreviousLevelOfEnchant(stack, enchantEntry, l) && !hasEnchantLevel){
                            bookToDraw = ENCHANTMENT_BOOK_DISABLED;
                            //context.drawGuiTexture(BOOK_GRAY_OVERLAY, 72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)), 200, 16,16);
                        }

                        if(this.client.player.experienceLevel < enchantLevelCost && !hasEnchantLevel){
                            q = Colors.RED;
                        }


                        //Draw the enchanted book texture.
                        ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                        enchantedBook.addEnchantment(enchantEntry, l+1);
                        //context.drawGuiTexture(bookToDraw, localWidth+72+(16*l)+(4*l), localHeight+14+(16*(k-indexStartOffset)), 16, 16);

                        if(hasEnchantLevel){
                            context.drawGuiTexture(CHECKMARK, 72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)), 350, 10,10);
                        }


                        if(bookToDraw == ENCHANTMENT_BOOK_DISABLED)
                            context.drawGuiTexture(ENCHANTMENT_BOOK_DISABLED, 72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)), 200, 16,16);
                        else
                            context.drawItem(enchantedBook, 72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)),1,-150);

                        if (frameBook)
                            context.drawGuiTexture(BOOK_SLOT_SELECTOR, 72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)), 300, 16,16);

                        RenderSystem.disableBlend();

                        context.getMatrices().push();
                        context.getMatrices().translate(0,0,350);
                        if(!this.client.player.isInCreativeMode() && bookToDraw != ENCHANTMENT_BOOK_DISABLED && !hasEnchantLevel)
                            context.drawTextWithShadow(this.textRenderer, ""+enchantLevelCost, 18+72+(16*l)+(4*l) - this.textRenderer.getWidth(""+enchantLevelCost), 14+8+(16*(k-indexStartOffset)), q);
                        context.getMatrices().pop();

                    }
                }
            }
        }

    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int localWidth = (this.width - this.backgroundWidth) / 2;
        int localHeight = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth, localHeight, 0, 0, this.backgroundWidth, this.backgroundHeight);
        this.drawBook(context, localWidth-3 , localHeight+24, delta);
        boolean playerInCreative = client.player.isInCreativeMode();
        int q = 8453920;

        int numberOfPossibleEnchants = 0;

        if(handler.getSlot(0).getStack().isEmpty())
            indexStartOffset = 0;

        if(this.handler.enchantmentId[0] == -5){
            context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth+63, localHeight+14, 182, 32, 16,16);

            return;
        }

        for(int k = 0; k<handler.ENCHANT_ARRAY_SIZE; k++){

            if(k<indexStartOffset)
                continue;

            //If item is not Magic Shard, draw books
            if(this.handler.enchantmentId[k] > -1){

                if(numberOfPossibleEnchants >= 7)
                    break;

                numberOfPossibleEnchants++;

                Optional<RegistryEntry.Reference<Enchantment>> enchant = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.handler.enchantmentId[k]);

                //Draw tree first row
                if(k == 0){
                    context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth+63, localHeight+14, 182, 0, 16,16);
                }else if(k ==14 || this.handler.enchantmentId[k+1]<=-1){
                    context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth+63, localHeight+14+(16*(k-indexStartOffset)), 182, 32, 16,16);
                }else {
                    boolean lastEntry = numberOfPossibleEnchants >=7;
                    context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth+63, localHeight+14+(16*(k-indexStartOffset)), 182, 16, lastEntry?13:16,16);
                }

                //Draw books and connexions
                for(int l = 0; l < this.handler.enchantmentLevel[k]; l++){

                    if (enchant != null && !enchant.isEmpty()) {

                        RenderSystem.enableBlend();


                        context.drawTexture(ENCHANTING_TABLE_BACKGROUND, localWidth+68+(16*l)+(4*l), localHeight+14+(16*(k-indexStartOffset)), 198, 0, 4,16);


                        RenderSystem.disableBlend();
                    }
                }
            }
        }

    }

    private void drawBook(DrawContext context, int x, int y, float delta) {
        float f = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        float g = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle);
        DiffuseLighting.method_34742();
        context.getMatrices().push();
        context.getMatrices().translate((float)x + 33.0F, (float)y + 31.0F, 100.0F);
        float h = 40.0F;
        context.getMatrices().scale(-40.0F, 40.0F, 40.0F);
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(25.0F));
        context.getMatrices().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float i = -(1.0F - f) * 90.0F - 90.0F;
        context.getMatrices().multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i));
        context.getMatrices().multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        float j = MathHelper.clamp(MathHelper.fractionalPart(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = MathHelper.clamp(MathHelper.fractionalPart(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.BOOK_MODEL.setPageAngles(0.0F, j, k, f);
        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(this.BOOK_MODEL.getLayer(BOOK_TEXTURE));
        this.BOOK_MODEL.render(context.getMatrices(), vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV);
        context.draw();
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //Render mouse tooltip on item over and enchant over
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        renderScrollbar(context, i, j, handler.totalEnchantForItem());

        boolean isInCreative = this.client.player.getAbilities().creativeMode;
        int lapisCount = this.handler.getLapisCount();

        if(handler.enchantmentId[0] == -5){
            if (this.isPointWithinBounds(72, 14, 15, 15, mouseX, mouseY)){
                List<Text> list = Lists.newArrayList();
                MutableText mutableText,mutableText2,mutableText3;
                mutableText3 = Text.translatable("container.betterenchanting.enchant.charge");
                mutableText = Text.translatable("container.enchant.lapis.many", CustomEnchantmentScreenHandler.SHARD_FILLING_LAPIS_COST);
                mutableText2 = Text.translatable("container.enchant.level.many", CustomEnchantmentScreenHandler.SHARD_FILLING_EXPERIENCE_COST);

                list.add(mutableText3);

                if (!isInCreative) {
                    list.add(ScreenTexts.EMPTY);
                    list.add(mutableText.formatted(lapisCount >= CustomEnchantmentScreenHandler.SHARD_FILLING_LAPIS_COST ? Formatting.GRAY : Formatting.RED));
                    list.add(mutableText2.formatted(client.player.experienceLevel >= CustomEnchantmentScreenHandler.SHARD_FILLING_EXPERIENCE_COST ? Formatting.GRAY : Formatting.RED));
                }

                context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
            }

            return;
        }

        int numberOfPossibleEnchants = 0;

        for(int k = 0; k<handler.ENCHANT_ARRAY_SIZE; k++){
            if(k<indexStartOffset)
                continue;

            if(numberOfPossibleEnchants >= 7)
                break;

            numberOfPossibleEnchants++;

            if(this.handler.enchantmentId[k] > -1){
                Optional<RegistryEntry.Reference<Enchantment>> enchant = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(this.handler.enchantmentId[k]);
                Enchantment enchantment;
                for(int l = 0; l < this.handler.enchantmentLevel[k]; l++){
                    //For each book of each line
                    if (enchant != null && !enchant.isEmpty()) {
                        //l is enchantment level between 0 and max
                        //k is current enchantment type
                        if (this.isPointWithinBounds(72+(16*l)+(4*l), 14+(16*(k-indexStartOffset)), 16, 14, mouseX, mouseY)
                                && l >= 0) {
                            //If mouse over the book of enchant k and level l
                            enchantment = enchant.get().value();
                            RegistryEntry<Enchantment> enchantEntry = this.client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(enchant.get().value());
                            Item enchantIngredient = ModEnchantmentHelper.getEnchantIngredient(enchantment, l);
                            int displayedEnchantLevel = l + 1;
                            int enchantLevelCost = ModEnchantmentHelper.getEnchantmentLevelCost(enchantment,displayedEnchantLevel,stack, client.world);
                            int enchantLevReq = ModEnchantmentHelper.getEnchantmentLeveRequierment(enchantment,displayedEnchantLevel);
                            int enchantIngredientCost = ModEnchantmentHelper.getEnchantmentIngredientCost(enchantment,displayedEnchantLevel, enchantIngredient);
                            int lapisCost = (int)Math.floor(enchantLevelCost/2);
                            lapisCost = lapisCost<=0?1:lapisCost;

                            MutableText mutableText;
                            List<Text> list = Lists.newArrayList();

                            //Add name to tooltip
                            mutableText = Text.translatable(Enchantment.getName(enchant.get(), displayedEnchantLevel).getString()).formatted(Formatting.WHITE);
                            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                            List<RegistryEntry<Enchantment>> registryEntryStream = new ArrayList<>();
                            registryEntryStream.add(enchantEntry);
                            EnchantmentHelper.enchant(random,enchantedBook,l,registryEntryStream.stream());
                            list.addAll(getTooltipFromItem(enchantedBook));
                            list.add(mutableText);


                            boolean hasEnchantLevel = EnchantmentHelper.getLevel(enchantEntry,stack)>=l+1;

                            ItemStack enchantIngredientStack;

                            if(enchantIngredient != null){
                                enchantIngredientStack = new ItemStack(enchantIngredient, 1);
                            }else {
                                enchantIngredientStack = new ItemStack(Items.BARRIER, 1);
                            }

                            if (!isInCreative && !hasEnchantLevel) {
                                list.add(ScreenTexts.EMPTY);

                                if (this.client.player.experienceLevel < enchantLevReq) {
                                    list.add(Text.translatable("container.enchant.level.requirement", new Object[]{enchantLevReq}).formatted(Formatting.RED));
                                }else{
                                    if (lapisCost == 1) {
                                        mutableText = Text.translatable("container.enchant.lapis.one");
                                    } else {
                                        mutableText = Text.translatable("container.enchant.lapis.many", new Object[]{lapisCost});
                                    }

                                    list.add(mutableText.formatted(lapisCount >= (int)Math.floor(enchantLevelCost/2) ? Formatting.GRAY : Formatting.RED));

                                    MutableText mutableText2;
                                    if (enchantLevReq == 1) {
                                        mutableText2 = Text.translatable("container.enchant.level.one");
                                    } else {
                                        mutableText2 = Text.translatable("container.enchant.level.many", enchantLevelCost);
                                    }

                                    list.add(mutableText2.formatted(client.player.experienceLevel >= enchantLevelCost ? Formatting.GRAY : Formatting.RED));

                                    MutableText mutableText3;
                                    mutableText3 = Text.translatable("container.betterenchanting.enchant.material.one", enchantIngredientCost, Text.translatable(enchantIngredientStack.getTranslationKey()));

                                    if(handler.getSlot(2).getStack().getItem() != enchantIngredient)
                                        list.add(mutableText3.formatted(Formatting.RED));
                                    else if (handler.getSlot(2).getStack().getCount() < enchantIngredientCost)
                                        list.add(mutableText3.formatted(Formatting.RED));
                                    else
                                        list.add(mutableText3.formatted(Formatting.GRAY));

                                }
                                //context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
                            }

                            context.drawItem(enchantIngredientStack,i +22, j +26);

                            context.drawTooltip(this.textRenderer, list, mouseX, mouseY);
                        }
                    }
                }
            }
        }

    }

    private boolean canScroll(int listSize) {
        return listSize > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int i = handler.totalEnchantForItem();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.indexStartOffset = MathHelper.clamp((int)((double)this.indexStartOffset - verticalAmount), 0, j);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        int i = handler.totalEnchantForItem();
        if (this.scrolling) {
            int j = this.y + 13;
            int k = j + 112;
            int l = i - 7;
            float f = ((float)mouseY - (float)j - 13.5f) / ((float)(k - j) - 27.0f);
            f = f * (float)l + 0.5f;
            this.indexStartOffset = MathHelper.clamp((int)f, 0, l);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void renderScrollbar(DrawContext context, int x, int y,int totalNumberOfEnchants) {
        int i = totalNumberOfEnchants + 1 - 7;
        if (i > 1) {
            int j = 112 - (27 + (i - 1) * 112 / i);
            int k = 1 + j / i + 112 / i;
            int l = 86;
            int m = Math.min(86, this.indexStartOffset * k);
            if (this.indexStartOffset == i - 1) {
                m = 86;
            }
            context.drawGuiTexture(SCROLLER, x + 56, y + 13 + m, 0, 6, 27);
        } else {
            context.drawGuiTexture(SCROLLER_DISABLED, x + 56, y + 13, 0, 6, 27);
        }
    }

    public void doTick() {
        ItemStack itemStack = this.handler.getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack, this.stack)) {
            this.stack = itemStack;

            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.nextPageAngle <= this.approximatePageAngle + 1.0F && this.nextPageAngle >= this.approximatePageAngle - 1.0F);
        }

        ++this.ticks;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;

        for(int i = 0; i < CustomEnchantmentScreenHandler.ENCHANT_ARRAY_SIZE; ++i) {
            if (this.handler.enchantmentLevel[i] != 0) {
                bl = true;
                break;
            }
        }

        if (bl) {
            this.nextPageTurningSpeed += 0.2F;
        } else {
            this.nextPageTurningSpeed -= 0.2F;
        }

        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4F;
        float g = 0.2F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9F;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}
