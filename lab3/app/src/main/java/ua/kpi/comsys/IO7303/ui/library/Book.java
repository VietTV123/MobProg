package ua.kpi.comsys.IO7303.ui.library;

public class Book {
    private String title, subtitle, price, isbn13, image;

    public Book(String title, String subtitle, String price, String isbn13, String image) {
        this.title = title;
        this.subtitle = subtitle;
        this.price = price;
        this.isbn13 = isbn13;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return  title + '\n' +
                subtitle + '\n' +
                "Type: " + price;
    }
}
