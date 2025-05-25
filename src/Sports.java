import java.util.*;
import java.util.stream.Collectors;

public class Sports {
    private Set<String> activities = new HashSet<>();
    private Map<String, Set<String>> categories = new HashMap<>();
    private Map<String, Product> products = new HashMap<>();

    // Ichki sinf: Rating
    private static class Rating {
        String user;
        int stars;
        String comment;

        Rating(String user, int stars, String comment) {
            this.user = user;
            this.stars = stars;
            this.comment = comment;
        }

        public int getStars() {
            return stars;
        }

        public String toString() {
            return stars + " : " + comment;
        }
    }

    // Ichki sinf: Product
    private static class Product {
        String name;
        String activity;
        String category;
        List<Rating> ratings = new ArrayList<>();

        Product(String name, String activity, String category) {
            this.name = name;
            this.activity = activity;
            this.category = category;
        }

        void addRating(String user, int stars, String comment) {
            if (stars < 0 || stars > 5) {
                throw new SportsException("Stars must be between 0 and 5.");
            }
            ratings.add(new Rating(user, stars, comment));
        }

        double getAverageStars() {
            return ratings.stream().mapToInt(Rating::getStars).average().orElse(0.0);
        }
    }

    // R1: Activities and Categories
    public void defineActivities(String... activities) {
        if (activities.length == 0) throw new SportsException("No activity provided.");
        this.activities.addAll(Arrays.asList(activities));
    }

    public List<String> getActivities() {
        return activities.stream().sorted().collect(Collectors.toList());
    }

    public void addCategory(String name, String... linkedActivities) {
        for (String activity : linkedActivities) {
            if (!activities.contains(activity)) {
                throw new SportsException("Activity not defined: " + activity);
            }
        }
        categories.put(name, new HashSet<>(Arrays.asList(linkedActivities)));
    }

    public int countCategories() {
        return categories.size();
    }

    public List<String> getCategoriesForActivity(String activity) {
        return categories.entrySet().stream()
                .filter(e -> e.getValue().contains(activity))
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    // R2: Products
    public void addProduct(String name, String activity, String category) {
        if (products.containsKey(name)) {
            throw new SportsException("Product already exists.");
        }
        if (!activities.contains(activity)) {
            throw new SportsException("Activity not defined: " + activity);
        }
        if (!categories.containsKey(category)) {
            throw new SportsException("Category not defined: " + category);
        }
        if (!categories.get(category).contains(activity)) {
            throw new SportsException("Category not linked to activity.");
        }
        products.put(name, new Product(name, activity, category));
    }

    public List<String> getProductsForCategory(String categoryName) {
        return products.values().stream()
                .filter(p -> p.category.equals(categoryName))
                .map(p -> p.name)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getProductsForActivity(String activityName) {
        return products.values().stream()
                .filter(p -> p.activity.equals(activityName))
                .map(p -> p.name)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getProducts(String activityName, String categoryNames) {
        Set<String> categorySet = new HashSet<>(Arrays.asList(categoryNames.split(",")));
        return products.values().stream()
                .filter(p -> p.activity.equals(activityName) && categorySet.contains(p.category))
                .map(p -> p.name)
                .sorted()
                .collect(Collectors.toList());
    }

    // R3: Users and Ratings
    public void addRating(String productName, String userName, int numStars, String comment) {
        Product p = products.get(productName);
        if (p == null) throw new SportsException("Product not found.");
        p.addRating(userName, numStars, comment);
    }

    public List<String> getRatingsForProduct(String productName) {
        Product p = products.get(productName);
        if (p == null) return new ArrayList<>();
        return p.ratings.stream()
                .sorted((r1, r2) -> Integer.compare(r2.stars, r1.stars))
                .map(Rating::toString)
                .collect(Collectors.toList());
    }

    // R4: Evaluations
    public double getStarsOfProduct(String productName) {
        Product p = products.get(productName);
        if (p == null) return 0.0;
        return p.getAverageStars();
    }

    public double averageStars() {
        return products.values().stream()
                .flatMap(p -> p.ratings.stream())
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);
    }

    // R5: Statistics
    public Map<String, Double> starsPerActivity() {
        Map<String, List<Product>> activityProducts = products.values().stream()
                .filter(p -> !p.ratings.isEmpty())
                .collect(Collectors.groupingBy(p -> p.activity));

        return activityProducts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .mapToDouble(Product::getAverageStars)
                                .average()
                                .orElse(0.0),
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

    public Map<Double, List<String>> getProductsPerStars() {
        return products.values().stream()
                .filter(p -> !p.ratings.isEmpty())
                .collect(Collectors.groupingBy(
                        Product::getAverageStars,
                        () -> new TreeMap<>(Comparator.reverseOrder()),
                        Collectors.mapping(p -> p.name, Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.sort(Comparator.naturalOrder());
                            return list;
                        }))
                ));
    }
}
