package com.fooddelivery.config;

import com.fooddelivery.entity.*;
import com.fooddelivery.repository.FoodItemRepository;
import com.fooddelivery.repository.RestaurantRepository;
import com.fooddelivery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Automatically seeds initial demo users, restaurants, and menu items if the database is empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RestaurantRepository restaurantRepository,
                           FoodItemRepository foodItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.foodItemRepository = foodItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedRestaurantsAndFoods();
    }

    private void seedUsers() {
        if (userRepository.findByEmail("admin@feastify.com").isEmpty()) {
            userRepository.save(User.builder()
                    .fullName("Platform Admin")
                    .email("admin@feastify.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .blocked(false)
                    .build());
        }

        if (userRepository.findByEmail("customer@feastify.com").isEmpty()) {
            userRepository.save(User.builder()
                    .fullName("Alex Customer")
                    .email("customer@feastify.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CUSTOMER)
                    .accountStatus(AccountStatus.ACTIVE)
                    .blocked(false)
                    .build());
        }
    }

    private void seedRestaurantsAndFoods() {
        // --- 1. Bella Italia Pizzeria ---
        if (!restaurantRepository.existsByName("Bella Italia Pizzeria")) {
            User mario = getOrCreateOwner("mario@feastify.com", "Mario Rossi");
            Restaurant bellaItalia = restaurantRepository.save(Restaurant.builder()
                    .name("Bella Italia Pizzeria")
                    .description("Authentic wood-fired Neapolitan pizzas, artisanal calzones, and hand-crafted desserts.")
                    .address("42 Little Italy Way, Downtown")
                    .phone("+1 (555) 234-5678")
                    .imageUrl("/images/categories/pizza.jpg")
                    .openingTime(LocalTime.of(10, 0))
                    .closingTime(LocalTime.of(23, 0))
                    .active(true)
                    .approved(true)
                    .verified(true)
                    .averageRating(4.8)
                    .totalReviews(124)
                    .owner(mario)
                    .build());

            foodItemRepository.saveAll(List.of(
                    FoodItem.builder()
                            .name("Margherita Classica")
                            .description("San Marzano tomato sauce, fresh mozzarella di bufala, basil, and extra virgin olive oil.")
                            .price(new BigDecimal("14.99"))
                            .imageUrl("/images/categories/pizza.jpg")
                            .category(Category.PIZZA)
                            .veg(true)
                            .available(true)
                            .restaurant(bellaItalia)
                            .build(),
                    FoodItem.builder()
                            .name("Truffle Mushroom Pizza")
                            .description("Wild forest mushrooms, truffle cream sauce, mozzarella, fontina cheese, and fresh thyme.")
                            .price(new BigDecimal("18.50"))
                            .imageUrl("/images/categories/pizza.jpg")
                            .category(Category.PIZZA)
                            .veg(true)
                            .available(true)
                            .restaurant(bellaItalia)
                            .build(),
                    FoodItem.builder()
                            .name("Pepperoni Rustica")
                            .description("Spicy cured pepperoni slices, roasted garlic, tomato sauce, and double mozzarella.")
                            .price(new BigDecimal("17.99"))
                            .imageUrl("/images/categories/pizza.jpg")
                            .category(Category.PIZZA)
                            .veg(false)
                            .available(true)
                            .restaurant(bellaItalia)
                            .build(),
                    FoodItem.builder()
                            .name("Tiramisu Italiano")
                            .description("Traditional espresso-soaked ladyfingers layered with sweet mascarpone cream and cocoa.")
                            .price(new BigDecimal("8.99"))
                            .imageUrl("/images/categories/dessert.jpg")
                            .category(Category.DESSERT)
                            .veg(true)
                            .available(true)
                            .restaurant(bellaItalia)
                            .build(),
                    FoodItem.builder()
                            .name("San Pellegrino Sparkling")
                            .description("Crisp Italian natural sparkling mineral water (500ml).")
                            .price(new BigDecimal("3.99"))
                            .imageUrl("/images/categories/drink.jpg")
                            .category(Category.DRINK)
                            .veg(true)
                            .available(true)
                            .restaurant(bellaItalia)
                            .build()
            ));
        }

        // --- 2. The Gourmet Burger Lab ---
        if (!restaurantRepository.existsByName("The Gourmet Burger Lab")) {
            User burgerOwner = getOrCreateOwner("burgerking@feastify.com", "Sam Burger");
            Restaurant burgerLab = restaurantRepository.save(Restaurant.builder()
                    .name("The Gourmet Burger Lab")
                    .description("Smash burgers made with 100% prime Angus beef, toasted brioche buns, and gourmet sides.")
                    .address("88 Broadway Ave, Midtown")
                    .phone("+1 (555) 345-6789")
                    .imageUrl("/images/categories/burger.jpg")
                    .openingTime(LocalTime.of(11, 0))
                    .closingTime(LocalTime.of(23, 30))
                    .active(true)
                    .approved(true)
                    .verified(true)
                    .averageRating(4.7)
                    .totalReviews(98)
                    .owner(burgerOwner)
                    .build());

            foodItemRepository.saveAll(List.of(
                    FoodItem.builder()
                            .name("Double Smoked Bacon Cheeseburger")
                            .description("Two Angus smashed patties, aged cheddar, applewood smoked bacon, and house secret sauce.")
                            .price(new BigDecimal("15.99"))
                            .imageUrl("/images/categories/burger.jpg")
                            .category(Category.BURGER)
                            .veg(false)
                            .available(true)
                            .restaurant(burgerLab)
                            .build(),
                    FoodItem.builder()
                            .name("Crispy Truffle Portobello Burger")
                            .description("Panko-crusted portobello mushroom stuffed with gouda, arugula, and truffle aioli.")
                            .price(new BigDecimal("13.99"))
                            .imageUrl("/images/categories/burger.jpg")
                            .category(Category.BURGER)
                            .veg(true)
                            .available(true)
                            .restaurant(burgerLab)
                            .build(),
                    FoodItem.builder()
                            .name("BBQ Chicken Crunch Roll")
                            .description("Grilled chicken breast strips with smoky barbecue glaze, crispy onions, and cheddar wrap.")
                            .price(new BigDecimal("11.50"))
                            .imageUrl("/images/categories/rolls.jpg")
                            .category(Category.ROLLS)
                            .veg(false)
                            .available(true)
                            .restaurant(burgerLab)
                            .build(),
                    FoodItem.builder()
                            .name("Craft Lemonade Fizz")
                            .description("Hand-squeezed Meyer lemons infused with mint and sparkling soda.")
                            .price(new BigDecimal("4.50"))
                            .imageUrl("/images/categories/drink.jpg")
                            .category(Category.DRINK)
                            .veg(true)
                            .available(true)
                            .restaurant(burgerLab)
                            .build()
            ));
        }

        // --- 3. Royal Taj Spice Kitchen ---
        if (!restaurantRepository.existsByName("Royal Taj Spice Kitchen")) {
            User spiceOwner = getOrCreateOwner("spice@feastify.com", "Rajesh Sharma");
            Restaurant royalTaj = restaurantRepository.save(Restaurant.builder()
                    .name("Royal Taj Spice Kitchen")
                    .description("Rich aromatic curries, tender clay-oven tandoori kebabs, and golden buttery naans.")
                    .address("104 Curry Garden, Eastside")
                    .phone("+1 (555) 456-7890")
                    .imageUrl("/images/categories/indian.jpg")
                    .openingTime(LocalTime.of(12, 0))
                    .closingTime(LocalTime.of(22, 30))
                    .active(true)
                    .approved(true)
                    .verified(true)
                    .averageRating(4.9)
                    .totalReviews(210)
                    .owner(spiceOwner)
                    .build());

            foodItemRepository.saveAll(List.of(
                    FoodItem.builder()
                            .name("Butter Chicken Deluxe")
                            .description("Tender tandoori chicken simmered in a silky tomato, cashew, and fenugreek butter gravy.")
                            .price(new BigDecimal("16.99"))
                            .imageUrl("/images/categories/indian.jpg")
                            .category(Category.INDIAN)
                            .veg(false)
                            .available(true)
                            .restaurant(royalTaj)
                            .build(),
                    FoodItem.builder()
                            .name("Paneer Tikka Masala")
                            .description("Char-grilled cottage cheese cubes tossed in rich spiced bell pepper and onion masala.")
                            .price(new BigDecimal("14.99"))
                            .imageUrl("/images/categories/indian.jpg")
                            .category(Category.INDIAN)
                            .veg(true)
                            .available(true)
                            .restaurant(royalTaj)
                            .build(),
                    FoodItem.builder()
                            .name("Hyderabadi Dum Biryani")
                            .description("Long grain fragrant basmati rice slow-cooked with aromatic spices and saffron.")
                            .price(new BigDecimal("17.50"))
                            .imageUrl("/images/categories/indian.jpg")
                            .category(Category.INDIAN)
                            .veg(false)
                            .available(true)
                            .restaurant(royalTaj)
                            .build(),
                    FoodItem.builder()
                            .name("Crispy Masala Dosa")
                            .description("Crispy fermented crepe filled with spiced potato masala, served with fresh coconut chutney & sambar.")
                            .price(new BigDecimal("11.99"))
                            .imageUrl("/images/categories/south_indian.jpg")
                            .category(Category.SOUTH_INDIAN)
                            .veg(true)
                            .available(true)
                            .restaurant(royalTaj)
                            .build(),
                    FoodItem.builder()
                            .name("Alphonso Mango Lassi")
                            .description("Thick creamy churned yogurt blended with sweet ripe Alphonso mangoes and cardamom.")
                            .price(new BigDecimal("4.99"))
                            .imageUrl("/images/categories/drink.jpg")
                            .category(Category.DRINK)
                            .veg(true)
                            .available(true)
                            .restaurant(royalTaj)
                            .build()
            ));
        }

        // --- 4. Golden Dragon Wok ---
        if (!restaurantRepository.existsByName("Golden Dragon Wok")) {
            User wokOwner = getOrCreateOwner("wok@feastify.com", "Chen Wei");
            Restaurant goldenDragon = restaurantRepository.save(Restaurant.builder()
                    .name("Golden Dragon Wok")
                    .description("Authentic Sichuan delicacies, hand-pulled noodles, spicy dim sums, and wok-tossed favorites.")
                    .address("15 Chinatown Plaza, West End")
                    .phone("+1 (555) 567-8901")
                    .imageUrl("/images/categories/chinese.jpg")
                    .openingTime(LocalTime.of(11, 30))
                    .closingTime(LocalTime.of(22, 0))
                    .active(true)
                    .approved(true)
                    .verified(true)
                    .averageRating(4.6)
                    .totalReviews(85)
                    .owner(wokOwner)
                    .build());

            foodItemRepository.saveAll(List.of(
                    FoodItem.builder()
                            .name("Dan Dan Spicy Noodles")
                            .description("Handcrafted noodles in savory chili oil, sesame broth, ground chicken, and crushed peanuts.")
                            .price(new BigDecimal("13.99"))
                            .imageUrl("/images/categories/noodles.jpg")
                            .category(Category.NOODLES)
                            .veg(false)
                            .available(true)
                            .restaurant(goldenDragon)
                            .build(),
                    FoodItem.builder()
                            .name("Kung Pao Crispy Tofu")
                            .description("Crispy tofu cubes with Sichuan peppers, zucchini, scallions, and roasted peanuts in sweet-spicy glaze.")
                            .price(new BigDecimal("12.50"))
                            .imageUrl("/images/categories/chinese.jpg")
                            .category(Category.CHINESE)
                            .veg(true)
                            .available(true)
                            .restaurant(goldenDragon)
                            .build(),
                    FoodItem.builder()
                            .name("Peking Crispy Spring Rolls")
                            .description("Golden flaky rolls stuffed with shredded vegetables and glass noodles with sweet plum dip.")
                            .price(new BigDecimal("8.99"))
                            .imageUrl("/images/categories/rolls.jpg")
                            .category(Category.ROLLS)
                            .veg(true)
                            .available(true)
                            .restaurant(goldenDragon)
                            .build(),
                    FoodItem.builder()
                            .name("Sweet & Sour Sesame Chicken")
                            .description("Wok-tossed battered chicken chunks in tangy pineapple honey sauce with sesame sprinkles.")
                            .price(new BigDecimal("15.50"))
                            .imageUrl("/images/categories/chinese.jpg")
                            .category(Category.CHINESE)
                            .veg(false)
                            .available(true)
                            .restaurant(goldenDragon)
                            .build()
            ));
        }

        // --- 5. Velvet & Bean Roastery ---
        if (!restaurantRepository.existsByName("Velvet & Bean Roastery")) {
            User cafeOwner = getOrCreateOwner("cafe@feastify.com", "Emma Baker");
            Restaurant velvetBean = restaurantRepository.save(Restaurant.builder()
                    .name("Velvet & Bean Roastery")
                    .description("Specialty third-wave single-origin coffees, artisanal matcha lattes, and artisan patisserie.")
                    .address("200 Artisan Square, Uptown")
                    .phone("+1 (555) 678-9012")
                    .imageUrl("/images/categories/coffee.jpg")
                    .openingTime(LocalTime.of(7, 30))
                    .closingTime(LocalTime.of(19, 0))
                    .active(true)
                    .approved(true)
                    .verified(true)
                    .averageRating(4.8)
                    .totalReviews(145)
                    .owner(cafeOwner)
                    .build());

            foodItemRepository.saveAll(List.of(
                    FoodItem.builder()
                            .name("Iced Caramel Macchiato")
                            .description("Freshly pulled espresso layered over silky vanilla milk and drizzled with buttery caramel.")
                            .price(new BigDecimal("5.50"))
                            .imageUrl("/images/categories/coffee.jpg")
                            .category(Category.COFFEE)
                            .veg(true)
                            .available(true)
                            .restaurant(velvetBean)
                            .build(),
                    FoodItem.builder()
                            .name("Belgian Dark Chocolate Truffle Cake")
                            .description("Decadent 70% dark chocolate sponge with rich chocolate ganache and chocolate curls.")
                            .price(new BigDecimal("7.50"))
                            .imageUrl("/images/categories/cake.jpg")
                            .category(Category.CAKE)
                            .veg(true)
                            .available(true)
                            .restaurant(velvetBean)
                            .build(),
                    FoodItem.builder()
                            .name("Acai Berry Superfood Bowl")
                            .description("Organic Amazonian acai blend topped with fresh blueberries, bananas, chia seeds, and granola.")
                            .price(new BigDecimal("9.99"))
                            .imageUrl("/images/categories/healthy.jpg")
                            .category(Category.HEALTHY)
                            .veg(true)
                            .available(true)
                            .restaurant(velvetBean)
                            .build(),
                    FoodItem.builder()
                            .name("Classic New York Cheesecake")
                            .description("Velvety cream cheese filling on buttery graham cracker crust with strawberry coulis.")
                            .price(new BigDecimal("6.99"))
                            .imageUrl("/images/categories/dessert.jpg")
                            .category(Category.DESSERT)
                            .veg(true)
                            .available(true)
                            .restaurant(velvetBean)
                            .build()
            ));
        }
    }

    private User getOrCreateOwner(String email, String fullName) {
        return userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .fullName(fullName)
                        .email(email)
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.RESTAURANT_OWNER)
                        .accountStatus(AccountStatus.ACTIVE)
                        .blocked(false)
                        .build())
        );
    }
}
