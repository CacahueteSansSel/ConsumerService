package studios.nightek.consume;

public class ConsumerGroups {
    public static final CustomItemGroup MAIN = new CustomItemGroup("main", () -> ConsumerItems.MOD_ICON_CART.getDefaultInstance());
    public static final CustomItemGroup FOOD = new CustomItemGroup("food", () -> ConsumerItems.FOOD_MINCED_BEEF.baked.getDefaultInstance());
    public static final CustomItemGroup WRAPPERS = new CustomItemGroup("wrappers", () -> ConsumerItems.WRAPPER.getDefaultInstance())
            .withSearchBar();
}
