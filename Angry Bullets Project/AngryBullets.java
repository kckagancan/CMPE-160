import java.awt.event.KeyEvent;

/**
 * @author Kagan Can, Student ID: 2022400240
 * @since Date: 10.03.2024
 */

public class AngryBullets {

    public static void main(String[] args) {

        // Game Parameters
        int width = 1600; //screen width
        int height = 800; // screen height
        double gravity = 9.80665; // gravity

        double x0 = 120; // x and y coordinates of the bullet’s starting position on the platform
        double y0 = 120;
        double bulletVelocity = 180; // initial velocity
        double bulletAngle = 45.0; // initial angle
        double bulletAngleRadian = Math.toRadians(bulletAngle);

        // Scale the X and Y velocity with a scalar
        double bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
        double bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;

        boolean isGameStarted = false;
        boolean isGameEnded = false;
        boolean cleared = true;
        boolean firstBall = true;

        // previous position of the ball
        double prevX = 0;
        double prevY = 0;

        double lineGrowthConstant = 0; // growth constant to speed up the visual growth of the line
        double radius = 5;
        int ballPauseDuration = 20;
        int linePauseDuration = 100;
        double timeInterval = 0.2;

        // Box coordinates for obstacles and targets
        // Each row stores a box containing the following information:
        // x and y coordinates of the lower left rectangle corner, width, and height

        double[][] obstacleArray = {
                {1200, 0, 60, 220},
                {1000, 0, 60, 160},
                {600, 0, 60, 80},
                {600, 180, 60, 160},
                {220, 0, 120, 180}
        };

        double[][] targetArray = {
                {1160, 0, 30, 30},
                {730, 0, 30, 30},
                {150, 0, 20, 20},
                {1480, 0, 60, 60},
                {340, 80, 60, 30},
                {1500, 600, 60, 60}
        };

        /*
        double[][] obstacleArray = {
                {1300, 0, 60, 150},
                {1300, 240, 60, 160},
                {700, 0, 60, 350},
                {300, 0, 60, 200},
                {180, 600, 20, 100},
                {235, 600, 20, 100},
                {750, 500, 150, 30},
                {650, 600, 250, 50}
        };

        double[][] targetArray = {
                {1160, 0, 30, 30},
                {600, 0, 30, 30},
                {1495, 0, 60, 60},
                {1500, 680, 60, 60},
                {210, 620, 15, 15},
                {840, 540, 40, 50},
                {1315, 410, 30, 30}
        };
        */

        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.enableDoubleBuffering();

        while (true) {

            if (isGameStarted) {

                // Check target collision
                for (double[] target : targetArray) {
                    if (!isGameEnded && target[0] <= x0 && x0 <= target[0] + target[2] && target[1] <= y0 && y0 <= target[1] + target[3]) {

                        StdDraw.textLeft(50, height - 50, "Congratulations: You hit the target!");
                        isGameEnded = true;
                    }
                }

                // Check obstacle collision
                for (double[] obstacle : obstacleArray) {
                    if (!isGameEnded && obstacle[0]<= x0 && x0 <= obstacle[0] + obstacle[2] && obstacle[1]<= y0 && y0 <= obstacle[1] + obstacle[3]) {

                        StdDraw.textLeft(50, height - 50, "Hit an obstacle. Press 'r' to shoot again.");
                        isGameEnded = true;
                    }
                }

                // Check right canvas collision
                if (!isGameEnded && x0 >= width) {

                    StdDraw.textLeft(50, height - 50, "Max X reached. Press 'r' to shoot again.");
                    isGameEnded = true;
                }

                // Check ground collision
                if (!isGameEnded && y0 <=0) {

                    StdDraw.textLeft(50, height - 50, "Hit the ground. Press 'r' to shoot again");
                    isGameEnded = true;
                }
            }


            // IF R is pressed, restart the game with default parameters
            if (isGameEnded && StdDraw.isKeyPressed(KeyEvent.VK_R)) {

                x0 = 120;
                y0 = 120;
                bulletVelocity = 180;
                bulletAngle = 45.0;
                lineGrowthConstant = 0;
                bulletAngleRadian = Math.toRadians(bulletAngle);
                bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
                bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;

                isGameStarted = false;
                isGameEnded = false;
                cleared = true; // checks if the window is cleared
                firstBall = true; // checks if it is the first ball to draw

                prevX = 0;
                prevY = 0;

                StdDraw.clear();
            }


            // arrange the bulletAngle using Up and Down keys
            if (!isGameStarted && StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {

                bulletAngle = bulletAngle - 1;
                bulletAngleRadian = Math.toRadians(bulletAngle);
                bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
                bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;
                StdDraw.clear();
                cleared = true;
                StdDraw.pause(linePauseDuration);
            }

            if (!isGameStarted && StdDraw.isKeyPressed(KeyEvent.VK_UP)) {

                bulletAngle = bulletAngle + 1;
                bulletAngleRadian = Math.toRadians(bulletAngle);
                bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
                bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;
                StdDraw.clear();
                cleared = true;
                StdDraw.pause(linePauseDuration);
            }


            // arrange the bulletVelocity using UP and DOWN keys
            if (!isGameStarted && StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {

                bulletVelocity = bulletVelocity + 1;
                bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
                bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;

                if (bulletVelocity >= 180){
                    lineGrowthConstant += 4;
                }
                else{
                    lineGrowthConstant = 0;
                }

                StdDraw.clear();
                cleared = true;
                StdDraw.pause(linePauseDuration);
            }

            if (!isGameStarted && StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {

                bulletVelocity = bulletVelocity - 1;
                bulletVelocityY = bulletVelocity * Math.sin(bulletAngleRadian)/1.725;
                bulletVelocityX = bulletVelocity * Math.cos(bulletAngleRadian)/1.725;

                if (bulletVelocity >= 180){
                    lineGrowthConstant -= 4;
                }
                else{
                    lineGrowthConstant = 0;
                }

                StdDraw.clear();
                cleared = true;
                StdDraw.pause(linePauseDuration);
            }


            // Re-draw the shapes if the canvas is cleared
            if (cleared) {

                // Draw default box and initial bullet
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle(x0 / 2, y0 / 2, x0 / 2, y0 / 2);
                StdDraw.setPenRadius(0.01);
                StdDraw.line(x0, y0, x0 + (bulletVelocity + lineGrowthConstant) * Math.cos(bulletAngleRadian)*2.0/5.0, y0 + (bulletVelocity + lineGrowthConstant) * Math.sin(bulletAngleRadian)*2.0/5.0);
                StdDraw.setPenRadius();


                // Add velocity and angle texts over the default box
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.textLeft(20, 80, "V: %.1f".formatted(bulletVelocity));
                StdDraw.textLeft(20, 40, "A: %.1f".formatted(bulletAngle));


                // Draw the obstacles
                StdDraw.setPenColor(StdDraw.DARK_GRAY);
                for (double[] obstacle : obstacleArray) {
                    StdDraw.filledRectangle(obstacle[0] + obstacle[2] / 2, obstacle[1] + obstacle[3] / 2, obstacle[2] / 2, obstacle[3] / 2);
                }


                // Draw the targets
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                for (double[] target : targetArray) {
                    StdDraw.filledRectangle(target[0] + target[2] / 2, target[1] + target[3] / 2, target[2] / 2, target[3] / 2);
                }
            }


            // If space key is pressed, start the game
            if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                isGameStarted = true;
            }

            // Shoot the bullet and draw the projectile motion
            if (isGameStarted && !isGameEnded) {

                // Draw the first ball
                if (firstBall) {
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.filledCircle(x0, y0, radius);
                    firstBall = false;
                }

                // Draw the balls and draw a line between them
                else {

                    x0 = x0 + bulletVelocityX * timeInterval;
                    y0 = y0 + bulletVelocityY * timeInterval - gravity * timeInterval * timeInterval * 0.5;
                    bulletVelocityY = bulletVelocityY - gravity * timeInterval;

                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.line(prevX, prevY, x0, y0);
                    StdDraw.filledCircle(x0, y0, radius);

                }

                StdDraw.pause(ballPauseDuration);

                // Keeping the previous position of the ball
                prevY = y0;
                prevX = x0;
            }

            cleared = false; // Make this false to avoid unnecessary drawings
            StdDraw.show();
        }
    }
}