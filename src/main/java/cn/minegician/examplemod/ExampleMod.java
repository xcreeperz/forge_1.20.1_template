package cn.minegician.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// 这里的值应该与 META-INF/mods.toml 文件中的条目匹配
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // 在一个公共的地方定义 mod id，以供所有引用
    public static final String MODID = "examplemod";
    // 直接引用一个 slf4j 日志记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个 Deferred Register 来保存所有的方块，它们将全部注册在 "examplemod" 命名空间下
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // 创建一个 Deferred Register 来保存所有的物品，它们将全部注册在 "examplemod" 命名空间下
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // 创建一个 Deferred Register 来保存所有的创造模式标签，它们将全部注册在 "examplemod" 命名空间下
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个新的方块，id 为 "examplemod:example_block"，结合了命名空间和路径
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    // 创建一个新的方块物品，id 为 "examplemod:example_block"，结合了命名空间和路径
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties()));

    // 创建一个新的食物物品，id 为 "examplemod:example_id"，营养值为 1，饱和度为 2
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEat().nutrition(1).saturationMod(2f).build())));

    // 创建一个创造模式标签，id 为 "examplemod:example_tab"，用于示例物品，并且放置在战斗标签之后
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // 将示例物品添加到标签中。对于你自己的标签，此方法优于事件
            }).build());

    public ExampleMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 将 commonSetup 方法注册到 modloading 事件中
        modEventBus.addListener(this::commonSetup);

        // 将 Deferred Register 注册到 mod 事件总线中，以便注册方块
        BLOCKS.register(modEventBus);
        // 将 Deferred Register 注册到 mod 事件总线中，以便注册物品
        ITEMS.register(modEventBus);
        // 将 Deferred Register 注册到 mod 事件总线中，以便注册标签
        CREATIVE_MODE_TABS.register(modEventBus);

        // 将自己注册到我们感兴趣的服务器和其他游戏事件中
        MinecraftForge.EVENT_BUS.register(this);

        // 将物品注册到一个创造模式标签中
        modEventBus.addListener(this::addCreative);

        // 注册我们的 mod 的 ForgeConfigSpec，以便 Forge 可以为我们创建并加载配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 一些公共的设置代码
        LOGGER.info("来自公共设置的问候");

        if (Config.logDirtBlock)
            LOGGER.info("泥土方块 >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("物品 >> {}", item.toString()));
    }

    // 将示例方块物品添加到建筑标签中
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // 您可以使用 SubscribeEvent 并让事件总线发现要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // 服务器启动时执行的操作
        LOGGER.info("来自服务器启动的问候");
    }

    // 您可以使用 EventBusSubscriber 来自动注册带有 @SubscribeEvent 注解的类中的所有静态方法
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 一些客户端设置代码
            LOGGER.info("来自客户端设置的问候");
            LOGGER.info("Minecraft 名称 >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
