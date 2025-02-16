public class House {

    public static int houseCount;
    public double x;
    public double y;
    public int houseNum;

    public House(){
    }

    public House(double x, double y){
        this.x = x;
        this.y = y;
        houseCount++;
        houseNum = houseCount;
    }
}
