package com.example.bookez.extras;

public class Book {
    private String mTitle;
    private String mAuthors;
    private String mPublishedDate;
    private String mDescription;
    private String mCategories;
    private String mThumbnail;
    private String mBuy;
    private String mPreview;
    private String mPrice;
    private String mIsbn;
    private int pageCount;
    private String mUrl;

    String getmUrl() {
        return mUrl;
    }

    String getmIsbn() {
        return mIsbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getTitle() {
        return mTitle;
    }

    String getAuthors() {
        return mAuthors;
    }

    String getPublishedDate() {
        return mPublishedDate;
    }

    String getDescription() {
        return mDescription;
    }

    String getCategories() {
        return mCategories;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    String getBuy() {
        return mBuy;
    }

    String getPreview() {
        return mPreview;
    }

    public String getPrice() {
        return mPrice;
    }

    public Book(String mTitle, String mAuthors, String mPublishedDate, String mDescription, String mCategories, String mThumbnail,
                String mBuy, String mPreview , String price,int pageCount , String mUrl, String mIsbn) {
        this.mTitle = mTitle;
        this.mAuthors = mAuthors;
        this.mPublishedDate = mPublishedDate;
        this.mDescription = mDescription;
        this.mCategories = mCategories;
        this.mThumbnail = mThumbnail;
        this.mBuy = mBuy;
        this.mPreview = mPreview;
        this.mPrice = price;
        this.pageCount = pageCount;
        this.mUrl = mUrl;
        this.mIsbn = mIsbn;
    }

}
