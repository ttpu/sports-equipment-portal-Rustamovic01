public class Main {
    public static void main(String[] args) {
        Sports portal = new Sports();

        //  Activat
        portal.defineActivities("Football", "Tennis", "Running");
        portal.addCategory("Shoes", "Running", "Tennis");
        portal.addCategory("Balls", "Football", "Tennis");

        //  Produc
        portal.addProduct("Nike Zoom", "Running", "Shoes");
        portal.addProduct("Adidas Ball", "Football", "Balls");

        //  Users and Ratgs
        portal.addRating("Nike Zoom", "Alice", 5, "Very comfortable");
        portal.addRating("Nike Zoom", "Bob", 4, "Good shoes");
        portal.addRating("Adidas Ball", "Charlie", 3, "Nice ball");

        //  Evaluations
        System.out.println("Average Stars of Nike Zoom: " + portal.getStarsOfProduct("Nike Zoom"));
        System.out.println("Overall Average Stars: " + portal.averageStars());

        // Statistics
        System.out.println("Stars Per Activity: " + portal.starsPerActivity());
        System.out.println("Products Per Stars: " + portal.getProductsPerStars());
    }
}
