import java.util.*;

class SportsException extends Exception {
    public SportsException(String message) {
        super(message);
    }
}

class Rating {
    String user;
    int stars;
    String comment;

    public Rating(String user, int stars, String comment) {
        this.user = user;
        this.stars = stars;
        this.comment = comment;
    }

    public String toString() {
        return stars + " : " + comment;
    }
}

class Product {
    String name;
    String activity;
    String category;
    List<Rating> ratings = new ArrayList<>();

    public Product(String name, String activity, String category) {
        this.name = name;
        this.activity = activity;
        this.category = category;
    }

    public void addRating(Rating r) {
        ratings.add(r);
    }

    public List<Rating> getSortedRatings() {
        ratings.sort((r1, r2) -> Integer.compare(r2.stars, r1.stars));
        return ratings;
    }

    public double averageStars() {
        return ratings.stream().mapToInt(r -> r.stars).average().orElse(0.0);
    }
}

public class Sports {
    Set<String> activities = new TreeSet<>();
    Map<String, Set<String>> categories = new TreeMap<>();
    Map<String, Product> products = new TreeMap<>();

    // R1
    public void defineActivities(String... activities) throws SportsException {
        if (activities.length == 0) throw new SportsException("No activities provided");
        this.activities.addAll(Arrays.asList(activities));
    }

    public List<String> getActivities() {
        return new ArrayList<>(activities);
    }

    public void addCategory(String name, String... linkedActivities) throws SportsException {
        for (String a : linkedActivities) {
            if (!activities.contains(a)) throw new SportsException("Activity not found: " + a);
        }
        categories.put(name, new TreeSet<>(Arrays.asList(linkedActivities)));
    }

    public int countCategories() {
        return categories.size();
    }

    public List<String> getCategoriesForActivity(String activity) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : categories.entrySet()) {
            if (entry.getValue().contains(activity)) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        return result;
    }

    // R2
    public void addProduct(String name, String activityName, String categoryName) throws SportsException {
        if (products.containsKey(name)) throw new SportsException("Duplicate product name");
        if (!activities.contains(activityName)) throw new SportsException("Activity not found");
        if (!categories.containsKey(categoryName)) throw new SportsException("Category not found");
        if (!categories.get(categoryName).contains(activityName)) throw new SportsException("Activity not linked to category");

        products.put(name, new Product(name, activityName, categoryName));
    }

    public List<String> getProductsForCategory(String categoryName) {
        List<String> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.category.equals(categoryName)) result.add(p.name);
        }
        Collections.sort(result);
        return result;
    }

    public List<String> getProductsForActivity(String activityName) {
        List<String> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.activity.equals(activityName)) result.add(p.name);
        }
        Collections.sort(result);
        return result;
    }

    public List<String> getProducts(String activityName, String... categoryNames) {
        Set<String> catSet = new HashSet<>(Arrays.asList(categoryNames));
        List<String> result = new ArrayList<>();
        for (Product p : products.values()) {
            if (p.activity.equals(activityName) && catSet.contains(p.category)) {
                result.add(p.name);
            }
        }
        Collections.sort(result);
        return result;
    }

    // R3
    public void addRating(String productName, String userName, int numStars, String comment) throws SportsException {
        if (numStars < 1 || numStars > 5) throw new SportsException("Stars must be between 1 and 5");
        Product p = products.get(productName);
        if (p != null) {
            p.addRating(new Rating(userName, numStars, comment));
        }
    }

    public List<String> getRatingsForProduct(String productName) {
        Product p = products.get(productName);
        List<String> result = new ArrayList<>();
        if (p != null) {
            for (Rating r : p.getSortedRatings()) {
                result.add(r.toString());
            }
        }
        return result;
    }

    // R4
    public double getStarsOfProduct(String productName) {
        Product p = products.get(productName);
        return p != null ? p.averageStars() : 0.0;
    }

    public double averageStars() {
        return products.values().stream()
                .flatMap(p -> p.ratings.stream())
                .mapToInt(r -> r.stars)
                .average()
                .orElse(0.0);
    }

    // R5
    public SortedMap<String, Double> starsPerActivity() {
        Map<String, List<Integer>> actStars = new HashMap<>();
        for (Product p : products.values()) {
            actStars.putIfAbsent(p.activity, new ArrayList<>());
            for (Rating r : p.ratings) {
                actStars.get(p.activity).add(r.stars);
            }
        }

        SortedMap<String, Double> result = new TreeMap<>();
        for (Map.Entry<String, List<Integer>> e : actStars.entrySet()) {
            double avg = e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0.0);
            result.put(e.getKey(), avg);
        }
        return result;
    }

    public SortedMap<Double, List<String>> getProductsPerStars() {
        Map<Double, List<String>> starsMap = new HashMap<>();
        for (Product p : products.values()) {
            double avg = p.averageStars();
            starsMap.computeIfAbsent(avg, k -> new ArrayList<>()).add(p.name);
        }

        SortedMap<Double, List<String>> result = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Double, List<String>> entry : starsMap.entrySet()) {
            List<String> sortedList = entry.getValue();
            Collections.sort(sortedList);
            result.put(entry.getKey(), sortedList);
        }
        return result;
    }
}
