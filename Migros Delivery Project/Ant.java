import java.util.Random;

public class Ant {

    int randomStartingNode;

    public Ant(){
    }

    public Ant(int houseCount){

        Random rand = new Random();
        randomStartingNode = rand.nextInt(houseCount);

    }
}