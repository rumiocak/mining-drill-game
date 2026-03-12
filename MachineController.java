import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;

import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Iterator;
import java.util.Random;

public class MachineController {
    private static final double FUEL_DECREASE_PER_MOVE = 100;
    private static final double FUEL_DECREASE_RATE_PER_MILLISECOND = 0.0005;
    private double fuelLevel = 10000.0;
    private int positionX = 0;
    private int positionY = 0;
    private ImageView machineView;
    private Timeline flyingAnimation = new Timeline();
    private Timeline fallingAnimation = new Timeline();
    private Direction currentDirection = Direction.RIGHT;
    private boolean upKeyPressed = false;
    private boolean downKeyPressed = false;
    private int totalHaul;
    private int totalMoney;
    public StageManager stageManager;
    public Stage primaryStage;

    /**
     * Gets the current fuel level of the vehicle.
     *
     * @return The current fuel level.
     */
    public double getFuelLevel() {
        return fuelLevel;
    }

    /**
     * Sets the fuel level of the vehicle to the specified value.
     *
     * @param fuelLevel The new fuel level to be set.
     */
    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    /**
     * Gets the rate at which the fuel decreases per millisecond.
     *
     * @return The fuel decrease rate per millisecond.
     */
    public static double getFuelDecreaseRatePerMillisecond() {
        return FUEL_DECREASE_RATE_PER_MILLISECOND;
    }


    /**
     * Initializes the drilling machine by setting its initial position, image, and adding it to the specified root group.
     *
     * @param root The group to which the drilling machine will be added.
     */
    public void initializeMachine(Group root) {
        // Set gravity for the machine
        setGravity(root);

        // Create an ImageView for the drilling machine
        Image drillRightImage = new Image("/assets/drill/drill_11.png");
        machineView = new ImageView(drillRightImage);

        // Set the dimensions of the machine
        machineView.setFitWidth(63);
        machineView.setFitHeight(63);

        // Generate random initial position for the machine and set it
        Random random = new Random();
        positionX = random.nextInt(15) * StageManager.CELL_SIZE;
        positionY = 2 * StageManager.CELL_SIZE;
        machineView.setX(positionX);
        machineView.setY(positionY);
        root.getChildren().add(machineView);
    }

