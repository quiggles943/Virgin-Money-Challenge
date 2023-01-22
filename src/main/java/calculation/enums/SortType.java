package calculation.enums;

public enum SortType {

    DATE_ASC("Oldest First"),
    DATE_DESC("Newest First");

    public String key;

    SortType(String key){
        this.key = key;
    }


}
