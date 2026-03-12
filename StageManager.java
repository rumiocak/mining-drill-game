import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javafx.util.Duration;
import java.util.*;

public class StageManager {
    public static final int GRID_SIZE = 16;
    public static final int CELL_SIZE = 50;
    public static CellType[][] cellTypes = new CellType[GRID_SIZE][GRID_SIZE];
    public static Valuable[][] valuables = new Valuable[GRID_SIZE][GRID_SIZE];
    private int totalHaul = 0;
    private int totalMoney = 0;
    private Timeline fuelDecreaseTimeline;
    public Text fuelText;
    private Text haulText;
    private Text moneyText;

    /**
     * Displays the gameplay scene.
     * This method sets up the gameplay environment by creating a root group and a scene with specified dimensions and background color.
     * It initializes a MachineController instance and configures its properties.
     * Then, it sets up the environment including sky, grass, obstacles, valuables, lavas, and soils.
     * Fuel level, total haul, and total money are displayed.
     * The machine is initialized, fuel decrease timeline is started, and machine movement is enabled.
     * Finally, it sets properties for the primary stage and displays it.
     *
     * @param primaryStage The primary stage for displaying the scene.
     */
    public void gamePlayScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE, Color.rgb(117, 74, 52));

        // Create a new instance of MachineController and set its stageManager and primaryStage properties
        MachineController machineController = new MachineController();
        machineController.stageManager = this;
        machineController.primaryStage = primaryStage;

        // Set up the environment: sky, grass, obstacles, valuables, lavas, and soils
        setSky(root);
        setGrass(root);
        setObstacles(root);
        List<Valuable> valuables = FileManager.readValuablesFromFile();
        addValuables(root, valuables);
        addLavas(root);
        addObstacles(root);
        addSoils(root);

        // Display fuel level, total haul, and total money
        fuelText = createText("Fuel: " + String.format("%.3f", machineController.getFuelLevel()), 20, 10, 30);
        root.getChildren().add(fuelText);
        haulText = createText("Haul: " + totalHaul, 20, 10, 60);
        root.getChildren().add(haulText);
        moneyText = createText("Money: " + totalMoney, 20, 10, 90);
        root.getChildren().add(moneyText);

        // Initialize the machine, start fuel decrease timeline, and enable machine movement
        machineController.initializeMachine(root);
        startFuelDecreaseTimeline(primaryStage, machineController);
        machineController.moveMachine(root, scene);

        // Set properties for the primary stage and display it
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.setTitle("HU-Load");
        primaryStage.show();
    }

    /**
     * Displays the scene when the game time is over, showing the "GAME OVER" message and the total money collected.
     *
     * @param primaryStage The primary stage for displaying the scene.
     */
    public void timeOverScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE, Color.DARKGREEN);

        Text timeOverText = createCenterAlignedText(scene, "GAME OVER", 70, 350);
        moneyText = createCenterAlignedText(scene, "Money Collected: " + totalMoney, 50, 450);
        root.getChildren().addAll(timeOverText, moneyText);

        primaryStage.setScene(scene);
        primaryStage.setTitle("HU-Load");
        primaryStage.show();
    }

    /**
     * Displays the scene when the game is over, presenting a "GAME OVER" message.
     *
     * @param primaryStage The primary stage for displaying the scene.
     */
    public void gameOverScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE, Color.DARKRED);

        Text gameOverText = createCenterAlignedText(scene, "GAME OVER", 70, 410);
        root.getChildren().add(gameOverText);

        primaryStage.setScene(scene);
        primaryStage.setTitle("HU-Load");
        primaryStage.show();
    }

    /**
     * Displays the scene when the game is won, presenting a "Congratulations!" message and the total money collected.
     *
     * @param primaryStage The primary stage for displaying the scene.
     */
    public void gameVictoryScene(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE, Color.BLUE);

        Text congratulationsText = createCenterAlignedText(scene, "Congratulations!", 70, 350);
        moneyText = createCenterAlignedText(scene, "Money Collected: " + totalMoney, 50, 450);
        root.getChildren().addAll(congratulationsText, moneyText);

        primaryStage.setScene(scene);
        primaryStage.setTitle("HU-Load");
        primaryStage.show();
    }

    /**
     * Starts the timeline for decreasing fuel level continuously.
     * This method creates a Timeline animation with a duration of 1 millisecond to continuously decrease the fuel level.
     * It updates the fuel level displayed in the scene based on the fuel decrease rate.
     * If the fuel level reaches or goes below 0, it stops the timeline and displays the timeOverScene.
     *
     * @param primaryStage The primary stage for displaying the scene.
     * @param machineController The machine controller for managing the machine.
     */
    private void startFuelDecreaseTimeline(Stage primaryStage, MachineController machineController) {
        fuelDecreaseTimeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            // Decrease fuel level
            double fuelLevel = machineController.getFuelLevel() - MachineController.getFuelDecreaseRatePerMillisecond();
            machineController.setFuelLevel(fuelLevel);

            fuelText.setText("Fuel: " + String.format("%.3f", Math.max(fuelLevel, 0)));

            if (fuelLevel <= 0) {
                fuelDecreaseTimeline.stop();
                timeOverScene(primaryStage); // Display the timeOverScene if fuel runs out
            }
        }));
        fuelDecreaseTimeline.setCycleCount(Animation.INDEFINITE);
        fuelDecreaseTimeline.play();
    }

    /**
     * Sets up the sky area in the game scene.
     * This method iterates through the first 3 rows of the game grid and assigns the CellType.SKY to each cell.
     * It creates Rectangle objects representing sky cells and adds them to the root group with a dark blue fill color.
     *
     * @param root The root group to which sky cells are added.
     */
    private void setSky(Group root) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cellTypes[row][col] = CellType.SKY;
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE + 3);
                cell.setX(col * CELL_SIZE);
                cell.setY(row * CELL_SIZE);
                cell.setFill(Color.DARKBLUE);
                root.getChildren().add(cell);
            }
        }
    }

    /**
     * Sets up the grass area in the game scene.
     * This method initializes a grass image.
     * It assigns the CellType.GRASS to each cell in the fourth row of the game grid and adds the grass image to each cell.
     *
     * @param root The root group to which grass cells are added.
     */
    private void setGrass(Group root) {
        Image grassImage = new Image("/assets/underground/top_02.png");

        for (int col = 0; col < GRID_SIZE; col++) {
            cellTypes[3][col] = CellType.GRASS;
            addAsset(root, grassImage, 3, col);
        }
    }


    /**
     * Sets up the obstacle area in the game scene.
     * This method initializes a list of obstacle images and randomly selects one.
     * It adds obstacles to the bottom row and left and right columns of the game grid.
     * Each obstacle is assigned the CellType.OBSTACLE, and its corresponding image is added to the root group.
     *
     * @param root The root group to which obstacle cells are added.
     */
    private void setObstacles(Group root) {
        List<Image> obstacleImages = new ArrayList<>();
        obstacleImages.add(new Image("/assets/underground/obstacle_01.png"));
        obstacleImages.add(new Image("/assets/underground/obstacle_02.png"));
        obstacleImages.add(new Image("/assets/underground/obstacle_03.png"));

        // Generate a random index within the range of obstacleImages
        Random random = new Random();
        int randomIndex = random.nextInt(obstacleImages.size());
        Image obstacleImage = obstacleImages.get(randomIndex);

        // Add bottom row obstacles
        for (int col = 0; col < GRID_SIZE; col++) {
            cellTypes[GRID_SIZE - 1][col] = CellType.OBSTACLE;
            addAsset(root, obstacleImage, GRID_SIZE - 1, col);
        }

        // Add left and right column obstacles
        for (int row = 4; row < GRID_SIZE - 1; row++) {
            cellTypes[row][0] = CellType.OBSTACLE;
            cellTypes[row][GRID_SIZE - 1] = CellType.OBSTACLE;
            addAsset(root, obstacleImage, row, 0);
            addAsset(root, obstacleImage, row, GRID_SIZE - 1);
        }
    }

    /**
     * Adds valuables randomly to the game scene.
     * This method randomly determines the number of valuables to add (between 7 and 14) and ensures at least 3 unique types of valuables are added.
     * It then iterates to add valuables to the game scene, each assigned a CellType.VALUABLE and a corresponding image.
     *
     * @param root The root group to which valuables are added.
     * @param allValuables A list of all available valuable objects.
     */
    private void addValuables(Group root, List<Valuable> allValuables) {
        // Randomly determine the number of valuables to add (between 7 and 14)
        Random random = new Random();
        int numberOfValuablesToAdd = random.nextInt(9) + 7;

        // Ensure at least 3 unique types of valuables are added
        Set<Valuable> uniqueValuables = new HashSet<>();
        while (uniqueValuables.size() < 4) {
            Valuable randomValuable = allValuables.get(random.nextInt(allValuables.size()));
            uniqueValuables.add(randomValuable);
        }

        // Add valuables to the game scene
        int valuablesAdded = 0;
        while (valuablesAdded < numberOfValuablesToAdd) {
            int randomRow = random.nextInt(11) + 4;
            int randomCol = random.nextInt(14) + 1;

            if (cellTypes[randomRow][randomCol] != CellType.VALUABLE) {
                cellTypes[randomRow][randomCol] = CellType.VALUABLE;

                Valuable randomValuable = allValuables.get(random.nextInt(allValuables.size()));
                Image valuableImage = randomValuable.getImage();
                addAsset(root, valuableImage, randomRow, randomCol);

                valuables[randomRow][randomCol] = randomValuable;
                valuablesAdded++;
            }
        }
    }

    /**
     * Adds lava cells to the game scene.
     * This method initializes a list of lava images and randomly determines the number of lavas to add (between 4 and 10).
     * It iterates to add lavas to the game scene, ensuring each cell is not already occupied by a valuable or lava.
     * Each added lava cell is assigned the CellType.LAVA and animated with sequential lava images.
     *
     * @param root The root group to which lava cells are added.
     */
    private void addLavas(Group root) {
        List<Image> lavaImages = new ArrayList<>();
        lavaImages.add(new Image("/assets/underground/lava_01.png"));
        lavaImages.add(new Image("/assets/underground/lava_02.png"));
        lavaImages.add(new Image("/assets/underground/lava_03.png"));

        // Randomly determine the number of lavas to add (between 4 and 10)
        Random random = new Random();
        int numberOfLavasToAdd = random.nextInt(6) + 4;

        // Add lavas to the game scene
        int lavasAdded = 0;
        while (lavasAdded < numberOfLavasToAdd) {
            int randomRow = random.nextInt(11) + 4;
            int randomCol = random.nextInt(14) + 1;

            // Check if the cell is not already occupied by a valuable or lava
            if (cellTypes[randomRow][randomCol] != CellType.VALUABLE && cellTypes[randomRow][randomCol] != CellType.LAVA) {
                cellTypes[randomRow][randomCol] = CellType.LAVA;

                animateSequentialLavaImages(root, lavaImages, randomRow, randomCol);
                lavasAdded++;
            }
        }
    }

    /**
     * Animates sequential lava images to simulate flowing lava and adds them to the game scene.
     * This method creates a Timeline animation with keyframes for each lava image, creating a sequential animation.
     * The frameDuration variable determines the duration between each keyframe.
     * The animation is set to cycle indefinitely for continuous flow.
     *
     * @param root The root group to which lava cells are added.
     * @param lavaImages A list of lava images to animate.
     * @param row The row index of the lava cell.
     * @param col The column index of the lava cell.
     */
    private void animateSequentialLavaImages(Group root, List<Image> lavaImages, int row, int col) {
        Timeline timeline = new Timeline();
        double frameDuration = 0.27; // Duration between each keyframe in seconds (adjust as needed)

        // Add keyframes for each lava image to create a sequential animation
        for (int i = 0; i < lavaImages.size(); i++) {
            Image lavaImage = lavaImages.get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(frameDuration * (i + 1)), event -> addAsset(root, lavaImage, row, col));
            timeline.getKeyFrames().add(keyFrame);
        }

        // Set the cycle count to INDEFINITE for continuous animation
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Adds obstacles randomly to the game scene.
     * This method randomly determines the number of obstacles to add (between 4 and 10).
     * It iterates to add obstacles to the game scene, ensuring each cell is not already occupied by a valuable or obstacle.
     * Each added obstacle is assigned the CellType.OBSTACLE and a randomly selected obstacle image.
     *
     * @param root The root group to which obstacles are added.
     */
    private void addObstacles(Group root) {
        List<Image> obstacleImages = new ArrayList<>();
        obstacleImages.add(new Image("/assets/underground/obstacle_01.png"));
        obstacleImages.add(new Image("/assets/underground/obstacle_02.png"));
        obstacleImages.add(new Image("/assets/underground/obstacle_03.png"));

        // Randomly determine the number of obstacles to add (between 4 and 10)
        Random random = new Random();
        int numberOfObstaclesToAdd = random.nextInt(6) + 4;

        // Add obstacles to the game scene
        int obstaclesAdded = 0;
        while (obstaclesAdded < numberOfObstaclesToAdd) {
            int randomRow = random.nextInt(11) + 4;
            int randomCol = random.nextInt(14) + 1;

            // Check if the cell is not already occupied by a valuable or obstacle
            if (cellTypes[randomRow][randomCol] != CellType.VALUABLE && cellTypes[randomRow][randomCol] != CellType.OBSTACLE) {
                cellTypes[randomRow][randomCol] = CellType.OBSTACLE;

                Image randomObstacle = obstacleImages.get(random.nextInt(obstacleImages.size()));
                addAsset(root, randomObstacle, randomRow, randomCol);
                obstaclesAdded++;
            }
        }
    }

    /**
     * Adds soils in suitable places to the game scene.
     *
     * @param root The group to which soil cells will be added.
     */
    private void addSoils(Group root) {
        Image soilImage = new Image("/assets/underground/soil_01.png");

        // Iterate through grid cells to add soil cells
        for (int row = 4; row < GRID_SIZE - 1; row++) {
            for (int col = 1; col < GRID_SIZE - 1; col++) {
                if (cellTypes[row][col] == null) {
                    cellTypes[row][col] = CellType.SOIL;
                    addAsset(root, soilImage, row, col);
                }
            }
        }
    }

    /**
     * Adds an asset, represented by an image, to the specified row and column in the game grid.
     *
     * @param root The group to which the asset image will be added.
     * @param assetImage The image representing the asset to be added.
     * @param row The row index in the grid where the asset will be placed.
     * @param col The column index in the grid where the asset will be placed.
     */
    private void addAsset(Group root, Image assetImage, int row, int col) {
        // Create an ImageView for the asset image
        ImageView imageView = new ImageView(assetImage);

        // Set the position of the ImageView based on row and col indices
        imageView.setX(col * CELL_SIZE);
        imageView.setY(row * CELL_SIZE);
        root.getChildren().add(imageView);
    }

    /**
     * Creates a Text node with the specified content, font size, position, and color.
     *
     * @param content The text content to be displayed.
     * @param size The font size of the text.
     * @param x The x-coordinate position of the text.
     * @param y The y-coordinate position of the text.
     * @return The Text node with the specified properties.
     */
    private Text createText(String content, int size, double x, double y) {
        // Create a Text node with the specified content
        Text text = new Text(content);
        text.setFont(Font.font(size));
        text.setFill(Color.WHITE);
        text.setX(x);
        text.setY(y);

        return text;
    }

    /**
     * Creates a Text node with the specified content, font size, and y-coordinate position, and aligns it to the center of the scene.
     *
     * @param scene The Scene to which the text will be aligned.
     * @param content The text content to be displayed.
     * @param size The font size of the text.
     * @param y The y-coordinate position of the text.
     * @return The Text node with the specified properties.
     */
    private Text createCenterAlignedText(Scene scene, String content, int size, double y) {
        // Create a Text node with the specified content
        Text text = new Text(content);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(size));
        text.setFill(Color.WHITE);
        text.setY(y);

        // Calculate the x-coordinate position to center the text
        double centerX = (scene.getWidth() - Math.max(text.getLayoutBounds().getWidth(), text.getLayoutBounds().getWidth())) / 2;
        text.setLayoutX(centerX);

        return text;
    }

    /**
     * Updates the haul text with the new total haul value.
     *
     * @param totalHaul The new total haul value to be displayed.
     */
    public void updateHaulText(int totalHaul) {
        haulText.setText("Haul: " + totalHaul);
        this.totalHaul = totalHaul;
    }

    /**
     * Updates the money text with the new total money value.
     *
     * @param totalMoney The new total money value to be displayed.
     */
    public void updateMoneyText(int totalMoney) {
        moneyText.setText("Money: " + totalMoney);
        this.totalMoney = totalMoney;
    }
}