    /**
     * Sets up gravity effect for the drilling machine, causing it to fall downward until it encounters soil or reaches the ground.
     * This method implements a basic falling logic for the drilling machine.
     * It calculates the new position of the machine by moving it downward by one cell size.
     * Then, it checks if the new position is within the valid range and if the cell at that position is either dug soil or sky.
     * If so, it updates the machine's position accordingly and sets its direction upward.
     * If the machine encounters anything other than dug soil or sky, the falling animation stops.
     *
     * @param root The group containing the game elements.
     */
    private void setGravity(Group root) {
        fallingAnimation = new Timeline(new KeyFrame(Duration.seconds(0.3), event -> {
            // Calculate new position of the machine
            int newX = positionX;
            int newY = positionY + StageManager.CELL_SIZE;

            // Check if the new position is within valid range and if it's soil or sky
            if (isWithinValidRange(newX, newY) && (StageManager.cellTypes[(newY / StageManager.CELL_SIZE)][(newX / StageManager.CELL_SIZE)] == CellType.DUG_SOIL ||
                    StageManager.cellTypes[(newY / StageManager.CELL_SIZE)][(newX / StageManager.CELL_SIZE)] == CellType.SKY)) {
                // Update machine direction and position
                currentDirection = Direction.UP;
                updateMachinePosition(root, newX, newY);
            } else {
                fallingAnimation.stop();
                flyingAnimation.stop();
            }
        }));
        // Set cycle count to indefinite and start the falling animation
        fallingAnimation.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Sets up event handling to move the drilling machine based on user input and updates the fuel level.
     *
     * @param root The group containing the game elements.
     * @param scene The scene where the machine movement will be handled.
     */
    public void moveMachine(Group root, Scene scene) {
        // Set up key press event handling
        scene.setOnKeyPressed(event -> {
            handleMachineMovement(event, root); // Handle machine movement based on user input
            handleGravityDuration(); // Handle gravity effect on the machine
            updateFuelLevel(); // Update the fuel level
        });
    }

    /**
     * Updates the fuel level based on the fuel decrease per move and ensures it doesn't go below 0.
     * The fuel level decreases by the specified amount per move. After decreasing, if the fuel level becomes negative,
     * it is set to 0 to prevent negative values.
     */
    private void updateFuelLevel() {
        fuelLevel -= FUEL_DECREASE_PER_MOVE;
        fuelLevel = Math.max(fuelLevel, 0);
        stageManager.fuelText.setText("Fuel: " + String.format("%.3f", fuelLevel));
    }


    /**
     * Handles the movement of the drilling machine based on the user's keyboard input.
     *
     * @param event The KeyEvent representing the keyboard input.
     * @param root The group containing the game elements.
     */
    private void handleMachineMovement(KeyEvent event, Group root) {
        // Initialize new positions as current positions
        int newX = positionX;
        int newY = positionY;

        // Determine movement based on the pressed key
        switch (event.getCode()) {
            case RIGHT:
                newX += StageManager.CELL_SIZE;
                currentDirection = Direction.RIGHT;
                break;
            case LEFT:
                newX -= StageManager.CELL_SIZE;
                currentDirection = Direction.LEFT;
                break;
            case DOWN:
                downKeyPressed = true;
                newY += StageManager.CELL_SIZE;
                currentDirection = Direction.DOWN;
                break;
            case UP:
                if (isValidUpPosition()) {
                    upKeyPressed = true;
                    newY -= StageManager.CELL_SIZE;
                    currentDirection = Direction.UP;
                }
                break;
        }

        updateMachinePosition(root, newX, newY);
    }

    /**
     * Handles the duration of gravity effect on the drilling machine based on user input.
     * If the machine is moving up or down, temporarily stops the falling animation before resuming it.
     * Otherwise, continues the falling animation.
     */
    private void handleGravityDuration() {
        if (upKeyPressed || downKeyPressed) {
            // If the machine is moving up or down, stop the falling animation temporarily
            fallingAnimation.stop();
            // Create a pause transition to delay the resumption of falling animation
            PauseTransition pause = new PauseTransition(Duration.millis(40));
            pause.setOnFinished(e -> {
                // After the delay, resume the falling animation and reset key presses
                fallingAnimation.play();
                upKeyPressed = false;
                downKeyPressed = false;
            });
            // Start the pause transition
            pause.play();
        } else {
            // If the machine is not moving up or down, continue the falling animation
            fallingAnimation.play();
        }
    }

    /**
     * Updates the position of the drilling machine on the game grid and checks for collisions with obstacles.
     *
     * @param root The group containing the game elements.
     * @param newX The new x-coordinate position of the machine.
     * @param newY The new y-coordinate position of the machine.
     */
    private void updateMachinePosition(Group root, int newX, int newY) {
        int row = newY / StageManager.CELL_SIZE;
        int col = newX / StageManager.CELL_SIZE;

        // Check if the new position is within the valid range and doesn't collide with obstacles
        if (isWithinValidRange(newX, newY) && StageManager.cellTypes[row][col] != CellType.OBSTACLE) {
            // Update machine position
            positionX = newX;
            positionY = newY;

            // Set the new position of the machine ImageView
            machineView.setX(positionX);
            machineView.setY(positionY);

            // Update the cell type in the game grid
            updateCellType(root, row, col);

            // Update the image of the drilling machine based on its direction
            updateMachineImage();
        }
    }

    /**
     * Updates the image of the drilling machine based on its current direction and surroundings.
     * This method calculates the row and column indices of the machine's position and loads images for different machine movements.
     * Depending on the current direction of the machine, it checks if the machine can fly and updates the machine image accordingly.
     * If the machine is moving left or right, it checks if it can fly and either starts the flying animation or sets the machine image for left or right movement.
     * If the machine is moving upward, it always flies the machine.
     * If the machine is moving downward, it checks if it can fly and either starts the flying animation or sets the machine image for downward movement.
     */
    private void updateMachineImage() {
        // Calculate the row and column indices of the machine's position
        int row = positionY / StageManager.CELL_SIZE;
        int col = positionX / StageManager.CELL_SIZE;

        // Load images for different machine movements
        Image leftMoveImage = new Image("/assets/drill/drill_12.png");
        Image downMoveImage = new Image("/assets/drill/drill_41.png");
        Image[] flyingMachineImages = FileManager.loadFlyingMachineImages();

        switch (currentDirection) {
            case LEFT:
            case RIGHT:
                // Check if the machine can fly and update the machine image accordingly
                if (StageManager.cellTypes[row + 1][col] == CellType.DUG_SOIL || StageManager.cellTypes[row + 1][col] == CellType.SKY) {
                    flyTheMachine(flyingMachineImages);
                    machineView.setScaleX(currentDirection == Direction.RIGHT ? -1 : 1);
                } else {
                    // Stop flying animation and set machine image for left or right movement
                    flyingAnimation.stop();
                    machineView.setImage(leftMoveImage);
                    machineView.setScaleX(currentDirection == Direction.RIGHT ? -1 : 1); // Flip the left move image horizontally if going right
                }
                break;
            case UP:
                // Fly the machine if moving up
                flyTheMachine(flyingMachineImages);
                break;
            case DOWN:
                // Check if the machine can fly and update the machine image accordingly
                if (StageManager.cellTypes[row + 1][col] == CellType.DUG_SOIL || StageManager.cellTypes[row + 1][col] == CellType.SKY) {
                    flyTheMachine(flyingMachineImages);
                } else {
                    // Stop flying animation and set machine image for downward movement
                    flyingAnimation.stop();
                    Image shiftedMachineUpImage = FileManager.shiftImageContentLeft(downMoveImage, 10);
                    machineView.setImage(shiftedMachineUpImage);
                    machineView.setScaleX(1);
                }
                break;
        }
    }

    /**
     * Initiates flying animation for the drilling machine using the provided images.
     * This method creates a Timeline animation with a very small duration (0.001 seconds) to simulate smooth flying motion.
     * It calculates the index to select the current flying machine image from the array based on the current time in milliseconds, creating a time step.
     * The modulus operation ensures that the index value cycles between 0 and 2, inclusive, representing the different frames of the flying animation.
     * The selected image content is shifted to create the illusion of movement, and then the machine image is updated accordingly.
     *
     * @param flyingMachineImages An array of images representing the frames for the flying animation.
     */
    private void flyTheMachine(Image[] flyingMachineImages) {
        // Check if the flying animation is not already running
        if (flyingAnimation.getStatus() != Animation.Status.RUNNING) {
            flyingAnimation = new Timeline(new KeyFrame(Duration.seconds(0.001), event -> {
                // Calculate the index to select the current flying machine image from the array
                int index = (int) ((System.currentTimeMillis() / 100) % 3);
                // Shift the selected image content and update the machine image
                Image flyingMachine = FileManager.shiftImageContentLeft(flyingMachineImages[index], 10);
                machineView.setImage(flyingMachine);
            }));
            // Set cycle count to indefinite and start the flying animation
            flyingAnimation.setCycleCount(Timeline.INDEFINITE);
            flyingAnimation.play();
        }
    }

    /**
     * Removes the asset located at the specified row and column from the game grid.
     *
     * @param root The group containing the game elements.
     * @param row The row index of the asset to be removed.
     * @param col The column index of the asset to be removed.
     */
    private void drill(Group root, int row, int col) {
        Iterator<Node> iterator = root.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            // Check if the node is an ImageView and not the drilling machine
            if (node instanceof ImageView && node != machineView) {
                // Get the coordinates and indices of the node
                double nodeX = node.getBoundsInParent().getMinX();
                double nodeY = node.getBoundsInParent().getMinY();
                int nodeRow = (int) (nodeY / StageManager.CELL_SIZE);
                int nodeCol = (int) (nodeX / StageManager.CELL_SIZE);
                // If the node is located at the specified row and column, remove it from the root group
                if (nodeRow == row && nodeCol == col) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Updates the cell type and game state based on the content of the specified cell.
     *
     * @param root The group containing the game elements.
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     */
    private void updateCellType(Group root, int row, int col) {
        CellType cellType = StageManager.cellTypes[row][col];

        if (cellType == CellType.LAVA) {
            // End the game if the cell contains lava
            stageManager.gameOverScene(primaryStage);
        } else if (cellType == CellType.SOIL ||
                cellType == CellType.VALUABLE ||
                cellType == CellType.GRASS) {
            // Perform actions for soil, valuable, or grass cells

            // If the cell contains a valuable, add its weight and money to the total haul and money
            if (cellType == CellType.VALUABLE) {
                totalHaul += StageManager.valuables[row][col].getHaul();
                stageManager.updateHaulText(totalHaul);

                totalMoney += StageManager.valuables[row][col].getMoney();
                stageManager.updateMoneyText(totalMoney);

                // Remove the valuable from the array
                StageManager.valuables[row][col] = null;

                // Check if all valuables are collected and end the game if so
                if (allValuablesCollected(StageManager.valuables)) {
                    stageManager.gameVictoryScene(primaryStage);
                }
            }

            // Remove the asset from the root
            drill(root, row, col);
            StageManager.cellTypes[row][col] = CellType.DUG_SOIL;
        }
    }

    /**
     * Checks if all valuable items have been collected.
     *
     * @param valuables The 2D array representing the positions of valuable items.
     * @return True if all valuable items are collected, false otherwise.
     */
    public boolean allValuablesCollected(Valuable[][] valuables) {
        for (Valuable[] row : valuables) {
            for (Valuable valuable : row) {
                if (valuable != null) {
                    return false;
                }
            }
        }
        // If all elements are null, return true
        return true;
    }

    /**
     * Checks if the specified coordinates (x, y) are within the valid game grid range.
     *
     * @param x The x-coordinate to be checked.
     * @param y The y-coordinate to be checked.
     * @return True if the coordinates are within the valid range, false otherwise.
     */
    private boolean isWithinValidRange(int x, int y) {
        return x >= 0 && x < StageManager.GRID_SIZE * StageManager.CELL_SIZE &&
                y >= 0 && y < 4 * StageManager.CELL_SIZE ||
                x >= 1 && x < (StageManager.GRID_SIZE - 1) * StageManager.CELL_SIZE &&
                        y >= 4 && y < (StageManager.GRID_SIZE - 1) * StageManager.CELL_SIZE;
    }

    /**
     * Checks if moving the drilling machine upward from its current position is a valid move.
     *
     * @return True if moving upward is valid, false otherwise.
     */
    private boolean isValidUpPosition() {
        int rowAbove = (positionY - StageManager.CELL_SIZE) / StageManager.CELL_SIZE;
        int col = positionX / StageManager.CELL_SIZE;

        if (!isWithinValidRange(rowAbove, col)) return false;

        // Retrieve the cell type of the cell above the machine
        CellType cellTypeAbove = StageManager.cellTypes[rowAbove][col];

        // Return true if the cell above is sky or dug soil, indicating a valid move upward
        return cellTypeAbove == CellType.SKY || cellTypeAbove == CellType.DUG_SOIL;
    }
}