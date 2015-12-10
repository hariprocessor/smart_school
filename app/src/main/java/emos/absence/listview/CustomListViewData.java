package emos.absence.listview;

public class CustomListViewData {
    private String tagID;
    private String isLate;

    public CustomListViewData(String tagID, String isLate) {
        this.tagID = tagID;
        this.isLate = isLate;
    }

    public String getTagID() {
        return tagID;
    }

    public String getIsLate() {
        return isLate;
    }
}
