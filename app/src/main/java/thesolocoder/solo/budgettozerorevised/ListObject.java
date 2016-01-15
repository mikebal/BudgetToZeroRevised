package thesolocoder.solo.budgettozerorevised;

/**
 * Created by Michael on 2015-06-05.
 */
public class ListObject {
    private String date;
    private String discription;
    private String cost;
    private String category;

    public ListObject(String date, String discription, String cost, String category) {
        this.date = date;
        this.discription = discription;
        this.cost = cost;
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public String getDiscription() {
        return discription;
    }

    public String getCost() {
        return cost;
    }

    public String getCategory(){return category;}

    public void  setCategory(String newName){ category = newName;}
}
