import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.*;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        String result = "";

        // GetItemList
        List<Item> itemList = this.itemRepository.findAll();
        // output the title
        result += "============= Order details =============\n";
        // Get the sumPrice and all items
        int sumPrice = 0;
        Map<String, Pair<Item, Integer>> sumItemMap = new HashMap<String, Pair<Item, Integer>>();
        // output the item
        for(String input : inputs) {
            // split nowItem to id and count
            String[] nowItem = input.split(" x ");
            String nowItemId = nowItem[0];
            int nowItemCount = Integer.parseInt(nowItem[1]);
            // find the item of sameId
            for(Item item : itemList) {
                // find the same item
                if(item.getId().equals(nowItemId)) {
                    // add the price
                    int itemPrice = (int)item.getPrice() * nowItemCount;
                    sumPrice += (int)item.getPrice() * nowItemCount;
                    // save the item
                    sumItemMap.put(item.getId(), new Pair<>(item, nowItemCount));
                    // output this item
                    result += String.format("%s x %s = %d yuan\n", item.getName(), nowItemCount, itemPrice);
                    break;
                }
            }
        }
        // output the split line
        result += "-----------------------------------\n";
        // output the Promotion used
        List<SalesPromotion> promotionList = this.salesPromotionRepository.findAll();
        String promotionString = new String();
        int sumOfSaving = 0;
        for(SalesPromotion promotion : promotionList) {
            switch (promotion.getType()) {
                case "50%_DISCOUNT_ON_SPECIFIED_ITEMS":
                    // get the sale items
                    List<String> promotionItems = promotion.getRelatedItems();
                    List<String> promotionItemsName = new ArrayList<>();
                    // the sale price
                    int savingPrice = 0, minCount = Integer.MAX_VALUE;
                    // the break flag
                    boolean breakFlag = false;
                    for(String promotionItem : promotionItems) {
                        Pair<Item, Integer> item = sumItemMap.get(promotionItem);
                        if(item == null) {
                            breakFlag = true;
                            break;
                        }
                        promotionItemsName.add(item.getKey().getName());
                        savingPrice += item.getKey().getPrice();
                        minCount = Math.min(minCount, item.getValue());
                    }
                    // if not have all the item of the sale items, just break
                    if(breakFlag) {
                        break;
                    }
                    savingPrice = savingPrice * minCount / 2;
                    promotionString += String.format("%s (%s), saving %d yuan\n", promotion.getDisplayName(), String.join(" and ", promotionItemsName), savingPrice);
                    sumOfSaving +=  savingPrice;
                    break;
                case "BUY_30_SAVE_6_YUAN":
                    if(sumPrice - sumOfSaving >= 30){
                        promotionString += promotion.getDisplayName() + "\n";
                        sumOfSaving += 6;
                    }
                    break;
            }
        }
        // output the promotions
        if(promotionString.length() > 0) {
            result += "Promotion used:\n";
            result += promotionString;
            result += "-----------------------------------\n";
        }
        // output the sumPrice
        result += String.format("Total: %d yuan\n", sumPrice - sumOfSaving);
        // output the end line
        result += "===================================";
        return result;
    }
}
