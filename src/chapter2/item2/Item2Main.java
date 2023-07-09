package chapter2.item2;

import static chapter2.item2.Pizza.Topping.*;

public class Item2Main {
    public static void main(String[] args) {
        NutritionFacts cola = new NutritionFacts.Builder(240, 8)
                .calories(100)
                .fat(0)
                .build();

        NyPizza newYorkPizza = new NyPizza.Builder(NyPizza.Size.SMALL)
                .addTopping(SAUSAGE)
                .addTopping(ONION)
                .build();

        CalzonePizza calzonePizza = new CalzonePizza.Builder()
                .addTopping(HAM)
                .sauceInside()
                .build();
    }
}
