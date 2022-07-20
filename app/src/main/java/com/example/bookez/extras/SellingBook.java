package com.example.bookez.extras;

public class SellingBook {

    //unique IDs
    private String sellerbookid;
    private String bookid;
    private String sellerid;

    //Book integer details
    private int quantities;
    private int sellingprice;
    private int rentingprice;
    private int deliverycharges;

    //Book string details
    private String title;
    private String author;
    private String description;
    private String category;

    //Links
    private String thumbnail;
    private String preview;

    public SellingBook(){ }

    public SellingBook(String bookid,String sellerbookid,String sellerid,int quantities,int sellingprice,int rentingprice,int deliverycharges,String title,String author,String description,String category,String thumbnail,String preview){
        this.bookid=bookid;
        this.sellerbookid=sellerbookid;
        this.sellerid=sellerid;
        this.quantities=quantities;
        this.sellingprice=sellingprice;
        this.rentingprice=rentingprice;
        this.deliverycharges=deliverycharges;
        this.title=title;
        this.author=author;
        this.description=description;
        this.category=category;
        this.thumbnail=thumbnail;
        this.preview=preview;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getSellerbookid() { return sellerbookid; }

    public void setSellerbookid(String sellerbookid) {
        this.sellerbookid = sellerbookid;
    }

    public String getSellerid() {
        return sellerid;
    }

    public void setSellerid(String sellerid) {
        this.sellerid = sellerid;
    }

    public int getQuantities() {
        return quantities;
    }

    public void setQuantities(int quantities) {
        this.quantities = quantities;
    }

    public int getSellingprice() {
        return sellingprice;
    }

    public void setSellingprice(int sellingprice) {
        this.sellingprice = sellingprice;
    }

    public int getRentingprice() {
        return rentingprice;
    }

    public void setRentingprice(int rentingprice) {
        this.rentingprice = rentingprice;
    }

    public int getDeliverycharges() {
        return deliverycharges;
    }

    public void setDeliverycharges(int deliverycharges) {
        this.deliverycharges = deliverycharges;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }


}
