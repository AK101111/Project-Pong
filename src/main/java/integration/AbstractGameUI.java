package integration;

/**
 * Represents the game UI (things that can be seen and manipulated by the user) on a single machine.
 * Contains handles to change the UI (move paddle), which will be used by the networking code.
 *
 * Assumptions :
 * 1. Exactly 4 players (user/other player/AI) play the game.
 * 2. Each player is assigned a unique ID (an integer), which is also consistent for all machines in the game network.
 *    Let's use this simple scheme :
 *		Top player -> 0, Right player -> 1, Bottom player -> 2, Right player -> 3
 *				0
 *			3		1
 *				2
 *	 (Some other scheme may equally be chosen, only condition is that (same) id must uniquely identify player (paddle)
 *	  on the boards of all machines playing simultaneously)
 */
public interface AbstractGameUI {

    /**
     * Moves the paddle with given id.
     * After this method new coordinates of paddle are (X0 + delX, Y0 + delY)
     * @param id ID to identify the paddle to be moved
     * @param delX number of pixels to be moved in the +ve X direction
     * @param delY number of pixels to be moved in the +ve Y direction
     * @return True if the motion is possible and successful, false if not (due blockage by wall etc.)
     */
    boolean movePaddle (int id, int delX, int delY);
    boolean movePaddleAbsolute (int id, int x, int y);

    interface PaddleMoveListener {
        void handlePaddleMove (int id, int delX, int delY);
    }
    interface AbsoluteMoveListener {
        void handlePaddlePosition (int id, int x, int y);
    }
    /**
     * Sets a listener on movement of the paddles in this UI instance (by the user on the particular machine).
     * Through this listener this motion will be conveyed to other machines in the network.
     * @param paddleMoveListener Call the {@code handlePaddleMove} with appropriate values when paddles are moved internally.
     */
    void setOnInternalPaddleMoveListener(PaddleMoveListener paddleMoveListener);
    void setOnInternalAbsolutePaddleMoveListener (AbsoluteMoveListener paddleMoveListener);

    //These methods need to be called before the game actually starts
    //Ensures that the paddle is controlled by pressing keys on the keyboard.
    void setPaddleAsKeyboardControlled (int paddleId, boolean owner);
    //Ensure that the paddle is controlled by the AI.
    void setPaddleAsAiControlled (int paddleId);
    //NOTE : make the Paddles AI/keyboard controlled only if these methods have been called before start of the game.
    //Paddles that haven't been marked as AI/keyboard controlled remain still on the board (unless they are moved by the `movePaddle` method).
    GameState getGameState ();
    void setGameState (GameState gameState);
}
